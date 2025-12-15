package com.example.finalproject.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    
    private var lastUpdate: Long = 0
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    
    companion object {
        private const val SHAKE_THRESHOLD = 800
        private const val MIN_TIME_BETWEEN_SHAKES = 1000L
    }
    
    private var lastShakeTime: Long = 0

    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return
        
        val currentTime = System.currentTimeMillis()
        
        if ((currentTime - lastUpdate) > 100) {
            val diffTime = currentTime - lastUpdate
            lastUpdate = currentTime
            
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            val speed = sqrt(
                ((x - lastX) * (x - lastX) + 
                 (y - lastY) * (y - lastY) + 
                 (z - lastZ) * (z - lastZ)).toDouble()
            ) / diffTime * 10000
            
            if (speed > SHAKE_THRESHOLD) {
                if ((currentTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES) {
                    lastShakeTime = currentTime
                    onShake()
                }
            }
            
            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
