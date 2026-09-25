package com.wificontrolpanel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.wificontrolpanel.ui.dashboard.DashboardScreen
import com.wificontrolpanel.ui.devices.DevicesScreen
import com.wificontrolpanel.ui.mac.MacControlScreen
import com.wificontrolpanel.ui.parental.ParentalControlScreen
import com.wificontrolpanel.ui.reports.ReportsScreen
import com.wificontrolpanel.ui.router.RouterSettingsScreen
import com.wificontrolpanel.ui.security.SecurityScreen
import com.wificontrolpanel.ui.settings.SettingsScreen
import com.wificontrolpanel.ui.speed.SpeedManagementScreen
import com.wificontrolpanel.ui.wifisettings.WifiSettingsScreen
import com.wificontrolpanel.ui.blocked.BlockedDevicesScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import android.content.Context

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navHost = NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.Devices.route) {
            DevicesScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.BlockedDevices.route) {
            BlockedDevicesScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.SpeedManagement.route) {
            SpeedManagementScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.MacControl.route) {
            MacControlScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.ParentalControl.route) {
            ParentalControlScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.Security.route) {
            SecurityScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.RouterSettings.route) {
            RouterSettingsScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.WifiSettings.route) {
            WifiSettingsScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.Reports.route) {
            ReportsScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(navArgument("macAddress") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val macAddress = backStackEntry.getString() ?: ""
            DeviceDetailScreen(
                macAddress = macAddress,
                onNavigate = { screen ->
                    navController.navigate(screen.route)
                }
            )
        }
    }
}

enum class Screen(
    val route: String,
    val title: String,
    val icon: String
) {
    Dashboard("dashboard", "Dashboard", "dashboard"),
    Devices("devices", "Connected Devices", "devices"),
    BlockedDevices("blocked", "Blocked Devices", "block"),
    SpeedManagement("speed", "Speed Management", "speed"),
    MacControl("mac", "MAC Control", "mac"),
    ParentalControl("parental", "Parental Control", "parental"),
    Security("security", "Security", "security"),
    RouterSettings("router", "Router Settings", "router"),
    WifiSettings("wifi", "Wi-Fi Settings", "wifi"),
    Reports("reports", "Reports", "reports"),
    Settings("settings", "Settings", "settings"),
    DeviceDetail("device/{macAddress}", "Device Details", "info")
}

@Composable
fun DeviceDetailScreen(
    macAddress: String,
    onNavigate: (Screen) -> Unit
) {
    // TODO: Implement device detail screen
    androidx.compose.material3.Text(text = "Device Detail: $macAddress")
}