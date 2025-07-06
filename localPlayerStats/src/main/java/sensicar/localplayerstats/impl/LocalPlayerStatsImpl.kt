package sensicar.localplayerstats.impl

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sensicar.dto.StatsDTO
import sensicar.localplayerstats.contract.LocalPlayerStats

class LocalPlayerStatsImpl(applicationContext: Context, val gameDuration: Long) : LocalPlayerStats {

    private var db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "player_stats"
    ).build()

    private var statDao = db.statDao()

    // in memory db for testing
    constructor(applicationContext: Context, gameDuration: Long, num:Int) : this(applicationContext, gameDuration) {
        this.db = Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java).build()
        this.statDao = this.db.statDao()
    }

    override suspend fun addStats(
        playerName: String,
        distance: Float,
        remainingTime: Float,
        endGameCause: String
    ) {
        val avgSpeed = (distance * 1000) / ((gameDuration.toFloat() / 1000F) - remainingTime / 10)
        val id = java.util.UUID.randomUUID().toString()
        val stat = Stat(id, playerName, distance, remainingTime / 10, endGameCause, avgSpeed)

       /* CoroutineScope(Dispatchers.IO).launch {
            statDao.insert(stat)
        }*/

        statDao.insert(stat)
    }

    override suspend fun getStats(): List<StatsDTO> {

        val daoList = withContext(Dispatchers.IO) {
            statDao.getAll()
        }
        val dtoList = mutableListOf<StatsDTO>()
        for (stat in daoList) {
            val statsDTO = StatsDTO(
                stat.playerName,
                stat.distance.toString(),
                stat.remainingTime.toString(),
                stat.endGameCause,
                stat.avgSpeed.toString()
            )
            dtoList.add(statsDTO)
        }
        return dtoList
    }
}