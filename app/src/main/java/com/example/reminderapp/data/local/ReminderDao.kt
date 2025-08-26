package com.example.reminderapp.data.local

import androidx.room.*
import com.example.reminderapp.data.model.Reminder
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime

@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders ORDER BY dateTime ASC")
    fun getAllReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getActiveReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE dateTime BETWEEN :startDate AND :endDate ORDER BY dateTime ASC")
    fun getRemindersByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("UPDATE reminders SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)
    
    @Query("DELETE FROM reminders WHERE isCompleted = 1 AND dateTime < :date")
    suspend fun deleteOldCompletedReminders(date: LocalDateTime)
}
