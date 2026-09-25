# Wi-Fi Control Panel

A professional Android app for managing fiber Wi-Fi routers. Built with modern Android architecture using Jetpack Compose, Hilt DI, and a router adapter pattern for multi-brand support.

## Features

### Main Control Panel (Dashboard)
- **Internet Status** - Real-time internet connectivity
- **Router Status** - Router online/offline status
- **Total Connected Users** - Live device count
- **Total Blocked Users** - Blocked device count
- **Unknown Devices** - Unrecognized device alerts
- **Current Download/Upload Speed** - Real-time bandwidth
- **Total Data Usage** - Cumulative data consumption

### Quick Control Buttons
- Wi-Fi ON/OFF
- Internet Pause/Resume
- Refresh Connected Devices
- Restart Router
- Guest Wi-Fi ON/OFF
- 2.4 GHz / 5 GHz Band Control
- Change Wi-Fi Password
- Change Wi-Fi Name (SSID)

### Connected Device Management
- Device Name, Type, IP, MAC Address
- Online/Offline/Blocked/Paused Status
- Real-time Download/Upload Speed
- Total Data Usage & Connection Time
- **Actions**: Block, Unblock, Pause, Resume, Speed Limits, Rename, Mark Trusted

### Speed Management
- Total Download/Upload Limits
- Unlimited Mode
- Equal / Custom / Priority-based Distribution
- Per-device Download/Upload Limits
- High/Normal/Low Priority
- Default Limits for New Devices

### MAC Address Control
- Connected MAC Addresses
- Blocked MAC Addresses
- Trusted MAC Addresses
- Manual MAC Entry
- Block/Unblock MAC
- Allow Only Trusted Devices Mode

### Parental Control
- Device Selection
- Internet Schedule (Time-based)
- Bedtime Schedule
- Daily Time Limits
- Instant Pause/Resume

### Security Center
- Unknown Device Alerts
- Admin Login Management
- Change Admin Password
- Trusted Device List
- Block History
- Login History
- Security Notifications

### Router Settings
- Router Brand/Model/Firmware
- LAN/WAN IP & MAC Address
- CPU/Memory/Temperature Monitoring
- Router Uptime
- Restart/Factory Reset/Firmware Update

### Wi-Fi Settings
- SSID & Password Management
- Guest Network
- Security Mode (WPA2/WPA3)
- Channel Selection
- TX Power Control
- Hide SSID
- Max Users Limit

### Reports & Analytics
- Daily/Weekly/Monthly Usage
- Per-Device Usage Breakdown
- Most Active Device
- Peak/Average Speeds
- Block/Restart Event History

## Architecture

### Router Adapter Pattern
Supports multiple router brands through a unified interface:
- **Huawei** - Full feature support
- **ZTE** - Core features
- **FiberHome** - Core features
- **Nokia** - Advanced features
- **TP-Link** - Advanced features
- **ASUS** - Full feature support
- **Netgear/Linksys/D-Link/Tenda/Xiaomi/Realme** - Core features
- **Generic** - Basic features

Each router brand has its own adapter implementing the `RouterAdapter` interface.

### Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **DI**: Hilt
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room (local caching)
- **Networking**: Retrofit + OkHttp
- **Charts**: MPAndroidChart
- **Image Loading**: Coil
- **Async**: Coroutines + Flow

### Project Structure
```
app/src/main/java/com/wificontrolpanel/
├── data/
│   ├── model/          # Data models (Device, NetworkStats, RouterInfo, etc.)
│   ├── repository/     # RouterRepository (single source of truth)
│   ├── router/         # RouterAdapter interface & base classes
│   └── adapter/        # Brand-specific implementations
├── ui/
│   ├── dashboard/      # Main dashboard screen
│   ├── devices/        # Connected devices management
│   ├── blocked/        # Blocked devices
│   ├── speed/          # Speed management
│   ├── mac/            # MAC address control
│   ├── parental/       # Parental controls
│   ├── security/       # Security center
│   ├── router/         # Router settings
│   ├── wifisettings/   # Wi-Fi configuration
│   ├── reports/        # Usage reports
│   └── settings/       # App settings
├── di/                 # Hilt modules
└── util/               # Utilities
```

## Security Features

- **Confirmation Dialogs** before critical actions:
  - Blocking a device
  - Turning Wi-Fi OFF
  - Restarting router
  - Changing Wi-Fi password/SSID
  - Applying speed limits

- **Feature Detection**: Shows "Feature not supported by this router" when a router doesn't support a specific function

- **Admin Authentication**: Secure admin password management

## Design System

- **Dark Blue & White Theme** with full Dark/Light mode support
- **Color Coding**:
  - Green: Active, Online, Unblock, Success
  - Red: Blocked, Offline, Stop, Error
  - Blue: Settings, Configuration, Info
  - Orange: Warnings, Paused, Attention
- **Modern Cards** with rounded corners (16dp)
- **Smooth Animations** and transitions
- **Mobile-First** responsive layout
- **Material3** components

## Requirements

- Android 7.0+ (API 24+)
- Compatible router with web API access
- Network connectivity to router

## Building

```bash
./gradlew assembleDebug
```

## Configuration

1. Open app → Settings
2. Select your router brand
3. Enter router IP, username, password
4. Tap "Connect to Router"
5. Start managing your network!

## License

Proprietary - All rights reserved