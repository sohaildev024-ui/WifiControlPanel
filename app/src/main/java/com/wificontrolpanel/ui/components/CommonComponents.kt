package com.wificontrolpanel.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trend: String? = null,
    trendPositive: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = color.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 10.sp,
                        color = color.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
                trend?.let {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (trendPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (trendPositive) Color.Green else Color.Red,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = it,
                            fontSize = 10.sp,
                            color = if (trendPositive) Color.Green else Color.Red,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false
) {
    val contentColor = if (isEnabled) Color.White else color.copy(alpha = 0.5f)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = if (isEnabled && !isLoading) onClick else null,
        enabled = isEnabled && !isLoading,
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) color else color.copy(alpha = 0.3f),
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (() -> Unit)? = null,
    actionText: String? = null,
    actionIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        action?.let {
            androidx.compose.material3.TextButton(
                onClick = it,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actionIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = actionText ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        actionText?.let { text ->
            action?.let { onClick ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.Button(onClick = onClick) {
                    Text(text = text)
                }
            }
        }
    }
}

@Composable
fun LoadingOverlay(message: String = "Loading...") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.CircularProgressIndicator()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                Text(text = message, fontSize = 14.sp)
            }
        }
    }
}

object Icons {
    object Default {
        val Dashboard = androidx.compose.material.icons.Icons.Default.Dashboard
        val Devices = androidx.compose.material.icons.Icons.Default.Devices
        val Block = androidx.compose.material.icons.Icons.Default.Block
        val Speed = androidx.compose.material.icons.Icons.Default.Speed
        val Router = androidx.compose.material.icons.Icons.Default.Router
        val Wifi = androidx.compose.material.icons.Icons.Default.Wifi
        val Security = androidx.compose.material.icons.Icons.Default.Security
        val Settings = androidx.compose.material.icons.Icons.Default.Settings
        val Report = androidx.compose.material.icons.Icons.Default.Assessment
        val ParentalControl = androidx.compose.material.icons.Icons.Default.FamilyRestroom
        val Mac = androidx.compose.material.icons.Icons.Default.Memory
        val Info = androidx.compose.material.icons.Icons.Default.Info
        val TrendingUp = androidx.compose.material.icons.Icons.Default.TrendingUp
        val TrendingDown = androidx.compose.material.icons.Icons.Default.TrendingDown
        val Refresh = androidx.compose.material.icons.Icons.Default.Refresh
        val Add = androidx.compose.material.icons.Icons.Default.Add
        val Edit = androidx.compose.material.icons.Icons.Default.Edit
        val Delete = androidx.compose.material.icons.Icons.Default.Delete
        val Search = androidx.compose.material.icons.Icons.Default.Search
        val Visibility = androidx.compose.material.icons.Icons.Default.Visibility
        val VisibilityOff = androidx.compose.material.icons.Icons.Default.VisibilityOff
        val CheckCircle = androidx.compose.material.icons.Icons.Default.CheckCircle
        val Error = androidx.compose.material.icons.Icons.Default.Error
        val Warning = androidx.compose.material.icons.Icons.Default.Warning
        val PowerSettingsNew = androidx.compose.material.icons.Icons.Default.PowerSettingsNew
        val PauseCircle = androidx.compose.material.icons.Icons.Default.PauseCircle
        val PlayCircle = androidx.compose.material.icons.Icons.Default.PlayCircle
        val Lock = androidx.compose.material.icons.Icons.Default.Lock
        val LockOpen = androidx.compose.material.icons.Icons.Default.LockOpen
        val Person = androidx.compose.material.icons.Icons.Default.Person
        val PersonAdd = androidx.compose.material.icons.Icons.Default.PersonAdd
        val Schedule = androidx.compose.material.icons.Icons.Default.Schedule
        val AccessTime = androidx.compose.material.icons.Icons.Default.AccessTime
        val CloudDownload = androidx.compose.material.icons.Icons.Default.CloudDownload
        val CloudUpload = androidx.compose.material.icons.Icons.Default.CloudUpload
        val DataUsage = androidx.compose.material.icons.Icons.Default.DataUsage
        val SignalWifi4Bar = androidx.compose.material.icons.Icons.Default.SignalWifi4Bar
        val SignalWifiOff = androidx.compose.material.icons.Icons.Default.SignalWifiOff
        val RestartAlt = androidx.compose.material.icons.Icons.Default.RestartAlt
        val Build = androidx.compose.material.icons.Icons.Default.Build
        val Memory = androidx.compose.material.icons.Icons.Default.Memory
        val Thermostat = androidx.compose.material.icons.Icons.Default.Thermostat
        val NetworkCell = androidx.compose.material.icons.Icons.Default.NetworkCell
        val DevicesOther = androidx.compose.material.icons.Icons.Default.DevicesOther
        val Phonelink = androidx.compose.material.icons.Icons.Default.Phonelink
        val Computer = androidx.compose.material.icons.Icons.Default.Computer
        val Tv = androidx.compose.material.icons.Icons.Default.Tv
        val VideogameAsset = androidx.compose.material.icons.Icons.Default.VideogameAsset
        val Print = androidx.compose.material.icons.Icons.Default.Print
        val Storage = androidx.compose.material.icons.Icons.Default.Storage
        val NetworkWifi = androidx.compose.material.icons.Icons.Default.NetworkWifi
        val TabletAndroid = androidx.compose.material.icons.Icons.Default.TabletAndroid
        val Laptop = androidx.compose.material.icons.Icons.Default.Laptop
        val FactoryReset = androidx.compose.material.icons.Icons.Default.FactoryReset
        val SystemUpdate = androidx.compose.material.icons.Icons.Default.SystemUpdate
        val LinkOff = androidx.compose.material.icons.Icons.Default.LinkOff
        val Link = androidx.compose.material.icons.Icons.Default.Link
        val DarkMode = androidx.compose.material.icons.Icons.Default.DarkMode
        val Timer = androidx.compose.material.icons.Icons.Default.Timer
        val Notifications = androidx.compose.material.icons.Icons.Default.Notifications
        val Calendar = androidx.compose.material.icons.Icons.Default.Calendar
        val Login = androidx.compose.material.icons.Icons.Default.Login
        val Bedtime = androidx.compose.material.icons.Icons.Default.Bedtime
        val ChevronRight = androidx.compose.material.icons.Icons.Default.ChevronRight
        val Cancel = androidx.compose.material.icons.Icons.Default.Cancel
        val Close = androidx.compose.material.icons.Icons.Default.Close
        val FilterList = androidx.compose.material.icons.Icons.Default.FilterList
        val Verified = androidx.compose.material.icons.Icons.Default.Verified
        val DeviceUnknown = androidx.compose.material.icons.Icons.Default.DeviceUnknown
        val DeveloperBoard = androidx.compose.material.icons.Icons.Default.DeveloperBoard
        val MoreVert = androidx.compose.material.icons.Icons.Default.MoreVert
    }
}