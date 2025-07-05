package sensicar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sensicar.model.EngineImpl
import sensicar.sensor.MotionSensorManagerImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sensicar.dto.StatsDTO

class AppViewModel(
    private val motionSensorManager: MotionSensorManagerImpl,
    val applicationContext: Context,
    private val engine: EngineImpl
) : ViewModel() {

    //private var engine: EngineImpl = EngineImpl()
    private val statsManager = StatsManager(applicationContext)

    val stats = MutableStateFlow<List<StatsDTO>>(emptyList())
    val navigateToLeaderboardsEvent = MutableSharedFlow<Unit>()

    var obstacleOffsets: List<StateFlow<Float>> = engine.obstacleOffsets
    var carPositionX: MutableStateFlow<Float> = engine.carPositionX

    var screenWidthDp = 300F
    var carWidth = 20F

    //var remainingSec: StateFlow<Long> = engine.timer.seconds
    var remainingSec: StateFlow<Long> = engine.seconds

    //var remainingDeciSec: StateFlow<Long> = engine.timer.deciSeconds
    var remainingDeciSec: StateFlow<Long> = engine.deciSeconds

    //val distance = engine.distanceTracker.distance
    val distance = engine.distance

    private val _gameEnded = MutableSharedFlow<Unit>()
    val gameEnded: SharedFlow<Unit> = _gameEnded

    val speed = engine.speed

    var numberOfLanes = 0

    private var motionJob: Job? = null

    companion object {
        const val QUIT = 0.01F
        const val NO_QUIT = 0.02F
    }

    init {
        // timer ended
        viewModelScope.launch {
            //engine.timer.timerEnded.collect {
            engine.timerEnded.collect {
                statsManager.setCurrentEngineStats(
                    remainingSec.value,
                    remainingDeciSec.value,
                    distance.value,
                    "time up"
                )
                println("time up")
                _gameEnded.emit(Unit)
            }
        }


        // speed == 0
        viewModelScope.launch {
            engine.speed.collect { currentSpeed ->

                if (currentSpeed == 0f) {

                    statsManager.setCurrentEngineStats(
                        remainingSec.value,
                        remainingDeciSec.value,
                        distance.value,
                        "crashed"
                    )
                    println("crashed")
                    _gameEnded.emit(Unit)
                }
            }
        }

        viewModelScope.launch {
            engine.speed.collect { currentSpeed ->

                if (currentSpeed == 0.01f) {
                    statsManager.setCurrentEngineStats(
                        remainingSec.value,
                        remainingDeciSec.value,
                        distance.value,
                        "quit"
                    )
                    println("quit")
                }
            }
        }

        viewModelScope.launch {
            stats.value = statsManager.getSavedStats()
        }
    }



    fun setEngineScreenSize(screenHeightDp: Float, screenWidthDp: Float) {
        engine.setScreenSize(screenHeightDp, screenWidthDp)
    }

    fun setEngineObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidth: Float) {
        engine.setObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
    }

    fun setEngineLanes(numberOfLanes: Int) {
        this.numberOfLanes = numberOfLanes
        engine.setLanes(numberOfLanes)
        this.observeCrashes(numberOfLanes)
    }

    private fun observeCrashes(numberOfLanes: Int) {

        for (i in 0..<numberOfLanes) {
            viewModelScope.launch {
                engine.newCrashes[i].collect {
                    println("new crash at lane $i")
                }
            }
        }

    }

    fun setEngineCarPositionY(position: Float) {
        engine.carPositionY = position
    }

    fun startEngine() {

        engine._speed.value = 300F

        val leftScreenBorder = -(screenWidthDp / 2) + (carWidth / 2)
        val rightScreenBorder = (screenWidthDp / 2) - (carWidth / 2)

        /* // start collecting data from sensors
         motionJob = viewModelScope.launch {
             motionSensorManager.offsetX.collect { sensorData ->

                 // make sure car does not overflow screen
                 val clamped = sensorData.coerceIn(leftScreenBorder, rightScreenBorder)
                 engine.carPositionX.value = clamped

                 //engine.carPositionX.value = sensorData
             }
         }*/

        observeMotionSensor(leftScreenBorder, rightScreenBorder)

        engine.start(viewModelScope)
    }

    fun observeMotionSensor(leftScreenBorder: Float, rightScreenBorder: Float) {

        motionJob = viewModelScope.launch {
            motionSensorManager.offsetX.collect { sensorData ->

                // make sure car does not overflow screen
                val clamped = sensorData.coerceIn(leftScreenBorder, rightScreenBorder)
                engine.carPositionX.value = clamped
            }
        }
    }

    fun stopEngine(cause: Float) {
        engine.stop(cause)
        motionJob?.cancel()

        /*engine = Engine()
        obstacleOffsets = engine.obstacleOffsets
        carPositionX = engine.carPositionX*/
    }

    fun updateAndShowStats(playerName: String) {
        this.statsManager.playerName = playerName

        viewModelScope.launch {
            statsManager.insertStat()
            stats.value = statsManager.getSavedStats()
            navigateToLeaderboardsEvent.emit(Unit)
        }
    }

    //fun getStats() = stats.value
}

