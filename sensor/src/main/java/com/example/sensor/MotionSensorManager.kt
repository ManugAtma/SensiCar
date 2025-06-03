package com.example.sensor

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MotionSensorManager(private val context: Context) : SensorEventListener {

    private val _offsetX = MutableStateFlow(0F)
    val offsetX: StateFlow<Float> = _offsetX
    private var sensorManager: SensorManager =
        context.getSystemService(SENSOR_SERVICE) as SensorManager

    init {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this, it, SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    var gravity = 1F
    val alpha = 0.8F

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            gravity = alpha * gravity + (1 - alpha) * event.values[0]
            _offsetX.value = gravity * -40

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // handle accuracy change
    }
}
