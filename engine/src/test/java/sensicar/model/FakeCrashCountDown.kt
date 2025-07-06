package sensicar.model

class FakeCrashCountDown : CrashCountDown(mutableMapOf(), -1, -1) {

    override fun start() {
        // do nothing
    }

    override fun getInstance(
        crashesByLane: MutableMap<Int, CrashState>,
        laneNumber: Int,
        wait: Long
    ): CrashCountDown {
        return FakeCrashCountDown()
    }
}
