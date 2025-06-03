package com.example.myapplication

import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppViewModel

@Composable
fun GameScreen(viewModel: AppViewModel, onQuit: () -> Unit, metrics: DisplayMetrics) {

    val obstacleHeightPercent = 0.1F

    //val metrics = resources.displayMetrics
    val screenHeightDp = metrics.heightPixels / metrics.density
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

    LaunchedEffect(Unit) {
        viewModel.startEngine()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
        Button(onClick = onQuit, modifier = Modifier.offset(y = 40.dp)) {
            Text("Quit")
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