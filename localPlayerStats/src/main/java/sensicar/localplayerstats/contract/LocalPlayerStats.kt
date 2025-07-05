package sensicar.localplayerstats.contract

import android.content.Context
import sensicar.dto.StatsDTO
import sensicar.localplayerstats.impl.LocalPlayerStatsImpl

interface LocalPlayerStats {

    companion object {
        private var statManager: LocalPlayerStats? = null
        fun getSingleton(context: Context, gameDuration: Long): LocalPlayerStats {
            if (statManager == null) statManager = LocalPlayerStatsImpl(context, gameDuration)
            return statManager as LocalPlayerStats
        }
    }

    suspend fun addStats(playerName: String,
                         distance: Float,
                         remainingTime: Float,
                         endGameCause: String
    )

    suspend fun getStats(): List<StatsDTO>
}