package sensicar.model

import android.os.CountDownTimer

class CrashCountDown(private val crashesByLane: MutableMap<Int, CrashState>, private val laneNumber: Int, wait: Long): CountDownTimer(wait, wait) {

    override fun onTick(millisUntilFinished: Long) {}

    override fun onFinish() {
        crashesByLane[laneNumber]?.currentlyCrashed = false
        crashesByLane[laneNumber]?.blocked = false
    }
}