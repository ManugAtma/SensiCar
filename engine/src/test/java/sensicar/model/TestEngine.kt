package sensicar.model

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Test
import kotlinx.coroutines.test.runTest

import org.junit.Assert.*
import org.junit.Before


class TestEngine {

    var engine: EngineImpl = EngineImpl()


    @Before
    fun setUp() {
        engine.setScreenSize(800F, 300F)
        engine.setObjectSizes(100F, 100F, 80F)
        engine.carPositionY = 0F
        engine.crashCountdowns = mutableListOf<CrashCountDown>()
        engine.setLanes(3, FakeCrashCountDown())

        val fakeTimer = object : RaceTimer(600000, engine) {
            override fun start() {
                // do nothing
            }
            override fun stop() {
                // do nothing
            }
        }
        engine.setRaceTimer(fakeTimer)
    }


    @Test
    fun crashOnLane1() = runTest {

        // start listening if crash was emitted
        var crashed = false
        val collectorJob = launch(UnconfinedTestDispatcher()) {
            engine.newCrashes[1].collect {
                crashed = true
            }
        }

        // set conditions to cause crash
        engine.setObstacleProbability(-1F)
        engine.carPositionX.value = 0F

        // execute
        engine.start(this)
        advanceTimeBy(2000)

        // clean up
        engine.stop(0.02F)
        collectorJob.cancel()

        assertTrue("The crash state was not collected.", crashed)
    }

    @Test
    fun noCrashOnLane2() = runTest {

        // start listening if crash was emitted
        var crashed = false
        val collectorJob = launch(UnconfinedTestDispatcher()) {
            engine.newCrashes[2].collect {
                crashed = true
            }
        }

        // set conditions to not cause crash
        engine.setObstacleProbability(-1F)
        engine.carPositionX.value = 0F

        // execute
        engine.start(this)
        advanceTimeBy(2000)

        // clean up
        engine.stop(0.02F)
        collectorJob.cancel()

        assertFalse("The crash state was collected.", crashed)
    }

    @Test
    fun crashOnLane2_boundaryCase() = runTest {

        // start listening if crash was emitted
        var crashed = false
        val collectorJob = launch(UnconfinedTestDispatcher()) {
            engine.newCrashes[2].collect {
                crashed = true
            }
        }

        // set minimum car offset that causes crash
        engine.setObstacleProbability(-1F)
        engine.carPositionX.value = 10F

        // execute
        engine.start(this)
        advanceTimeBy(2000)

        // clean up
        engine.stop(0.02F)
        collectorJob.cancel()

        assertTrue("The crash state was not collected.", crashed)
    }

    @Test
    fun crashOnLane2_speedReducedBy25Percent() = runTest {

        // start listening if crash was emitted
        var crashed = false
        val collectorJob = launch(UnconfinedTestDispatcher()) {
            engine.newCrashes[1].collect {
                crashed = true
            }
        }

        // set conditions to not cause crash
        engine.setObstacleProbability(-1F)
        engine.carPositionX.value = 0F
        engine.setSpeed(100F)

        // execute
        engine.start(this)
        advanceTimeBy(3000)

        assertEquals(75F, engine.speed.value)

        // clean up
        engine.stop(0.02F)
        collectorJob.cancel()
    }
}