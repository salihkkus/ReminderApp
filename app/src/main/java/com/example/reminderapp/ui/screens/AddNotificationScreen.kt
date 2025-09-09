package com.example.reminderapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.data.local.TokenManager
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.NotificationViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    // Form state variables
    var firma by remember { mutableStateOf("") }
    var adSoyad by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var gsm by remember { mutableStateOf("") }
    var aciklama by remember { mutableStateOf("") }
    var tarihSaat by remember { mutableStateOf(LocalDateTime.now()) }
    var kullanici by remember { mutableStateOf("") }
    
    // Success state
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Token artık NotificationViewModel içinde TokenManager ile alınıyor
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Bildirim") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık
            Text(
                text = "Bildirim Bilgileri",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form alanları
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Firma
                    OutlinedTextField(
                        value = firma,
                        onValueChange = { firma = it },
                        label = { Text("Firma") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("Firma adını girin") }
                    )
                    
                    // Ad Soyad
                    OutlinedTextField(
                        value = adSoyad,
                        onValueChange = { adSoyad = it },
                        label = { Text("Ad Soyad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("Ad ve soyadını girin") }
                    )
                    
                    // Telefon
                    OutlinedTextField(
                        value = telefon,
                        onValueChange = { telefon = it },
                        label = { Text("Telefon") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        supportingText = { Text("Telefon numarasını girin") }
                    )
                    
                    // GSM
                    OutlinedTextField(
                        value = gsm,
                        onValueChange = { gsm = it },
                        label = { Text("GSM") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        supportingText = { Text("GSM numarasını girin") }
                    )
                    
                    // Açıklama
                    OutlinedTextField(
                        value = aciklama,
                        onValueChange = { aciklama = it },
                        label = { Text("Açıklama") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = { Text("Bildirim açıklamasını girin") }
                    )
                    
                    // Tarih Saat
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Tarih ve Saat",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = tarihSaat.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Not: Şu anda sadece mevcut tarih/saat kullanılıyor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    // Kullanıcı
                    OutlinedTextField(
                        value = kullanici,
                        onValueChange = { kullanici = it },
                        label = { Text("Kullanıcı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("Kullanıcı adını girin") }
                    )
                }
            }
            
                // Kaydet butonu
                Button(
                    onClick = {
                        // Validation
                        if (firma.isBlank() || adSoyad.isBlank() || telefon.isBlank() || 
                            gsm.isBlank() || aciklama.isBlank() || kullanici.isBlank()) {
                            showErrorMessage = true
                            errorMessage = "Lütfen tüm alanları doldurun"
                            return@Button
                        }
                        
                        // Add notification via API (token otomatik alınıyor)
                        viewModel.addNotification(
                            firma = firma,
                            adSoyad = adSoyad,
                            telefon = telefon,
                            gsm = gsm,
                            aciklama = aciklama,
                            tarihSaat = tarihSaat,
                            kullanici = kullanici
                        )
                        
                        showSuccessMessage = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Kaydet", style = MaterialTheme.typography.titleMedium)
                }
            
            // Başarı mesajı
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✅ Bildirim Başarıyla Kaydedildi!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Ana sayfaya dönmek için geri butonuna basın",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // Hata mesajı
            if (showErrorMessage) {
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
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddNotificationScreenPreview() {
    ReminderappTheme {
        AddNotificationScreen(navController = rememberNavController())
    }
}
