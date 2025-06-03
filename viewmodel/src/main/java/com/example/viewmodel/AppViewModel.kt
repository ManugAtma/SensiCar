package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Engine
import com.example.sensor.MotionSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val motionSensorManager: MotionSensorManager) : ViewModel() {

    private var engine: Engine = Engine()

    // val offsetY: StateFlow<Float> = engine.offsetY
    // val offsetY: StateFlow<Float> = engine.obstacleOffsets[0]
    var obstacleOffsets: List<StateFlow<Float>> = engine.obstacleOffsets
    var carPositionX: MutableStateFlow<Float> = engine.carPositionX

    var remainingSec: StateFlow<Long> = engine.timer.seconds
    var remainingDeciSec: StateFlow<Long> = engine.timer.deciSeconds

    private var motionJob: Job? = null

    /*  init {
          viewModelScope.launch {
              motionSensorManager.offsetX.collect{ sensorData ->
                  engine.carPositionX.value = sensorData
              }
          }
      }*/

    fun setEngineScreenSize(screenHeightDp: Float, screenWidthDp: Float) {
        engine.setScreenSize(screenHeightDp, screenWidthDp)
    }

    fun setEngineObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidth: Float) {
        engine.setObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
    }

    fun setEngineLanes(numberOfLanes: Int) {
        engine.setLanes(numberOfLanes)
    }

    fun setEngineCarPositionY(position: Float) {
        engine.carPositionY = position
    }

    fun startEngine() {

        engine.speed = 300F

        // start collecting data from sensors
        motionJob = viewModelScope.launch {
            motionSensorManager.offsetX.collect { sensorData ->
                engine.carPositionX.value = sensorData
            }
        }

        engine.start(viewModelScope)
    }

    fun stopEngine() {
        engine.stop()
        /*engine = Engine()
        obstacleOffsets = engine.obstacleOffsets
        carPositionX = engine.carPositionX*/
    }
}

