package sensicar.viewmodel.contract

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import sensicar.dto.StatsDTO

interface GameDataMediator {

    val stats: MutableStateFlow<List<StatsDTO>>

    var obstacleOffsets: List<StateFlow<Float>>
    var carPositionX: MutableStateFlow<Float>
    var remainingSec: StateFlow<Long>
    var remainingDeciSec: StateFlow<Long>
    val distance: MutableStateFlow<Float>
    val speed: MutableStateFlow<Float>
    val gameEnded: SharedFlow<Unit>


    fun setEngineScreenSize(screenHeightDp: Float, screenWidthDp: Float)
    fun setEngineObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidth: Float)
    fun setEngineLanes(numberOfLanes: Int)

    fun observeEngine() // see init blocks of view model
    fun startEngine()
    fun stopEngine(cause: Float)

    fun observeMotionSensor(leftScreenBorder: Float, rightScreenBorder: Float)

    fun updateStats(playerName: String)
}