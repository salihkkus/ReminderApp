package com.example.reminderapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.ui.screens.HomeScreen
import com.example.reminderapp.ui.screens.LoginScreen
import com.example.reminderapp.ui.screens.AddNotificationScreen
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "Notification permission granted")
        } else {
            android.util.Log.w("MainActivity", "Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Bildirim kanalı oluştur
        createNotificationChannel()
        
        // Bildirim izni kontrolü ve isteme
        checkNotificationPermission()
        
        setContent {
            ReminderappTheme {
                ReminderApp()
            }
        }
    }
    
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    android.util.Log.d("MainActivity", "Notification permission already granted")
                }
                else -> {
                    android.util.Log.d("MainActivity", "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        
        // Exact alarm izni kontrolü (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                android.util.Log.w("MainActivity", "Cannot schedule exact alarms - user needs to grant permission")
                // Bu durumda kullanıcıyı ayarlara yönlendirebiliriz
                // Intent intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                // startActivity(intent)
            } else {
                android.util.Log.d("MainActivity", "Can schedule exact alarms")
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ajanda_notifications",
                "Ajanda Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ajanda hatırlatma bildirimleri"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun ReminderApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    
    // Otomatik giriş durumunu kontrol et
    var isAutoLoginChecked by remember { mutableStateOf(false) }
    var shouldShowLoginScreen by remember { mutableStateOf(true) }
    
    // Otomatik giriş kontrolü
    LaunchedEffect(Unit) {
        if (!isAutoLoginChecked) {
            isAutoLoginChecked = true
            val autoLoginSuccess = loginViewModel.autoLogin()
            if (autoLoginSuccess) {
                android.util.Log.d("ReminderApp", "Auto login initiated")
                shouldShowLoginScreen = false
            } else {
                android.util.Log.d("ReminderApp", "No saved credentials found")
                shouldShowLoginScreen = true
            }
        }
    }
    
    // Otomatik giriş başarılı olduğunda home'a yönlendir
    LaunchedEffect(loginState.isSuccess) {
        if (loginState.isSuccess) {
            android.util.Log.d("ReminderApp", "Auto login successful, navigating to home")
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (shouldShowLoginScreen) "login" else "home",
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