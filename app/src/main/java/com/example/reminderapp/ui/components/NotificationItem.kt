package com.example.reminderapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.data.model.AjandaNotData
import com.example.reminderapp.ui.theme.ReminderappTheme
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: ApiNotificationData,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isPrioritized: Boolean = false,
    onTogglePriority: () -> Unit = {},
    ajandaNot: AjandaNotData? = null,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bildirim içeriği
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Firma: ${notification.firma ?: "Belirtilmemiş"}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Tamamlanma durumu göstergesi
                    if (notification.okundu == true) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Tamamlandı",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = "Ad Soyad: ${notification.adSoyad ?: "Belirtilmemiş"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "Tel: ${notification.tel ?: "Belirtilmemiş"} | GSM: ${notification.cep ?: "Belirtilmemiş"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                if (!notification.aciklama.isNullOrBlank()) {
                    Text(
                        text = "Açıklama: ${notification.aciklama}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Text(
                    text = "Tarih: ${notification.tarih ?: "Belirtilmemiş"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Ajanda notu göster (diğer alanlarla aynı stil)
                if (!ajandaNot?.notlar.isNullOrBlank()) {
                    Text(
                        text = "Not: ${ajandaNot?.notlar}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Aksiyon butonları
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Öncelik (yıldız) butonu
                IconButton(onClick = onTogglePriority) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = if (isPrioritized) "Öncelikli" else "Öncelik ver",
                        tint = if (isPrioritized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun NotificationItemPreview() {
    ReminderappTheme {
        NotificationItem(
            notification = ApiNotificationData(
                id = 1,
                firma = "ABC Şirketi",
                adSoyad = "Ahmet Yılmaz",
                tel = "0212 555 1234",
                cep = "0532 555 5678",
                aciklama = "Muhasebe işlemleri için görüşme yapılacak",
                tarih = "2025-01-15T14:30:00Z",
                okundu = true,
                userId = 1,
                user = "Salih Bey",
                ajandaDosya = emptyList()
            ),
            onDelete = { },
            onEdit = { },
            ajandaNot = AjandaNotData(
                ajandaId = "1",
                id = 1,
                notlar = "Müşteri ile görüşme yapıldı. Detaylar takip edilecek."
            )
        )
    }
}
