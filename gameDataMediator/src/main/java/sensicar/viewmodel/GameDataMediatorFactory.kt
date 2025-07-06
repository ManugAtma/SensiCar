package sensicar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sensicar.localplayerstats.contract.LocalPlayerStats
import sensicar.model.CrashCountDown
import sensicar.model.EngineImpl
import sensicar.model.RaceTimer
import sensicar.sensor.MotionSensorManagerImpl

class GameDataMediatorFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sensorManager = MotionSensorManagerImpl(context)

        if (modelClass.isAssignableFrom(GameDataMediatorImpl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val engine = EngineImpl()
            engine.crashCountdowns = mutableListOf<CrashCountDown>()
            engine.setRaceTimer(RaceTimer(60000, engine))
            val localPlayerStats = LocalPlayerStats.getSingleton(context, 60000)
            return GameDataMediatorImpl(sensorManager, context, engine, localPlayerStats) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
