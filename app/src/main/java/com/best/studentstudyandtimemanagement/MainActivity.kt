package com.best.studentstudyandtimemanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.best.studentstudyandtimemanagement.ui.theme.StudentStudyAndTimeManagementTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Screen {
    LOGIN, SIGNUP, FORGOT_PASSWORD, HOME, PLAN_STUDY, TRACK_TASK
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentStudyAndTimeManagementTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginSuccess = { currentScreen = Screen.HOME },
                            onSignUpClick = { currentScreen = Screen.SIGNUP },
                            onForgotPasswordClick = { currentScreen = Screen.FORGOT_PASSWORD }
                        )
                        Screen.SIGNUP -> SignupScreen(onBack = { currentScreen = Screen.LOGIN })
                        Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(onBack = { currentScreen = Screen.LOGIN })
                        Screen.HOME -> StudyHomePage(
                            modifier = Modifier.padding(innerPadding),
                            onPlanStudyClick = { currentScreen = Screen.PLAN_STUDY },
                            onTrackTaskClick = { currentScreen = Screen.TRACK_TASK }
                        )
                        Screen.PLAN_STUDY -> PlanStudyScreen(onBack = { currentScreen = Screen.HOME })
                        Screen.TRACK_TASK -> TrackTaskScreen(onBack = { currentScreen = Screen.HOME })
                    }
                }
            }
        }
    }
}

// ---- UI Screens ---- //

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
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
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
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
fun SignupScreen(onBack: () -> Unit) {
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
            Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
            onBack()
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
    onTrackTaskClick: () -> Unit
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

// TrackableTask data class
data class TrackableTask(
    val name: String,
    var isRunning: MutableState<Boolean> = mutableStateOf(false),
    var elapsedTime: MutableState<Long> = mutableStateOf(0L)
)


@Composable
fun TrackTaskScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var newTaskName by remember { mutableStateOf("") }
    val taskList = remember { mutableStateListOf<TrackableTask>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Track Your Tasks", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { Text("Enter task name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (newTaskName.isNotBlank()) {
                taskList.add(TrackableTask(name = newTaskName))
                newTaskName = ""
            } else {
                Toast.makeText(context, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        taskList.forEach { task ->
            TaskTimerItem(task)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Back to Home")
        }
    }
}

@Composable
fun TaskTimerItem(task: TrackableTask) {
    val timeDisplay = remember { mutableStateOf(formatTime(task.elapsedTime.value)) }

    LaunchedEffect(task.isRunning.value) {
        while (task.isRunning.value) {
            delay(1000)
            task.elapsedTime.value += 1000
            if (task.elapsedTime.value >= 10_800_000L) {
                task.isRunning.value = false
            }
            timeDisplay.value = formatTime(task.elapsedTime.value)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.name, style = MaterialTheme.typography.titleMedium)
            Text("Time spent: ${timeDisplay.value}", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = {
                        if (!task.isRunning.value && task.elapsedTime.value < 10_800_000L) {
                            task.isRunning.value = true
                        }
                    },
                    enabled = !task.isRunning.value && task.elapsedTime.value < 10_800_000L
                ) {
                    Text("Start")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        task.isRunning.value = false
                    },
                    enabled = task.isRunning.value
                ) {
                    Text("Stop")
                }
            }
        }
    }
}


// Convert milliseconds to HH:MM:SS
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
//testing git
