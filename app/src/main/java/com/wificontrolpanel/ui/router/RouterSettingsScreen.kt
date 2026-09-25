package com.wificontrolpanel.ui.router

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wificontrolpanel.data.model.RouterInfo
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.LoadingOverlay
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun RouterSettingsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: RouterSettingsViewModel = hiltViewModel()
) {
    val routerInfo by viewModel.routerInfo.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SectionHeader(title = "Router Settings")

                    // Restart Router
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionHeader(title = "Router Control")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ActionButton(
                                    text = "Restart Router",
                                    icon = CommonComponents.Icons.Default.RestartAlt,
                                    color = Color.Red,
                                    onClick = { viewModel.restartRouter() },
                                    modifier = Modifier.weight(1f)
                                )
                                ActionButton(
                                    text = "Factory Reset",
                                    icon = CommonComponents.Icons.Default.FactoryReset,
                                    color = Color.Red.copy(alpha = 0.7f),
                                    onClick = { viewModel.factoryReset() },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ActionButton(
                                    text = "Update Firmware",
                                    icon = CommonComponents.Icons.Default.SystemUpdate,
                                    color = Color.Blue,
                                    onClick = { viewModel.updateFirmware() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    routerInfo?.let { info ->
                        // Router Information
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionHeader(title = "Router Information")

                                InfoRow("Brand", info.brand)
                                InfoRow("Model", info.model)
                                InfoRow("Firmware Version", info.firmwareVersion)
                                InfoRow("LAN IP", info.lanIp)
                                InfoRow("WAN IP", info.wanIp)
                                InfoRow("MAC Address", info.macAddress)
                                InfoRow("Uptime", formatUptime(info.uptime))
                                
                                info.cpuUsage?.let { cpu ->
                                    InfoRow("CPU Usage", "${cpu}%")
                                }
                                info.memoryUsage?.let { mem ->
                                    InfoRow("Memory Usage", "${mem}%")
                                }
                                info.temperature?.let { temp ->
                                    InfoRow("Temperature", "${temp}°C")
                                }
                            }
                        }

                        // Supported Features
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionHeader(title = "Supported Features")

                                androidx.compose.foundation.layout.Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    info.supportedFeatures.forEach { feature ->
                                        androidx.compose.material3.Chip(
                                            onClick = { /* feature info */ },
                                            modifier = Modifier.padding(4.dp),
                                            colors = androidx.compose.material3.ChipDefaults.colors(
                                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Text(
                                                text = feature.name.replace("_", " "),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } ?: run {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(32.dp)) {
                                com.wificontrolpanel.ui.components.EmptyState(
                                    icon = CommonComponents.Icons.Default.Router,
                                    title = "Connect to router",
                                    message = "Connect to your router to view information and controls"
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    LoadingOverlay("Loading router info...")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
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
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    )
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

class RouterSettingsViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _routerInfo = androidx.compose.runtime.MutableStateFlow<RouterInfo?>(null)
    val routerInfo = _routerInfo.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadRouterInfo()
    }

    fun loadRouterInfo() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getRouterInfo().onSuccess { _routerInfo.value = it }
            _isLoading.value = false
        }
    }

    fun restartRouter() {
        androidx.lifecycle.viewModelScope.launch {
            repository.restartRouter()
        }
    }

    fun factoryReset() {
        androidx.lifecycle.viewModelScope.launch {
            repository.factoryReset()
        }
    }

    fun updateFirmware() {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateFirmware()
        }
    }

    private fun com.wificontrolpanel.data.router.Result<RouterInfo>.onSuccess(action: (RouterInfo) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}