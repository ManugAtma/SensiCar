package sensicar.model

import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sensicar.model.contract.Engine

open class RaceTimer(var milliSec: Long, val engine: EngineImpl) {

   /* private var _seconds = MutableStateFlow(milliSec)
    var seconds: StateFlow<Long> = _seconds

    private var _deciSeconds = MutableStateFlow(0L)
    val deciSeconds: StateFlow<Long> = _deciSeconds

    private val _timerEnded = MutableSharedFlow<Unit>()
    val timerEnded: SharedFlow<Unit> = _timerEnded*/

    private var cdt: CountDownTimer? = null

    open fun start() {

        cdt = object : CountDownTimer(milliSec, 100) {

            override fun onTick(millisUntilFinished: Long) {
                //_seconds.value = Math.round(millisUntilFinished / 1000F).toLong()
                engine._seconds.value = Math.round(millisUntilFinished / 1000F).toLong()
                val remainder = millisUntilFinished % 1000
                //_deciSeconds.value = Math.round(remainder / 100F).toLong()
                engine._deciSeconds.value = Math.round(remainder / 100F).toLong()

            }

            override fun onFinish() {
                //_seconds.value = 0
                //_deciSeconds.value = 0

                CoroutineScope(Dispatchers.Default).launch {
                    //_timerEnded.emit(Unit)
                    engine._timerEnded.emit(Unit)
                }
            }
        }.start()
    }

    open fun stop() {
        cdt?.cancel()
    }
}