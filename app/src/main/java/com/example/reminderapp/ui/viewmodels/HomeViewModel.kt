package com.example.reminderapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    
    val reminders: StateFlow<List<Reminder>> = repository
        .getActiveReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun toggleReminderComplete(reminderId: Long) {
        viewModelScope.launch {
            // Get current reminder to toggle its completion status
            val reminder = repository.getReminderById(reminderId)
            reminder?.let {
                val newCompletionStatus = !it.isCompleted
                repository.updateCompletionStatus(reminderId, newCompletionStatus)
            }
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
    
    fun refreshReminders() {
        // This could trigger a sync with the server
        // For now, we'll just rely on the Flow to update automatically
    }
}
