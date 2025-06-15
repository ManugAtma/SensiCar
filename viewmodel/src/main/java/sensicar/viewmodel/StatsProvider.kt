package sensicar.viewmodel

import sensicar.dto.StatsDTO

class StatsProvider {

 var statsDTO: StatsDTO? = null

    fun setStats(){
    }

    fun getStats(): StatsDTO {
        return StatsDTO()
    }

}