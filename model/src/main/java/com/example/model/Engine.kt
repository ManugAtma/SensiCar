package com.example.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class Engine {

    private var screenHeight: Float = 800F
    private var screenWidth: Float = 300F
    private var obstacleHeightDp = 0F
    private var obstacleWidthDp = 0F
    private var numberOfLanes = 0

    private val obstacleProbability = 0.5F
    private var speed = 300F

    var carPositionX = MutableStateFlow(0F)

    private val _obstacleOffsets = mutableListOf<MutableStateFlow<Float>>()
    val obstacleOffsets: List<StateFlow<Float>> get() = _obstacleOffsets

   /* private val _offsetY = MutableStateFlow(-100F) //(-obstacleHeightDp)
    val offsetY: StateFlow<Float> = _offsetY
    private val _obstacleOffsets = List(5) { MutableStateFlow(-100F) }
    val obstacleOffsets: List<StateFlow<Float>> = _obstacleOffsets
    private var lastTime = System.currentTimeMillis()*/


    fun setScreenSize(screenHeight: Float, screenWidth: Float) {
        this.screenHeight = screenHeight
        this.screenWidth = screenWidth
    }

    fun setObjectSize(obstacleHeightDp: Float, obstacleWidthDp: Float) {
        this.obstacleHeightDp = obstacleHeightDp
        this.obstacleWidthDp = obstacleWidthDp
    }

    fun setNumberOfLanes(numberOfLanes: Int) {
        this.numberOfLanes = numberOfLanes
        for (i in 0..<numberOfLanes) {
            _obstacleOffsets.add(MutableStateFlow(-100F))
        }
    }

    fun start(scope: CoroutineScope) {
        repeat(numberOfLanes) { lane ->
            scope.launch {
                startLane(lane)
            }
        }
    }

    private suspend fun startLane(laneNumber: Int) {
        while (true) {

            val randomTime = Random.nextLong(0, 1000)
            delay(randomTime)

            val randomProbability = Random.nextFloat()
            println(randomProbability)

            if (randomProbability > obstacleProbability) {
                this.moveObstacle(laneNumber)
            }
        }
    }

    private suspend fun moveObstacle(laneNumber: Int) {
        _obstacleOffsets[laneNumber].value = -obstacleHeightDp
        var lastTime = System.currentTimeMillis()

        var bottomNotReached = true
        while (bottomNotReached) {
            val now = System.currentTimeMillis()
            val delta = (now - lastTime) / 1000F
            _obstacleOffsets[laneNumber].value += speed * delta
            lastTime = now

            delay(16) // ~60 frames per second

            if (_obstacleOffsets[laneNumber].value > screenHeight + screenHeight / 6F) bottomNotReached =
                false
        }
        println("Engine inner loop finished")
    }


    /*
suspend fun start() {

    println(screenHeight)
    while (true) {

        delay(1000)
        val random = Random.nextFloat()
        println(random)

        if (random > obstacleProbability) {

            _offsetY.value = -obstacleHeightDp
            var lastTime = System.currentTimeMillis()

            var bottomNotReached = true;
            while (bottomNotReached) {
                val now = System.currentTimeMillis()
                val delta = (now - lastTime) / 1000F
                _offsetY.value += speed * delta
                //println(offsetY.value)
                lastTime = now

                delay(16) // ~60 frames per second

                if (_offsetY.value > screenHeight + screenHeight / 6F) bottomNotReached = false
            }
            println("Engine inner loop finished")
        }
    }

}*/

}

