package com.example.reminderapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.reminderapp.ui.viewmodels.CalendarViewModel
import com.example.reminderapp.ui.viewmodels.NotificationViewModel
import androidx.compose.material3.CardDefaults
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val reminders by viewModel.remindersForMonth.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    fun parseIsoToLocalDate(iso: String?): org.threeten.bp.LocalDate? {
        if (iso.isNullOrBlank()) return null
        return try {
            val cleaned = iso.replace("Z", "")
            val dt = org.threeten.bp.LocalDateTime.parse(cleaned.substring(0, 19))
            dt.toLocalDate()
        } catch (e: Exception) { null }
    }

    val daysWithReminders: Set<LocalDate> = buildSet {
        reminders.forEach { add(it.dateTime.toLocalDate()) }
        notifications.forEach { n ->
            val d = parseIsoToLocalDate(n.tarih)
            if (d != null) add(d)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Takvim") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Önceki Ay")
                        }
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Sonraki Ay")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val monthTitle = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr")))
            Text(
                text = monthTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Weekday headers
            val dayNames = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dayNames.forEach { name ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = name, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Build month grid
            val firstDayOfMonth = currentMonth
            val weekFields = WeekFields.of(Locale("tr"))
            val firstDayOfWeekIndex = 1 // Monday
            val shift = ((firstDayOfMonth.dayOfWeek.value - firstDayOfWeekIndex) + 7) % 7
            val daysInMonth = firstDayOfMonth.lengthOfMonth()
            val totalCells = shift + daysInMonth
            val rows = (totalCells + 6) / 7

            var dayCounter = 1
            repeat(rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = it * 7 + col
                        if (cellIndex < shift || dayCounter > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).height(48.dp)) { }
                        } else {
                            val date = firstDayOfMonth.withDayOfMonth(dayCounter)
                            val hasReminder = daysWithReminders.contains(date)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { selectedDate = date },
                                contentAlignment = Alignment.Center
                            ) {
                                val bgModifier = if (hasReminder) {
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                                } else Modifier

                                Box(contentAlignment = Alignment.Center, modifier = bgModifier) {
                                    Text(
                                        text = dayCounter.toString(),
                                        color = if (hasReminder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                            dayCounter++
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            selectedDate?.let { day ->
                val dayTitle = day.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                val dayReminders = reminders.filter { it.dateTime.toLocalDate().isEqual(day) }
                val dayNotifications = notifications.filter { n ->
                    val d = parseIsoToLocalDate(n.tarih)
                    d != null && d.isEqual(day)
                }

                Text(
                    text = "Seçili gün: $dayTitle",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (dayReminders.isEmpty() && dayNotifications.isEmpty()) {
                    Text(
                        text = "Bu tarihte bir şey yok",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (dayReminders.isNotEmpty()) {
                            Text(
                                text = "Hatırlatmalar",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            dayReminders.forEach { r ->
                                androidx.compose.material3.Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = r.title, style = MaterialTheme.typography.titleSmall)
                                        val timeText = r.dateTime.toLocalTime().toString()
                                        Text(text = timeText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        r.description?.takeIf { it.isNotBlank() }?.let {
                                            Text(text = it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }

                        if (dayNotifications.isNotEmpty()) {
                            Text(
                                text = "Bildirimler",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            dayNotifications.forEach { n ->
                                val dt = n.tarih?.let {
                                    try {
                                        val cleaned = it.replace("Z", "")
                                        org.threeten.bp.LocalDateTime.parse(cleaned.substring(0, 19))
                                    } catch (e: Exception) { null }
                                }
                                val timeText = dt?.toLocalTime()?.toString() ?: "--:--"
                                androidx.compose.material3.Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = (n.adSoyad ?: n.firma ?: "Bildirim"), style = MaterialTheme.typography.titleSmall)
                                        Text(text = timeText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        n.aciklama?.takeIf { it.isNotBlank() }?.let {
                                            Text(text = it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


