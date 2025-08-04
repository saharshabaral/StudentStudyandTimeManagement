package com.best.studentstudyandtimemanagement

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.best.studentstudyandtimemanagement.database.UserDatabase

import com.best.studentstudyandtimemanagement.data.User
import com.best.studentstudyandtimemanagement.ui.theme.StudentStudyAndTimeManagementTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class Screen {
    LOGIN, SIGNUP, FORGOT_PASSWORD, HOME, PLAN_STUDY, TRACK_TASK
}

data class TrackableTask(
    val name: String,
    val deadline: MutableState<Long>,
    var isRunning: MutableState<Boolean> = mutableStateOf(false),
    var elapsedTime: MutableState<Long> = mutableStateOf(0L),
    var isCompleted: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    private val CHANNEL_ID = "study_app_channel"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val db = UserDatabase.getDatabase(applicationContext)

        val userDao = db.userDao()

        setContent {
            StudentStudyAndTimeManagementTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginSuccess = { currentScreen = Screen.HOME },
                            onSignUpClick = { currentScreen = Screen.SIGNUP },
                            onForgotPasswordClick = { currentScreen = Screen.FORGOT_PASSWORD },
                            userDao = userDao
                        )
                        Screen.SIGNUP -> SignupScreen(onBack = { currentScreen = Screen.LOGIN }, userDao = userDao)
                        Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(onBack = { currentScreen = Screen.LOGIN })
                        Screen.HOME -> StudyHomePage(
                            modifier = Modifier.padding(innerPadding),
                            onPlanStudyClick = { currentScreen = Screen.PLAN_STUDY },
                            onTrackTaskClick = { currentScreen = Screen.TRACK_TASK },
                            onLogoutClick = { currentScreen = Screen.LOGIN }
                        )
                        Screen.PLAN_STUDY -> PlanStudyScreen(onBack = { currentScreen = Screen.HOME })
                        Screen.TRACK_TASK -> TrackTaskScreen(onBack = { currentScreen = Screen.HOME })
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "StudyApp Notifications"
            val descriptionText = "Notifications for deadlines and reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    userDao: com.best.studentstudyandtimemanagement.database.UserDao
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.logincover),
            contentDescription = "Login Cover",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Life Tracker", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF1E88E5))

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (username.isNotBlank() && password.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.getUserByEmail(username)
                    if (user != null && user.password == password) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot Password?")
        }

        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up")
        }
    }
}

@Composable
fun SignupScreen(onBack: () -> Unit, userDao: com.best.studentstudyandtimemanagement.database.UserDao) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (email.isNotBlank() && password.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.insertUser(User(email = email, password = password))
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("Back to Login")
        }
    }

}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            Toast.makeText(context, "Password reset link sent!", Toast.LENGTH_SHORT).show()
            onBack()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Send Reset Link")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("Back to Login")
        }
    }
}

@Composable
fun StudyHomePage(
    modifier: Modifier = Modifier,
    onPlanStudyClick: () -> Unit,
    onTrackTaskClick: () -> Unit,
    onLogoutClick: () -> Unit // 🔴 NEW PARAMETER
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.cover),
            contentDescription = "Study App Cover Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text("Study & Time Management", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))
        Text("Welcome back, Student!", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)

        Button(onClick = onPlanStudyClick, modifier = Modifier.fillMaxWidth()) {
            Text("Plan Your Study")
        }

        Button(onClick = onTrackTaskClick, modifier = Modifier.fillMaxWidth()) {
            Text("Track Tasks")
        }

        Button(onClick = {
            Toast.makeText(context, "Time Reminders Coming Soon", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Time Reminders")
        }

        // Separator
        Divider(color = Color.LightGray, thickness = 1.dp)

        // 🔴 LOGOUT BUTTON
        Button(
            onClick = {
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                onLogoutClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

    }
}


@Composable
fun TrackTaskScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val mainActivity = (context as? MainActivity)
    val taskList = remember { mutableStateListOf<TrackableTask>() }
    var taskName by remember { mutableStateOf("") }
    var deadlineInput by remember { mutableStateOf("") }
    val mediaPlayer = remember { MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Track Tasks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = deadlineInput,
            onValueChange = { deadlineInput = it },
            label = { Text("Deadline (yyyy-MM-dd)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (taskName.isNotBlank() && deadlineInput.isNotBlank()) {
                try {
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val deadline = formatter.parse(deadlineInput)?.time ?: System.currentTimeMillis()
                    taskList.add(TrackableTask(taskName, mutableStateOf(deadline)))
                    taskName = ""
                    deadlineInput = ""
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill task name and deadline", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Task")
        }
        Spacer(modifier = Modifier.height(16.dp))

        taskList.forEachIndexed { index, task ->
            TaskTimerItem(
                task = task,
                onTaskComplete = {
                    mediaPlayer.start()
                    Toast.makeText(context, "${task.name} marked completed!", Toast.LENGTH_SHORT).show()
                },
                onCheckDeadline = {
                    val now = System.currentTimeMillis()
                    val diff = task.deadline.value - now
                    if (diff in 0..(2 * 24 * 3600 * 1000)) {  // Within 2 days
                        mainActivity?.sendNotification(
                            context,
                            "Task Deadline Approaching",
                            "Task '${task.name}' deadline is within 2 days!",
                            notificationId = index
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
            Text("Back")
        }
    }
}

@Composable
fun TaskTimerItem(
    task: TrackableTask,
    onTaskComplete: () -> Unit,
    onCheckDeadline: () -> Unit
) {
    val timeDisplay = remember { mutableStateOf(formatTime(task.elapsedTime.value)) }

    LaunchedEffect(task.isRunning.value) {
        while (task.isRunning.value) {
            delay(1000)
            task.elapsedTime.value += 1000
            if (task.elapsedTime.value >= 10_800_000L) { // 3 hours in ms
                task.isRunning.value = false
                task.isCompleted.value = true
                onTaskComplete()
            }
            timeDisplay.value = formatTime(task.elapsedTime.value)
            onCheckDeadline()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = if (task.isCompleted.value) Color(0xFFD0F0C0) else Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.name, style = MaterialTheme.typography.titleMedium)
            Text("Time spent: ${timeDisplay.value}", color = Color.Gray)
            val deadlineStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(task.deadline.value))
            Text("Deadline: $deadlineStr", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = {
                        if (!task.isRunning.value && !task.isCompleted.value && task.elapsedTime.value < 10_800_000L) {
                            task.isRunning.value = true
                        }
                    },
                    enabled = !task.isRunning.value && !task.isCompleted.value && task.elapsedTime.value < 10_800_000L
                ) {
                    Text("Start")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { task.isRunning.value = false },
                    enabled = task.isRunning.value
                ) {
                    Text("Stop")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        task.isRunning.value = false
                        task.isCompleted.value = true
                        onTaskComplete()
                    },
                    enabled = !task.isCompleted.value
                ) {
                    Text("Complete")
                }
            }
        }
    }
}

@Composable
fun PlanStudyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var taskName by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }
    var taskGoal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Plan Your Study", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("What do you want to achieve?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = taskGoal,
            onValueChange = { taskGoal = it },
            label = { Text("Describe your goal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = taskTime,
            onValueChange = { taskTime = it },
            label = { Text("When will you do it?") },
            placeholder = { Text("e.g., 4 PM or Tomorrow 10 AM") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (taskName.isNotBlank() && taskTime.isNotBlank()) {
                Toast.makeText(context, "Task Saved!", Toast.LENGTH_SHORT).show()
                onBack()
            } else {
                Toast.makeText(context, "Please fill in the required fields", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Task")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("Back to Home")
        }
    }
}

// Utility function to format time in HH:mm:ss
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
//added features in track tasks