package com.example.rfpresence

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class PresenceService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var wifiScanner: WifiScanner
    private lateinit var bleScanner: BLEScanner
    
    companion object {
        const val CHANNEL_ID = "RF_PRESENCE_CHANNEL"
        const val NOTIFICATION_ID = 1001
    }
    
    override fun onCreate() {
        super.onCreate()
        wifiScanner = WifiScanner(this)
        bleScanner = BLEScanner(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startScanning()
        return START_STICKY
    }
    
    private fun startScanning() {
        serviceScope.launch {
            while (isActive) {
                val wifiDevices = wifiScanner.scan()
                val bleDevices = bleScanner.scan()
                val allDevices = wifiDevices + bleDevices
                
                var deviceList = "No devices found nearby\n"
                if (allDevices.isNotEmpty()) {
                    deviceList = "Found ${allDevices.size} device(s):\n\n"
                    allDevices.forEach {
                        deviceList += "📱 ${it.name}\n"
                        deviceList += "   Signal: ${it.rssi} dBm\n"
                        deviceList += "   Distance: ${String.format("%.2f", it.distanceMeters)} m\n\n"
                    }
                }
                
                val activity = MainActivity()
                activity.updateDeviceList(deviceList)
                
                delay(5000)
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RF Presence Detection",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RF Presence Detector")
            .setContentText("Scanning for devices...")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        wifiScanner.close()
        bleScanner.close()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}
