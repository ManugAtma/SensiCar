package sensicar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onLeaderboards: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "SensiCar",
            fontSize = 50.sp, // Large font size for a prominent title
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 64.dp) // Add padding below the title
        )

        val buttonWidth = 0.4f
        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(buttonWidth)
        ) {
            Text("Play!")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLeaderboards,
            modifier = Modifier.fillMaxWidth(buttonWidth)
        ) {
            Text("Leaderboards")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth(buttonWidth)
        ) {
            Text("Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MenuScreen(onPlay = {}, onSettings = {}, onLeaderboards = {})
}
