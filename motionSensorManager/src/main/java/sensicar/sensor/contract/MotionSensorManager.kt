package sensicar.sensor.contract

import android.hardware.SensorEventListener
import kotlinx.coroutines.flow.StateFlow

interface MotionSensorManager: SensorEventListener {

    val offsetX: StateFlow<Float>
    var sensitivity: Int
}