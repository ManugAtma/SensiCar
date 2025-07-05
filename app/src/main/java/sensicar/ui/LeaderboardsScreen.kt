package sensicar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sensicar.dto.StatsDTO


@Composable
fun LeaderboardsScreen(
    leaderboardEntries: List<StatsDTO>, // Data will be passed here
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Add padding around the screen
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Distribute content vertically
    ) {
        // Leaderboards Heading
        Text(
            text = "Leaderboards",
            fontSize = 40.sp, // A bit smaller than "Game Over" but still prominent
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        // Spacer to push the table slightly down
        // Spacer(modifier = Modifier.height(24.dp)) // If you don't use SpaceBetween and want fixed spacing

        // Leaderboard Table
        Column(
            modifier = Modifier
                .fillMaxWidth() // Table can fill width
                .weight(1f) // Makes the table take up available space in the center
        ) {
            // Table Header Row
            LeaderboardHeader()
            Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)

            // Table Content (LazyColumn for performance)
            LazyColumn {
                items(leaderboardEntries) { entry ->
                    LeaderboardRow(entry = entry)
                    Divider(color = Color.LightGray, thickness = 0.5.dp) // Subtle row divider
                }
            }
        }

        // Spacer to push the button slightly up
        Spacer(modifier = Modifier.height(24.dp))

        // Menu Button
        Button(
            onClick = onMenuClick,
            modifier = Modifier.fillMaxWidth(0.6f) // Make button a bit wider
        ) {
            Text("Menu")
        }
    }
}

@Composable
fun LeaderboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // Slightly different background for header
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player (weight 2f for more space)
        Text(
            text = "Player",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        // Distance
        Text(
            text = "Distance",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
        // Time Left
        Text(
            text = "Time Left",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
        // Endgame Caused
        Text(
            text = "Endgame Caused",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        // Average Speed (using the average sign)
        Text(
            text = "ø Speed", // Unicode for diameter/average sign
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun LeaderboardRow(entry: StatsDTO) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.player,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = entry.distance,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = entry.remainingTime,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = entry.endgameCaused,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = entry.avgSpeed,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardsScreenPreview() {
    val sampleEntries = listOf(
        StatsDTO("Player 1", "1234m", "35s", "Collision", "25.1 ø"),
        StatsDTO("Hero", "987m", "22s", "Fuel", "18.7 ø"),
        StatsDTO("GamerX", "1500m", "48s", "Time Up", "30.0 ø"),
        StatsDTO("FastOne", "1100m", "30s", "Collision", "22.3 ø"),
        StatsDTO("Elite", "1800m", "55s", "Time Up", "35.5 ø"),
        StatsDTO("Player 6", "700m", "15s", "Fuel", "15.0 ø"),
        StatsDTO("Racer", "1300m", "40s", "Collision", "28.9 ø"),
    )
    LeaderboardsScreen(leaderboardEntries = sampleEntries, onMenuClick = {})
}

