package com.wificontrolpanel.data.adapter

import com.wificontrolpanel.data.router.*

class RouterAdapterFactory {

    companion object {
        fun createAdapter(config: RouterConnectionConfig): RouterAdapter {
            return when (config.brand) {
                RouterBrand.HUAWEI -> HuaweiRouterAdapter(config)
                RouterBrand.ZTE -> ZteRouterAdapter(config)
                RouterBrand.FIBERHOME -> FiberHomeRouterAdapter(config)
                RouterBrand.NOKIA -> NokiaRouterAdapter(config)
                RouterBrand.TP_LINK -> TpLinkRouterAdapter(config)
                RouterBrand.ASUS -> AsusRouterAdapter(config)
                RouterBrand.NETGEAR -> NetgearRouterAdapter(config)
                RouterBrand.LINKSYS -> LinksysRouterAdapter(config)
                RouterBrand.D_LINK -> DLinkRouterAdapter(config)
                RouterBrand.TENDA -> TendaRouterAdapter(config)
                RouterBrand.XIAOMI -> XiaomiRouterAdapter(config)
                RouterBrand.REALME -> RealmeRouterAdapter(config)
                else -> GenericRouterAdapter(config)
            }
        }

        fun getSupportedBrands(): List<RouterBrand> {
            return listOf(
                RouterBrand.HUAWEI,
                RouterBrand.ZTE,
                RouterBrand.FIBERHOME,
                RouterBrand.NOKIA,
                RouterBrand.TP_LINK,
                RouterBrand.ASUS,
                RouterBrand.NETGEAR,
                RouterBrand.LINKSYS,
                RouterBrand.D_LINK,
                RouterBrand.TENDA,
                RouterBrand.XIAOMI,
                RouterBrand.REALME,
                RouterBrand.GENERIC
            )
        }

        fun getBrandDisplayName(brand: RouterBrand): String {
            return when (brand) {
                RouterBrand.HUAWEI -> "Huawei"
                RouterBrand.ZTE -> "ZTE"
                RouterBrand.FIBERHOME -> "FiberHome"
                RouterBrand.NOKIA -> "Nokia"
                RouterBrand.TP_LINK -> "TP-Link"
                RouterBrand.ASUS -> "ASUS"
                RouterBrand.NETGEAR -> "Netgear"
                RouterBrand.LINKSYS -> "Linksys"
                RouterBrand.D_LINK -> "D-Link"
                RouterBrand.TENDA -> "Tenda"
                RouterBrand.XIAOMI -> "Xiaomi"
                RouterBrand.REALME -> "Realme"
                RouterBrand.GENERIC -> "Generic/Other"
            }
        }
    }
}

// Stub adapters for other brands
class ZteRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.INTERNET_PAUSE_RESUME,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.GUEST_WIFI,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to ZTE router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class FiberHomeRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to FiberHome router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class NokiaRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.INTERNET_PAUSE_RESUME,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.SPEED_LIMIT_PER_DEVICE,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Nokia router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class TpLinkRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.INTERNET_PAUSE_RESUME,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.SPEED_LIMIT_PER_DEVICE,
        RouterFeature.GUEST_WIFI,
        RouterFeature.BAND_2_4GHZ_CONTROL,
        RouterFeature.BAND_5GHZ_CONTROL,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.PARENTAL_CONTROL,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to TP-Link router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class AsusRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.INTERNET_PAUSE_RESUME,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.SPEED_LIMIT_PER_DEVICE,
        RouterFeature.SPEED_LIMIT_TOTAL,
        RouterFeature.GUEST_WIFI,
        RouterFeature.BAND_2_4GHZ_CONTROL,
        RouterFeature.BAND_5GHZ_CONTROL,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.MAC_FILTERING,
        RouterFeature.PARENTAL_CONTROL,
        RouterFeature.QOS,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to ASUS router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class NetgearRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.GUEST_WIFI,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Netgear router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class LinksysRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.GUEST_WIFI,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Linksys router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class DLinkRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to D-Link router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class TendaRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Tenda router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class XiaomiRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.INTERNET_PAUSE_RESUME,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.SPEED_LIMIT_PER_DEVICE,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.GUEST_WIFI,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Xiaomi router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class RealmeRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to Realme router")
    override fun startPolling() {}
    override fun stopPolling() {}
}

class GenericRouterAdapter(config: RouterConnectionConfig) : BaseRouterAdapter(config) {
    override fun getSupportedFeatures(): Set<RouterFeature> = setOf(
        RouterFeature.WIFI_ON_OFF,
        RouterFeature.DEVICE_BLOCK_UNBLOCK,
        RouterFeature.CHANGE_SSID,
        RouterFeature.CHANGE_PASSWORD,
        RouterFeature.ROUTER_RESTART
    )
    override suspend fun performConnection(config: RouterConnectionConfig): RouterOperationResult = 
        RouterOperationResult(true, "Connected to generic router")
    override fun startPolling() {}
    override fun stopPolling() {}
}