package sensicar.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sensicar.localplayerstats.impl.LocalPlayerStatsImpl
import sensicar.model.EngineImpl
import sensicar.model.RaceTimer
import sensicar.sensor.MotionSensorManagerImpl
import kotlin.coroutines.resume


@RunWith(AndroidJUnit4::class)
class IntegrationTests {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var engine: EngineImpl

    private val obstacleHeightPercent = 0.1F
    private val screenHeightDp = 800F
    private val screenWidthDp = 300F
    private val numberOfLanes = (screenWidthDp.toInt() / 100) + 1
    private val obstacleWidthDp = screenWidthDp / numberOfLanes
    private val obstacleHeightDp = screenHeightDp * obstacleHeightPercent
    private val carWidth = obstacleWidthDp - obstacleWidthDp / 3   // carWidth == 50
    val carPositionY = 0F


    @Before
    fun setUp() {
        this.engine = EngineImpl()
    }


    @Test
    fun engineToMediator_Speed_DataBinding() {

        engine.setObstacleProbability(1F)
        //val mockLocalPlayerStats = mock<LocalPlayerStats>()
        //val mockMotionSensorManager = mock<MotionSensorManagerImpl>()
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val mediator = GameDataMediatorImpl(motionSensorManager, appContext,
            engine, LocalPlayerStatsImpl(appContext, 60000,1)
        )
        val cause = 0.02F

        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        runOnUiThread {
            mediator.setEngineLanes(numberOfLanes)
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth

        // execute
        mediator.startEngine()

        assertEquals(300F, mediator.speed.value)

        // clean up
        mediator.stopEngine(cause)
    }

    @Test
    fun engineToMediator_CarPositionX_DataBinding() {

        engine.setObstacleProbability(1F)
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val mediator = GameDataMediatorImpl(motionSensorManager, appContext,
            engine, LocalPlayerStatsImpl(appContext, 60000,1)
        )
        val cause = 0.02F
        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        runOnUiThread {
            mediator.setEngineLanes(numberOfLanes)
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth

        // execute
        mediator.startEngine()

        assertEquals(engine.carPositionX, mediator.carPositionX)

        // clean up
        mediator.stopEngine(cause)
    }

    @Test
    fun engineToMediator_emitGameEnded_whenSpeedIsZero() = runBlocking {

        // setUp
        engine.setObstacleProbability(-1F) // make sure enough crashes occur
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val mediator = GameDataMediatorImpl(motionSensorManager, appContext,
            engine, LocalPlayerStatsImpl(appContext, 60000,1)
        )
        mediator.speedSetting = 600F
        val cause = 0.02F
        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        val largeCarWidth = 150F
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, largeCarWidth)
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.setEngineLanes(numberOfLanes)
                cont.resume(Unit)
            }
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth

        // start listening if event was fired
        var gameEnded = false
        val testScope = CoroutineScope(Dispatchers.Main)
        val collectorJob = testScope.launch {
            mediator.gameEnded.collect {
                gameEnded = true
            }
        }

        // execute
        mediator.startEngine()
        delay(10000)

        // check if event was fired
        assertTrue(gameEnded)

        // clean up
        mediator.stopEngine(cause)
    }

    @Test
    fun engineToMediator_emitGameEnded_whenTimeIsUp() = runBlocking {

        // setUp
        engine.setObstacleProbability(1F) // make sure no crashes occur
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                engine.setRaceTimer(RaceTimer(1000, engine)) // race duration 1s
                cont.resume(Unit)
            }
        }
        val motionSensorManager = MotionSensorManagerImpl(appContext)
        val mediator = GameDataMediatorImpl(motionSensorManager, appContext,
            engine, LocalPlayerStatsImpl(appContext, 60000,1)
        )
        mediator.speedSetting = 600F
        val cause = 0.02F
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

        // start listening if event was fired
        var gameEnded = false
        val testScope = CoroutineScope(Dispatchers.Main)
        val collectorJob = testScope.launch {
            mediator.gameEnded.collect {
                gameEnded = true
            }
        }

        // execute
        suspendCancellableCoroutine { cont ->
            runOnUiThread {
                mediator.startEngine()
                cont.resume(Unit)
            }
        }
        delay(2000)

        // check if event was fired
        assertTrue(gameEnded)

        // clean up
        mediator.stopEngine(cause)
    }

    @Test
    fun sensorToMediatorToEngine_CarPositionX_PreventOverflow() {

        // setUp
        val motionSensorManager = object: MotionSensorManagerImpl(appContext) {
            override val offsetX = MutableStateFlow(200F)
        }
        val mediator = GameDataMediatorImpl(motionSensorManager, appContext,
            engine, LocalPlayerStatsImpl(appContext, 60000,1)
        )
        val cause = 0.02F
        mediator.setEngineScreenSize(screenHeightDp, screenWidthDp)
        mediator.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        runOnUiThread {
            mediator.setEngineLanes(numberOfLanes)
        }
        mediator.setEngineCarPositionY(carPositionY)
        mediator.screenWidthDp = screenWidthDp
        mediator.carWidth = carWidth

        // execute
        mediator.startEngine()

        // check if car has max offset without overflow.
        // max offset is 125 because initially car is centered,
        // screenWidth is 300 and 150 + 25 + 125 == 300
        assertEquals(125F, engine.carPositionX.value) //

        // clean up
        mediator.stopEngine(cause)
    }
}