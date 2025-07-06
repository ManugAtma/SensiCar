package sensicar.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import sensicar.localplayerstats.impl.LocalPlayerStatsImpl
import sensicar.model.EngineImpl
import sensicar.model.RaceTimer
import sensicar.sensor.MotionSensorManagerImpl
import kotlin.coroutines.resume

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var engine: EngineImpl

    private val obstacleHeightPercent = 0.1F
    private val screenHeightDp = 800F
    private val screenWidthDp = 300F
    private val numberOfLanes = (screenWidthDp.toInt() / 100) + 1
    private val obstacleWidthDp = screenWidthDp / numberOfLanes
    private val obstacleHeightDp = screenHeightDp * obstacleHeightPercent
    private val carWidth = 150F
    private val carPositionY = 0F


    @Test
    fun runGameWithTimeOut_statsUpdatedCorrectly() = runBlocking {

        //setUp
        engine = EngineImpl()
        engine.setObstacleProbability(1F) // make sure no crashes occur
        val gameDuration = 1000L
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                engine.setRaceTimer(RaceTimer(gameDuration, engine)) // race duration 1s
                cont.resume(Unit)
            }
        }
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val localPlayerStatsImpl = LocalPlayerStatsImpl(appContext, gameDuration, 1)
        val mediator = GameDataMediatorImpl(
            motionSensorManager, appContext,
            engine, localPlayerStatsImpl
        )
        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.setEngineLanes(numberOfLanes)
                cont.resume(Unit)
            }
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth


        // run game
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.startEngine()
                cont.resume(Unit)
            }
        }
        delay(2000) // wait for game to time out


        // game finished, update stats
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.updateAndShowStats("Alice")
                cont.resume(Unit)
            }
        }


        // fetch stats
        val result = localPlayerStatsImpl.getStats()
        println(result[0].player)


        // check if stats were updated correctly
        assertTrue(result.any { it.player == "Alice" && it.endgameCaused == "time up" })
    }

    @Test
    fun runGameWithCrashAsEndgameCause_statsUpdatedCorrectly() = runBlocking {

        //setUp
        engine = EngineImpl()
        engine.setObstacleProbability(-1F) // make sure no crashes occur
        val gameDuration = 60000L
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                engine.setRaceTimer(RaceTimer(gameDuration, engine)) // race duration 1s
                cont.resume(Unit)
            }
        }
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val localPlayerStatsImpl = LocalPlayerStatsImpl(appContext, gameDuration, 1)
        val mediator = GameDataMediatorImpl(
            motionSensorManager, appContext,
            engine, localPlayerStatsImpl
        )
        mediator.speedSetting = 600F
        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.setEngineLanes(numberOfLanes)
                cont.resume(Unit)
            }
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth


        // run game
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.startEngine()
                cont.resume(Unit)
            }
        }
        delay(10000) // wait for crashes to occur


        // game finished, update stats
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.updateAndShowStats("Alice")
                cont.resume(Unit)
            }
        }


        // fetch stats
        val result = localPlayerStatsImpl.getStats()
        println("name: ${result[0].player}, cause: ${result[0].endgameCaused}")


        // check if stats were updated correctly
        assertTrue(result.any { it.player == "Alice" && it.endgameCaused == "crashed" })
    }
}

