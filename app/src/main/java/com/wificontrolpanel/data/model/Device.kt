package com.wificontrolpanel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wificontrolpanel.util.DateConverter
import java.io.Serializable
import java.util.*

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey
    val macAddress: String,
    val ipAddress: String,
    val name: String,
    val deviceType: DeviceType = DeviceType.UNKNOWN,
    val vendor: String? = null,
    var isOnline: Boolean = true,
    var isBlocked: Boolean = false,
    var isPaused: Boolean = false,
    var isTrusted: Boolean = false,
    var downloadSpeed: Long = 0,
    var uploadSpeed: Long = 0,
    var totalDownload: Long = 0,
    var totalUpload: Long = 0,
    var connectedTime: Long = 0,
    var lastSeen: Date = Date(),
    var downloadLimit: Long? = null,
    var uploadLimit: Long? = null,
    var priority: Priority = Priority.NORMAL,
    var customName: String? = null,
    @TypeConverters(DateConverter::class)
    var firstConnected: Date = Date(),
    var connectionType: ConnectionType = ConnectionType.WIFI,
    var frequencyBand: FrequencyBand = FrequencyBand.UNKNOWN
) : Serializable {
    val displayName: String
        get() = customName?.ifBlank { null } ?: name ?: "Unknown Device"

    val isManaged: Boolean
        get() = isBlocked || isPaused || downloadLimit != null || uploadLimit != null

    val totalDataUsage: Long
        get() = totalDownload + totalUpload

    val formattedDownloadSpeed: String
        get() = formatSpeed(downloadSpeed)

    val formattedUploadSpeed: String
        get() = formatSpeed(uploadSpeed)

    val formattedTotalUsage: String
        get() = formatDataSize(totalDataUsage)

    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond >= 1_000_000_000 -> String.format("%.1f Gbps", bytesPerSecond / 1_000_000_000.0)
            bytesPerSecond >= 1_000_000 -> String.format("%.1f Mbps", bytesPerSecond / 1_000_000.0)
            bytesPerSecond >= 1_000 -> String.format("%.1f Kbps", bytesPerSecond / 1_000.0)
            else -> "$bytesPerSecond bps"
        }
    }

    private fun formatDataSize(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000_000L -> String.format("%.2f TB", bytes / 1_000_000_000_000.0)
            bytes >= 1_000_000_000 -> String.format("%.2f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.2f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }
}

enum class DeviceType {
    PHONE, TABLET, LAPTOP, DESKTOP, TV, GAME_CONSOLE, IOT, PRINTER, NAS, ROUTER, UNKNOWN
}

enum class Priority {
    HIGH, NORMAL, LOW
}

enum class ConnectionType {
    WIFI, ETHERNET, GUEST, VPN
}

enum class FrequencyBand {
    BAND_2_4GHZ, BAND_5GHZ, BAND_6GHZ, UNKNOWN
}

enum class DeviceStatus {
    ONLINE, OFFLINE, BLOCKED, PAUSED, UNKNOWN
}

data class NetworkStats(
    val isInternetConnected: Boolean,
    val isRouterOnline: Boolean,
    val totalConnectedDevices: Int,
    val totalBlockedDevices: Int,
    val unknownDevices: Int,
    val currentDownloadSpeed: Long,
    val currentUploadSpeed: Long,
    val totalDataUsage: Long,
    val uptime: Long,
    val cpuUsage: Float? = null,
    val memoryUsage: Float? = null,
    val temperature: Float? = null
) {
    val formattedDownloadSpeed: String
        get() = formatSpeed(currentDownloadSpeed)

    val formattedUploadSpeed: String
        get() = formatSpeed(currentUploadSpeed)

    val formattedTotalUsage: String
        get() = formatDataSize(totalDataUsage)

    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond >= 1_000_000_000 -> String.format("%.1f Gbps", bytesPerSecond / 1_000_000_000.0)
            bytesPerSecond >= 1_000_000 -> String.format("%.1f Mbps", bytesPerSecond / 1_000_000.0)
            bytesPerSecond >= 1_000 -> String.format("%.1f Kbps", bytesPerSecond / 1_000.0)
            else -> "$bytesPerSecond bps"
        }
    }

    private fun formatDataSize(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000_000L -> String.format("%.2f TB", bytes / 1_000_000_000_000.0)
            bytes >= 1_000_000_000 -> String.format("%.2f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.2f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }
}

data class RouterInfo(
    val brand: String,
    val model: String,
    val firmwareVersion: String,
    val lanIp: String,
    val wanIp: String,
    val macAddress: String,
    val uptime: Long,
    val cpuUsage: Float? = null,
    val memoryUsage: Float? = null,
    val temperature: Float? = null,
    val supportedFeatures: Set<RouterFeature> = emptySet()
)

enum class RouterFeature {
    WIFI_ON_OFF,
    INTERNET_PAUSE_RESUME,
    DEVICE_BLOCK_UNBLOCK,
    SPEED_LIMIT_PER_DEVICE,
    SPEED_LIMIT_TOTAL,
    GUEST_WIFI,
    BAND_2_4GHZ_CONTROL,
    BAND_5GHZ_CONTROL,
    BAND_6GHZ_CONTROL,
    CHANGE_SSID,
    CHANGE_PASSWORD,
    MAC_FILTERING,
    PARENTAL_CONTROL,
    SCHEDULES,
    DEVICE_RENAME,
    TRUSTED_DEVICES,
    BANDWIDTH_MONITORING,
    TRAFFIC_STATS,
    ROUTER_RESTART,
    FIRMWARE_UPDATE,
    LOGS,
    SECURITY_LOGS,
    CHANNEL_SELECTION,
    TX_POWER_CONTROL,
    BEAMFORMING,
    MU_MIMO,
    OFDMA,
    WPA3,
    WPS,
    VPN_SERVER,
    VPN_CLIENT,
    DDNS,
    PORT_FORWARDING,
    DMZ,
    QOS,
    TRAFFIC_SHAPING,
    VLAN,
    MESH,
    BRIDGE_MODE,
    REPEATER_MODE,
    AP_MODE
}

data class WifiSettings(
    val ssid2_4: String,
    val ssid5: String,
    val ssid6: String?,
    val password: String,
    val guestSsid: String?,
    val guestPassword: String?,
    val isWifiEnabled: Boolean,
    val isGuestEnabled: Boolean,
    val isBand2_4Enabled: Boolean,
    val isBand5Enabled: Boolean,
    val isBand6Enabled: Boolean,
    val isSsidHidden: Boolean,
    val securityMode: SecurityMode,
    val channel2_4: Int,
    val channel5: Int,
    val channel6: Int?,
    val maxUsers: Int,
    val txPower: TxPower
)

enum class SecurityMode {
    OPEN, WEP, WPA_PSK, WPA2_PSK, WPA3_PSK, WPA2_WPA3_MIXED, WPA_ENTERPRISE
}

enum class TxPower {
    LOW, MEDIUM, HIGH, AUTO
}

data class SpeedLimitConfig(
    val totalDownloadLimit: Long? = null,
    val totalUploadLimit: Long? = null,
    val isUnlimited: Boolean = true,
    val distributionMode: DistributionMode = DistributionMode.EQUAL,
    val defaultDownloadLimit: Long? = null,
    val defaultUploadLimit: Long? = null
)

enum class DistributionMode {
    EQUAL, CUSTOM, PRIORITY_BASED
}

data class MacAddressEntry(
    val macAddress: String,
    val name: String?,
    val deviceType: DeviceType,
    val isBlocked: Boolean,
    val isTrusted: Boolean,
    val dateAdded: Date = Date(),
    val addedBy: String = "admin"
)

data class ParentalControlRule(
    val deviceMac: String,
    val deviceName: String,
    val schedules: List<TimeSchedule>,
    val bedtimeSchedule: TimeSchedule?,
    val dailyLimitMinutes: Int,
    val isEnabled: Boolean,
    val isPaused: Boolean
)

data class TimeSchedule(
    val days: Set<DayOfWeek>,
    val startTime: String, // HH:mm
    val endTime: String,   // HH:mm
    val isInternetAllowed: Boolean
)

data class SecurityEvent(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Date = Date(),
    val type: SecurityEventType,
    val deviceMac: String?,
    val deviceName: String?,
    val description: String,
    val severity: SecuritySeverity
)

enum class SecurityEventType {
    UNKNOWN_DEVICE_DETECTED,
    DEVICE_BLOCKED,
    DEVICE_UNBLOCKED,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    PASSWORD_CHANGED,
    SETTINGS_CHANGED,
    FIRMWARE_UPDATE,
    ROUTER_RESTART,
    MAC_SPOOFING_ATTEMPT,
    BRUTE_FORCE_ATTEMPT,
    UNAUTHORIZED_ACCESS
}

enum class SecuritySeverity {
    INFO, WARNING, CRITICAL
}

data class UsageReport(
    val period: ReportPeriod,
    val startDate: Date,
    val endDate: Date,
    val totalDownload: Long,
    val totalUpload: Long,
    val deviceUsage: Map<String, DeviceUsage>,
    val peakDownloadSpeed: Long,
    val peakUploadSpeed: Long,
    val averageDownloadSpeed: Long,
    val averageUploadSpeed: Long,
    val mostActiveDevice: String?,
    val blockEvents: Int,
    val restartEvents: Int
)

enum class ReportPeriod {
    DAILY, WEEKLY, MONTHLY, CUSTOM
}

data class DeviceUsage(
    val deviceMac: String,
    val deviceName: String,
    val download: Long,
    val upload: Long,
    val onlineTime: Long,
    val peakDownload: Long,
    val peakUpload: Long
)

data class SpeedLimitResult(
    val success: Boolean,
    val message: String,
    val unsupportedFeatures: List<RouterFeature> = emptyList()
)

data class RouterOperationResult(
    val success: Boolean,
    val message: String,
    val unsupportedFeatures: List<RouterFeature> = emptyList()
)