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
import sensicar.localplayerstats.contract.LocalPlayerStats
import sensicar.model.CrashCountDown
import sensicar.model.CrashState
import sensicar.viewmodel.contract.GameDataMediator

class GameDataMediatorImpl(
    private val motionSensorManager: MotionSensorManagerImpl,
    val applicationContext: Context,
    private val engine: EngineImpl,
    localPlayerStats: LocalPlayerStats
) : GameDataMediator, ViewModel() {

    //private val statsManager = StatsManager(applicationContext)
    private val statsManager = StatsManager(applicationContext, localPlayerStats)


    override val stats = MutableStateFlow<List<StatsDTO>>(emptyList())
    val navigateToLeaderboardsEvent = MutableSharedFlow<Unit>()

    override var obstacleOffsets: List<StateFlow<Float>> = engine.obstacleOffsets
    override var carPositionX: MutableStateFlow<Float> = engine.carPositionX

    var screenWidthDp = 300F
    var carWidth = 20F

    //var remainingSec: StateFlow<Long> = engine.timer.seconds
    override var remainingSec: StateFlow<Long> = engine.seconds

    //var remainingDeciSec: StateFlow<Long> = engine.timer.deciSeconds
    override var remainingDeciSec: StateFlow<Long> = engine.deciSeconds

    //val distance = engine.distanceTracker.distance
    override val distance = engine.distance

    private val _gameEnded = MutableSharedFlow<Unit>()
    override val gameEnded: SharedFlow<Unit> = _gameEnded

    override val speed = engine.speed

    var speedSetting = 300F

    var numberOfLanes = 0

    private var motionJob: Job? = null

    val streetOffset = engine.streetOffset

    val newCrashes = engine.newCrashes

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



    override fun setEngineScreenSize(screenHeightDp: Float, screenWidthDp: Float) {
        engine.setScreenSize(screenHeightDp, screenWidthDp)
    }

    override fun setEngineObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidth: Float) {
        engine.setObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
    }

    override fun setEngineLanes(numberOfLanes: Int) {
        this.numberOfLanes = numberOfLanes
        engine.setLanes(numberOfLanes, CrashCountDown(mutableMapOf<Int, CrashState>(), -1, -1))
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

    override fun startEngine() {

        //engine._speed.value = 300F
        engine.setSpeed(this.speedSetting)

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

    override fun observeMotionSensor(leftScreenBorder: Float, rightScreenBorder: Float) {

        motionJob = viewModelScope.launch {
            motionSensorManager.offsetX.collect { sensorData ->

                // make sure car does not overflow screen
                val clamped = sensorData.coerceIn(leftScreenBorder, rightScreenBorder)
                engine.carPositionX.value = clamped
            }
        }
    }

    override fun stopEngine(cause: Float) {
        engine.stop(cause)
        motionJob?.cancel()

        /*engine = Engine()
        obstacleOffsets = engine.obstacleOffsets
        carPositionX = engine.carPositionX*/
    }

    override fun updateAndShowStats(playerName: String) {
        this.statsManager.playerName = playerName

        viewModelScope.launch {
            statsManager.insertStat()
            stats.value = statsManager.getSavedStats()
            navigateToLeaderboardsEvent.emit(Unit)
        }
    }

    fun setSensitivity(sensitivity:Int){
        this.motionSensorManager.sensitivity = -sensitivity
    }

    fun getSensitivity() = -
    this.motionSensorManager.sensitivity

    override fun observeEngine(){}
}

