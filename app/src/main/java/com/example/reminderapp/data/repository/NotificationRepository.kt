package com.example.reminderapp.data.repository

import com.example.reminderapp.data.local.NotificationDao
import com.example.reminderapp.data.model.Notification
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    
    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications()
    
    suspend fun getNotificationById(id: Long): Notification? = notificationDao.getNotificationById(id)
    
    suspend fun insertNotification(notification: Notification): Long = notificationDao.insertNotification(notification)
    
    suspend fun updateNotification(notification: Notification) = notificationDao.updateNotification(notification)
    
    suspend fun deleteNotification(notification: Notification) = notificationDao.deleteNotification(notification)
    
    suspend fun deleteNotificationById(id: Long) = notificationDao.deleteNotificationById(id)
}
