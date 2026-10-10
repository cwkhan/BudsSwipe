package com.example.engine

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.core.content.ContextCompat

class BluetoothMonitor(private val context: Context) {

    data class BluetoothState(
        val isConnected: Boolean,
        val connectedDeviceName: String?,
        val hasPermission: Boolean
    )

    fun checkBluetoothStatus(): BluetoothState {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        // Check using AudioManager (works even without BLUETOOTH_CONNECT for general detection)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        var isAudioConnected = false
        var deviceName: String? = null

        if (audioManager != null) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                    device.type == AudioDeviceInfo.TYPE_BLE_HEADSET
                ) {
                    isAudioConnected = true
                    deviceName = device.productName?.toString()
                    break
                }
            }
        }

        // If permission is available, try getting specific paired/connected device info
        if (hasPermission) {
            try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val adapter = bluetoothManager?.adapter
                if (adapter != null && adapter.isEnabled) {
                    // Check connected profile devices
                    @SuppressLint("MissingPermission")
                    val bondedDevices = adapter.bondedDevices
                    if (deviceName == null && !bondedDevices.isNullOrEmpty()) {
                        // Find first likely audio device (earbuds/headphones)
                        for (dev in bondedDevices) {
                            if (dev.name?.contains("Buds", ignoreCase = true) == true ||
                                dev.name?.contains("Realme", ignoreCase = true) == true ||
                                dev.name?.contains("Ear", ignoreCase = true) == true ||
                                dev.name?.contains("Wireless", ignoreCase = true) == true
                            ) {
                                deviceName = dev.name
                                break
                            }
                        }
                    }
                }
            } catch (_: SecurityException) {
                // Handled gracefully
            }
        }

        return BluetoothState(
            isConnected = isAudioConnected,
            connectedDeviceName = deviceName ?: if (isAudioConnected) "Bluetooth Earbuds Connected" else null,
            hasPermission = hasPermission
        )
    }
}
