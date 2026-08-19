package com.example.rfpresence

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

data class DeviceInfo(
    val name: String,
    val rssi: Int,
    val distanceMeters: Double,
    val type: String
)

class WifiScanner(private val context: Context) {
    
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    fun scan(): List<DeviceInfo> {
        val devices = mutableListOf<DeviceInfo>()
        
        // Check for location permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            return devices
        }
        
        // Start Wi-Fi scan
        val success = wifiManager.startScan()
        if (success) {
            val results = wifiManager.scanResults
            for (result in results) {
                val distance = calculateDistance(result.level)
                // Only show devices within 5 meters
                if (distance <= 5.0) {
                    devices.add(
                        DeviceInfo(
                            name = result.SSID.ifEmpty { "Unknown WiFi" },
                            rssi = result.level,
                            distanceMeters = distance,
                            type = "WiFi"
                        )
                    )
                }
            }
        }
        
        return devices
    }
    
    private fun calculateDistance(rssi: Int): Double {
        val txPower = -59.0 // Signal strength at 1 meter
        val n = 2.5 // Environmental factor
        
        if (rssi >= -30) return 0.5
        if (rssi <= -100) return 10.0
        
        val ratio = (txPower - rssi) / (10 * n)
        val distance = Math.pow(10.0, ratio) // This is the correct way
        
        return distance.coerceIn(0.1, 10.0)
    }
    
    fun close() {
        // Cleanup if needed
    }
}
