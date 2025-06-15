package sensicar.localplayerstats.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StatDao {

    @Query("SELECT * FROM stat")
    suspend fun getAll(): List<Stat>

    @Insert
    suspend fun insert(stat: Stat)
}