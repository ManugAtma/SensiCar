package sensicar.localplayerstats.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sensicar.localplayerstats.contract.LocalPlayerStats

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocalPlayerStatsImplTest {

    private lateinit var stats: LocalPlayerStats

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        stats = LocalPlayerStats.getSingleton(context, 60_000L)
    }

    @Test
    fun testAddAndRetrieveStats() = runTest {
        stats.addStats("Bob", distance = 300f, remainingTime = 20f, endGameCause = "Timeout")

        val result = stats.getStats()
        assertTrue(result.any { it.player == "Bob" && it.endgameCaused == "Timeout" })
    }
}
