package sensicar.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DistanceTracker (val speed: MutableStateFlow<Float>) {

    var distance = MutableStateFlow(0F)

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    fun start(){
        println("starting tracker")
        scope.launch {
            track()
        }
    }

    private suspend fun track(){

        var lastTime = System.currentTimeMillis()
        println("track before loop")
        while (speed.value > 0){
            val now = System.currentTimeMillis()
            val delta = now - lastTime
            distance.value += speed.value * (delta / 1000F)
            lastTime = now
            delay(200)
            //println(speed.value)
        }
    }

    fun stop(){
        //job.cancel()
        this.distance.value = 0F
    }

}