package com.wificontrolpanel.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.wificontrolpanel.data.model.SecurityEvent
import com.wificontrolpanel.data.model.SecuritySeverity
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun SecurityScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()
    val loginHistory by viewModel.loginHistory.collectAsStateWithLifecycle()
    val blockHistory by viewModel.blockHistory.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val unknownDeviceAlerts by viewModel.unknownDeviceAlerts.collectAsStateWithLifecycle()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SectionHeader(title = "Security Center")

                // Unknown Device Alerts
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionHeader(title = "Unknown Device Alerts")
                            Switch(
                                checked = unknownDeviceAlerts,
                                onCheckedChange = { viewModel.setUnknownDeviceAlerts(it) },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.Orange,
                                    checkedTrackColor = Color.Orange.copy(alpha = 0.3f)
                                )
                            )
                        }
                        Text(
                            text = "Get notified when new unknown devices connect to your network",
                            fontSize = 13.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Change Admin Password
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Change Admin Password")

                        androidx.compose.material3.TextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Current Password") },
                            singleLine = true,
                            visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                        )

                        androidx.compose.material3.TextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("New Password") },
                            singleLine = true,
                            visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                        )

                        androidx.compose.material3.TextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("Confirm New Password") },
                            singleLine = true,
                            visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                        )

                        ActionButton(
                            text = "Change Password",
                            icon = CommonComponents.Icons.Default.Lock,
                            color = Color.Blue,
                            onClick = {
                                if (newPassword == confirmPassword && newPassword.length >= 8) {
                                    viewModel.changeAdminPassword(oldPassword, newPassword)
                                    oldPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }

                // Security Logs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Security Logs",
                            action = { /* show all logs */ },
                            actionText = "View All",
                            actionIcon = CommonComponents.Icons.Default.Visibility
                        )

                        if (securityLogs.isEmpty()) {
                            EmptyState(
                                icon = CommonComponents.Icons.Default.Security,
                                title = "No security events",
                                message = "Security events will appear here"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(securityLogs.take(10)) { event ->
                                    SecurityEventRow(event = event)
                                }
                            }
                        }
                    }
                }

                // Login History
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Login History",
                            action = { /* show all */ },
                            actionText = "View All",
                            actionIcon = CommonComponents.Icons.Default.Visibility
                        )

                        if (loginHistory.isEmpty()) {
                            EmptyState(
                                icon = CommonComponents.Icons.Default.Login,
                                title = "No login history",
                                message = "Admin login attempts will appear here"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(loginHistory.take(10)) { event ->
                                    SecurityEventRow(event = event)
                                }
                            }
                        }
                    }
                }

                // Block History
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Block/Unblock History",
                            action = { /* show all */ },
                            actionText = "View All",
                            actionIcon = CommonComponents.Icons.Default.Visibility
                        )

                        if (blockHistory.isEmpty()) {
                            EmptyState(
                                icon = CommonComponents.Icons.Default.Block,
                                title = "No block history",
                                message = "Device block/unblock events will appear here"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(blockHistory.take(10)) { event ->
                                    SecurityEventRow(event = event)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityEventRow(event: SecurityEvent) {
    val severityColor = when (event.severity) {
        SecuritySeverity.INFO -> Color.Blue
        SecuritySeverity.WARNING -> Color.Orange
        SecuritySeverity.CRITICAL -> Color.Red
    }

    val icon = when (event.type) {
        com.wificontrolpanel.data.model.SecurityEventType.UNKNOWN_DEVICE_DETECTED -> CommonComponents.Icons.Default.Warning
        com.wificontrolpanel.data.model.SecurityEventType.DEVICE_BLOCKED -> CommonComponents.Icons.Default.Block
        com.wificontrolpanel.data.model.SecurityEventType.DEVICE_UNBLOCKED -> CommonComponents.Icons.Default.LockOpen
        com.wificontrolpanel.data.model.SecurityEventType.LOGIN_SUCCESS -> CommonComponents.Icons.Default.Login
        com.wificontrolpanel.data.model.SecurityEventType.LOGIN_FAILED -> CommonComponents.Icons.Default.Error
        com.wificontrolpanel.data.model.SecurityEventType.PASSWORD_CHANGED -> CommonComponents.Icons.Default.Lock
        com.wificontrolpanel.data.model.SecurityEventType.ROUTER_RESTART -> CommonComponents.Icons.Default.RestartAlt
        else -> CommonComponents.Icons.Default.Security
    }

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
                    tint = severityColor,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = event.description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${event.deviceName ?: event.deviceMac ?: "System"} • ${formatTimestamp(event.timestamp)}",
                        fontSize = 11.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            androidx.compose.material3.Badge(
                backgroundColor = severityColor.copy(alpha = 0.2f),
                badgeContent = {
                    Text(
                        text = event.severity.name,
                        fontSize = 10.sp,
                        color = severityColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            ) {
                androidx.compose.foundation.layout.Box()
            }
        }
    }
}

fun formatTimestamp(date: java.util.Date): String {
    val now = java.util.Date()
    val diff = now.time - date.time
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> java.text.SimpleDateFormat("MMM dd, yyyy").format(date)
    }
}

class SecurityViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _securityLogs = androidx.compose.runtime.MutableStateFlow<List<SecurityEvent>>(emptyList())
    val securityLogs = _securityLogs.asStateFlow()

    private val _loginHistory = androidx.compose.runtime.MutableStateFlow<List<SecurityEvent>>(emptyList())
    val loginHistory = _loginHistory.asStateFlow()

    private val _blockHistory = androidx.compose.runtime.MutableStateFlow<List<SecurityEvent>>(emptyList())
    val blockHistory = _blockHistory.asStateFlow()

    private val _unknownDeviceAlerts = androidx.compose.runtime.MutableStateFlow(true)
    val unknownDeviceAlerts = _unknownDeviceAlerts.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getSecurityLogs(50).onSuccess { _securityLogs.value = it }
            repository.getLoginHistory(50).onSuccess { _loginHistory.value = it }
            repository.getBlockHistory(50).onSuccess { _blockHistory.value = it }
            _isLoading.value = false
        }
    }

    fun setUnknownDeviceAlerts(enabled: Boolean) {
        _unknownDeviceAlerts.value = enabled
        androidx.lifecycle.viewModelScope.launch {
            repository.enableUnknownDeviceAlerts(enabled)
        }
    }

    fun changeAdminPassword(oldPassword: String, newPassword: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.changeAdminPassword(oldPassword, newPassword)
        }
    }

    private fun com.wificontrolpanel.data.router.Result<List<SecurityEvent>>.onSuccess(action: (List<SecurityEvent>) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}