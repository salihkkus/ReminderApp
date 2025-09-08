package com.example.reminderapp.data.local

import androidx.room.*
import com.example.reminderapp.data.model.Notification
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDateTime

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications ORDER BY tarihSaat DESC")
    fun getAllNotifications(): Flow<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Long): Notification?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Delete
    suspend fun deleteNotification(notification: Notification)
    
    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)
}
