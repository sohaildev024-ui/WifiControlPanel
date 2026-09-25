package com.wificontrolpanel.ui.reports

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
import com.wificontrolpanel.data.model.ReportPeriod
import com.wificontrolpanel.data.model.UsageReport
import com.wificontrolpanel.data.repository.RouterRepository
import com.wificontrolpanel.ui.components.ActionButton
import com.wificontrolpanel.ui.components.CommonComponents
import com.wificontrolpanel.ui.components.EmptyState
import com.wificontrolpanel.ui.components.LoadingOverlay
import com.wificontrolpanel.ui.components.SectionHeader
import com.wificontrolpanel.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ReportsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val report by viewModel.currentReport.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.DAILY) }

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
                SectionHeader(title = "Network Reports")

                // Period Selector
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Select Period")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportPeriodChip(
                                period = ReportPeriod.DAILY,
                                selectedPeriod = selectedPeriod,
                                onClick = { selectedPeriod = ReportPeriod.DAILY; viewModel.loadReport(ReportPeriod.DAILY, null, null) }
                            )
                            ReportPeriodChip(
                                period = ReportPeriod.WEEKLY,
                                selectedPeriod = selectedPeriod,
                                onClick = { selectedPeriod = ReportPeriod.WEEKLY; viewModel.loadReport(ReportPeriod.WEEKLY, null, null) }
                            )
                            ReportPeriodChip(
                                period = ReportPeriod.MONTHLY,
                                selectedPeriod = selectedPeriod,
                                onClick = { selectedPeriod = ReportPeriod.MONTHLY; viewModel.loadReport(ReportPeriod.MONTHLY, null, null) }
                            )
                        }
                    }
                }

                report?.let { r ->
                    // Summary Cards
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportStatCard(
                            title = "Total Download",
                            value = formatDataSize(r.totalDownload),
                            icon = CommonComponents.Icons.Default.CloudDownload,
                            color = Color.Green
                        )
                        ReportStatCard(
                            title = "Total Upload",
                            value = formatDataSize(r.totalUpload),
                            icon = CommonComponents.Icons.Default.CloudUpload,
                            color = Color.Blue
                        )
                    }

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportStatCard(
                            title = "Peak Download",
                            value = "${formatSpeed(r.peakDownloadSpeed)}/s",
                            icon = CommonComponents.Icons.Default.TrendingUp,
                            color = Color.Orange
                        )
                        ReportStatCard(
                            title = "Peak Upload",
                            value = "${formatSpeed(r.peakUploadSpeed)}/s",
                            icon = CommonComponents.Icons.Default.TrendingUp,
                            color = Color.Orange
                        )
                    }

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportStatCard(
                            title = "Avg Download",
                            value = "${formatSpeed(r.averageDownloadSpeed)}/s",
                            icon = CommonComponents.Icons.Default.Speed,
                            color = Color.Cyan
                        )
                        ReportStatCard(
                            title = "Avg Upload",
                            value = "${formatSpeed(r.averageUploadSpeed)}/s",
                            icon = CommonComponents.Icons.Default.Speed,
                            color = Color.Cyan
                        )
                    }

                    // Device Usage
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionHeader(title = "Per Device Usage")

                            if (r.deviceUsage.isEmpty()) {
                                EmptyState(
                                    icon = CommonComponents.Icons.Default.Devices,
                                    title = "No device data",
                                    message = "Device usage data will appear here"
                                )
                            } else {
                                r.deviceUsage.values
                                    .sortedByDescending { it.download + it.upload }
                                    .take(10)
                                    .forEach { usage ->
                                        DeviceUsageRow(usage = usage)
                                    }
                            }
                        }
                    }

                    // Events Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionHeader(title = "Events Summary")

                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EventCard(
                                    title = "Block Events",
                                    count = r.blockEvents,
                                    icon = CommonComponents.Icons.Default.Block,
                                    color = Color.Red
                                )
                                EventCard(
                                    title = "Router Restarts",
                                    count = r.restartEvents,
                                    icon = CommonComponents.Icons.Default.RestartAlt,
                                    color = Color.Orange
                                )
                            }
                        }
                    }

                    // Most Active Device
                    r.mostActiveDevice?.let { deviceName ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionHeader(title = "Most Active Device")
                                Text(
                                    text = deviceName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                            }
                        }
                    }
                } ?: run {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            EmptyState(
                                icon = CommonComponents.Icons.Default.Report,
                                title = "Select a period",
                                message = "Choose a time period to view network usage reports"
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                LoadingOverlay("Generating report...")
            }
        }
    }
}

@Composable
fun ReportPeriodChip(
    period: ReportPeriod,
    selectedPeriod: ReportPeriod,
    onClick: () -> Unit
) {
    val isSelected = period == selectedPeriod
    androidx.compose.material3.Chip(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = if (isSelected) 
                androidx.compose.material3.MaterialTheme.colorScheme.primary 
            else 
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = when (period) {
                ReportPeriod.DAILY -> "Daily"
                ReportPeriod.WEEKLY -> "Weekly"
                ReportPeriod.MONTHLY -> "Monthly"
                ReportPeriod.CUSTOM -> "Custom"
            },
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) 
                androidx.compose.material3.MaterialTheme.colorScheme.onPrimary 
            else 
                androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ReportStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DeviceUsageRow(usage: com.wificontrolpanel.data.model.DeviceUsage) {
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
            Column {
                Text(
                    text = usage.deviceName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = usage.deviceMac,
                    fontSize = 11.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "↓ ${formatDataSize(usage.download)} ↑ ${formatDataSize(usage.upload)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Online: ${formatDuration(usage.onlineTime)}",
                    fontSize = 10.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun EventCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

fun formatDataSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000_000L -> String.format("%.2f TB", bytes / 1_000_000_000_000.0)
        bytes >= 1_000_000_000 -> String.format("%.2f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.2f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}

fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond >= 1_000_000_000 -> String.format("%.1f Gbps", bytesPerSecond / 1_000_000_000.0)
        bytesPerSecond >= 1_000_000 -> String.format("%.1f Mbps", bytesPerSecond / 1_000_000.0)
        bytesPerSecond >= 1_000 -> String.format("%.1f Kbps", bytesPerSecond / 1_000.0)
        else -> "$bytesPerSecond bps"
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

class ReportsViewModel @Inject constructor(
    private val repository: RouterRepository
) : androidx.lifecycle.ViewModel() {
    private val _currentReport = androidx.compose.runtime.MutableStateFlow<UsageReport?>(null)
    val currentReport = _currentReport.asStateFlow()

    private val _isLoading = androidx.compose.runtime.MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadReport(period: ReportPeriod, customStart: Date?, customEnd: Date?) {
        _isLoading.value = true
        androidx.lifecycle.viewModelScope.launch {
            repository.getUsageReport(period, customStart, customEnd).onSuccess { _currentReport.value = it }
            _isLoading.value = false
        }
    }

    private fun com.wificontrolpanel.data.router.Result<UsageReport>.onSuccess(action: (UsageReport) -> Unit) {
        if (this is com.wificontrolpanel.data.router.Result.Success) action(data)
    }
}