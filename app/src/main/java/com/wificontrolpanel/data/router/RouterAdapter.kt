package com.wificontrolpanel.data.router

import com.wificontrolpanel.data.model.*
import kotlinx.coroutines.flow.Flow

interface RouterAdapter {
    val routerBrand: RouterBrand
    val supportedFeatures: Set<RouterFeature>
    val isConnected: Boolean
    val connectionState: Flow<ConnectionState>

    suspend fun connect(config: RouterConnectionConfig): RouterOperationResult
    suspend fun disconnect(): RouterOperationResult

    // Network Status
    suspend fun getNetworkStats(): Result<NetworkStats>
    suspend fun getRouterInfo(): Result<RouterInfo>
    val networkStatsFlow: Flow<NetworkStats>

    // Device Management
    suspend fun getConnectedDevices(): Result<List<Device>>
    val devicesFlow: Flow<List<Device>>
    suspend fun blockDevice(macAddress: String): RouterOperationResult
    suspend fun unblockDevice(macAddress: String): RouterOperationResult
    suspend fun pauseDeviceInternet(macAddress: String): RouterOperationResult
    suspend fun resumeDeviceInternet(macAddress: String): RouterOperationResult
    suspend fun renameDevice(macAddress: String, newName: String): RouterOperationResult
    suspend fun setDeviceTrusted(macAddress: String, trusted: Boolean): RouterOperationResult
    suspend fun setDeviceDownloadLimit(macAddress: String, limitBps: Long?): RouterOperationResult
    suspend fun setDeviceUploadLimit(macAddress: String, limitBps: Long?): RouterOperationResult
    suspend fun setDevicePriority(macAddress: String, priority: Priority): RouterOperationResult

    // WiFi Controls
    suspend fun setWifiEnabled(enabled: Boolean): RouterOperationResult
    suspend fun setGuestWifiEnabled(enabled: Boolean): RouterOperationResult
    suspend fun setBandEnabled(band: FrequencyBand, enabled: Boolean): RouterOperationResult
    suspend fun changeSsid(band: FrequencyBand, newSsid: String): RouterOperationResult
    suspend fun changePassword(newPassword: String): RouterOperationResult
    suspend fun getWifiSettings(): Result<WifiSettings>
    suspend fun setWifiSettings(settings: WifiSettings): RouterOperationResult
    suspend fun setChannel(band: FrequencyBand, channel: Int): RouterOperationResult
    suspend fun setTxPower(band: FrequencyBand, power: TxPower): RouterOperationResult
    suspend fun setSecurityMode(mode: SecurityMode): RouterOperationResult
    suspend fun setMaxUsers(max: Int): RouterOperationResult
    suspend fun setSsidHidden(hidden: Boolean): RouterOperationResult

    // Internet Controls
    suspend fun pauseInternet(): RouterOperationResult
    suspend fun resumeInternet(): RouterOperationResult

    // Router Controls
    suspend fun restartRouter(): RouterOperationResult
    suspend fun factoryReset(): RouterOperationResult
    suspend fun updateFirmware(): RouterOperationResult

    // Speed Management
    suspend fun setTotalDownloadLimit(limitBps: Long?): RouterOperationResult
    suspend fun setTotalUploadLimit(limitBps: Long?): RouterOperationResult
    suspend fun setSpeedDistributionMode(mode: DistributionMode): RouterOperationResult
    suspend fun setDefaultDeviceLimits(download: Long?, upload: Long?): RouterOperationResult
    suspend fun getSpeedLimitConfig(): Result<SpeedLimitConfig>

    // MAC Control
    suspend fun getBlockedMacAddresses(): Result<List<MacAddressEntry>>
    suspend fun getTrustedMacAddresses(): Result<List<MacAddressEntry>>
    suspend fun blockMacAddress(mac: String, name: String?): RouterOperationResult
    suspend fun unblockMacAddress(mac: String): RouterOperationResult
    suspend fun addTrustedMacAddress(mac: String, name: String?): RouterOperationResult
    suspend fun removeTrustedMacAddress(mac: String): RouterOperationResult
    suspend fun setMacFilterMode(mode: MacFilterMode): RouterOperationResult
    suspend fun getMacFilterMode(): Result<MacFilterMode>

    // Parental Control
    suspend fun getParentalControlRules(): Result<List<ParentalControlRule>>
    suspend fun setParentalControlRule(rule: ParentalControlRule): RouterOperationResult
    suspend fun deleteParentalControlRule(deviceMac: String): RouterOperationResult
    suspend fun pauseDeviceInternetSchedule(deviceMac: String): RouterOperationResult
    suspend fun resumeDeviceInternetSchedule(deviceMac: String): RouterOperationResult

    // Security
    suspend fun getSecurityLogs(limit: Int): Result<List<SecurityEvent>>
    suspend fun getLoginHistory(limit: Int): Result<List<SecurityEvent>>
    suspend fun getBlockHistory(limit: Int): Result<List<SecurityEvent>>
    suspend fun changeAdminPassword(oldPassword: String, newPassword: String): RouterOperationResult
    suspend fun enableUnknownDeviceAlerts(enabled: Boolean): RouterOperationResult

    // Reports
    suspend fun getUsageReport(period: ReportPeriod, customStart: Date?, customEnd: Date?): Result<UsageReport>

    fun isFeatureSupported(feature: RouterFeature): Boolean = supportedFeatures.contains(feature)

    fun checkFeatureSupport(feature: RouterFeature): RouterOperationResult {
        return if (isFeatureSupported(feature)) {
            RouterOperationResult(true, "Feature supported")
        } else {
            RouterOperationResult(false, "Feature not supported by this router", listOf(feature))
        }
    }
}

enum class RouterBrand {
    HUAWEI, ZTE, FIBERHOME, NOKIA, TP_LINK, ASUS, NETGEAR, LINKSYS, D_LINK, TENDA, XIAOMI, REALME, GENERIC
}

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, AUTHENTICATING, AUTH_FAILED, CONNECTION_LOST, ERROR
}

data class RouterConnectionConfig(
    val brand: RouterBrand,
    val host: String,
    val port: Int = 80,
    val username: String = "admin",
    val password: String,
    val useHttps: Boolean = false,
    val apiVersion: String? = null,
    val additionalParams: Map<String, String> = emptyMap()
)

enum class MacFilterMode {
    DISABLED, ALLOW_LIST, BLOCK_LIST
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val error: RouterError) : Result<Nothing>()
}

data class RouterError(
    val code: Int,
    val message: String,
    val unsupportedFeatures: List<RouterFeature> = emptyList(),
    val cause: Throwable? = null
) {
    companion object {
        fun notSupported(feature: RouterFeature) = RouterError(
            code = -1,
            message = "Feature not supported by this router",
            unsupportedFeatures = listOf(feature)
        )

        fun connectionFailed(message: String) = RouterError(code = -2, message = message)
        fun authenticationFailed() = RouterError(code = 401, message = "Authentication failed")
        fun timeout() = RouterError(code = -3, message = "Connection timeout")
        fun unknown(message: String) = RouterError(code = -999, message = message)
    }
}