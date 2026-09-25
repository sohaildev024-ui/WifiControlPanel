package com.wificontrolpanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.wificontrolpanel.ui.navigation.AppNavHost
import com.wificontrolpanel.ui.theme.WifiControlPanelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiControlPanelTheme {
                Surface {
                    AppNavHost()
                }
            }
        }
    }
}