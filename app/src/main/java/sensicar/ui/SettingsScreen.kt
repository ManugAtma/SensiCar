package sensicar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextFieldDefaults // For modern TextField colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions // Import for KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

/**
 * A simpler Composable function for game settings.
 * Allows users to adjust sensitivity, initial speed (via basic text fields),
 * and difficulty (via a basic dropdown).
 *
 * @param initialSensitivity The initial sensitivity value.
 * @param onSensitivityChange Callback when sensitivity changes.
 * @param initialSpeed The initial speed value.
 * @param onSpeedChange Callback when speed changes.
 * @param initialDifficulty The initial difficulty.
 * @param onDifficultyChange Callback when difficulty changes.
 */
@Composable
fun Settings(
    initialSensitivity: Int = 50,
    onSensitivityChange: (Int) -> Unit = {},
    initialSpeed: Float = 300F,
    onSpeedChange: (Float) -> Unit = {},
    initialDifficulty: Difficulty = Difficulty.MEDIUM,
    //onDifficultyChange: (Difficulty) -> Unit = {}
    onSaveClick: () -> Unit = {}
) {
    // State for sensitivity (as a String for TextField)
    var sensitivityText by remember { mutableStateOf(initialSensitivity.toString()) }
    // State for initial speed (as a String for TextField)
    var speedText by remember { mutableStateOf(initialSpeed.toString()) }
    // State for difficulty dropdown
    var expanded by remember { mutableStateOf(false) }
    var currentDifficulty by remember { mutableStateOf(initialDifficulty) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Settings", fontSize = 32.sp, modifier = Modifier.padding(bottom = 16.dp))

        // 1. Sensitivity Input (Basic OutlinedTextField)
        OutlinedTextField(
            value = sensitivityText,
            onValueChange = { newValue ->
                sensitivityText = newValue.filter { it.isDigit() } // Allow only digits
                val intValue = sensitivityText.toIntOrNull()
                if (intValue != null && intValue >= 1) { // Ensure positive
                    onSensitivityChange(intValue)
                } else if (sensitivityText.isEmpty()) {
                    onSensitivityChange(1) // Treat empty as 1 or a default min
                }
            },
            label = { Text("Sensitivity (1-100)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        // 2. Initial Speed Input (Basic OutlinedTextField)
        OutlinedTextField(
            value = speedText,
            onValueChange = { newValue ->
                speedText = newValue.filter { it.isDigit() } // Allow only digits
                val floatValue = speedText.toFloatOrNull()
                if (floatValue != null && floatValue >= 1) { // Ensure positive
                    onSpeedChange(floatValue)
                } else if (speedText.isEmpty()) {
                    onSpeedChange(1F) // Treat empty as 1 or a default min
                }
            },
            label = { Text("Initial Speed (e.g., 300)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        // 3. Difficulty Dropdown (Simple DropdownMenu)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Difficulty:", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clickable { expanded = true } // Make the text clickable to open dropdown
                    .background(Color.LightGray.copy(alpha = 0.2f)) // Simple background for clickable area
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Center the text within the box
            ) {
                Text(currentDifficulty.label, fontSize = 18.sp, color = Color.Black)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Difficulty.values().forEach { difficulty ->
                    DropdownMenuItem(
                        text = { Text(difficulty.label) },
                        onClick = {
                            currentDifficulty = difficulty
                            //onDifficultyChange(difficulty)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            // Convert text inputs to Int before passing to save function,
            // handling empty or invalid input by defaulting to initial values.
            val finalSensitivity =
                sensitivityText.toIntOrNull()?.coerceAtLeast(1) ?: initialSensitivity
            val finalSpeed = speedText.toFloatOrNull()?.coerceAtLeast(1F) ?: initialSpeed

            println("Settings Saved: Sensitivity=$finalSensitivity, Speed=$finalSpeed, Difficulty=${currentDifficulty.label}")
            // Call your actual save/update logic here
            onSensitivityChange(finalSensitivity)
            onSpeedChange(finalSpeed)
            //onDifficultyChange(currentDifficulty)
            onSaveClick()
        }) {
            Text("Save Settings")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewSimpleSettings() {
    Settings()
}

