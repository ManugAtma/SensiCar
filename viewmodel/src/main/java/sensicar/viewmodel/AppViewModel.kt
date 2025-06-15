package sensicar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sensicar.model.Engine
import sensicar.sensor.MotionSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val motionSensorManager: MotionSensorManager) : ViewModel() {

    private var engine: Engine = Engine()

    var obstacleOffsets: List<StateFlow<Float>> = engine.obstacleOffsets
    var carPositionX: MutableStateFlow<Float> = engine.carPositionX

    var screenWidthDp = 300F
    var carWidth = 20F

    var remainingSec: StateFlow<Long> = engine.timer.seconds
    var remainingDeciSec: StateFlow<Long> = engine.timer.deciSeconds

    val distance = engine.distanceTracker.distance

    private val _gameEnded = MutableSharedFlow<Unit>()
    val gameEnded: SharedFlow<Unit> = _gameEnded

    val speed = engine.speed

    init {
        // timer ended
        viewModelScope.launch {
            engine.timer.timerEnded.collect {

                // TODO save result somewhere
                roundToOneDecimal(distance.value)

                _gameEnded.emit(Unit)
            }
        }

        // speed == 0
        viewModelScope.launch {
            engine.speed.collect { currentSpeed ->
                if (currentSpeed == 0f) {

                    // TODO save result somewhere
                    roundToOneDecimal(distance.value)
                    val secs = remainingSec
                    val deciSecs = remainingDeciSec

                    _gameEnded.emit(Unit)
                }
            }
        }
    }

    private var motionJob: Job? = null

    private fun roundToOneDecimal(distance: Float): Float {

        val coveredDistanceKm = distance / 1000
        val rounded = String.format("%.1f", coveredDistanceKm)
        val roundedAsFloat = rounded.toFloat()
        println(roundedAsFloat)
        return roundedAsFloat
    }

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

        engine._speed.value = 300F

        val leftScreenBorder = -(screenWidthDp / 2) + (carWidth / 2)
        val rightScreenBorder = (screenWidthDp / 2) - (carWidth / 2)

        // start collecting data from sensors
        motionJob = viewModelScope.launch {
            motionSensorManager.offsetX.collect { sensorData ->

                // make sure car does not overflow screen
                val clamped = sensorData.coerceIn(leftScreenBorder, rightScreenBorder)
                engine.carPositionX.value = clamped

                //engine.carPositionX.value = sensorData
            }
        }

        engine.start(viewModelScope)
    }

    fun stopEngine() {
        engine.stop()
        motionJob?.cancel()

        /*engine = Engine()
        obstacleOffsets = engine.obstacleOffsets
        carPositionX = engine.carPositionX*/
    }
}

