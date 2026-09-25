package com.wificontrolpanel.ui.blocked

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
import com.wificontrolpanel.data.model.Device
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun BlockedDevicesScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: BlockedDevicesViewModel = hiltViewModel()
) {
    val blockedDevices by viewModel.blockedDevices.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
                SectionHeader(
                    title = "Blocked Devices",
                    subtitle = "${blockedDevices.size} devices blocked"
                )

                if (isLoading && blockedDevices.isEmpty()) {
                    com.wificontrolpanel.ui.components.LoadingOverlay("Loading...")
                } else if (blockedDevices.isEmpty()) {
                    EmptyState(
                        icon = CommonComponents.Icons.Default.Block,
                        title = "No blocked devices",
                        message = "Devices you block will appear here"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(blockedDevices) { device ->
                            BlockedDeviceCard(
                                device = device,
                                onUnblock = { viewModel.unblockDevice(device.macAddress) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockedDeviceCard(
    device: Device,
    onUnblock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
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
                    imageVector = CommonComponents.Icons.Default.Block,
                    contentDescription = null,
                    tint = Color.Red,
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
            ActionButton(
                text = "Unblock",
                icon = CommonComponents.Icons.Default.LockOpen,
                color = Color.Green,
                onClick = onUnblock
            )
        }
    }
}

@Composable
fun BlockedDevicesViewModel(
    repository: RouterRepository
): BlockedDevicesViewModel = BlockedDevicesViewModel(repository)

class BlockedDevicesViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _blockedDevices = androidx.compose.runtime.MutableStateFlow<List<Device>>(emptyList())
    val blockedDevices = _blockedDevices.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadBlockedDevices()
    }

    fun loadBlockedDevices() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            // In real implementation, fetch from router
            _blockedDevices.value = emptyList()
            _isLoading.value = false
        }
    }

    fun unblockDevice(macAddress: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.unblockDevice(macAddress)
            loadBlockedDevices()
        }
    }
}