package com.example.myapplication

import android.app.Activity
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun GameScreen(viewModel: AppViewModel, onQuit: () -> Unit, metrics: DisplayMetrics) {

    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window

    DisposableEffect(Unit) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

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

        TimerDisplay(viewModel.remainingSec, viewModel.remainingDeciSec)
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


@Composable
fun TimerDisplay(
    secondsFlow: StateFlow<Long>,
    deciSecondsFlow: StateFlow<Long>
) {
    val seconds by secondsFlow.collectAsState()
    val deciSeconds by deciSecondsFlow.collectAsState()

    val displayText = String.format("%02d.%01d", seconds, deciSeconds)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .width(100.dp) // Fixed width to prevent text shifting
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(8.dp)) // soft yellow
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFB71C1C) // soft red
            )
        }
    }
}





