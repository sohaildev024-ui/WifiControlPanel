package com.wificontrolpanel.ui.speed

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
import androidx.compose.material3.Slider
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
import com.wificontrolpanel.data.model.DistributionMode
import com.wificontrolpanel.data.model.SpeedLimitConfig
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun SpeedManagementScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SpeedManagementViewModel = hiltViewModel()
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var totalDownloadLimit by remember { mutableStateOf(config.totalDownloadLimit?.let { it / 1_000_000 } ?: 100) }
    var totalUploadLimit by remember { mutableStateOf(config.totalUploadLimit?.let { it / 1_000_000 } ?: 50) }
    var isUnlimited by remember { mutableStateOf(config.isUnlimited) }
    var distributionMode by remember { mutableStateOf(config.distributionMode) }
    var defaultDownloadLimit by remember { mutableStateOf(config.defaultDownloadLimit?.let { it / 1_000_000 } ?: 10) }
    var defaultUploadLimit by remember { mutableStateOf(config.defaultUploadLimit?.let { it / 1_000_000 } ?: 5) }

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
            SectionHeader(title = "Speed Management")

            // Total Bandwidth Limits
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Total Bandwidth Limits")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.TextField(
                            value = totalDownloadLimit.toString(),
                            onValueChange = { totalDownloadLimit = it.toIntOrNull() ?: 0 },
                            modifier = Modifier.weight(1f),
                            label = { Text("Total Download Limit (Mbps)") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            enabled = !isUnlimited
                        )
                        androidx.compose.material3.TextField(
                            value = totalUploadLimit.toString(),
                            onValueChange = { totalUploadLimit = it.toIntOrNull() ?: 0 },
                            modifier = Modifier.weight(1f),
                            label = { Text("Total Upload Limit (Mbps)") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            enabled = !isUnlimited
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Unlimited Mode",
                            fontSize = 16.sp
                        )
                        Switch(
                            checked = isUnlimited,
                            onCheckedChange = { isUnlimited = it },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = Color.Green,
                                checkedTrackColor = Color.Green.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // Distribution Mode
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Speed Distribution")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DistributionOption(
                            title = "Equal Distribution",
                            description = "All devices get equal share of bandwidth",
                            mode = DistributionMode.EQUAL,
                            selectedMode = distributionMode,
                            onClick = { distributionMode = DistributionMode.EQUAL }
                        )
                        DistributionOption(
                            title = "Custom Distribution",
                            description = "Set individual speed limits per device",
                            mode = DistributionMode.CUSTOM,
                            selectedMode = distributionMode,
                            onClick = { distributionMode = DistributionMode.CUSTOM }
                        )
                        DistributionOption(
                            title = "Priority Based",
                            description = "High priority devices get more bandwidth",
                            mode = DistributionMode.PRIORITY_BASED,
                            selectedMode = distributionMode,
                            onClick = { distributionMode = DistributionMode.PRIORITY_BASED }
                        )
                    }
                }
            }

            // Default Limits for New Devices
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Default Limits for New Devices")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.TextField(
                            value = defaultDownloadLimit.toString(),
                            onValueChange = { defaultDownloadLimit = it.toIntOrNull() ?: 0 },
                            modifier = Modifier.weight(1f),
                            label = { Text("Default Download (Mbps)") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        androidx.compose.material3.TextField(
                            value = defaultUploadLimit.toString(),
                            onValueChange = { defaultUploadLimit = it.toIntOrNull() ?: 0 },
                            modifier = Modifier.weight(1f),
                            label = { Text("Default Upload (Mbps)") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }
            }

            // Apply Button
            ActionButton(
                text = "Apply Speed Limits",
                icon = CommonComponents.Icons.Default.CheckCircle,
                color = Color.Green,
                onClick = {
                    viewModel.applySpeedLimits(
                        totalDownloadLimit = if (isUnlimited) null else (totalDownloadLimit * 1_000_000L),
                        totalUploadLimit = if (isUnlimited) null else (totalUploadLimit * 1_000_000L),
                        isUnlimited = isUnlimited,
                        distributionMode = distributionMode,
                        defaultDownloadLimit = defaultDownloadLimit * 1_000_000L,
                        defaultUploadLimit = defaultUploadLimit * 1_000_000L
                    )
                }
            )

            // Quick Device Speed Controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(
                        title = "Quick Device Controls",
                        action = { onNavigate(Screen.Devices) },
                        actionText = "Manage All",
                        actionIcon = CommonComponents.Icons.Default.Devices
                    )
                    Text(
                        text = "Go to Connected Devices to set individual speed limits, priorities, and pause/resume internet for each device.",
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun DistributionOption(
    title: String,
    description: String,
    mode: DistributionMode,
    selectedMode: DistributionMode,
    onClick: () -> Unit
) {
    val isSelected = mode == selectedMode
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer 
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
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) 
                        androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (isSelected) 
                        androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                    else 
                        androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = CommonComponents.Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

class SpeedManagementViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _config = androidx.compose.runtime.MutableStateFlow<SpeedLimitConfig>(SpeedLimitConfig())
    val config = _config.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadConfig()
    }

    fun loadConfig() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            val result = repository.getSpeedLimitConfig()
            result.onSuccess { config ->
                _config.value = config
            }
            _isLoading.value = false
        }
    }

    fun applySpeedLimits(
        totalDownloadLimit: Long?,
        totalUploadLimit: Long?,
        isUnlimited: Boolean,
        distributionMode: DistributionMode,
        defaultDownloadLimit: Long,
        defaultUploadLimit: Long
    ) {
        androidx.lifecycle.viewModelScope.launch {
            if (!isUnlimited) {
                repository.setTotalDownloadLimit(totalDownloadLimit)
                repository.setTotalUploadLimit(totalUploadLimit)
            }
            repository.setSpeedDistributionMode(distributionMode)
            repository.setDefaultDeviceLimits(defaultDownloadLimit, defaultUploadLimit)
            loadConfig()
        }
    }

    private fun com.wificontrolpanel.data.router.Result<SpeedLimitConfig>.onSuccess(action: (SpeedLimitConfig) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}