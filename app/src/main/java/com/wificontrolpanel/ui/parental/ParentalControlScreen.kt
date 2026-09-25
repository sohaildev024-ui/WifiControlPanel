package com.wificontrolpanel.ui.parental

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
import com.wificontrolpanel.data.model.DayOfWeek
import com.wificontrolpanel.data.model.ParentalControlRule
import com.wificontrolpanel.data.model.TimeSchedule
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Composable
fun ParentalControlScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: ParentalControlViewModel = hiltViewModel()
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedDeviceMac by remember { mutableStateOf<String?>(null) }

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
                SectionHeader(title = "Parental Control")

                // Device Selector
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Select Device")
                        
                        // TODO: Show device selector dropdown
                        Text(
                            text = "Select a device from the connected devices list to configure parental controls",
                            fontSize = 14.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        ActionButton(
                            text = "Choose Device",
                            icon = CommonComponents.Icons.Default.Devices,
                            color = Color.Blue,
                            onClick = { onNavigate(Screen.Devices) }
                        )
                    }
                }

                selectedDeviceMac?.let { mac ->
                    val rule = rules.find { it.deviceMac == mac }
                    rule?.let { r ->
                        ParentalControlRuleCard(
                            rule = r,
                            onToggleEnabled = { viewModel.setRuleEnabled(r.deviceMac, it) },
                            onTogglePaused = { viewModel.setRulePaused(r.deviceMac, it) },
                            onEditSchedule = { /* show schedule editor */ },
                            onEditBedtime = { /* show bedtime editor */ },
                            onEditDailyLimit = { /* show daily limit editor */ }
                        )
                    }
                }

                // Existing Rules List
                if (rules.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionHeader(title = "Configured Devices")

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(rules) { rule ->
                                    ParentalControlSummaryCard(
                                        rule = rule,
                                        onClick = { selectedDeviceMac = rule.deviceMac }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    EmptyState(
                        icon = CommonComponents.Icons.Default.FamilyRestroom,
                        title = "No parental controls configured",
                        message = "Select a device to set up internet schedules, bedtime, and daily limits"
                    )
                }
            }
        }
    }
}

@Composable
fun ParentalControlRuleCard(
    rule: ParentalControlRule,
    onToggleEnabled: (Boolean) -> Unit,
    onTogglePaused: (Boolean) -> Unit,
    onEditSchedule: () -> Unit,
    onEditBedtime: () -> Unit,
    onEditDailyLimit: () -> Unit
) {
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
                Column {
                    Text(
                        text = rule.deviceName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = rule.deviceMac,
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = Color.Green,
                        checkedTrackColor = Color.Green.copy(alpha = 0.3f)
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    text = if (rule.isPaused) "Resume" else "Pause Now",
                    icon = if (rule.isPaused) CommonComponents.Icons.Default.PlayCircle else CommonComponents.Icons.Default.PauseCircle,
                    color = if (rule.isPaused) Color.Green else Color.Orange,
                    onClick = { onTogglePaused(!rule.isPaused) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Schedule",
                    icon = CommonComponents.Icons.Default.Schedule,
                    color = Color.Blue,
                    onClick = onEditSchedule,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Bedtime",
                    icon = CommonComponents.Icons.Default.Bedtime,
                    color = Color.Purple,
                    onClick = onEditBedtime,
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
                    text = "Daily Limit: ${rule.dailyLimitMinutes} min",
                    icon = CommonComponents.Icons.Default.AccessTime,
                    color = Color.Cyan,
                    onClick = onEditDailyLimit,
                    modifier = Modifier.weight(1f)
                )
            }

            // Schedule Summary
            if (rule.schedules.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Internet Schedules",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    rule.schedules.forEach { schedule ->
                        ScheduleRow(schedule = schedule)
                    }
                }
            }

            rule.bedtimeSchedule?.let { bedtime ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Bedtime Schedule",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    ScheduleRow(schedule = bedtime)
                }
            }
        }
    }
}

@Composable
fun ParentalControlSummaryCard(
    rule: ParentalControlRule,
    onClick: () -> Unit
) {
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
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = rule.deviceName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${rule.schedules.size} schedules • ${rule.dailyLimitMinutes} min daily limit",
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (rule.isEnabled) CommonComponents.Icons.Default.CheckCircle else CommonComponents.Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (rule.isEnabled) Color.Green else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = CommonComponents.Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ScheduleRow(schedule: TimeSchedule) {
    val daysText = schedule.days.joinToString(", ") { it.name.substring(0, 3) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "$daysText: ${schedule.startTime} - ${schedule.endTime}",
                fontSize = 13.sp
            )
            Text(
                text = if (schedule.isInternetAllowed) "Internet Allowed" else "Internet Blocked",
                fontSize = 11.sp,
                color = if (schedule.isInternetAllowed) Color.Green else Color.Red
            )
        }
    }
}

class ParentalControlViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _rules = androidx.compose.runtime.MutableStateFlow<List<ParentalControlRule>>(emptyList())
    val rules = _rules.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadRules()
    }

    fun loadRules() {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getParentalControlRules().onSuccess { _rules.value = it }
            _isLoading.value = false
        }
    }

    fun setRuleEnabled(deviceMac: String, enabled: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            // Update rule
            loadRules()
        }
    }

    fun setRulePaused(deviceMac: String, paused: Boolean) {
        androidx.lifecycle.viewModelScope.launch {
            if (paused) {
                repository.pauseDeviceInternetSchedule(deviceMac)
            } else {
                repository.resumeDeviceInternetSchedule(deviceMac)
            }
            loadRules()
        }
    }

    private fun com.wificontrolpanel.data.router.Result<List<ParentalControlRule>>.onSuccess(action: (List<ParentalControlRule>) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}