package com.example.reminderapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dateTime: LocalDateTime,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}
