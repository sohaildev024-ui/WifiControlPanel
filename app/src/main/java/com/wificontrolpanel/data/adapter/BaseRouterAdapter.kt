package com.wificontrolpanel.data.adapter

import com.wificontrolpanel.data.model.*
import com.wificontrolpanel.data.router.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

abstract class BaseRouterAdapter(protected val config: RouterConnectionConfig) : RouterAdapter {
    protected val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    override val connectionState: Flow<ConnectionState> = _connectionState.asStateFlow()

    protected val _networkStats = MutableStateFlow<NetworkStats>(NetworkStats(
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
    override val networkStatsFlow: Flow<NetworkStats> = _networkStats.asStateFlow()

    protected val _devices = MutableStateFlow<List<Device>>(emptyList())
    override val devicesFlow: Flow<List<Device>> = _devices.asStateFlow()

    override val routerBrand: RouterBrand = config.brand
    override val supportedFeatures: Set<RouterFeature> = getSupportedFeatures()
    override var isConnected: Boolean = false

    protected abstract fun getSupportedFeatures(): Set<RouterFeature>

    override suspend fun connect(config: RouterConnectionConfig): RouterOperationResult {
        _connectionState.value = ConnectionState.CONNECTING
        return try {
            val result = performConnection(config)
            if (result.success) {
                isConnected = true
                _connectionState.value = ConnectionState.CONNECTED
                startPolling()
            } else {
                _connectionState.value = ConnectionState.ERROR
            }
            result
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            RouterOperationResult(false, "Connection failed: ${e.message}")
        }
    }

    protected abstract suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult
    protected abstract fun startPolling()
    protected abstract fun stopPolling()

    override suspend fun disconnect(): RouterOperationResult {
        stopPolling()
        isConnected = false
        _connectionState.value = ConnectionState.DISCONNECTED
        return RouterOperationResult(true, "Disconnected")
    }

    override suspend fun getNetworkStats(): Result<NetworkStats> {
        return if (isConnected) {
            Result.Success(_networkStats.value)
        } else {
            Result.Failure(RouterError.connectionFailed("Not connected"))
        }
    }

    override suspend fun getRouterInfo(): Result<RouterInfo> {
        return Result.Failure(RouterError.notSupported(RouterFeature.LOGS))
    }

    override suspend fun getConnectedDevices(): Result<List<Device>> {
        return if (isConnected) {
            Result.Success(_devices.value)
        } else {
            Result.Failure(RouterError.connectionFailed("Not connected"))
        }
    }

    override suspend fun blockDevice(macAddress: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.DEVICE_BLOCK_UNBLOCK)

    override suspend fun unblockDevice(macAddress: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.DEVICE_BLOCK_UNBLOCK)

    override suspend fun pauseDeviceInternet(macAddress: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun resumeDeviceInternet(macAddress: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun renameDevice(macAddress: String, newName: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.DEVICE_RENAME)

    override suspend fun setDeviceTrusted(macAddress: String, trusted: Boolean): RouterOperationResult =
        checkFeatureSupport(RouterFeature.TRUSTED_DEVICES)

    override suspend fun setDeviceDownloadLimit(macAddress: String, limitBps: Long?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.SPEED_LIMIT_PER_DEVICE)

    override suspend fun setDeviceUploadLimit(macAddress: String, limitBps: Long?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.SPEED_LIMIT_PER_DEVICE)

    override suspend fun setDevicePriority(macAddress: String, priority: Priority): RouterOperationResult =
        checkFeatureSupport(RouterFeature.QOS)

    override suspend fun setWifiEnabled(enabled: Boolean): RouterOperationResult =
        checkFeatureSupport(RouterFeature.WIFI_ON_OFF)

    override suspend fun setGuestWifiEnabled(enabled: Boolean): RouterOperationResult =
        checkFeatureSupport(RouterFeature.GUEST_WIFI)

    override suspend fun setBandEnabled(band: FrequencyBand, enabled: Boolean): RouterOperationResult =
        when (band) {
            FrequencyBand.BAND_2_4GHZ -> checkFeatureSupport(RouterFeature.BAND_2_4GHZ_CONTROL)
            FrequencyBand.BAND_5GHZ -> checkFeatureSupport(RouterFeature.BAND_5GHZ_CONTROL)
            FrequencyBand.BAND_6GHZ -> checkFeatureSupport(RouterFeature.BAND_6GHZ_CONTROL)
            else -> RouterOperationResult(false, "Unknown band")
        }

    override suspend fun changeSsid(band: FrequencyBand, newSsid: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.CHANGE_SSID)

    override suspend fun changePassword(newPassword: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.CHANGE_PASSWORD)

    override suspend fun getWifiSettings(): Result<WifiSettings> =
        Result.Failure(RouterError.notSupported(RouterFeature.CHANGE_SSID))

    override suspend fun setWifiSettings(settings: WifiSettings): RouterOperationResult =
        checkFeatureSupport(RouterFeature.CHANGE_SSID)

    override suspend fun setChannel(band: FrequencyBand, channel: Int): RouterOperationResult =
        checkFeatureSupport(RouterFeature.CHANNEL_SELECTION)

    override suspend fun setTxPower(band: FrequencyBand, power: TxPower): RouterOperationResult =
        checkFeatureSupport(RouterFeature.TX_POWER_CONTROL)

    override suspend fun setSecurityMode(mode: SecurityMode): RouterOperationResult =
        checkFeatureSupport(RouterFeature.WPA3)

    override suspend fun setMaxUsers(max: Int): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun setSsidHidden(hidden: Boolean): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun pauseInternet(): RouterOperationResult =
        checkFeatureSupport(RouterFeature.INTERNET_PAUSE_RESUME)

    override suspend fun resumeInternet(): RouterOperationResult =
        checkFeatureSupport(RouterFeature.INTERNET_PAUSE_RESUME)

    override suspend fun restartRouter(): RouterOperationResult =
        checkFeatureSupport(RouterFeature.ROUTER_RESTART)

    override suspend fun factoryReset(): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun updateFirmware(): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun setTotalDownloadLimit(limitBps: Long?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.SPEED_LIMIT_TOTAL)

    override suspend fun setTotalUploadLimit(limitBps: Long?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.SPEED_LIMIT_TOTAL)

    override suspend fun setSpeedDistributionMode(mode: DistributionMode): RouterOperationResult =
        checkFeatureSupport(RouterFeature.TRAFFIC_SHAPING)

    override suspend fun setDefaultDeviceLimits(download: Long?, upload: Long?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.SPEED_LIMIT_PER_DEVICE)

    override suspend fun getSpeedLimitConfig(): Result<SpeedLimitConfig> =
        Result.Failure(RouterError.notSupported(RouterFeature.SPEED_LIMIT_TOTAL))

    override suspend fun getBlockedMacAddresses(): Result<List<MacAddressEntry>> =
        Result.Failure(RouterError.notSupported(RouterFeature.MAC_FILTERING))

    override suspend fun getTrustedMacAddresses(): Result<List<MacAddressEntry>> =
        Result.Failure(RouterError.notSupported(RouterFeature.TRUSTED_DEVICES))

    override suspend fun blockMacAddress(mac: String, name: String?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.MAC_FILTERING)

    override suspend fun unblockMacAddress(mac: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.MAC_FILTERING)

    override suspend fun addTrustedMacAddress(mac: String, name: String?): RouterOperationResult =
        checkFeatureSupport(RouterFeature.TRUSTED_DEVICES)

    override suspend fun removeTrustedMacAddress(mac: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.TRUSTED_DEVICES)

    override suspend fun setMacFilterMode(mode: MacFilterMode): RouterOperationResult =
        checkFeatureSupport(RouterFeature.MAC_FILTERING)

    override suspend fun getMacFilterMode(): Result<MacFilterMode> =
        Result.Failure(RouterError.notSupported(RouterFeature.MAC_FILTERING))

    override suspend fun getParentalControlRules(): Result<List<ParentalControlRule>> =
        Result.Failure(RouterError.notSupported(RouterFeature.PARENTAL_CONTROL))

    override suspend fun setParentalControlRule(rule: ParentalControlRule): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun deleteParentalControlRule(deviceMac: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun pauseDeviceInternetSchedule(deviceMac: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun resumeDeviceInternetSchedule(deviceMac: String): RouterOperationResult =
        checkFeatureSupport(RouterFeature.PARENTAL_CONTROL)

    override suspend fun getSecurityLogs(limit: Int): Result<List<SecurityEvent>> =
        Result.Failure(RouterError.notSupported(RouterFeature.SECURITY_LOGS))

    override suspend fun getLoginHistory(limit: Int): Result<List<SecurityEvent>> =
        Result.Failure(RouterError.notSupported(RouterFeature.LOGS))

    override suspend fun getBlockHistory(limit: Int): Result<List<SecurityEvent>> =
        Result.Failure(RouterError.notSupported(RouterFeature.LOGS))

    override suspend fun changeAdminPassword(oldPassword: String, newPassword: String): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun enableUnknownDeviceAlerts(enabled: Boolean): RouterOperationResult =
        RouterOperationResult(false, "Feature not supported by this router")

    override suspend fun getUsageReport(period: ReportPeriod, customStart: Date?, customEnd: Date?): Result<UsageReport> =
        Result.Failure(RouterError.notSupported(RouterFeature.TRAFFIC_STATS))
}