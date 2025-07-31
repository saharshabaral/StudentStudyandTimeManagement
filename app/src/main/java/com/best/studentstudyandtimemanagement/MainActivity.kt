//package com.best.studentstudyandtimemanagement
//
//import android.os.Bundle
//
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.ui.unit.dp
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.material3.Button
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import com.best.studentstudyandtimemanagement.ui.theme.StudentStudyandTimeManagementTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            StudentStudyandTimeManagementTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    TestLayout(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding),
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TestLayout(name: String, modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    var result by remember { mutableStateOf(1) }
//
//    Row(
//        modifier = modifier.fillMaxSize()
//    ) {
//        // Yellow Column
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .width(100.dp)
//                .background(Color.Yellow),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.SpaceEvenly,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                repeat(6) {
//                    Image(
//                        painter = painterResource(image_ids[it]),
//                        contentDescription = "Dice ${it + 1}",
//                        modifier = Modifier.clickable {
//                            Toast.makeText(context, "Clicked dice ${it + 1}", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                }
//            }
//
//            Button(
//                onClick = {
//                    result = (1..6).random()
//                    Toast.makeText(context, "You rolled a $result!", Toast.LENGTH_SHORT).show()
//                },
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text("Roll")
//            }
//        }
//
//        // Red Column
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .width(100.dp)
//                .background(Color.Red),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Column 2")
//        }
//
//        // Gray Column
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth()
//                .background(Color.Gray),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Column 3")
//            Spacer(modifier = Modifier.height(16.dp))
//            Image(
//                painter = painterResource(image_ids[result - 1]),
//                contentDescription = "Rolled Dice"
//            )
//        }
//    }
//}
//
//// test another screen
//@Composable
//fun TestLoginScreeen(modifier: Modifier = Modifier){
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TestLayout() {
//    StudentStudyandTimeManagementTheme  {
//        TestLayout("Android")
//    }
//}
//
//private val image_ids = listOf(
//    R.drawable.dice_1,
//    R.drawable.dice_2,
//    R.drawable.dice_3,
//    R.drawable.dice_4,
//    R.drawable.dice_5,
//    R.drawable.dice_6
//)

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

enum class Screen {
    LOGIN, SIGNUP, FORGOT_PASSWORD, HOME
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
                        Screen.HOME -> StudyHomePage(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

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
            painter = painterResource(id = R.drawable.logincover), // Replace with your image file name
            contentDescription = "Login Cover",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Life Tracker",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF1E88E5)
        )

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

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
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
fun StudyHomePage(modifier: Modifier = Modifier) {
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
            painter = painterResource(R.drawable.cover), // Add your own image in drawable folder
            contentDescription = "Study App Cover Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text("Study & Time Management", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF1E88E5))
        Text("Welcome back, Student!", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)

        Button(onClick = {
            Toast.makeText(context, "Study Planner Coming Soon", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Plan Your Study")
        }

        Button(onClick = {
            Toast.makeText(context, "Task Tracker Coming Soon", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Track Tasks")
        }

        Button(onClick = {
            Toast.makeText(context, "Time Reminders Coming Soon", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Time Reminders")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StudentStudyAndTimeManagementTheme {
        LoginScreen(
            onLoginSuccess = {},
            onSignUpClick = {},
            onForgotPasswordClick = {}
        )
    }
}
///testing git