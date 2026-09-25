package com.wificontrolpanel.data.repository

import com.wificontrolpanel.data.adapter.RouterAdapterFactory
import com.wificontrolpanel.data.model.*
import com.wificontrolpanel.data.router.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouterRepository @Inject constructor() {

    private var currentAdapter: RouterAdapter? = null
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: Flow<ConnectionState> = _connectionState

    private val _currentConfig = MutableStateFlow<RouterConnectionConfig?>(null)
    val currentConfig: Flow<RouterConnectionConfig?> = _currentConfig

    suspend fun connect(config: RouterConnectionConfig): RouterOperationResult {
        val adapter = RouterAdapterFactory.createAdapter(config)
        currentAdapter = adapter
        _currentConfig.value = config

        val result = adapter.connect(config)
        _connectionState.value = adapter.connectionState.value

        if (result.success) {
            _connectionState.value = ConnectionState.CONNECTED
        }

        return result
    }

    suspend fun disconnect(): RouterOperationResult {
        val result = currentAdapter?.disconnect() ?: RouterOperationResult(true, "Not connected")
        currentAdapter = null
        _connectionState.value = ConnectionState.DISCONNECTED
        return result
    }

    val adapter: RouterAdapter?
        get() = currentAdapter

    val isConnected: Boolean
        get() = currentAdapter?.isConnected == true

    val supportedFeatures: Set<RouterFeature>
        get() = currentAdapter?.supportedFeatures ?: emptySet()

    val routerBrand: RouterBrand?
        get() = currentAdapter?.routerBrand

    // Network Stats
    val networkStatsFlow: Flow<NetworkStats>
        get() = currentAdapter?.networkStatsFlow ?: MutableStateFlow(NetworkStats(
            isInternetConnected = false,
            isRouterOnline = false,
            totalConnectedDevices = 0,
            totalBlockedDevices = 0,
            unknownDevices = 0,
            currentDownloadSpeed = 0,
            currentUploadSpeed = 0,
            totalDataUsage = 0,
            uptime = 0
        )).asStateFlow()

    suspend fun getNetworkStats(): Result<NetworkStats> =
        currentAdapter?.getNetworkStats() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun getRouterInfo(): Result<RouterInfo> =
        currentAdapter?.getRouterInfo() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    // Devices
    val devicesFlow: Flow<List<Device>>
        get() = currentAdapter?.devicesFlow ?: MutableStateFlow(emptyList()).asStateFlow()

    suspend fun getConnectedDevices(): Result<List<Device>> =
        currentAdapter?.getConnectedDevices() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun blockDevice(macAddress: String): RouterOperationResult =
        currentAdapter?.blockDevice(macAddress) ?: RouterOperationResult(false, "Not connected")

    suspend fun unblockDevice(macAddress: String): RouterOperationResult =
        currentAdapter?.unblockDevice(macAddress) ?: RouterOperationResult(false, "Not connected")

    suspend fun pauseDeviceInternet(macAddress: String): RouterOperationResult =
        currentAdapter?.pauseDeviceInternet(macAddress) ?: RouterOperationResult(false, "Not connected")

    suspend fun resumeDeviceInternet(macAddress: String): RouterOperationResult =
        currentAdapter?.resumeDeviceInternet(macAddress) ?: RouterOperationResult(false, "Not connected")

    suspend fun renameDevice(macAddress: String, newName: String): RouterOperationResult =
        currentAdapter?.renameDevice(macAddress, newName) ?: RouterOperationResult(false, "Not connected")

    suspend fun setDeviceTrusted(macAddress: String, trusted: Boolean): RouterOperationResult =
        currentAdapter?.setDeviceTrusted(macAddress, trusted) ?: RouterOperationResult(false, "Not connected")

    suspend fun setDeviceDownloadLimit(macAddress: String, limitBps: Long?): RouterOperationResult =
        currentAdapter?.setDeviceDownloadLimit(macAddress, limitBps) ?: RouterOperationResult(false, "Not connected")

    suspend fun setDeviceUploadLimit(macAddress: String, limitBps: Long?): RouterOperationResult =
        currentAdapter?.setDeviceUploadLimit(macAddress, limitBps) ?: RouterOperationResult(false, "Not connected")

    suspend fun setDevicePriority(macAddress: String, priority: Priority): RouterOperationResult =
        currentAdapter?.setDevicePriority(macAddress, priority) ?: RouterOperationResult(false, "Not connected")

    // WiFi Controls
    suspend fun setWifiEnabled(enabled: Boolean): RouterOperationResult =
        currentAdapter?.setWifiEnabled(enabled) ?: RouterOperationResult(false, "Not connected")

    suspend fun setGuestWifiEnabled(enabled: Boolean): RouterOperationResult =
        currentAdapter?.setGuestWifiEnabled(enabled) ?: RouterOperationResult(false, "Not connected")

    suspend fun setBandEnabled(band: FrequencyBand, enabled: Boolean): RouterOperationResult =
        currentAdapter?.setBandEnabled(band, enabled) ?: RouterOperationResult(false, "Not connected")

    suspend fun changeSsid(band: FrequencyBand, newSsid: String): RouterOperationResult =
        currentAdapter?.changeSsid(band, newSsid) ?: RouterOperationResult(false, "Not connected")

    suspend fun changePassword(newPassword: String): RouterOperationResult =
        currentAdapter?.changePassword(newPassword) ?: RouterOperationResult(false, "Not connected")

    suspend fun getWifiSettings(): Result<WifiSettings> =
        currentAdapter?.getWifiSettings() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun setWifiSettings(settings: WifiSettings): RouterOperationResult =
        currentAdapter?.setWifiSettings(settings) ?: RouterOperationResult(false, "Not connected")

    suspend fun setChannel(band: FrequencyBand, channel: Int): RouterOperationResult =
        currentAdapter?.setChannel(band, channel) ?: RouterOperationResult(false, "Not connected")

    suspend fun setTxPower(band: FrequencyBand, power: TxPower): RouterOperationResult =
        currentAdapter?.setTxPower(band, power) ?: RouterOperationResult(false, "Not connected")

    suspend fun setSecurityMode(mode: SecurityMode): RouterOperationResult =
        currentAdapter?.setSecurityMode(mode) ?: RouterOperationResult(false, "Not connected")

    suspend fun setMaxUsers(max: Int): RouterOperationResult =
        currentAdapter?.setMaxUsers(max) ?: RouterOperationResult(false, "Not connected")

    suspend fun setSsidHidden(hidden: Boolean): RouterOperationResult =
        currentAdapter?.setSsidHidden(hidden) ?: RouterOperationResult(false, "Not connected")

    // Internet Controls
    suspend fun pauseInternet(): RouterOperationResult =
        currentAdapter?.pauseInternet() ?: RouterOperationResult(false, "Not connected")

    suspend fun resumeInternet(): RouterOperationResult =
        currentAdapter?.resumeInternet() ?: RouterOperationResult(false, "Not connected")

    // Router Controls
    suspend fun restartRouter(): RouterOperationResult =
        currentAdapter?.restartRouter() ?: RouterOperationResult(false, "Not connected")

    suspend fun factoryReset(): RouterOperationResult =
        currentAdapter?.factoryReset() ?: RouterOperationResult(false, "Not connected")

    suspend fun updateFirmware(): RouterOperationResult =
        currentAdapter?.updateFirmware() ?: RouterOperationResult(false, "Not connected")

    // Speed Management
    suspend fun setTotalDownloadLimit(limitBps: Long?): RouterOperationResult =
        currentAdapter?.setTotalDownloadLimit(limitBps) ?: RouterOperationResult(false, "Not connected")

    suspend fun setTotalUploadLimit(limitBps: Long?): RouterOperationResult =
        currentAdapter?.setTotalUploadLimit(limitBps) ?: RouterOperationResult(false, "Not connected")

    suspend fun setSpeedDistributionMode(mode: DistributionMode): RouterOperationResult =
        currentAdapter?.setSpeedDistributionMode(mode) ?: RouterOperationResult(false, "Not connected")

    suspend fun setDefaultDeviceLimits(download: Long?, upload: Long?): RouterOperationResult =
        currentAdapter?.setDefaultDeviceLimits(download, upload) ?: RouterOperationResult(false, "Not connected")

    suspend fun getSpeedLimitConfig(): Result<SpeedLimitConfig> =
        currentAdapter?.getSpeedLimitConfig() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    // MAC Control
    suspend fun getBlockedMacAddresses(): Result<List<MacAddressEntry>> =
        currentAdapter?.getBlockedMacAddresses() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun getTrustedMacAddresses(): Result<List<MacAddressEntry>> =
        currentAdapter?.getTrustedMacAddresses() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun blockMacAddress(mac: String, name: String?): RouterOperationResult =
        currentAdapter?.blockMacAddress(mac, name) ?: RouterOperationResult(false, "Not connected")

    suspend fun unblockMacAddress(mac: String): RouterOperationResult =
        currentAdapter?.unblockMacAddress(mac) ?: RouterOperationResult(false, "Not connected")

    suspend fun addTrustedMacAddress(mac: String, name: String?): RouterOperationResult =
        currentAdapter?.addTrustedMacAddress(mac, name) ?: RouterOperationResult(false, "Not connected")

    suspend fun removeTrustedMacAddress(mac: String): RouterOperationResult =
        currentAdapter?.removeTrustedMacAddress(mac) ?: RouterOperationResult(false, "Not connected")

    suspend fun setMacFilterMode(mode: MacFilterMode): RouterOperationResult =
        currentAdapter?.setMacFilterMode(mode) ?: RouterOperationResult(false, "Not connected")

    suspend fun getMacFilterMode(): Result<MacFilterMode> =
        currentAdapter?.getMacFilterMode() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    // Parental Control
    suspend fun getParentalControlRules(): Result<List<ParentalControlRule>> =
        currentAdapter?.getParentalControlRules() ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun setParentalControlRule(rule: ParentalControlRule): RouterOperationResult =
        currentAdapter?.setParentalControlRule(rule) ?: RouterOperationResult(false, "Not connected")

    suspend fun deleteParentalControlRule(deviceMac: String): RouterOperationResult =
        currentAdapter?.deleteParentalControlRule(deviceMac) ?: RouterOperationResult(false, "Not connected")

    suspend fun pauseDeviceInternetSchedule(deviceMac: String): RouterOperationResult =
        currentAdapter?.pauseDeviceInternetSchedule(deviceMac) ?: RouterOperationResult(false, "Not connected")

    suspend fun resumeDeviceInternetSchedule(deviceMac: String): RouterOperationResult =
        currentAdapter?.resumeDeviceInternetSchedule(deviceMac) ?: RouterOperationResult(false, "Not connected")

    // Security
    suspend fun getSecurityLogs(limit: Int): Result<List<SecurityEvent>> =
        currentAdapter?.getSecurityLogs(limit) ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun getLoginHistory(limit: Int): Result<List<SecurityEvent>> =
        currentAdapter?.getLoginHistory(limit) ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun getBlockHistory(limit: Int): Result<List<SecurityEvent>> =
        currentAdapter?.getBlockHistory(limit) ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    suspend fun changeAdminPassword(oldPassword: String, newPassword: String): RouterOperationResult =
        currentAdapter?.changeAdminPassword(oldPassword, newPassword) ?: RouterOperationResult(false, "Not connected")

    suspend fun enableUnknownDeviceAlerts(enabled: Boolean): RouterOperationResult =
        currentAdapter?.enableUnknownDeviceAlerts(enabled) ?: RouterOperationResult(false, "Not connected")

    // Reports
    suspend fun getUsageReport(period: ReportPeriod, customStart: Date?, customEnd: Date?): Result<UsageReport> =
        currentAdapter?.getUsageReport(period, customStart, customEnd) ?: Result.Failure(RouterError.connectionFailed("Not connected"))

    fun isFeatureSupported(feature: RouterFeature): Boolean =
        currentAdapter?.isFeatureSupported(feature) == true

    fun checkFeatureSupport(feature: RouterFeature): RouterOperationResult =
        currentAdapter?.checkFeatureSupport(feature) ?: RouterOperationResult(false, "Not connected")
}