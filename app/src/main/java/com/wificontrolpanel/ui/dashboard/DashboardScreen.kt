package com.wificontrolpanel.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wificontrolpanel.R
import com.wificontrolpanel.data.model.FrequencyBand
import com.wificontrolpanel.data.model.NetworkStats
import com.wificontrolpanel.data.model.RouterInfo
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.LoadingOverlay
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.components.StatCard
import com.wificontrolpanel.ui.navigation.Screen
import com.wificontrolpanel.ui.theme.WifiControlPanelTheme
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun DashboardScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.networkStats.collectAsStateWithLifecycle()
    val routerInfo by viewModel.routerInfo.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    WifiControlPanelTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Connection Status Bar
                ConnectionStatusBar(connectionState = connectionState)

                // Stats Grid
                StatsGrid(stats = stats)

                // Quick Actions
                QuickActionsSection(
                    onNavigate = onNavigate,
                    stats = stats,
                    routerInfo = routerInfo,
                    onWifiToggle = { viewModel.toggleWifi(it) },
                    onInternetToggle = { viewModel.toggleInternet(it) },
                    onRestartRouter = { viewModel.restartRouter() },
                    onRefreshDevices = { viewModel.refreshDevices() },
                    onGuestWifiToggle = { viewModel.toggleGuestWifi(it) },
                    onBand24Toggle = { viewModel.toggleBand(FrequencyBand.BAND_2_4GHZ, it) },
                    onBand5Toggle = { viewModel.toggleBand(FrequencyBand.BAND_5GHZ, it) }
                )

                // Router Info Card
                routerInfo?.let {
                    RouterInfoCard(routerInfo = it)
                }
            }
                .padding(16.dp)

            if (isLoading) {
                LoadingOverlay("Refreshing...")
            }

            errorMessage?.let { msg ->
                ErrorSnackbar(message = msg)
            }
        }
    }
}

@Composable
fun ConnectionStatusBar(connectionState: com.wificontrolpanel.data.router.ConnectionState) {
    val (color, text, icon) = when (connectionState) {
        com.wificontrolpanel.data.router.ConnectionState.CONNECTED -> 
            Pair(Color.Green, "Connected") to CommonComponents.Icons.Default.CheckCircle
        com.wificontrolpanel.data.router.ConnectionState.CONNECTING,
        com.wificontrolpanel.data.router.ConnectionState.AUTHENTICATING -> 
            Pair(Color.Orange, "Connecting...") to CommonComponents.Icons.Default.Refresh
        com.wificontrolpanel.data.router.ConnectionState.AUTH_FAILED -> 
            Pair(Color.Red, "Authentication Failed") to CommonComponents.Icons.Default.Error
        com.wificontrolpanel.data.router.ConnectionState.CONNECTION_LOST -> 
            Pair(Color.Red, "Connection Lost") to CommonComponents.Icons.Default.SignalWifiOff
        com.wificontrolpanel.data.router.ConnectionState.ERROR -> 
            Pair(Color.Red, "Error") to CommonComponents.Icons.Default.Error
        else -> 
            Pair(Color.Gray, "Disconnected") to CommonComponents.Icons.Default.SignalWifiOff
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun StatsGrid(stats: NetworkStats) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Internet",
            value = if (stats.isInternetConnected) "Online" else "Offline",
            icon = if (stats.isInternetConnected) 
                CommonComponents.Icons.Default.SignalWifi4Bar 
            else 
                CommonComponents.Icons.Default.SignalWifiOff,
            color = if (stats.isInternetConnected) Color.Green else Color.Red,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Router",
            value = if (stats.isRouterOnline) "Online" else "Offline",
            icon = if (stats.isRouterOnline) 
                CommonComponents.Icons.Default.Router 
            else 
                CommonComponents.Icons.Default.Error,
            color = if (stats.isRouterOnline) Color.Blue else Color.Red,
            modifier = Modifier.weight(1f)
        )
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Connected",
            value = stats.totalConnectedDevices.toString(),
            icon = CommonComponents.Icons.Default.Devices,
            color = Color.Blue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Blocked",
            value = stats.totalBlockedDevices.toString(),
            icon = CommonComponents.Icons.Default.Block,
            color = Color.Red,
            modifier = Modifier.weight(1f)
        )
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Unknown",
            value = stats.unknownDevices.toString(),
            icon = CommonComponents.Icons.Default.Warning,
            color = Color.Orange,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Uptime",
            value = formatUptime(stats.uptime),
            icon = CommonComponents.Icons.Default.AccessTime,
            color = Color.Cyan,
            modifier = Modifier.weight(1f)
        )
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Download",
            value = stats.formattedDownloadSpeed,
            icon = CommonComponents.Icons.Default.CloudDownload,
            color = Color.Green,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Upload",
            value = stats.formattedUploadSpeed,
            icon = CommonComponents.Icons.Default.CloudUpload,
            color = Color.Blue,
            modifier = Modifier.weight(1f)
        )
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Total Usage",
            value = stats.formattedTotalUsage,
            icon = CommonComponents.Icons.Default.DataUsage,
            color = Color.Purple,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionsSection(
    onNavigate: (Screen) -> Unit,
    stats: NetworkStats,
    routerInfo: RouterInfo?,
    onWifiToggle: (Boolean) -> Unit,
    onInternetToggle: (Boolean) -> Unit,
    onRestartRouter: () -> Unit,
    onRefreshDevices: () -> Unit,
    onGuestWifiToggle: (Boolean) -> Unit,
    onBand24Toggle: (Boolean) -> Unit,
    onBand5Toggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = "Quick Controls")

            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = if (stats.isInternetConnected) "Wi-Fi ON" else "Wi-Fi OFF",
                    icon = if (stats.isInternetConnected) 
                        CommonComponents.Icons.Default.SignalWifi4Bar 
                    else 
                        CommonComponents.Icons.Default.SignalWifiOff,
                    color = if (stats.isInternetConnected) Color.Green else Color.Red,
                    onClick = { onWifiToggle(!stats.isInternetConnected) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Pause Internet",
                    icon = CommonComponents.Icons.Default.PauseCircle,
                    color = Color.Orange,
                    onClick = { onInternetToggle(true) },
                    modifier = Modifier.weight(1f)
                )
            }

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Refresh Devices",
                    icon = CommonComponents.Icons.Default.Refresh,
                    color = Color.Blue,
                    onClick = onRefreshDevices,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Restart Router",
                    icon = CommonComponents.Icons.Default.RestartAlt,
                    color = Color.Red,
                    onClick = onRestartRouter,
                    modifier = Modifier.weight(1f)
                )
            }

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Guest Wi-Fi",
                    icon = CommonComponents.Icons.Default.PersonAdd,
                    color = Color.Purple,
                    onClick = { onGuestWifiToggle(true) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "2.4 GHz",
                    icon = CommonComponents.Icons.Default.NetworkWifi,
                    color = Color.Cyan,
                    onClick = { onBand24Toggle(true) },
                    modifier = Modifier.weight(1f)
                )
            }

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "5 GHz",
                    icon = CommonComponents.Icons.Default.NetworkWifi,
                    color = Color.Cyan,
                    onClick = { onBand5Toggle(true) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Change Password",
                    icon = CommonComponents.Icons.Default.Lock,
                    color = Color.Blue,
                    onClick = { onNavigate(Screen.WifiSettings) },
                    modifier = Modifier.weight(1f)
                )
            }

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = "Change SSID",
                    icon = CommonComponents.Icons.Default.Edit,
                    color = Color.Blue,
                    onClick = { onNavigate(Screen.WifiSettings) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Speed Control",
                    icon = CommonComponents.Icons.Default.Speed,
                    color = Color.Green,
                    onClick = { onNavigate(Screen.SpeedManagement) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RouterInfoCard(routerInfo: RouterInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = "Router Information")

            InfoRow("Brand", routerInfo.brand)
            InfoRow("Model", routerInfo.model)
            InfoRow("Firmware", routerInfo.firmwareVersion)
            InfoRow("LAN IP", routerInfo.lanIp)
            InfoRow("WAN IP", routerInfo.wanIp)
            InfoRow("MAC Address", routerInfo.macAddress)
            InfoRow("Uptime", formatUptime(routerInfo.uptime))
            
            routerInfo.cpuUsage?.let { cpu ->
                InfoRow("CPU Usage", "${cpu}%")
            }
            routerInfo.memoryUsage?.let { mem ->
                InfoRow("Memory Usage", "${mem}%")
            }
            routerInfo.temperature?.let { temp ->
                InfoRow("Temperature", "${temp}°C")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ErrorSnackbar(message: String) {
    // TODO: Implement proper snackbar using Scaffold
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.BottomCenter)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = message, fontSize = 14.sp)
                IconButton(onClick = { /* dismiss */ }) {
                    Icon(
                        imageVector = CommonComponents.Icons.Default.Close,
                        contentDescription = "Dismiss"
                    )
                }
            }
        }
    }
}

fun formatUptime(millis: Long): String {
    val seconds = millis / 1000
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60
    
    return when {
        days > 0 -> "${days}d ${hours}h ${minutes}m"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}