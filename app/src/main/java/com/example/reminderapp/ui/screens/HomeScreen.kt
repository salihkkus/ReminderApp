package com.example.reminderapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.data.model.Priority
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.ui.components.ReminderItem
import com.example.reminderapp.ui.components.NotificationItem
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.HomeViewModel
import com.example.reminderapp.ui.viewmodels.NotificationViewModel
import org.threeten.bp.LocalDateTime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val reminders by homeViewModel.reminders.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()
    
    // Başarı mesajı için state
    var showSuccessMessage by remember { mutableStateOf(true) }
    
    // Başarı mesajını 3 saniye sonra otomatik gizle
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(1700)
            showSuccessMessage = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.reminderapp.R.mipmap.bilsoft),
                        contentDescription = "Bilsoft Logo",
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
                CenterAlignedTopAppBar(
                    title = { Text("Ajanda Modülü") }
                )
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
            
            // Butonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("add_notification") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Yeni Bildirim", style = MaterialTheme.typography.titleMedium)
                }
                
                Button(
                    onClick = { notificationViewModel.loadNotifications() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Yenile", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            // Filtre durumları (bildirimler için)
            var nameQuery by remember { mutableStateOf("") }
            var startDateText by remember { mutableStateOf("") }
            var endDateText by remember { mutableStateOf("") }
            var selectedUser by remember { mutableStateOf<String?>(null) }
            var statusFilter by remember { mutableStateOf(0) } // 0: Tümü, 1: Tamamlanan (okundu=true), 2: Tamamlanmayan (okundu=false)
            var filteredNotifications by remember(notifications) { mutableStateOf(notifications) }

            // Kullanıcı listesi (mevcut bildirimlerden)
            val userOptions = remember(notifications) {
                notifications.mapNotNull { it.user }.distinct().sorted()
            }

            // Filtre formu (bildirimlerin üstünde)
            if (notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Filtrele",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // İsim (ad soyad) arama
                        OutlinedTextField(
                            value = nameQuery,
                            onValueChange = { nameQuery = it },
                            label = { Text("İsim ile ara (Ad Soyad)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Tarih aralığı (dd.MM.yyyy)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = startDateText,
                                onValueChange = { startDateText = it },
                                label = { Text("Başlangıç (dd.MM.yyyy)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = endDateText,
                                onValueChange = { endDateText = it },
                                label = { Text("Bitiş (dd.MM.yyyy)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Kullanıcı seçimi (mevcut bildirimlerden)
                        if (userOptions.isNotEmpty()) {
                            var userMenuExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = userMenuExpanded,
                                onExpandedChange = { userMenuExpanded = !userMenuExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedUser ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Kullanıcı (opsiyonel)") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userMenuExpanded) }
                                )
                                ExposedDropdownMenu(
                                    expanded = userMenuExpanded,
                                    onDismissRequest = { userMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("(Hepsi)") },
                                        onClick = {
                                            selectedUser = null
                                            userMenuExpanded = false
                                        }
                                    )
                                    userOptions.forEach { user ->
                                        DropdownMenuItem(
                                            text = { Text(user) },
                                            onClick = {
                                                selectedUser = user
                                                userMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Durum filtresi: Tümü / Tamamlanan / Tamamlanmayan
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = statusFilter == 0,
                                onClick = { statusFilter = 0 },
                                label = { Text("Tümü") }
                            )
                            FilterChip(
                                selected = statusFilter == 1,
                                onClick = { statusFilter = 1 },
                                label = { Text("Tamamlanan") }
                            )
                            FilterChip(
                                selected = statusFilter == 2,
                                onClick = { statusFilter = 2 },
                                label = { Text("Tamamlanmayan") }
                            )
                        }

                        // Ara butonu
                        Button(
                            onClick = {
                                // Mevcut listeyi filtrele
                                var result = notifications

                                // İsim filtre
                                if (nameQuery.isNotBlank()) {
                                    val q = nameQuery.trim().lowercase()
                                    result = result.filter { (it.adSoyad ?: "").lowercase().contains(q) }
                                }

                                // Tarih aralığı filtre (bildirim.tarih ISO olabilir)
                                fun parseIsoToLocalDate(iso: String?): org.threeten.bp.LocalDate? {
                                    if (iso.isNullOrBlank()) return null
                                    return try {
                                        val cleaned = iso.replace("Z", "")
                                        val dt = org.threeten.bp.LocalDateTime.parse(cleaned.substring(0, 19))
                                        dt.toLocalDate()
                                    } catch (e: Exception) { null }
                                }
                                fun parseUiDate(text: String): org.threeten.bp.LocalDate? {
                                    return try {
                                        org.threeten.bp.LocalDate.parse(text, org.threeten.bp.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    } catch (e: Exception) { null }
                                }
                                val startDate = parseUiDate(startDateText)
                                val endDate = parseUiDate(endDateText)
                                if (startDate != null) {
                                    result = result.filter { n ->
                                        val d = parseIsoToLocalDate(n.tarih)
                                        d != null && (d.isAfter(startDate) || d.isEqual(startDate))
                                    }
                                }
                                if (endDate != null) {
                                    result = result.filter { n ->
                                        val d = parseIsoToLocalDate(n.tarih)
                                        d != null && (d.isBefore(endDate) || d.isEqual(endDate))
                                    }
                                }

                                // Kullanıcı filtre
                                if (!selectedUser.isNullOrBlank()) {
                                    result = result.filter { it.user == selectedUser }
                                }

                                // Durum filtre (okundu alanı)
                                result = when (statusFilter) {
                                    1 -> result.filter { it.okundu == true }
                                    2 -> result.filter { it.okundu != true }
                                    else -> result
                                }

                                filteredNotifications = result
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ara")
                        }
                    }
                }
            }

            // Ana içerik
            if (reminders.isEmpty() && notifications.isEmpty() && !isLoading) {
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
                            text = "Henüz içerik yok",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Yeni bildirim veya hatırlatma eklemek için butonları kullanın",
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
                    // Loading durumu
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    // Hata mesajı
                    if (error != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "❌ Hata!",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = error ?: "Bilinmeyen hata",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Bildirimler bölümü
                    if (filteredNotifications.isNotEmpty()) {
                        item {
                            Text(
                                text = "📢 Bildirimler (${filteredNotifications.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(filteredNotifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                onDelete = { notificationViewModel.deleteNotification(notification) },
                                onEdit = { navController.navigate("edit_notification/${notification.id}") }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    // Hatırlatmalar bölümü
                    if (reminders.isNotEmpty()) {
                        item {
                            Text(
                                text = "📅 Hatırlatmalar (${reminders.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(reminders) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onToggleComplete = { homeViewModel.toggleReminderComplete(reminder.id) },
                                onDelete = { homeViewModel.deleteReminder(reminder) },
                                onEdit = { /* TODO: Navigate to edit reminder */ }
                            )
                        }
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
