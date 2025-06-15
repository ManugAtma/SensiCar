package sensicar.localplayerstats.contract

import android.content.Context
import sensicar.dto.StatsDTO
import sensicar.localplayerstats.impl.AddStatsManagerImpl

interface AddStatsManager {

    companion object {
        fun getInstance(context: Context) = AddStatsManagerImpl(context, 60000)
    }

    fun add(name: String, distance: Float, remainingTime: Float, endGameCause: String,)

    suspend fun getStats(): List<StatsDTO>
}