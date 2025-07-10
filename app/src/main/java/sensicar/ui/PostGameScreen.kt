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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sensicar.viewmodel.GameDataMediatorImpl

@Composable
fun PostGameScreen(
    viewModel: GameDataMediatorImpl,
    /* onLeaderboardsClick: (playerName: String) -> Unit*/
) {

    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Over Heading
        Text(
            text = "Game Over!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))


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
                modifier = Modifier.widthIn(min = 250.dp, max = 300.dp), // Control width
                colors = OutlinedTextFieldDefaults.colors( // Changed from TextFieldDefaults.outlinedTextFieldColors
                    focusedTextColor = Color.Black, // Set text color when focused
                    unfocusedTextColor = Color.Black, // Set text color when unfocused
                    focusedContainerColor = Color.Transparent, // Optional: ensure background is transparent
                    unfocusedContainerColor = Color.Transparent, // Optional: ensure background is transparent
                    cursorColor = Color.Black, // Set cursor color
                    focusedBorderColor = Color.Black, // Optional: for focused border
                    unfocusedBorderColor = Color.Gray // Optional: for unfocused border
                )
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                viewModel.updateAndShowStats(playerName)
            },
            enabled = playerName.isNotBlank()
        ) {
            Text("Leaderboards")
        }
    }
}

