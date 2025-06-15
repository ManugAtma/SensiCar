package sensicar.localplayerstats.impl

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey

@Entity
data class Stat(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "player_name") val playerName: String,
    @ColumnInfo(name = "distance") val distance: Float,
    @ColumnInfo(name = "remaining_time") val remainingTime: Float,
    @ColumnInfo(name = "end_game_cause") val endGameCause: String,
    @ColumnInfo(name = "avg_speed") val avgSpeed: Float,
)
