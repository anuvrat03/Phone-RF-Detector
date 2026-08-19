package com.example.rfpresence

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build

class BLEScanner(private val context: Context) {
    
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val discoveredDevices = mutableListOf<DeviceInfo>()
    
    fun scan(): List<DeviceInfo> {
        discoveredDevices.clear()
        
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return emptyList()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = object : android.bluetooth.le.ScanCallback() {
                override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
                    result?.let {
                        val device = it.device
                        val rssi = it.rssi
                        val distance = calculateDistance(rssi)
                        if (distance <= 5.0) {
                            discoveredDevices.add(
                                DeviceInfo(
                                    name = device.name ?: "Unknown BLE",
                                    rssi = rssi,
                                    distanceMeters = distance,
                                    type = "BLE"
                                )
                            )
                        }
                    }
                }
            }
            
            val scanner = bluetoothAdapter.bluetoothLeScanner
            scanner.startScan(callback)
            Thread.sleep(2000)
            scanner.stopScan(callback)
            
        } else {
            bluetoothAdapter.startLeScan { device, rssi, _ ->
                val distance = calculateDistance(rssi)
                if (distance <= 5.0) {
                    discoveredDevices.add(
                        DeviceInfo(
                            name = device.name ?: "Unknown BLE",
                            rssi = rssi,
                            distanceMeters = distance,
                            type = "BLE"
                        )
                    )
                }
            }
            Thread.sleep(2000)
            bluetoothAdapter.stopLeScan(null)
        }
        
        return discoveredDevices.distinctBy { it.name }
    }
    
    private fun calculateDistance(rssi: Int): Double {
        val txPower = -65.0
        val n = 3.5
        
        if (rssi >= -30) return 0.3
        if (rssi <= -100) return 10.0
        
        val ratio = (txPower - rssi) / (10 * n)
        val distance = Math.pow(10.0, ratio)
        
        return distance.coerceIn(0.1, 10.0)
    }
    
    fun close() {}
}
