package com.example.reminderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.data.model.Priority
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.ui.components.ReminderItem
import com.example.reminderapp.ui.theme.ReminderappTheme
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    // Şu an için sample data kullanıyoruz
    // Hilt entegrasyonu tamamlandığında ViewModel kullanacağız
    val sampleReminders = remember {
        listOf(
            Reminder(
                id = 1,
                title = "Vergi ödemesi",
                description = "KDV beyannamesi son günü",
                dateTime = LocalDateTime.now().plusDays(3),
                priority = Priority.HIGH,
                category = "Vergi"
            ),
            Reminder(
                id = 2,
                title = "Müşteri toplantısı",
                description = "ABC Şirketi ile görüşme",
                dateTime = LocalDateTime.now().plusDays(1),
                priority = Priority.MEDIUM,
                category = "Toplantı"
            ),
            Reminder(
                id = 3,
                title = "Fatura gönderimi",
                description = "Ocak ayı faturaları",
                dateTime = LocalDateTime.now().plusHours(6),
                priority = Priority.URGENT,
                category = "Fatura"
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajanda Modülü") },
                navigationIcon = {
                    IconButton(onClick = { 
                        // Geri dönüş için login sayfasına yönlendir
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to add reminder */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Hatırlatma Ekle")
            }
        }
    ) { padding ->
        if (sampleReminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Henüz hatırlatma yok",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Yeni hatırlatma eklemek için + butonuna tıklayın",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleReminders) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onToggleComplete = { /* TODO: Implement toggle */ },
                        onDelete = { /* TODO: Implement delete */ },
                        onEdit = { /* TODO: Navigate to edit reminder */ }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenEmptyPreview() {
    ReminderappTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenWithRemindersPreview() {
    ReminderappTheme {
        HomeScreen(navController = rememberNavController())
    }
}
