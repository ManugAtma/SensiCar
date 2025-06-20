package sensicar.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class Engine {

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
    private val crashCountdowns = mutableListOf<CrashCountDown>()

    private val obstacleProbability = 0.5F
    var _speed = MutableStateFlow(300f)
    var speed: StateFlow<Float> = _speed
    private var crashPenalty = _speed.value * 0.25F

    var carPositionX = MutableStateFlow(0F)
    var carPositionY = 0F

    private val _obstacleOffsets = mutableListOf<MutableStateFlow<Float>>()
    val obstacleOffsets: List<StateFlow<Float>> get() = _obstacleOffsets

    private val raceDurationInMillis = 60000L
    val timer = RaceTimer(raceDurationInMillis)
    val distanceTracker = DistanceTracker(_speed)


    fun setScreenSize(screenHeight: Float, screenWidth: Float) {
        this.screenHeight = screenHeight
        this.screenWidth = screenWidth
        this.screenCenterX = screenWidth / 2F
    }

    fun setObjectSizes(obstacleHeightDp: Float, obstacleWidthDp: Float, carWidthDp: Float) {
        this.obstacleHeightDp = obstacleHeightDp
        this.obstacleWidthDp = obstacleWidthDp
        // println("obstacle height: $obstacleHeightDp")
        this.carWidthDp = carWidthDp
        this.carWidthDivByTwo = carWidthDp / 2
        this.carHeightDp = obstacleHeightDp
    }

    fun setLanes(numberOfLanes: Int) {
        setNumberOfLanes(numberOfLanes)
        setLaneBorders(numberOfLanes)
        for (i in 0..<numberOfLanes) {
            crashesByLane[i] = CrashState(false, false)
        }
        setCrashCountdowns(numberOfLanes)
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
        }
    }

    private fun setCrashCountdowns(laneNumber: Int) {
        val wait = ((obstacleHeightDp / _speed.value) * 1000).toLong()
        for (i in 0..<laneNumber) {
            this.crashCountdowns.add(CrashCountDown(crashesByLane, i, wait))
        }
    }


    fun start(scope: CoroutineScope) {
        //println("car top: $carPositionY, car bottom: ${carPositionY + carHeightDp} ")

        timer.start()
        distanceTracker.start()

        repeat(numberOfLanes) { lane ->
            scope.launch {
                startLane(lane)
            }
        }
    }

    private suspend fun startLane(laneNumber: Int) {
        while (_speed.value > 0) {

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
        while (bottomNotReached && _speed.value > 0) {
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

    private fun newCrash(laneNumber: Int){
        _speed.value -= crashPenalty

        //println("crash at lane $laneNumber, obstacle top: ${_obstacleOffsets[laneNumber].value}, obstacle bottom: ${_obstacleOffsets[laneNumber].value + obstacleHeightDp}, carPositionY $carPositionY ")
        crashesByLane[laneNumber]?.blocked = true
        crashesByLane[laneNumber]?.currentlyCrashed = true
        val wait = ((obstacleHeightDp / _speed.value) * 1000).toLong() * 2
        //println("wait: $wait")
        crashCountdowns[laneNumber] = CrashCountDown(crashesByLane, laneNumber, wait)
        crashCountdowns[laneNumber].start()
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

    fun stop(){
        this._speed.value = 0F;
        this.timer.stop()
        this.distanceTracker.stop()
    }

    class LaneBorders(val left: Float, val right: Float)
}