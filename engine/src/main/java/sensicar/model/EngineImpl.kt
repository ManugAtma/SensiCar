package sensicar.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sensicar.model.contract.Engine
import kotlin.random.Random

class EngineImpl : Engine {

    private var screenHeight: Float = 800F
    private var screenWidth: Float = 300F
    private var screenCenterX = 150F
    private var obstacleHeightDp = 0F
    private var obstacleWidthDp = 0F
    private var carHeightDp = 0F
    private var carWidthDp = 0F
    private var carWidthDivByTwo = 0F
    private var leftCarBorderCenter = screenCenterX - carWidthDivByTwo
    private var rightCarBorderCenter = screenCenterX + carWidthDivByTwo
    private var numberOfLanes = 0
    private val laneBorders = mutableMapOf<Int, LaneBorders>()
    private val crashesByLane = mutableMapOf<Int, CrashState>()

    //private val crashCountdowns = mutableListOf<CrashCountDown>()
    var crashCountdowns: MutableList<CrashCountDown>? = null


    private var obstacleProbability = 0.5F
    val _speed = MutableStateFlow(300f)
    override val speed: StateFlow<Float> = _speed
    private var crashPenalty = _speed.value * 0.25F

    override val carPositionX = MutableStateFlow(0F)
    var carPositionY = 0F

    private val _obstacleOffsets = mutableListOf<MutableStateFlow<Float>>()
    override val obstacleOffsets: List<StateFlow<Float>> get() = _obstacleOffsets

    private var raceDurationInMillis = 60000L

    //var timer = RaceTimer(raceDurationInMillis, this)
    var timer: RaceTimer? = null

    internal val _seconds = MutableStateFlow(raceDurationInMillis)
    override val seconds: StateFlow<Long> = _seconds

    internal var _deciSeconds = MutableStateFlow(0L)
    override val deciSeconds: StateFlow<Long> = _deciSeconds

    internal val _timerEnded = MutableSharedFlow<Unit>()
    override val timerEnded: SharedFlow<Unit> = _timerEnded

    private val distanceTracker = DistanceTracker(_speed, this)
    override val distance = MutableStateFlow(0F)

    override val newCrashes = mutableListOf<MutableSharedFlow<Unit>>()

    var crashCountDownFactory: CrashCountDown? = null

    override fun setSpeed(speed: Float) {
        this._speed.value = speed
        this.crashPenalty = _speed.value * 0.25F
    }

    override fun setRaceDuration(raceDurationMs: Long) {
        this.raceDurationInMillis = raceDurationMs
    }

    override fun setScreenSize(screenHeight: Float, screenWidth: Float) {
        this.screenHeight = screenHeight
        this.screenWidth = screenWidth
        this.screenCenterX = screenWidth / 2F
    }

    override fun setObjectSizes(
        obstacleHeightDp: Float,
        obstacleWidthDp: Float,
        carWidthDp: Float
    ) {
        this.obstacleHeightDp = obstacleHeightDp
        this.obstacleWidthDp = obstacleWidthDp
        // println("obstacle height: $obstacleHeightDp")
        this.carWidthDp = carWidthDp
        this.carWidthDivByTwo = carWidthDp / 2
        this.carHeightDp = obstacleHeightDp

        this.leftCarBorderCenter = screenCenterX - carWidthDivByTwo
        this.rightCarBorderCenter = screenCenterX + carWidthDivByTwo
    }

    override fun setLanes(numberOfLanes: Int, crashCountDown: CrashCountDown) {
        setNumberOfLanes(numberOfLanes)
        setLaneBorders(numberOfLanes)
        for (i in 0..<numberOfLanes) {
            crashesByLane[i] = CrashState(false, false)
            newCrashes.add(MutableSharedFlow()) // new
        }
        this.crashCountDownFactory = crashCountDown
        setCrashCountdowns(numberOfLanes, crashCountDown)
    }

    override fun setRaceTimer(timer: RaceTimer) {
        this.timer = timer
    }

    private fun setNumberOfLanes(numberOfLanes: Int) {
        this.numberOfLanes = numberOfLanes
        // clear data from last game
        _obstacleOffsets.clear()
        for (i in 0..<numberOfLanes) {
            _obstacleOffsets.add(MutableStateFlow(-100F))
        }
    }

    private fun setLaneBorders(numberOfLanes: Int) {
        for (i in 0..<numberOfLanes) {
            val left = i * obstacleWidthDp
            val right = left + obstacleWidthDp
            // println("lane $i : $left / $right")
            laneBorders[i] = LaneBorders(left, right)
            println("lane $i: left: $left, right: $right")
        }
    }

    private fun setCrashCountdowns(laneNumber: Int, crashCountDown: CrashCountDown) {
        val wait = ((obstacleHeightDp / _speed.value) * 1000).toLong()
        for (i in 0..<laneNumber) {
            //this.crashCountdowns?.add(CrashCountDown(crashesByLane, i, wait))
            this.crashCountdowns?.add(crashCountDown.getInstance(crashesByLane, i, wait))

        }
    }

    override fun setObstacleProbability(p: Float) {
        this.obstacleProbability = p
    }


    override fun start(scope: CoroutineScope) {

        timer?.start()
        distanceTracker.start()

        repeat(numberOfLanes) { lane ->
            scope.launch {
                startLane(lane)
            }
        }
    }

    private suspend fun startLane(laneNumber: Int) {

        while (_speed.value > 0.02) {

            if (laneNumber == 1) println("seconds " + seconds.value)

            val randomTime = Random.nextLong(0, 1000)
            delay(randomTime)

            val randomProbability = Random.nextFloat()

            if (randomProbability > obstacleProbability) {
                this.moveObstacle(laneNumber)
            }
        }
    }

    private suspend fun moveObstacle(laneNumber: Int) {

        _obstacleOffsets[laneNumber].value = -obstacleHeightDp
        var lastTime = System.currentTimeMillis()

        var bottomNotReached = true
        while (bottomNotReached && _speed.value > 0.02) {
            val now = System.currentTimeMillis()
            val delta = (now - lastTime) / 1000F
            _obstacleOffsets[laneNumber].value += _speed.value * delta

            if (this.crashed(laneNumber)) this.newCrash(laneNumber)

            lastTime = now
            delay(16) // ~60 frames per second

            if (_obstacleOffsets[laneNumber].value > screenHeight + screenHeight / 6F) bottomNotReached =
                false
        }
    }

    private suspend fun newCrash(laneNumber: Int) {
        _speed.value -= crashPenalty

        //println("crash at lane $laneNumber, obstacle top: ${_obstacleOffsets[laneNumber].value}, obstacle bottom: ${_obstacleOffsets[laneNumber].value + obstacleHeightDp}, carPositionY $carPositionY ")
        crashesByLane[laneNumber]?.blocked = true
        crashesByLane[laneNumber]?.currentlyCrashed = true
        val wait = ((obstacleHeightDp / _speed.value) * 1000).toLong() * 2
        //println("wait: $wait")
        //crashCountdowns[laneNumber] = CrashCountDown(crashesByLane, laneNumber, wait)
        //crashCountdowns[laneNumber]?.start()
        //crashCountdowns?.set(laneNumber, CrashCountDown(crashesByLane, laneNumber, wait))
        this.crashCountDownFactory?.getInstance(crashesByLane, laneNumber, wait)
            ?.let { crashCountdowns?.set(laneNumber, it) }
        crashCountdowns?.get(laneNumber)?.start()
        // new
        newCrashes[laneNumber].emit(Unit)
        println(
            "crash at lane " +
                    "$laneNumber, " +
                    "left border: ${laneBorders[laneNumber]?.left} ,right border: ${laneBorders[laneNumber]?.right}" +
                    " ,car position: ${carPositionX.value}"
        )
    }

    private fun crashed(laneNumber: Int): Boolean {

        val currentOffsetY = _obstacleOffsets[laneNumber].value
        val leftCarBorder = leftCarBorderCenter + carPositionX.value
        val rightCarBorder = rightCarBorderCenter + carPositionX.value

        return (!crashesByLane[laneNumber]!!.blocked
                && currentOffsetY + obstacleHeightDp > carPositionY
                && currentOffsetY < carPositionY + carHeightDp
                &&
                (((leftCarBorder >= laneBorders[laneNumber]!!.left) && (leftCarBorder <= laneBorders[laneNumber]!!.right))
                        || ((rightCarBorder >= laneBorders[laneNumber]!!.left)) && (rightCarBorder <= laneBorders[laneNumber]!!.right)))
    }

    override fun stop(cause: Float) {
        this._speed.value = cause;
        this.timer?.stop()
        this.distanceTracker.stop()
    }

    class LaneBorders(val left: Float, val right: Float)
}