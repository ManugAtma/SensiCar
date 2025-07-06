package sensicar.model.contract

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import sensicar.model.CrashCountDown
import sensicar.model.RaceTimer

interface Engine {

    val distance: MutableStateFlow<Float>
    val seconds: StateFlow<Long>
    val deciSeconds: StateFlow<Long>
    val timerEnded: SharedFlow<Unit>
    val carPositionX: MutableStateFlow<Float>
    val obstacleOffsets: List<StateFlow<Float>>
    val newCrashes: List<SharedFlow<Unit>>
    val speed: StateFlow<Float>

    fun start(scope: CoroutineScope)

    fun stop(cause: Float)

    fun setObstacleProbability(p: Float)

    fun setScreenSize(screenHeight: Float, screenWidth: Float)

    fun setObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidthDp: Float)

    fun setLanes(numberOfLanes: Int, crashCountDown: CrashCountDown)

    fun setSpeed(speed: Float)

    fun setRaceDuration(raceDurationMs: Long)

    fun setRaceTimer(timer: RaceTimer)
}