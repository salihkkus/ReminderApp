package com.example.reminderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.data.model.Priority
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.ui.components.ReminderItem
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.HomeViewModel
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    
    // Başarı mesajı için state
    var showSuccessMessage by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajanda Modülü") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Başarı mesajı
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Başarılı",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "✅ Giriş başarılı! Hoş geldiniz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        IconButton(
                            onClick = { showSuccessMessage = false }
                        ) {
                            Text(
                                text = "✕",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            // Ana içerik
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
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
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onToggleComplete = { viewModel.toggleReminderComplete(reminder.id) },
                            onDelete = { viewModel.deleteReminder(reminder) },
                            onEdit = { /* TODO: Navigate to edit reminder */ }
                        )
                    }
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
        val sampleReminders = listOf(
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
        
        // Bu preview için mock data kullanıyoruz
        // Gerçek uygulamada bu veriler ViewModel'dan gelir
        HomeScreenWithData(navController = rememberNavController(), reminders = sampleReminders)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithData(
    navController: NavController,
    reminders: List<Reminder>
) {
    var showSuccessMessage by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajanda Modülü") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Başarı mesajı
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Başarılı",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "✅ Giriş başarılı! Hoş geldiniz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        IconButton(
                            onClick = { showSuccessMessage = false }
                        ) {
                            Text(
                                text = "✕",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onToggleComplete = { /* Preview'da işlev yok */ },
                        onDelete = { /* Preview'da işlev yok */ },
                        onEdit = { /* Preview'da işlev yok */ }
                    )
                }
            }
        }
    }
}
