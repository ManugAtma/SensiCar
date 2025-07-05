package sensicar.model

import kotlinx.coroutines.launch
import org.junit.Test
import kotlinx.coroutines.test.runTest

import org.junit.Assert.*
import org.junit.Before
import sensicar.model.contract.Engine

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var engine: Engine = EngineImpl()


    @Before
    fun setUp() {
        engine.setLanes(3)
        engine.setScreenSize(800F, 300F)
        engine.setObjectSizes(100F, 100F, 80F)
    }


    /*@Test
    fun testCrashOnLane3() = runTest {

        var crashed = false

        val collectorJob = launch {
            engine.newCrashes[1].collect {
                crashed = true
            }
        }

        engine.setObstacleProbability(0F)
        engine.carPositionX.value = 0F
        engine.start(this)
        Thread.sleep(10000)
        assertTrue(crashed)
    }*/
}