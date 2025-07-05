package sensicar.dto

data class StatsDTO(
    val player: String,
    val distance: String,
    val remainingTime: String,
    val endgameCaused: String,
    val avgSpeed: String // Using String for display flexibility
)