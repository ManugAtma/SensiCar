package sensicar.localplayerstats.contract

import sensicar.dto.StatsDTO
import sensicar.localplayerstats.impl.GetStatsManagerImpl

interface GetStatsManager {

    companion object {
        fun getInstance() = GetStatsManagerImpl()
    }

    fun getStats(): Collection<StatsDTO>
}