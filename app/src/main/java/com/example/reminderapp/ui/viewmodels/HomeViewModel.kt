package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    
    val reminders: StateFlow<List<Reminder>> = repository
        .getActiveReminders()
        .catch { error ->
            Log.e("HomeViewModel", "Error loading reminders", error)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun toggleReminderComplete(reminderId: Long) {
        viewModelScope.launch {
            try {
                // Get current reminder to toggle its completion status
                val reminder = repository.getReminderById(reminderId)
                reminder?.let {
                    val newCompletionStatus = !it.isCompleted
                    repository.updateCompletionStatus(reminderId, newCompletionStatus)
                    Log.d("HomeViewModel", "Reminder completion toggled: $reminderId")
                } ?: run {
                    Log.w("HomeViewModel", "Reminder not found: $reminderId")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error toggling reminder completion", e)
            }
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.deleteReminder(reminder)
                Log.d("HomeViewModel", "Reminder deleted: ${reminder.id}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting reminder", e)
            }
        }
    }
    
    fun refreshReminders() {
        viewModelScope.launch {
            try {
                // This could trigger a sync with the server
                // For now, we'll just rely on the Flow to update automatically
                Log.d("HomeViewModel", "Refreshing reminders")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error refreshing reminders", e)
            }
        }
    }
}
