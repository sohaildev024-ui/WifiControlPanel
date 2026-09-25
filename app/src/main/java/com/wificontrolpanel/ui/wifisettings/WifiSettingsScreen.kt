package com.wificontrolpanel.ui.wifisettings

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
import com.wificontrolpanel.data.model.FrequencyBand
import com.wificontrolpanel.data.model.SecurityMode
import com.wificontrolpanel.data.model.TxPower
import com.wificontrolpanel.data.model.WifiSettings
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.LoadingOverlay
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun WifiSettingsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: WifiSettingsViewModel = hiltViewModel()
) {
    val wifiSettings by viewModel.wifiSettings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var ssid24 by remember { mutableStateOf(wifiSettings?.ssid2_4 ?: "") }
    var ssid5 by remember { mutableStateOf(wifiSettings?.ssid5 ?: "") }
    var password by remember { mutableStateOf(wifiSettings?.password ?: "") }
    var guestSsid by remember { mutableStateOf(wifiSettings?.guestSsid ?: "") }
    var guestPassword by remember { mutableStateOf(wifiSettings?.guestPassword ?: "") }
    var showPassword by remember { mutableStateOf(false) }
    var isWifiEnabled by remember { mutableStateOf(wifiSettings?.isWifiEnabled ?: true) }
    var isGuestEnabled by remember { mutableStateOf(wifiSettings?.isGuestEnabled ?: false) }
    var isBand24Enabled by remember { mutableStateOf(wifiSettings?.isBand2_4Enabled ?: true) }
    var isBand5Enabled by remember { mutableStateOf(wifiSettings?.isBand5Enabled ?: true) }
    var isSsidHidden by remember { mutableStateOf(wifiSettings?.isSsidHidden ?: false) }
    var securityMode by remember { mutableStateOf(wifiSettings?.securityMode ?: SecurityMode.WPA2_PSK) }
    var channel24 by remember { mutableStateOf(wifiSettings?.channel2_4 ?: 1) }
    var channel5 by remember { mutableStateOf(wifiSettings?.channel5 ?: 36) }
    var maxUsers by remember { mutableStateOf(wifiSettings?.maxUsers ?: 32) }
    var txPower by remember { mutableStateOf(wifiSettings?.txPower ?: TxPower.AUTO) }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionHeader(title = "Wi-Fi Settings")

                // Main Wi-Fi Toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Main Wi-Fi")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Wi-Fi Status", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (isWifiEnabled) "Enabled" else "Disabled",
                                    fontSize = 12.sp,
                                    color = if (isWifiEnabled) Color.Green else Color.Red
                                )
                            }
                            Switch(
                                checked = isWifiEnabled,
                                onCheckedChange = { 
                                    isWifiEnabled = it
                                    viewModel.setWifiEnabled(it)
                                },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.Green,
                                    checkedTrackColor = Color.Green.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                // Band Controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Frequency Bands")

                        BandToggle(
                            title = "2.4 GHz Band",
                            subtitle = "Better range, compatible with all devices",
                            isEnabled = isBand24Enabled,
                            onChange = { isBand24Enabled = it; viewModel.setBandEnabled(FrequencyBand.BAND_2_4GHZ, it) },
                            icon = CommonComponents.Icons.Default.NetworkWifi
                        )

                        BandToggle(
                            title = "5 GHz Band",
                            subtitle = "Faster speeds, shorter range",
                            isEnabled = isBand5Enabled,
                            onChange = { isBand5Enabled = it; viewModel.setBandEnabled(FrequencyBand.BAND_5GHZ, it) },
                            icon = CommonComponents.Icons.Default.NetworkWifi
                        )
                    }
                }

                // SSID & Password
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Network Name & Password")

                        androidx.compose.material3.TextField(
                            value = ssid24,
                            onValueChange = { ssid24 = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("2.4 GHz SSID") },
                            singleLine = true
                        )

                        androidx.compose.material3.TextField(
                            value = ssid5,
                            onValueChange = { ssid5 = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("5 GHz SSID") },
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.TextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Password") },
                                singleLine = true,
                                visualTransformation = if (showPassword) 
                                    androidx.compose.foundation.text.CoreTextFieldDefaults.NoVisualTransformation 
                                else 
                                    androidx.compose.material3.PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            imageVector = if (showPassword) 
                                                CommonComponents.Icons.Default.VisibilityOff 
                                            else 
                                                CommonComponents.Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                            ActionButton(
                                text = "Change SSID",
                                icon = CommonComponents.Icons.Default.Edit,
                                color = Color.Blue,
                                onClick = { viewModel.changeSsid(FrequencyBand.BAND_2_4GHZ, ssid24) },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Change Password",
                                icon = CommonComponents.Icons.Default.Lock,
                                color = Color.Blue,
                                onClick = { viewModel.changePassword(password) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Security Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Security & Advanced")

                        androidx.compose.material3.DropdownMenu(
                            expanded = true,
                            onDismissRequest = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // This would be a dropdown for security mode
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            androidx.compose.material3.TextField(
                                value = channel24.toString(),
                                onValueChange = { channel24 = it.toIntOrNull() ?: 1 },
                                modifier = Modifier.weight(1f),
                                label = { Text("2.4 GHz Channel") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )
                            androidx.compose.material3.TextField(
                                value = channel5.toString(),
                                onValueChange = { channel5 = it.toIntOrNull() ?: 36 },
                                modifier = Modifier.weight(1f),
                                label = { Text("5 GHz Channel") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Hide SSID", fontSize = 14.sp)
                            Switch(
                                checked = isSsidHidden,
                                onCheckedChange = { isSsidHidden = it; viewModel.setSsidHidden(it) }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Max Users", fontSize = 14.sp)
                            androidx.compose.material3.TextField(
                                value = maxUsers.toString(),
                                onValueChange = { maxUsers = it.toIntOrNull() ?: 32 },
                                modifier = Modifier.width(80.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )
                        }
                    }
                }

                // Guest Wi-Fi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Guest Wi-Fi")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Guest Network", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (isGuestEnabled) "Enabled" else "Disabled",
                                    fontSize = 12.sp,
                                    color = if (isGuestEnabled) Color.Green else Color.Red
                                )
                            }
                            Switch(
                                checked = isGuestEnabled,
                                onCheckedChange = { 
                                    isGuestEnabled = it
                                    viewModel.setGuestWifiEnabled(it)
                                }
                            )
                        }

                        if (isGuestEnabled) {
                            androidx.compose.material3.TextField(
                                value = guestSsid,
                                onValueChange = { guestSsid = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                label = { Text("Guest SSID") },
                                singleLine = true
                            )

                            androidx.compose.material3.TextField(
                                value = guestPassword,
                                onValueChange = { guestPassword = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                label = { Text("Guest Password") },
                                singleLine = true,
                                visualTransformation = androidx.compose.material3.PasswordVisualTransformation()
                            )
                        }
                    }
                }

                // Apply Button
                ActionButton(
                    text = "Apply All Settings",
                    icon = CommonComponents.Icons.Default.CheckCircle,
                    color = Color.Green,
                    onClick = {
                        val settings = WifiSettings(
                            ssid2_4 = ssid24,
                            ssid5 = ssid5,
                            ssid6 = null,
                            password = password,
                            guestSsid = guestSsid.ifBlank { null },
                            guestPassword = guestPassword.ifBlank { null },
                            isWifiEnabled = isWifiEnabled,
                            isGuestEnabled = isGuestEnabled,
                            isBand2_4Enabled = isBand24Enabled,
                            isBand5Enabled = isBand5Enabled,
                            isBand6Enabled = false,
                            isSsidHidden = isSsidHidden,
                            securityMode = securityMode,
                            channel2_4 = channel24,
                            channel5 = channel5,
                            channel6 = null,
                            maxUsers = maxUsers,
                            txPower = txPower
                        )
                        viewModel.applySettings(settings)
                    }
                )
            }

            if (isLoading) {
                LoadingOverlay("Loading Wi-Fi settings...")
            }
        }
    }
}

@Composable
fun BandToggle(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onChange(!isEnabled) },
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) 
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
            else 
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
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
                    tint = if (isEnabled) Color.Green else Color.Gray,
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
            Switch(
                checked = isEnabled,
                onCheckedChange = onChange,
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.Green,
                    checkedTrackColor = Color.Green.copy(alpha = 0.3f)
                )
            )
        }
    }
}

class WifiSettingsViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _wifiSettings = androidx.compose.runtime.MutableStateFlow<WifiSettings?>(null)
    val wifiSettings = _wifiSettings.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadWifiSettings()
    }

    fun loadWifiSettings() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getWifiSettings().onSuccess { _wifiSettings.value = it }
            _isLoading.value = false
        }
    }

    fun setWifiEnabled(enabled: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setWifiEnabled(enabled)
        }
    }

    fun setBandEnabled(band: FrequencyBand, enabled: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setBandEnabled(band, enabled)
        }
    }

    fun setGuestWifiEnabled(enabled: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setGuestWifiEnabled(enabled)
        }
    }

    fun changeSsid(band: FrequencyBand, newSsid: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.changeSsid(band, newSsid)
        }
    }

    fun changePassword(newPassword: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.changePassword(newPassword)
        }
    }

    fun setSsidHidden(hidden: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setSsidHidden(hidden)
        }
    }

    fun applySettings(settings: WifiSettings) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setWifiSettings(settings)
            loadWifiSettings()
        }
    }

    private fun com.wificontrolpanel.data.router.Result<WifiSettings>.onSuccess(action: (WifiSettings) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}