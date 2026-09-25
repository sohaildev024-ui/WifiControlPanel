package com.wificontrolpanel.ui.mac

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
import com.wificontrolpanel.data.model.DeviceType
import com.wificontrolpanel.data.model.MacAddressEntry
import com.wificontrolpanel.data.model.MacFilterMode
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun MacControlScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: MacControlViewModel = hiltViewModel()
) {
    val blockedMacs by viewModel.blockedMacs.collectAsStateWithLifecycle()
    val trustedMacs by viewModel.trustedMacs.collectAsStateWithLifecycle()
    val filterMode by viewModel.filterMode.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var newMacAddress by remember { mutableStateOf("") }
    var newMacName by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

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
                SectionHeader(title = "MAC Address Control")

                // Tab Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacTab(text = "Connected", isSelected = selectedTab == 0, onClick = { selectedTab = 0 })
                    MacTab(text = "Blocked", isSelected = selectedTab == 1, onClick = { selectedTab = 1 })
                    MacTab(text = "Trusted", isSelected = selectedTab == 2, onClick = { selectedTab = 2 })
                }

                // Add MAC Address Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Add MAC Address Manually")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            androidx.compose.material3.TextField(
                                value = newMacAddress,
                                onValueChange = { newMacAddress = it.uppercase() },
                                modifier = Modifier.weight(1f),
                                label = { Text("MAC Address (AA:BB:CC:DD:EE:FF)") },
                                singleLine = true
                            )
                            androidx.compose.material3.TextField(
                                value = newMacName,
                                onValueChange = { newMacName = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Device Name (Optional)") },
                                singleLine = true
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                text = "Add to Blocked",
                                icon = CommonComponents.Icons.Default.Block,
                                color = Color.Red,
                                onClick = {
                                    viewModel.blockMacAddress(newMacAddress, newMacName.ifBlank { null })
                                    newMacAddress = ""
                                    newMacName = ""
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Add to Trusted",
                                icon = CommonComponents.Icons.Default.Verified,
                                color = Color.Green,
                                onClick = {
                                    viewModel.addTrustedMacAddress(newMacAddress, newMacName.ifBlank { null })
                                    newMacAddress = ""
                                    newMacName = ""
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Filter Mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "MAC Filter Mode")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterModeOption(
                                title = "Disabled",
                                description = "No MAC filtering",
                                mode = MacFilterMode.DISABLED,
                                selectedMode = filterMode,
                                onClick = { viewModel.setFilterMode(MacFilterMode.DISABLED) }
                            )
                            FilterModeOption(
                                title = "Allow List",
                                description = "Only trusted devices can connect",
                                mode = MacFilterMode.ALLOW_LIST,
                                selectedMode = filterMode,
                                onClick = { viewModel.setFilterMode(MacFilterMode.ALLOW_LIST) }
                            )
                            FilterModeOption(
                                title = "Block List",
                                description = "Blocked devices cannot connect",
                                mode = MacFilterMode.BLOCK_LIST,
                                selectedMode = filterMode,
                                onClick = { viewModel.setFilterMode(MacFilterMode.BLOCK_LIST) }
                            )
                        }
                    }
                }

                // Lists based on selected tab
                when (selectedTab) {
                    0 -> ConnectedMacsList()
                    1 -> BlockedMacsList(blockedMacs = blockedMacs, onUnblock = { mac -> viewModel.unblockMacAddress(mac) })
                    2 -> TrustedMacsList(trustedMacs = trustedMacs, onRemove = { mac -> viewModel.removeTrustedMacAddress(mac) })
                }
            }
        }
    }
}

@Composable
fun MacTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) 
                androidx.compose.material3.MaterialTheme.colorScheme.primary 
            else 
                androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun FilterModeOption(
    title: String,
    description: String,
    mode: MacFilterMode,
    selectedMode: MacFilterMode,
    onClick: () -> Unit
) {
    val isSelected = mode == selectedMode
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer 
            else 
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = if (isSelected) 
                    androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = if (isSelected) 
                    androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                else 
                    androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ConnectedMacsList() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Currently Connected Devices")
            EmptyState(
                icon = CommonComponents.Icons.Default.Devices,
                title = "Connect to router to see devices",
                message = "Connect to your router to view currently connected MAC addresses"
            )
        }
    }
}

@Composable
fun BlockedMacsList(
    blockedMacs: List<MacAddressEntry>,
    onUnblock: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Blocked MAC Addresses", subtitle = "${blockedMacs.size} addresses")

            if (blockedMacs.isEmpty()) {
                EmptyState(
                    icon = CommonComponents.Icons.Default.Block,
                    title = "No blocked MAC addresses",
                    message = "Add MAC addresses to block list using the form above"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(blockedMacs) { entry ->
                        MacListItem(
                            entry = entry,
                            onAction = { onUnblock(entry.macAddress) },
                            actionText = "Unblock",
                            actionColor = Color.Green,
                            actionIcon = CommonComponents.Icons.Default.LockOpen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrustedMacsList(
    trustedMacs: List<MacAddressEntry>,
    onRemove: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Trusted MAC Addresses", subtitle = "${trustedMacs.size} addresses")

            if (trustedMacs.isEmpty()) {
                EmptyState(
                    icon = CommonComponents.Icons.Default.Verified,
                    title = "No trusted MAC addresses",
                    message = "Add trusted MAC addresses using the form above"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trustedMacs) { entry ->
                        MacListItem(
                            entry = entry,
                            onAction = { onRemove(entry.macAddress) },
                            actionText = "Remove",
                            actionColor = Color.Red,
                            actionIcon = CommonComponents.Icons.Default.Delete
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacListItem(
    entry: MacAddressEntry,
    onAction: () -> Unit,
    actionText: String,
    actionColor: Color,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Column {
                Text(
                    text = entry.name ?: entry.macAddress,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = entry.macAddress,
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            ActionButton(
                text = actionText,
                icon = actionIcon,
                color = actionColor,
                onClick = onAction
            )
        }
    }
}

class MacControlViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _blockedMacs = androidx.compose.runtime.MutableStateFlow<List<MacAddressEntry>>(emptyList())
    val blockedMacs = _blockedMacs.asStateFlow()

    private val _trustedMacs = androidx.compose.runtime.MutableStateFlow<List<MacAddressEntry>>(emptyList())
    val trustedMacs = _trustedMacs.asStateFlow()

    private val _filterMode = androidx.compose.runtime.MutableStateFlow<MacFilterMode>(MacFilterMode.DISABLED)
    val filterMode = _filterMode.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getBlockedMacAddresses().onSuccess { _blockedMacs.value = it }
            repository.getTrustedMacAddresses().onSuccess { _trustedMacs.value = it }
            repository.getMacFilterMode().onSuccess { _filterMode.value = it }
            _isLoading.value = false
        }
    }

    fun blockMacAddress(mac: String, name: String?) {
        androidx.lifecycle.viewModelScope.launch {
            repository.blockMacAddress(mac, name)
            loadData()
        }
    }

    fun unblockMacAddress(mac: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.unblockMacAddress(mac)
            loadData()
        }
    }

    fun addTrustedMacAddress(mac: String, name: String?) {
        androidx.lifecycle.viewModelScope.launch {
            repository.addTrustedMacAddress(mac, name)
            loadData()
        }
    }

    fun removeTrustedMacAddress(mac: String) {
        androidx.lifecycle.viewModelScope.launch {
            repository.removeTrustedMacAddress(mac)
            loadData()
        }
    }

    fun setFilterMode(mode: MacFilterMode) {
        androidx.lifecycle.viewModelScope.launch {
            repository.setMacFilterMode(mode)
            _filterMode.value = mode
        }
    }

    private fun com.wificontrolpanel.data.router.Result<List<MacAddressEntry>>.onSuccess(action: (List<MacAddressEntry>) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }

    private fun com.wificontrolpanel.data.router.Result<MacFilterMode>.onSuccess(action: (MacFilterMode) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}