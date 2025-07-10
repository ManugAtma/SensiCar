package sensicar.ui

import android.app.Activity
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.graphics.drawscope.EmptyCanvas.drawLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.ui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import sensicar.viewmodel.GameDataMediatorImpl
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun GameScreen(viewModel: GameDataMediatorImpl,
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
                viewModel.stopEngine(GameDataMediatorImpl.NO_QUIT)
                //navController.navigate("menu")
                navController.navigate("postGame")
            }
    }

    val obstacleHeightPercent = 0.1F

    //val metrics = resources.displayMetrics
    val screenHeightDp = metrics.heightPixels / metrics.density
    val screenWidthDp = metrics.widthPixels / metrics.density
    println(screenHeightDp)
    println(screenWidthDp)


    // calculate number of lanes based on screen width
    val numberOfLanes = (screenWidthDp.toInt() / 100) + 1

    val laneColors = remember { mutableStateListOf<MutableStateFlow<Color>>() }
    for (i in 0..<numberOfLanes){
        laneColors.add(MutableStateFlow(Color.Green))
    }

    val obstacleWidthDp = screenWidthDp / numberOfLanes
    val obstacleHeightDp = screenHeightDp * obstacleHeightPercent
    val carWidth = obstacleWidthDp - obstacleWidthDp / 3
    val carPositionY = (screenHeightDp / 8) * 5

    // order of setters matters for correct engine field values
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

        //val animationPhaseFlow = viewModel.obstacleOffsets[0]
        val animationPhaseFlow = viewModel.streetOffset
        StreetBackground(animationPhaseFlow = animationPhaseFlow)

        Obstacles(numberOfLanes, viewModel,
            obstacleHeightDp, obstacleWidthDp,
            viewModel.newCrashes, laneColors
        )

        Box(
            modifier = Modifier.fillMaxSize().testTag("gameScreen"),
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
            //.background(Color.Red)
            .width(width.dp)
            .height(height.dp)
            .border(2.dp, Color.Red)
    ){
        Image(
            painter = painterResource(id = R.drawable.car_rotated),
            contentDescription = "Player Car",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )
    }
}

@Composable
fun Obstacles(
    numberOfLanes: Int,
    viewModel: GameDataMediatorImpl,
    obstacleHeightDp: Float,
    obstacleWidthDp: Float,
    crashFlow: MutableList<MutableSharedFlow<Unit>>,
    laneColors: MutableList<MutableStateFlow<Color>>
) {
    for (i in 0..<numberOfLanes) {

        LaunchedEffect(Unit) {
            crashFlow[i].collect {
                println("UI: crash on lane $i")
                for (j in 0..6) {
                    laneColors[i].value = Color.Blue
                    delay(100)
                    laneColors[i].value = Color.Green
                    delay(100)
                }
            }
        }


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
                .background(laneColors[i].collectAsState().value)
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
    val displayText = "${speed.roundToInt()} km/h" // round to nearest int

    val boxWidth = screenWidthDp / 3

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .width(boxWidth)
                .border(2.dp, Color.Black)
                .background(Color(0xFFFFF9C4))
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
                .padding(bottom = 64.dp)
                .testTag("quitButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB71C1C),
                contentColor = Color(0xFFFFF9C4)
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

@Composable
fun StreetBackground(
    modifier: Modifier = Modifier,
    streetColor: Color = Color(0xFF333333), // Dark grey for the road
    lineColor: Color = Color.White,
    dashLength: Float = 150f, // Length of each dash (as requested)
    gapLength: Float = 70f,  // Length of the gap between dashes (as requested)
    lineWidth: Float = 15f,    // Width of the dashed line (as requested)
    animationPhaseFlow: StateFlow<Float> // Now takes a StateFlow
) {
    // Collect the current value from the StateFlow.
    // `by` delegate ensures that recomposition happens when the flow emits a new value.
    val animationPhase by animationPhaseFlow.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize() // Make the background fill the entire available space
            .background(streetColor) // Apply the street color
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f

            // Define the path effect for the dashed line
            val pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashLength, gapLength),
                phase = animationPhase // Use the collected animation phase here
            )

            // Draw a line from the top center to the bottom center of the canvas
            drawLine(
                color = lineColor,
                start = Offset(x = centerX, y = 0f), // Start at the top center
                end = Offset(x = centerX, y = size.height), // End at the bottom center
                strokeWidth = lineWidth, // Set the width of the line
                pathEffect = pathEffect // Apply the dashed effect
            )
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewStreetBackgroundWithFlow() {
    // For preview purposes, create a dummy MutableStateFlow that changes over time.
    // In a real app, this would come from your ViewModel or Engine.
    val dummyAnimationPhaseFlow = remember { MutableStateFlow(0f) }

    // Use LaunchedEffect to update the dummy flow for the preview animation
    LaunchedEffect(Unit) {
        val dashLength = 150f
        val gapLength = 70f
        val totalDashCycleLength = dashLength + gapLength
        var currentPhase = 0f
        while (true) {
            //currentPhase = (currentPhase + 5f) % totalDashCycleLength // Simulate movement
            /*currentPhase = (currentPhase - 5f) // Subtract to move in opposite direction
            if (currentPhase < 0) {
                currentPhase += totalDashCycleLength // Wrap around if it becomes negative
            }*/
            dummyAnimationPhaseFlow.value = currentPhase
            delay(16)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        StreetBackground(
            modifier = Modifier.weight(1f), // Make it fill available height in preview
            animationPhaseFlow = dummyAnimationPhaseFlow // Pass the dummy flow to the composable
        )
    }
}





