package com.example.reminderapp.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.ui.theme.ReminderappTheme
import com.example.reminderapp.ui.viewmodels.NotificationViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateNotificationScreen(
    navController: NavController,
    notificationId: Int,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val ajandaNot by viewModel.ajandaNot.collectAsState()
    
    // Form state variables
    var firma by remember { mutableStateOf("") }
    var adSoyad by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var gsm by remember { mutableStateOf("") }
    var aciklama by remember { mutableStateOf("") }
    var tarihSaat by remember { mutableStateOf<LocalDateTime?>(null) }
    var kullanici by remember { mutableStateOf("") }
    var tamamlandi by remember { mutableStateOf(false) } // Tamamlanma durumu
    
    // Ajanda notları için state
    var yeniNot by remember { mutableStateOf("") }
    var showNotSuccessMessage by remember { mutableStateOf(false) }
    
    // Context for dialogs
    val context = LocalContext.current
    
    // Success state
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Mevcut verileri doldur
    LaunchedEffect(notificationId, notifications) {
        val current = notifications.firstOrNull { it.id == notificationId }
        current?.let { n ->
            firma = n.firma.orEmpty()
            adSoyad = n.adSoyad.orEmpty()
            telefon = n.tel.orEmpty()
            gsm = n.cep.orEmpty()
            aciklama = n.aciklama.orEmpty()
            // API tarih formatı ISO 8601 bekliyor; ekranda gösterim için parse etmeye çalış
            try {
                // Basit bir parse: Z içeren ISO tarihleri LocalDateTime'a çevirmek için 'Z' kısmını kaldırıyoruz
                val iso = n.tarih
                if (!iso.isNullOrBlank()) {
                    val cleaned = iso.replace("Z", "")
                    val parsed = LocalDateTime.parse(cleaned.substring(0, 19))
                    tarihSaat = parsed
                }
            } catch (_: Exception) { /* gösterim için zorunlu değil */ }
            // Kullanıcı alanı API'den gelmiyor olabilir
            kullanici = n.user.orEmpty()
            // Tamamlanma durumu
            tamamlandi = n.okundu ?: false
        }
        
        // Ajanda notunu yükle - önce ajanda ID'sini kullanarak dene
        // Eğer bu çalışmazsa, farklı bir yaklaşım gerekebilir
        viewModel.getAjandaNotById(notificationId)
    }
    
    // Tarih/Saat seçici fonksiyonu
    fun showDateTimePicker(onDateTimeSelected: (LocalDateTime) -> Unit) {
        val calendar = Calendar.getInstance()
        
        // Önce tarih seçici
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Sonra saat seçici
                val timePickerDialog = android.app.TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bildirim Güncelle") },
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
                text = "Bildirim Bilgilerini Güncelle",
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
                    
                    // Tarih Saat - Tıklanabilir seçici
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDateTimePicker { selectedDateTime ->
                                    tarihSaat = selectedDateTime
                                }
                            },
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
                                text = tarihSaat?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                                    ?: "Tarih ve saat seçiniz",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp),
                                color = if (tarihSaat == null) 
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tıklayarak tarih ve saat seçebilirsiniz",
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
                    
                    // Tamamlanma durumu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tamamlandi,
                            onCheckedChange = { tamamlandi = it }
                        )
                        Text(
                            text = "İşlem Tamamlandı",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            // Ajanda Notları Bölümü
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "📝 Ajanda Notları",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Text(
                        text = "Bu ajandaya not ekleyebilirsiniz:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    // Mevcut notları göster
                    ajandaNot?.let { not ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "📋 Mevcut Not:",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                Text(
                                    text = not.notlar ?: "Not bulunamadı",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } ?: run {
                        Text(
                            text = "Bu ajanda için henüz not eklenmemiş.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    Text(
                        text = "Yeni not eklemek için:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = yeniNot,
                        onValueChange = { yeniNot = it },
                        label = { Text("Yeni Not") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = { Text("Notunuzu buraya yazın") },
                        placeholder = { Text("Örnek: Müşteri ile görüşme yapıldı, detaylar takip edilecek...") }
                    )
                    
                    Button(
                        onClick = {
                            if (yeniNot.isNotBlank()) {
                                viewModel.addAjandaNot(
                                    ajandaId = notificationId.toString(),
                                    notlar = yeniNot.trim()
                                )
                                yeniNot = ""
                                showNotSuccessMessage = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = yeniNot.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Not Ekle", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    // Not başarı mesajı
                    if (showNotSuccessMessage) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "✅ Not başarıyla eklendi!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
            
            // Güncelle butonu
            Button(
                onClick = {
                    // Validation
                    if (firma.isBlank() || adSoyad.isBlank() || telefon.isBlank() || 
                        gsm.isBlank() || aciklama.isBlank() || kullanici.isBlank() || 
                        tarihSaat == null) {
                        showErrorMessage = true
                        errorMessage = "Lütfen tüm alanları doldurun ve tarih/saat seçin"
                        return@Button
                    }
                    
                    // Loading state'i göster
                    showSuccessMessage = false
                    showErrorMessage = false
                    
                    // Güncelle
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    val formattedDate = tarihSaat!!.format(formatter)
                    viewModel.updateNotification(
                        notification = ApiNotificationData(
                            id = notificationId,
                            aciklama = aciklama,
                            adSoyad = adSoyad,
                            cep = gsm,
                            firma = firma,
                            okundu = tamamlandi,
                            tarih = formattedDate,
                            tel = telefon,
                            userId = null,
                            user = kullanici,
                            ajandaDosya = emptyList()
                        )
                    )
                    
                    showSuccessMessage = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Güncelle", style = MaterialTheme.typography.titleMedium)
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
                            text = "✅ Bildirim Başarıyla Güncellendi!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Değişiklikler kaydedildi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Button(
                            onClick = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Ana Sayfaya Dön")
                        }
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
fun UpdateNotificationScreenPreview() {
    ReminderappTheme {
        UpdateNotificationScreen(
            navController = rememberNavController(),
            notificationId = 1
        )
    }
}
