package sensicar.localplayerstats.impl

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sensicar.dto.StatsDTO
import sensicar.localplayerstats.contract.AddStatsManager

class AddStatsManagerImpl(applicationContext: Context, val gameDuration: Long) : AddStatsManager {

    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "player_stats"
    ).build()

    private val statDao = db.statDao()

    override fun add(
        name: String,
        distance: Float,
        remainingTime: Float,
        endGameCause: String
    ) {
        val avgSpeed = distance / (gameDuration - remainingTime)
        val id = java.util.UUID.randomUUID().toString()
        val stat = Stat(id, name, distance, remainingTime, endGameCause, avgSpeed)

        CoroutineScope(Dispatchers.IO).launch {
            statDao.insert(stat)
        }
    }

    override suspend fun getStats(): List<StatsDTO> {

        val daoList = withContext(Dispatchers.IO) {
            statDao.getAll()
        }
        // println(daoList[0].playerName)
        val dtoList = listOf<StatsDTO>()

        // add objects from daoList to dtoList
        return dtoList
    }


}