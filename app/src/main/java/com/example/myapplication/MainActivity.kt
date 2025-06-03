package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.AppViewModelFactory


class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory(applicationContext) // Use applicationContext to avoid potential memory leaks
    }
    private val obstacleHeightPercent = 0.1F


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val metrics = resources.displayMetrics

        /*val screenHeightDp = metrics.heightPixels / metrics.density
        val screenWidthDp = metrics.widthPixels / metrics.density

        // calculate number of lanes based on screen width
        val numberOfLanes = (screenWidthDp.toInt() / 100) + 1

        val obstacleWidthDp = screenWidthDp / numberOfLanes
        val obstacleHeightDp = screenHeightDp * obstacleHeightPercent
        val carWidth = obstacleWidthDp - obstacleWidthDp / 3
        val carPositionY = (screenHeightDp / 8) * 5

        viewModel.setEngineScreenSize(screenHeightDp, screenWidthDp)
        viewModel.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp, carWidth)
        viewModel.setEngineLanes(numberOfLanes)
        viewModel.setEngineCarPositionY(carPositionY)

        viewModel.startEngine()*/

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Menu.route) {

                    composable(Screen.Menu.route) {
                        MenuScreen(onPlay = {
                            navController.navigate(Screen.Game.route)
                        }, onSettings = {
                            //navController.navigate(Screen.Settings.route)
                        })
                    }

                    composable(Screen.Game.route) {
                        GameScreen(viewModel = viewModel, metrics = metrics)
                    }
                }
                //GameScreen(viewModel, metrics)
            }
        }
    }
}


/*@Composable
fun Car(
    offsetX: Float,
    offsetY: Float,
    modifier: Modifier = Modifier,
    height: Float,
    width: Float
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .background(Color.Red)
            .width(width.dp)
            .height(height.dp)
    )
}

@Composable
fun Obstacles(
    numberOfLanes: Int,
    viewModel: AppViewModel,
    obstacleHeightDp: Float,
    obstacleWidthDp: Float
) {
    for (i in 0..<numberOfLanes) {
        Box(
            modifier = Modifier
                .offset(
                    y = viewModel.obstacleOffsets[i].collectAsState().value.dp,
                    x = obstacleWidthDp.dp * i
                )
                .size(
                    height = obstacleHeightDp.dp,
                    width = obstacleWidthDp.dp
                )
                .background(Color.Green)
        )
    }
}*/


/*MyApplicationTheme {
    *//* Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
         Box(
             modifier = Modifier.fillMaxSize(),
             contentAlignment = Alignment.TopCenter
         ) {
             Car(
                 offsetX = viewModel.carPositionX.collectAsState().value,
                 offsetY = carPositionY,
                 height = obstacleHeightDp,
                 width = carWidth,
                 modifier = Modifier.padding(innerPadding)
             )
         }
         Obstacles(numberOfLanes, viewModel, obstacleHeightDp, obstacleWidthDp)
     }*//*
}*/


/*
override fun onSensorChanged(event: SensorEvent?) {
    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
        // x = -event.values[0] * 40
        // y = event.values[1] * 20
    }
}

override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

private fun setupSensor() {
    sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
        sensorManager.registerListener(
            this, it, SensorManager.SENSOR_DELAY_GAME
        )
    }
}

// private lateinit var sensorManager: SensorManager
    // private var x by mutableStateOf(0F)
    // private var y by mutableStateOf(0F)


*/

