package sensicar.viewmodel

import android.content.Context
import sensicar.dto.StatsDTO
import sensicar.localplayerstats.contract.LocalPlayerStats

class StatsManager(val context: Context, private val localPlayerStats: LocalPlayerStats) {

    // private val localPlayerStats: LocalPlayerStats =
    // LocalPlayerStats.getSingleton(this.context, 60000)
    private var remainingTimeDeciSecsStat = 0F
    private var distanceStat = 0F
    private var endGameCauseStat = ""
    var playerName = ""

    suspend fun getSavedStats(): List<StatsDTO> {
        return this.localPlayerStats.getStats()
    }

    suspend fun insertStat() {
        this.localPlayerStats.addStats(
            playerName,
            distanceStat,
            remainingTimeDeciSecsStat,
            endGameCauseStat
        )
    }

    fun setCurrentEngineStats(secs: Long, deciSecs: Long, distance: Float, endGameCause: String) {
        this.remainingTimeDeciSecsStat = (secs * 10F) + deciSecs
        this.distanceStat = roundToOneDecimal(distance)
        this.endGameCauseStat = endGameCause
    }


    private fun roundToOneDecimal(distance: Float): Float {

        val coveredDistanceKm = distance / 1000
        val rounded = String.format("%.1f", coveredDistanceKm)
        val roundedAsFloat = rounded.toFloat()
        println(roundedAsFloat)
        return roundedAsFloat
    }

    /* private fun toOneDecimalString(distanceStat: Float): String {
         val coveredDistanceKm = distanceStat / 1000
         return String.format("%.1f", coveredDistanceKm)
     }*/
}