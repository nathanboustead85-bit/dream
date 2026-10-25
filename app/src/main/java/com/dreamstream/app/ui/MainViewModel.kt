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

    private var repository: XtreamRepository? = null

    val savedServer = prefs.server.stateIn(viewModelScope, SharingStarted.Eagerly, "http://vpn-esxtrapro.info:80")
    val savedUsername = prefs.username.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val savedPassword = prefs.password.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun login(server: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                var cleanServer = server.trim()
                if (!cleanServer.startsWith("http://") && !cleanServer.startsWith("https://")) {
                    cleanServer = "http://$cleanServer"
                }
                cleanServer = cleanServer.trimEnd('/')

                val repo = XtreamRepository(cleanServer, username.trim(), password)
                val info = repo.login()

                val userInfo = info.user_info
                val isAuthorized = userInfo?.auth == 1 ||
                        userInfo?.status?.equals("Active", ignoreCase = true) == true ||
                        userInfo?.status?.equals("active", ignoreCase = true) == true

                if (userInfo == null) {
                    _uiState.value = UiState.Error("Invalid response from server. Check server address.")
                    return@launch
                }

                if (!isAuthorized) {
                    _uiState.value = UiState.Error(
                        "Login failed. Status: ${userInfo.status ?: "Unknown"}"
                    )
                    return@launch
                }

                repository = repo
                prefs.saveCredentials(cleanServer, username.trim(), password)
                _accountInfo.value = info
                _uiState.value = UiState.LoggedIn

                loadLiveCategories()
                loadVodCategories()
                loadSeriesCategories()
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                        "Cannot reach server. Check the server address."
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Connection timeout. Server may be slow or down."
                    else -> e.message ?: "Connection error"
                }
                _uiState.value = UiState.Error(message)
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

    fun loadSeriesCategories() {
        viewModelScope.launch {
            try {
                _seriesCategories.value = repository?.getSeriesCategories() ?: emptyList()
            } catch (_: Exception) { }
        }
    }

    fun loadSeries(categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _seriesList.value = repository?.getSeries(categoryId) ?: emptyList()
                _uiState.value = UiState.LoggedIn
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load series")
            }
        }
    }

    fun getLiveUrl(streamId: Int): String? = repository?.liveStreamUrl(streamId)
    fun getVodUrl(streamId: Int): String? = repository?.vodStreamUrl(streamId)
    fun getSeriesEpisodeUrl(episodeId: String): String? = repository?.seriesEpisodeUrl(episodeId)

    fun logout() {
        viewModelScope.launch {
            prefs.clear()
            repository = null
            _accountInfo.value = null
            _liveCategories.value = emptyList()
            _liveStreams.value = emptyList()
            _vodCategories.value = emptyList()
            _vodStreams.value = emptyList()
            _seriesCategories.value = emptyList()
            _seriesList.value = emptyList()
            _uiState.value = UiState.Idle
        }
    }
}
