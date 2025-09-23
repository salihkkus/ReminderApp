package com.example.reminderapp.data.repository

import com.example.reminderapp.data.local.ReminderDao
import com.example.reminderapp.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    
    // Local database operations
    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()
    
    fun getActiveReminders(): Flow<List<Reminder>> = reminderDao.getActiveReminders()
    
    fun getRemindersByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Reminder>> =
        reminderDao.getRemindersByDateRange(startDate, endDate)
    
    suspend fun getReminderById(id: Long): Reminder? = reminderDao.getReminderById(id)
    
    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder)
    
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean) =
        reminderDao.updateCompletionStatus(id, isCompleted)

}
