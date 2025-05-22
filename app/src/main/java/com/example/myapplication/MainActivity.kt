package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.AppViewModelFactory


class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory(applicationContext) // Use applicationContext to avoid potential memory leaks
    }
    private val obstacleHeightPercent = 10F


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val metrics = resources.displayMetrics
        val screenHeightDp = metrics.heightPixels / metrics.density
        val screenWidthDp = metrics.widthPixels / metrics.density

        // calculate number of lanes based on screen width
        val numberOfLanes = screenWidthDp.toInt() / 100 + 1

        val obstacleWidthDp = screenWidthDp / numberOfLanes
        val obstacleHeightDp = screenHeightDp / obstacleHeightPercent

        viewModel.setEngineScreenSize(screenHeightDp, screenWidthDp)
        viewModel.setEngineObjectSizes(obstacleHeightDp, obstacleWidthDp)
        viewModel.setEngineNumberOfLanes(numberOfLanes)

        viewModel.startEngine()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Car(
                            offsetX = viewModel.carPositionX.collectAsState().value,
                            offsetY = 100F,
                            height = obstacleHeightDp,
                            width = obstacleWidthDp - obstacleWidthDp / 5,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    Obstacles(numberOfLanes, viewModel, obstacleHeightDp, obstacleWidthDp)
                }
            }
        }
    }
}


@Composable
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
}




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

