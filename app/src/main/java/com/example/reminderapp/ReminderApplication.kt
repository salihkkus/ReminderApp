package com.example.reminderapp

import android.app.Application
import com.example.reminderapp.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import com.jakewharton.threetenabp.AndroidThreeTen

@HiltAndroidApp
class ReminderApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ThreeTenABP for date/time handling
        AndroidThreeTen.init(this)
        
        // Initialize database
        AppDatabase.getDatabase(this)
    }
}
