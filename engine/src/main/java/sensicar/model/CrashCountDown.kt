package sensicar.model

import android.os.CountDownTimer

/*class CrashCountDown(private val crashesByLane: MutableMap<Int, CrashState>, private val laneNumber: Int, wait: Long): CountDownTimer(wait, wait) {

    override fun onTick(millisUntilFinished: Long) {}

    override fun onFinish() {
        crashesByLane[laneNumber]?.currentlyCrashed = false
        crashesByLane[laneNumber]?.blocked = false
    }
}*/

open class CrashCountDown(
    private val crashesByLane: MutableMap<Int, CrashState>,
    private val laneNumber: Int,
    wait: Long
) {

    val cdt = object : CountDownTimer(wait, wait) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            crashesByLane[laneNumber]?.currentlyCrashed = false
            crashesByLane[laneNumber]?.blocked = false
        }
    }

    open fun start() {
        cdt.start()
    }

    open fun getInstance(crashesByLane: MutableMap<Int, CrashState>, laneNumber: Int, wait: Long) =
        CrashCountDown(crashesByLane, laneNumber, wait)
}