package com.wificontrolpanel.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wificontrolpanel.data.model.NetworkStats
import com.wificontrolpanel.data.model.RouterInfo
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.data.router.ConnectionState
import com.wificontrolpanel.data.router.Result
import com.wificontrolpanel.data.router.RouterOperationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: RouterRepository
) : ViewModel() {

    private val _networkStats = MutableStateFlow<NetworkStats>(NetworkStats(
        isInternetConnected = false,
        isRouterOnline = false,
        totalConnectedDevices = 0,
        totalBlockedDevices = 0,
        unknownDevices = 0,
        currentDownloadSpeed = 0,
        currentUploadSpeed = 0,
        totalDataUsage = 0,
        uptime = 0
    ))
    val networkStats = _networkStats.asStateFlow()

    private val _routerInfo = MutableStateFlow<RouterInfo?>(null)
    val routerInfo = _routerInfo.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.connectionState.collect { state ->
                _connectionState.value = state
            }
        }

        viewModelScope.launch {
            repository.networkStatsFlow.collect { stats ->
                _networkStats.value = stats
            }
        }
    }

    fun refresh() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val statsResult = repository.getNetworkStats()
            val infoResult = repository.getRouterInfo()

            statsResult.onSuccess { stats ->
                _networkStats.value = stats
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            infoResult.onSuccess { info ->
                _routerInfo.value = info
            }.onFailure { error ->
                // Router info is optional
            }

            _isLoading.value = false
        }
    }

    fun toggleWifi(enabled: Boolean) {
        viewModelScope.launch {
            val result = repository.setWifiEnabled(enabled)
            if (!result.success) {
                _errorMessage.value = result.message
            } else {
                refresh()
            }
        }
    }

    fun toggleInternet(pause: Boolean) {
        viewModelScope.launch {
            val result = if (pause) repository.pauseInternet() else repository.resumeInternet()
            if (!result.success) {
                _errorMessage.value = result.message
            } else {
                refresh()
            }
        }
    }

    fun restartRouter() {
        viewModelScope.launch {
            val result = repository.restartRouter()
            if (!result.success) {
                _errorMessage.value = result.message
            }
        }
    }

    fun refreshDevices() {
        viewModelScope.launch {
            repository.getConnectedDevices()
            refresh()
        }
    }

    fun toggleGuestWifi(enabled: Boolean) {
        viewModelScope.launch {
            val result = repository.setGuestWifiEnabled(enabled)
            if (!result.success) {
                _errorMessage.value = result.message
            }
        }
    }

    fun toggleBand(band: com.wificontrolpanel.data.model.FrequencyBand, enabled: Boolean) {
        viewModelScope.launch {
            val result = repository.setBandEnabled(band, enabled)
            if (!result.success) {
                _errorMessage.value = result.message
            }
        }
    }

    private fun Result<NetworkStats>.onSuccess(action: (NetworkStats) -> Unit) {
        if (this is Result.Success) action(data)
    }

    private fun Result<NetworkStats>.onFailure(action: (com.wificontrolpanel.data.router.RouterError) -> Unit) {
        if (this is Result.Failure) action(error)
    }

    private fun Result<RouterInfo>.onSuccess(action: (RouterInfo) -> Unit) {
        if (this is Result.Success) action(data)
    }

    private fun Result<RouterInfo>.onFailure(action: (com.wificontrolpanel.data.router.RouterError) -> Unit) {
        if (this is Result.Failure) action(error)
    }

    private fun RouterOperationResult.onSuccess(action: () -> Unit) {
        if (success) action()
    }

    private fun RouterOperationResult.onFailure(action: (String) -> Unit) {
        if (!success) action(message)
    }
}