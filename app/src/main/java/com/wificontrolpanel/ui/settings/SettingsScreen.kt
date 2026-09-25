package com.wificontrolpanel.ui.settings

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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wificontrolpanel.data.adapter.RouterAdapterFactory
import com.wificontrolpanel.data.router.RouterBrand
import com.wificontrolpanel.data.router.RouterConnectionConfig
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun SettingsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val connectionConfig by viewModel.connectionConfig.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val routerBrand by viewModel.routerBrand.collectAsStateWithLifecycle()

    var selectedBrand by remember { mutableStateOf(routerBrand ?: RouterBrand.HUAWEI) }
    var host by remember { mutableStateOf(connectionConfig?.host ?: "192.168.1.1") }
    var username by remember { mutableStateOf(connectionConfig?.username ?: "admin") }
    var password by remember { mutableStateOf("") }
    var port by remember { mutableStateOf(connectionConfig?.port ?: 80) }
    var useHttps by remember { mutableStateOf(connectionConfig?.useHttps ?: false) }

    var darkMode by remember { mutableStateOf(false) }
    var autoRefresh by remember { mutableStateOf(true) }
    var refreshInterval by remember { mutableStateOf(30) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Router Connection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Router Connection")

                    if (isConnected) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Connected", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Green)
                                Text(
                                    text = "${RouterAdapterFactory.getBrandDisplayName(selectedBrand)} • $host",
                                    fontSize = 12.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            ActionButton(
                                text = "Disconnect",
                                icon = CommonComponents.Icons.Default.LinkOff,
                                color = Color.Red,
                                onClick = { viewModel.disconnect() }
                            )
                        }
                    } else {
                        // Brand Selector
                        androidx.compose.material3.Text(
                            text = "Router Brand",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        
                        androidx.compose.material3.DropdownMenu(
                            expanded = true,
                            onDismissRequest = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Brand dropdown would go here
                        }

                        androidx.compose.material3.TextField(
                            value = host,
                            onValueChange = { host = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("Router IP Address") },
                            singleLine = true
                        )

                        androidx.compose.material3.TextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("Username") },
                            singleLine = true
                        )

                        androidx.compose.material3.TextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            androidx.compose.material3.TextField(
                                value = port.toString(),
                                onValueChange = { port = it.toIntOrNull() ?: 80 },
                                modifier = Modifier.weight(1f),
                                label = { Text("Port") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "HTTPS", fontSize = 14.sp)
                                Switch(
                                    checked = useHttps,
                                    onCheckedChange = { useHttps = it }
                                )
                            }
                        }

                        ActionButton(
                            text = "Connect to Router",
                            icon = CommonComponents.Icons.Default.Link,
                            color = Color.Green,
                            onClick = {
                                val config = RouterConnectionConfig(
                                    brand = selectedBrand,
                                    host = host,
                                    port = port,
                                    username = username,
                                    password = password,
                                    useHttps = useHttps
                                )
                                viewModel.connect(config)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }
            }

            // App Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "App Settings")

                    SettingRow(
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        icon = CommonComponents.Icons.Default.DarkMode,
                        trailing = {
                            Switch(
                                checked = darkMode,
                                onCheckedChange = { darkMode = it }
                            )
                        }
                    )

                    SettingRow(
                        title = "Auto Refresh",
                        subtitle = "Automatically refresh data",
                        icon = CommonComponents.Icons.Default.Refresh,
                        trailing = {
                            Switch(
                                checked = autoRefresh,
                                onCheckedChange = { autoRefresh = it }
                            )
                        }
                    )

                    SettingRow(
                        title = "Refresh Interval",
                        subtitle = "${refreshInterval} seconds",
                        icon = CommonComponents.Icons.Default.Timer,
                        trailing = {
                            androidx.compose.material3.Text(text = "${refreshInterval}s", fontSize = 14.sp, color = Color.Gray)
                        }
                    )

                    SettingRow(
                        title = "Notifications",
                        subtitle = "Enable push notifications",
                        icon = CommonComponents.Icons.Default.Notifications,
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                    )
                }
            }

            // About
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "About")

                    SettingRow(
                        title = "App Version",
                        subtitle = "1.0.0",
                        icon = CommonComponents.Icons.Default.Info
                    )

                    SettingRow(
                        title = "Build Date",
                        subtitle = "2026",
                        icon = CommonComponents.Icons.Default.Calendar
                    )

                    ActionButton(
                        text = "Check for Updates",
                        icon = CommonComponents.Icons.Default.SystemUpdate,
                        color = Color.Blue,
                        onClick = { /* check updates */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            trailing()
        }
    }
}

class SettingsViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _connectionConfig = androidx.compose.runtime.MutableStateFlow<RouterConnectionConfig?>(null)
    val connectionConfig = _connectionConfig.asStateFlow()

    private val _isConnected = androidx.compose.runtime.MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _routerBrand = androidx.compose.runtime.MutableStateFlow<RouterBrand?>(null)
    val routerBrand = _routerBrand.asStateFlow()

    fun connect(config: RouterConnectionConfig) {
        androidx.lifecycle.viewModelScope.launch {
            val result = repository.connect(config)
            if (result.success) {
                _connectionConfig.value = config
                _isConnected.value = true
                _routerBrand.value = config.brand
            }
        }
    }

    fun disconnect() {
        androidx.lifecycle.viewModelScope.launch {
            repository.disconnect()
            _isConnected.value = false
        }
    }
}