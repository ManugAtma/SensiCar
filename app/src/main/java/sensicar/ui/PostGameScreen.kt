package sensicar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sensicar.viewmodel.GameDataMediatorImpl

@Composable
fun PostGameScreen(
    viewModel: GameDataMediatorImpl,
    /* onLeaderboardsClick: (playerName: String) -> Unit*/
) {
    // State to hold the player's name input
    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add some padding around the edges
        verticalArrangement = Arrangement.SpaceAround, // Distribute content vertically
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Over Heading
        Text(
            text = "Game Over!",
            fontSize = 48.sp, // Larger font for the heading
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp) // Push it slightly down from the very top
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Name input section (centered)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Provide your name:",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Your Name") },
                singleLine = true, // Ensure it stays on one line
                modifier = Modifier.widthIn(min = 250.dp, max = 300.dp) // Control width
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Leaderboards Button
        Button(
            onClick = {
                viewModel.updateAndShowStats(playerName)
            },
            // Enable the button only if the name is not blank
            enabled = playerName.isNotBlank()
        ) {
            Text("Leaderboards")
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PostGameScreenPreview() {
    PostGameScreen(onLeaderboardsClick = {})
}*/
