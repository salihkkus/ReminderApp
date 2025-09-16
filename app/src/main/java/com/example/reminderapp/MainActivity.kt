package com.example.reminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.ui.screens.HomeScreen
import com.example.reminderapp.ui.screens.LoginScreen
import com.example.reminderapp.ui.screens.AddNotificationScreen
import com.example.reminderapp.ui.theme.ReminderappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderappTheme {
                ReminderApp()
            }
        }
    }
}

@Composable
fun ReminderApp() {
    val navController = rememberNavController()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("add_notification") {
                AddNotificationScreen(navController = navController)
            }
            composable("edit_notification/{id}") { backStackEntry ->
                val idArg = backStackEntry.arguments?.getString("id")
                AddNotificationScreen(navController = navController, editId = idArg?.toIntOrNull())
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReminderAppPreview() {
    ReminderappTheme {
        ReminderApp()
    }
}