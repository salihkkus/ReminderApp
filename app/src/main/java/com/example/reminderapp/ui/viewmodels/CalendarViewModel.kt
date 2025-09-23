package com.example.reminderapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _currentMonth: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    private val _remindersForMonth: MutableStateFlow<List<Reminder>> = MutableStateFlow(emptyList())
    val remindersForMonth: StateFlow<List<Reminder>> = _remindersForMonth.asStateFlow()

    init {
        loadMonth(_currentMonth.value)
    }

    fun previousMonth() {
        val newMonth = _currentMonth.value.minusMonths(1).withDayOfMonth(1)
        _currentMonth.value = newMonth
        loadMonth(newMonth)
    }

    fun nextMonth() {
        val newMonth = _currentMonth.value.plusMonths(1).withDayOfMonth(1)
        _currentMonth.value = newMonth
        loadMonth(newMonth)
    }

    fun loadMonth(monthStartDate: LocalDate) {
        viewModelScope.launch {
            val startDateTime = LocalDateTime.of(monthStartDate, LocalTime.MIN)
            val endDateExclusive = monthStartDate.plusMonths(1)
            val endDateTime = LocalDateTime.of(endDateExclusive.minusDays(1), LocalTime.MAX)
            reminderRepository
                .getRemindersByDateRange(startDateTime, endDateTime)
                .collect { list ->
                    _remindersForMonth.value = list
                }
        }
    }
}


