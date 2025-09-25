package com.example.reminderapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Edit
// Removed calendar/work icons due to availability issues in current icon set
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
    var showNotEditDialog by remember { mutableStateOf(false) }
    var showNotDeleteDialog by remember { mutableStateOf(false) }
    var editNotText by remember { mutableStateOf("") }
    
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = MaterialTheme.colorScheme.primary)
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
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        supportingText = { Text("Firma adını girin") }
                    )
                    
                    // Ad Soyad
                    OutlinedTextField(
                        value = adSoyad,
                        onValueChange = { adSoyad = it },
                        label = { Text("Ad Soyad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        supportingText = { Text("Bildirim açıklamasını girin") }
                    )
                    
                    // Tarih Saat - Outlined kutu içinde tıklanabilir alan
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = tarihSaat?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Tarih ve Saat") },
                            placeholder = { Text("Tarih ve saat seçiniz") },
                            supportingText = { Text("Tıklayarak tarih ve saat seçebilirsiniz") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    showDateTimePicker { selectedDateTime ->
                                        tarihSaat = selectedDateTime
                                    }
                                }
                        )
                    }
                    
                    // Kullanıcı
                    OutlinedTextField(
                        value = kullanici,
                        onValueChange = { kullanici = it },
                        label = { Text("Kullanıcı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📋 Mevcut Not:",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Güncelle butonu
                                        Button(
                                            onClick = {
                                                editNotText = not.notlar ?: ""
                                                showNotEditDialog = true
                                            },
                                            modifier = Modifier.height(32.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Text(
                                                text = "✏️",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        
                                        // Sil butonu
                                        Button(
                                            onClick = {
                                                showNotDeleteDialog = true
                                            },
                                            modifier = Modifier.height(32.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text(
                                                text = "🗑️",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                                
                                Text(
                                    text = not.notlar ?: "Not bulunamadı",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
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
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
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
    
    // Not güncelleme dialog'u
    if (showNotEditDialog) {
        AlertDialog(
            onDismissRequest = { showNotEditDialog = false },
            title = { Text("Notu Güncelle") },
            text = {
                OutlinedTextField(
                    value = editNotText,
                    onValueChange = { editNotText = it },
                    label = { Text("Not") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        ajandaNot?.let { not ->
                            viewModel.updateAjandaNot(
                                notId = not.id,
                                ajandaId = notificationId.toString(),
                                notlar = editNotText.trim()
                            )
                            showNotEditDialog = false
                            showNotSuccessMessage = true
                        }
                    },
                    enabled = editNotText.isNotBlank()
                ) {
                    Text("Güncelle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotEditDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
    
    // Not silme dialog'u
    if (showNotDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showNotDeleteDialog = false },
            title = { Text("Notu Sil") },
            text = { Text("Bu notu silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(
                    onClick = {
                        ajandaNot?.let { not ->
                            viewModel.deleteAjandaNot(
                                notId = not.id,
                                ajandaId = notificationId.toString(),
                                notlar = not.notlar ?: ""
                            )
                            showNotDeleteDialog = false
                            showNotSuccessMessage = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
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
