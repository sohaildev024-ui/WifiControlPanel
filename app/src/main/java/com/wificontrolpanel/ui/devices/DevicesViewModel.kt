package com.wificontrolpanel.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wificontrolpanel.data.model.Device
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.data.router.Result
import com.wificontrolpanel.data.router.RouterOperationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val repository: RouterRepository
) : ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _selectedDevice = MutableStateFlow<Device?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    init {
        loadDevices()
    }

    fun loadDevices() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getConnectedDevices()
            result.onSuccess { devices ->
                _devices.value = devices
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    fun refreshDevices() {
        loadDevices()
    }

    fun selectDevice(device: Device) {
        _selectedDevice.value = device
    }

    fun clearSelection() {
        _selectedDevice.value = null
    }

    fun blockDevice(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.blockDevice(macAddress)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun unblockDevice(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.unblockDevice(macAddress)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun pauseDeviceInternet(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.pauseDeviceInternet(macAddress)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun resumeDeviceInternet(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.resumeDeviceInternet(macAddress)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun renameDevice(macAddress: String, newName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.renameDevice(macAddress, newName)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun setDeviceTrusted(macAddress: String, trusted: Boolean, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setDeviceTrusted(macAddress, trusted)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun setDeviceDownloadLimit(macAddress: String, limitBps: Long?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setDeviceDownloadLimit(macAddress, limitBps)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun setDeviceUploadLimit(macAddress: String, limitBps: Long?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setDeviceUploadLimit(macAddress, limitBps)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    fun setDevicePriority(macAddress: String, priority: com.wificontrolpanel.data.model.Priority, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setDevicePriority(macAddress, priority)
            onResult(result.success, result.message)
            if (result.success) loadDevices()
        }
    }

    private fun Result<List<Device>>.onSuccess(action: (List<Device>) -> Unit) {
        if (this is Result.Success) action(data)
    }

    private fun Result<List<Device>>.onFailure(action: (com.wificontrolpanel.data.router.RouterError) -> Unit) {
        if (this is Result.Failure) action(error)
    }
}