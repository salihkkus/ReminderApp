package com.example.reminderapp.data.repository

import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.local.ReminderDao
import com.example.reminderapp.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao,
    private val apiService: BilsoftApiService
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
    
    // API operations
    suspend fun syncRemindersFromServer(token: String) {
        try {
            val response = apiService.getReminders(token)
            if (response.success && response.data != null) {
                response.data.forEach { reminder ->
                    reminderDao.insertReminder(reminder)
                }
            }
        } catch (e: Exception) {
            // Handle error - could be logged or reported
        }
    }
    
    suspend fun createReminderOnServer(token: String, reminder: Reminder): Result<Reminder> {
        return try {
            val response = apiService.createReminder(token, reminder)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateReminderOnServer(token: String, reminder: Reminder): Result<Reminder> {
        return try {
            val response = apiService.updateReminder(token, reminder.id, reminder)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteReminderOnServer(token: String, id: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteReminder(token, id)
            if (response.success && response.data == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
