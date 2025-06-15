package sensicar.ui

import android.app.Activity
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import sensicar.viewmodel.AppViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun GameScreen(viewModel: AppViewModel,
               onQuit: () -> Unit,
               navController: NavController,
               metrics: DisplayMetrics) {

    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window

    // prevent screen sleep
    DisposableEffect(Unit) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.gameEnded
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect {
                viewModel.stopEngine()
                navController.navigate("menu")
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

    viewModel.screenWidthDp = screenWidthDp
    viewModel.carWidth = carWidth

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

        QuitButton(onQuit = onQuit)
        TimerDisplay(viewModel.remainingSec, viewModel.remainingDeciSec, screenWidthDp.dp)
        DistanceDisplay(viewModel.distance, screenWidthDp.dp)
        SpeedDisplay(viewModel.speed, screenWidthDp.dp)
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
    deciSecondsFlow: StateFlow<Long>,
    screenWidthDp: Dp
) {
    val seconds by secondsFlow.collectAsState()
    val deciSeconds by deciSecondsFlow.collectAsState()

    val displayText = String.format("%d.%01d s", seconds, deciSeconds)

    // Calculate one-third of the screen width
    val boxWidth = screenWidthDp / 3

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .width(boxWidth)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = displayText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB71C1C)
                )
            }
        }
    }
}



@Composable
fun DistanceDisplay(
    distanceFlow: StateFlow<Float>,
    screenWidthDp: Dp
) {
    val distance by distanceFlow.collectAsState()
    val displayText = String.format("%.1f km", distance / 1000F)

    val boxWidth = screenWidthDp / 3

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
                .width(boxWidth)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Distance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = displayText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB71C1C),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SpeedDisplay(
    speedFlow: StateFlow<Float>,
    screenWidthDp: Dp
) {
    val speed by speedFlow.collectAsState()
    val displayText = "${speed.roundToInt()} km/h" // Round to nearest int

    val boxWidth = screenWidthDp / 3

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .width(boxWidth)
                .border(2.dp, Color.Black) // No rounded corners
                .background(Color(0xFFFFF9C4)) // No rounded shape
                .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Speed",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = displayText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB71C1C),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}





@Composable
fun QuitButton(onQuit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onQuit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB71C1C),  // soft red
                contentColor = Color(0xFFFFF9C4)    // soft yellow
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text(
                text = "Quit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}





