package com.example.rfpresence

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.guolindev.permissionx.PermissionX

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvStatus: TextView
    private lateinit var tvDevices: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        tvStatus = findViewById(R.id.tvStatus)
        tvDevices = findViewById(R.id.tvDevices)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        
        requestPermissions()
        
        btnStart.setOnClickListener {
            startPresenceDetection()
        }
        
        btnStop.setOnClickListener {
            stopPresenceDetection()
        }
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        
        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    tvStatus.text = "Permissions granted. Ready to scan."
                } else {
                    tvStatus.text = "Some permissions denied. App may not work."
                }
            }
    }
    
    private fun startPresenceDetection() {
        val intent = Intent(this, PresenceService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        tvStatus.text = "Scanning for devices..."
        btnStart.isEnabled = false
        btnStop.isEnabled = true
    }
    
    private fun stopPresenceDetection() {
        val intent = Intent(this, PresenceService::class.java)
        stopService(intent)
        tvStatus.text = "Stopped scanning"
        btnStart.isEnabled = true
        btnStop.isEnabled = false
    }
    
    fun updateDeviceList(devices: String) {
        runOnUiThread {
            tvDevices.text = devices
        }
    }
}
