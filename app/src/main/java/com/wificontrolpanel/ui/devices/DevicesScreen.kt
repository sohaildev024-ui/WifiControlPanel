package com.wificontrolpanel.ui.devices

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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wificontrolpanel.data.model.Device
import com.wificontrolpanel.data.model.Priority
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.LoadingOverlay
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun DevicesScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: DevicesViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    var showBlockedOnly by remember { mutableStateOf(false) }

    val filteredDevices = devices.filter { device ->
        (searchText.isBlank() || device.displayName.lowercase().contains(searchText.lowercase()) 
            || device.macAddress.lowercase().contains(searchText.lowercase())
            || device.ipAddress.contains(searchText))
            && (!showBlockedOnly || device.isBlocked)
    }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with search
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SectionHeader(
                        title = "Connected Devices",
                        subtitle = "${devices.size} devices (${devices.count { it.isOnline }} online, ${devices.count { it.isBlocked }} blocked)"
                    )

                    // Search and filter row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            placeholder = { Text("Search devices...") },
                            leadingIcon = { Icon(CommonComponents.Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )
                        androidx.compose.material3.IconButton(onClick = { showBlockedOnly = !showBlockedOnly }) {
                            Icon(
                                imageVector = CommonComponents.Icons.Default.FilterList,
                                contentDescription = "Filter blocked",
                                tint = if (showBlockedOnly) Color.Blue else Color.Gray
                            )
                        }
                    }
                }

                // Device List
                if (isLoading && devices.isEmpty()) {
                    LoadingOverlay("Loading devices...")
                } else if (filteredDevices.isEmpty()) {
                    EmptyState(
                        icon = CommonComponents.Icons.Default.Devices,
                        title = if (searchText.isNotBlank()) "No devices found" else "No connected devices",
                        message = if (searchText.isNotBlank()) 
                            "Try a different search term" 
                        else 
                            "Connect devices to your Wi-Fi network to see them here"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredDevices) { device ->
                            DeviceCard(
                                device = device,
                                onClick = { viewModel.selectDevice(device) },
                                onBlock = { viewModel.blockDevice(device.macAddress) { success, msg -> 
                                    // Show result
                                }},
                                onUnblock = { viewModel.unblockDevice(device.macAddress) { success, msg -> 
                                    // Show result
                                }},
                                onPause = { viewModel.pauseDeviceInternet(device.macAddress) { success, msg -> 
                                    // Show result
                                }},
                                onResume = { viewModel.resumeDeviceInternet(device.macAddress) { success, msg -> 
                                    // Show result
                                }},
                                onRename = { newName -> viewModel.renameDevice(device.macAddress, newName) { success, msg -> 
                                    // Show result
                                }},
                                onTrusted = { trusted -> viewModel.setDeviceTrusted(device.macAddress, trusted) { success, msg -> 
                                    // Show result
                                }},
                                onSetDownloadLimit = { limit -> viewModel.setDeviceDownloadLimit(device.macAddress, limit) { success, msg -> 
                                    // Show result
                                }},
                                onSetUploadLimit = { limit -> viewModel.setDeviceUploadLimit(device.macAddress, limit) { success, msg -> 
                                    // Show result
                                }},
                                onSetPriority = { priority -> viewModel.setDevicePriority(device.macAddress, priority) { success, msg -> 
                                    // Show result
                                }}
                            )
                        }
                    }
                }
            }

            if (isLoading && devices.isNotEmpty()) {
                LoadingOverlay("Refreshing...")
            }

            errorMessage?.let { msg ->
                ErrorSnackbar(message = msg)
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRename: (String) -> Unit,
    onTrusted: (Boolean) -> Unit,
    onSetDownloadLimit: (Long?) -> Unit,
    onSetUploadLimit: (Long?) -> Unit,
    onSetPriority: (Priority) -> Unit
) {
    val statusColor = when {
        device.isBlocked -> Color.Red
        device.isPaused -> Color.Orange
        device.isOnline -> Color.Green
        else -> Color.Gray
    }

    val statusText = when {
        device.isBlocked -> "Blocked"
        device.isPaused -> "Paused"
        device.isOnline -> "Online"
        else -> "Offline"
    }

    val statusIcon = when {
        device.isBlocked -> CommonComponents.Icons.Default.Block
        device.isPaused -> CommonComponents.Icons.Default.PauseCircle
        device.isOnline -> CommonComponents.Icons.Default.CheckCircle
        else -> CommonComponents.Icons.Default.DeviceUnknown
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getDeviceIcon(device.deviceType),
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = device.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${device.ipAddress} • ${device.macAddress}",
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                    if (device.isTrusted) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = CommonComponents.Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(
                    icon = CommonComponents.Icons.Default.CloudDownload,
                    label = "Down",
                    value = device.formattedDownloadSpeed
                )
                StatItem(
                    icon = CommonComponents.Icons.Default.CloudUpload,
                    label = "Up",
                    value = device.formattedUploadSpeed
                )
                StatItem(
                    icon = CommonComponents.Icons.Default.DataUsage,
                    label = "Total",
                    value = device.formattedTotalUsage
                )
                StatItem(
                    icon = CommonComponents.Icons.Default.AccessTime,
                    label = "Time",
                    value = formatDuration(device.connectedTime)
                )
            }

            // Actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (device.isBlocked) {
                    ActionButton(
                        text = "Unblock",
                        icon = CommonComponents.Icons.Default.LockOpen,
                        color = Color.Green,
                        onClick = onUnblock,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    ActionButton(
                        text = "Block",
                        icon = CommonComponents.Icons.Default.Block,
                        color = Color.Red,
                        onClick = onBlock,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (device.isPaused) {
                    ActionButton(
                        text = "Resume",
                        icon = CommonComponents.Icons.Default.PlayCircle,
                        color = Color.Green,
                        onClick = onResume,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    ActionButton(
                        text = "Pause",
                        icon = CommonComponents.Icons.Default.PauseCircle,
                        color = Color.Orange,
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    )
                }

                ActionButton(
                    text = "Speed",
                    icon = CommonComponents.Icons.Default.Speed,
                    color = Color.Blue,
                    onClick = { /* show speed dialog */ },
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    text = "More",
                    icon = CommonComponents.Icons.Default.MoreVert,
                    color = Color.Gray,
                    onClick = { /* show more options */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorSnackbar(message: String) {
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

fun getDeviceIcon(deviceType: com.wificontrolpanel.data.model.DeviceType): ImageVector {
    return when (deviceType) {
        com.wificontrolpanel.data.model.DeviceType.PHONE -> CommonComponents.Icons.Default.Phonelink
        com.wificontrolpanel.data.model.DeviceType.TABLET -> CommonComponents.Icons.Default.TabletAndroid
        com.wificontrolpanel.data.model.DeviceType.LAPTOP -> CommonComponents.Icons.Default.Laptop
        com.wificontrolpanel.data.model.DeviceType.DESKTOP -> CommonComponents.Icons.Default.Computer
        com.wificontrolpanel.data.model.DeviceType.TV -> CommonComponents.Icons.Default.Tv
        com.wificontrolpanel.data.model.DeviceType.GAME_CONSOLE -> CommonComponents.Icons.Default.VideogameAsset
        com.wificontrolpanel.data.model.DeviceType.PRINTER -> CommonComponents.Icons.Default.Print
        com.wificontrolpanel.data.model.DeviceType.NAS -> CommonComponents.Icons.Default.Storage
        com.wificontrolpanel.data.model.DeviceType.ROUTER -> CommonComponents.Icons.Default.Router
        com.wificontrolpanel.data.model.DeviceType.IOT -> CommonComponents.Icons.Default.DeveloperBoard
        else -> CommonComponents.Icons.Default.DevicesOther
    }
}

fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60
    
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}