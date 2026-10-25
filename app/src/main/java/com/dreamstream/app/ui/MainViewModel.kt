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

    private var repository: XtreamRepository? = null

    val savedServer = prefs.server.stateIn(viewModelScope, SharingStarted.Eagerly, "http://vpn-esxtrapro.info:80")
    val savedUsername = prefs.username.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val savedPassword = prefs.password.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun login(server: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val repo = XtreamRepository(server, username, password)
                val info = repo.login()

                if (info.user_info?.auth != 1 && info.user_info?.status?.lowercase() != "active") {
                    _uiState.value = UiState.Error("Login failed. Check credentials or account status.")
                    return@launch
                }

                repository = repo
                prefs.saveCredentials(server, username, password)
                _accountInfo.value = info
                _uiState.value = UiState.LoggedIn

                loadLiveCategories()
                loadVodCategories()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Connection error")
            }
        }
    }

    fun loadLiveCategories() {
        viewModelScope.launch {
            try {
                _liveCategories.value = repository?.getLiveCategories() ?: emptyList()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load live categories")
            }
        }
    }

    fun loadLiveStreams(categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _liveStreams.value = repository?.getLiveStreams(categoryId) ?: emptyList()
                _uiState.value = UiState.LoggedIn
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load channels")
            }
        }
    }

    fun loadVodCategories() {
        viewModelScope.launch {
            try {
                _vodCategories.value = repository?.getVodCategories() ?: emptyList()
            } catch (_: Exception) { }
        }
    }

    fun loadVodStreams(categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _vodStreams.value = repository?.getVodStreams(categoryId) ?: emptyList()
                _uiState.value = UiState.LoggedIn
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    fun getLiveUrl(streamId: Int): String? = repository?.liveStreamUrl(streamId)
    fun getVodUrl(streamId: Int): String? = repository?.vodStreamUrl(streamId)

    fun logout() {
        viewModelScope.launch {
            prefs.clear()
            repository = null
            _accountInfo.value = null
            _liveCategories.value = emptyList()
            _liveStreams.value = emptyList()
            _vodCategories.value = emptyList()
            _vodStreams.value = emptyList()
            _uiState.value = UiState.Idle
        }
    }
}
