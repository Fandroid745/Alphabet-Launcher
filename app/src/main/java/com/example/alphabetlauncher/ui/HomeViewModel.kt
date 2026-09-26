package com.example.alphabetlauncher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphabetlauncher.data.AppInfo
import com.example.alphabetlauncher.data.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _appsByLetter = MutableStateFlow<Map<Char, List<AppInfo>>>(emptyMap())
    val appsByLetter: StateFlow<Map<Char, List<AppInfo>>> = _appsByLetter.asStateFlow()

    private val _favouriteApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val favouriteApps: StateFlow<List<AppInfo>> = _favouriteApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLetter = MutableStateFlow<Char?>(null)
    val selectedLetter: StateFlow<Char?> = _selectedLetter.asStateFlow()

    private val _touchYRatio = MutableStateFlow<Float?>(null)
    val touchYRatio: StateFlow<Float?> = _touchYRatio.asStateFlow()

    private val _currentTimeString = MutableStateFlow("")
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    private val _currentDateString = MutableStateFlow("")
    val currentDateString: StateFlow<String> = _currentDateString.asStateFlow()

    init {
        loadInstalledApps()
        startClock()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val appsMap = appRepository.getAppsByLetter()
            _appsByLetter.value = appsMap

            // Select first 6 installed apps as default favourites for resting view
            val allApps = appsMap.values.flatten()
            _favouriteApps.value = allApps.take(6)

            _isLoading.value = false
        }
    }

    private fun startClock() {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

            while (isActive) {
                val now = Date()
                _currentTimeString.value = timeFormat.format(now)
                _currentDateString.value = dateFormat.format(now)
                delay(1000L)
            }
        }
    }

    fun onLetterTouched(letter: Char, touchYRatio: Float) {
        _selectedLetter.value = letter
        _touchYRatio.value = touchYRatio
    }

    fun onFingerReleased() {
        _selectedLetter.value = null
        _touchYRatio.value = null
    }

    fun launchApp(packageName: String): Boolean {
        return appRepository.launchApp(packageName)
    }
}
