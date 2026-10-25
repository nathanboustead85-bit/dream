package com.dreamstream.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamstream.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Error(val message: String) : UiState()
    object LoggedIn : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = Preferences(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo.asStateFlow()

    private val _liveCategories = MutableStateFlow<List<Category>>(emptyList())
    val liveCategories: StateFlow<List<Category>> = _liveCategories.asStateFlow()

    private val _liveStreams = MutableStateFlow<List<LiveStream>>(emptyList())
    val liveStreams: StateFlow<List<LiveStream>> = _liveStreams.asStateFlow()

    private val _vodCategories = MutableStateFlow<List<Category>>(emptyList())
    val vodCategories: StateFlow<List<Category>> = _vodCategories.asStateFlow()

    private val _vodStreams = MutableStateFlow<List<VodStream>>(emptyList())
    val vodStreams: StateFlow<List<VodStream>> = _vodStreams.asStateFlow()

    private val _seriesCategories = MutableStateFlow<List<Category>>(emptyList())
    val seriesCategories: StateFlow<List<Category>> = _seriesCategories.asStateFlow()

    private val _seriesList = MutableStateFlow<List<Series>>(emptyList())
    val seriesList: StateFlow<List<Series>> = _seriesList.asStateFlow()

    private var repository: Xt
