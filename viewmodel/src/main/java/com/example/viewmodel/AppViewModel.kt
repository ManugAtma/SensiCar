package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Engine
import com.example.sensor.MotionSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val motionSensorManager: MotionSensorManager) : ViewModel() {

    private var engine: Engine = Engine()

    // val offsetY: StateFlow<Float> = engine.offsetY
    // val offsetY: StateFlow<Float> = engine.obstacleOffsets[0]
    val obstacleOffsets: List<StateFlow<Float>> = engine.obstacleOffsets
    val carPositionX: MutableStateFlow<Float> = engine.carPositionX

    init {
        viewModelScope.launch {
            motionSensorManager.offsetX.collect{ sensorData ->
                engine.carPositionX.value = sensorData
            }
        }
    }

    fun setEngineScreenSize(screenHeightDp: Float, screenWidthDp: Float) {
        engine.setScreenSize(screenHeightDp, screenWidthDp)
    }

    fun setEngineObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float) {
        engine.setObjectSize(obstacleHeightDp, obstacleWidthDp)
    }

    fun setEngineNumberOfLanes(numberOfLanes: Int) {
        engine.setNumberOfLanes(numberOfLanes)
    }

    fun startEngine() {
        engine.start(viewModelScope)
    }
}

