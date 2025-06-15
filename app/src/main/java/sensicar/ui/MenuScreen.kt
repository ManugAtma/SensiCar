package sensicar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(onPlay: () -> Unit, onSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onPlay) {
            Text("Play")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSettings) {
            Text("Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MenuScreen(onPlay = {}, onSettings = {})
}
