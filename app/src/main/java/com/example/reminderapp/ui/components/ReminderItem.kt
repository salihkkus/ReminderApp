package com.example.reminderapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.model.Priority
import com.example.reminderapp.ui.theme.ReminderappTheme
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderItem(
    reminder: Reminder,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    
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
            // Checkbox for completion
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            
            // Reminder content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                reminder.description?.let { description ->
                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = reminder.dateTime.format(dateFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    PriorityChip(priority = reminder.priority)
                }
            }
            
            // Action buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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

@Composable
fun PriorityChip(priority: Priority) {
    val (backgroundColor, textColor) = when (priority) {
        Priority.LOW -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        Priority.HIGH -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        Priority.URGENT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = priority.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// Preview'lar
@Preview(showBackground = true, widthDp = 400)
@Composable
fun ReminderItemPreview() {
    ReminderappTheme {
        ReminderItem(
            reminder = Reminder(
                id = 1,
                title = "Vergi ödemesi",
                description = "KDV beyannamesi son günü - Bu çok önemli bir işlem ve mutlaka zamanında yapılmalı",
                dateTime = LocalDateTime.now().plusDays(3),
                priority = Priority.HIGH,
                category = "Vergi"
            ),
            onToggleComplete = { },
            onDelete = { },
            onEdit = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ReminderItemCompletedPreview() {
    ReminderappTheme {
        ReminderItem(
            reminder = Reminder(
                id = 2,
                title = "Müşteri toplantısı",
                description = "ABC Şirketi ile görüşme",
                dateTime = LocalDateTime.now().minusDays(1),
                isCompleted = true,
                priority = Priority.MEDIUM,
                category = "Toplantı"
            ),
            onToggleComplete = { },
            onDelete = { },
            onEdit = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ReminderItemUrgentPreview() {
    ReminderappTheme {
        ReminderItem(
            reminder = Reminder(
                id = 3,
                title = "Fatura gönderimi",
                description = "Ocak ayı faturaları",
                dateTime = LocalDateTime.now().plusHours(2),
                priority = Priority.URGENT,
                category = "Fatura"
            ),
            onToggleComplete = { },
            onDelete = { },
            onEdit = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ReminderItemLowPriorityPreview() {
    ReminderappTheme {
        ReminderItem(
            reminder = Reminder(
                id = 4,
                title = "Ofis temizliği",
                description = "Haftalık ofis temizliği",
                dateTime = LocalDateTime.now().plusDays(7),
                priority = Priority.LOW,
                category = "Genel"
            ),
            onToggleComplete = { },
            onDelete = { },
            onEdit = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PriorityChipPreview() {
    ReminderappTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            PriorityChip(Priority.LOW)
            PriorityChip(Priority.MEDIUM)
            PriorityChip(Priority.HIGH)
            PriorityChip(Priority.URGENT)
        }
    }
}
