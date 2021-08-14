package idnull.znz.losing_weight_is_easy.presentation.run

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import idnull.znz.losing_weight_is_easy.data.database.MainRepository
import idnull.znz.losing_weight_is_easy.domain.Run
import idnull.znz.losing_weight_is_easy.domain.SortType
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
     repository: MainRepository
) : ViewModel() {

   private val runSortedByDate = repository.getAllRunsSortedByDate()
   private val runSortedByDistance = repository.getAllRunsSortedByDistanceInMeters()
   private val runSortedByCaloriesBurned = repository.getAllRunsSortedByTCaloriesBurned()
   private val runSortedByTimeInMils = repository.getAllRunsSortedByTimeInMills()
   private val runSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeedInKMH()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE


    init {
        runs.addSource(runSortedByDate){result ->
            if (sortType==SortType.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByAvgSpeed){result ->
            if (sortType==SortType.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByCaloriesBurned){result ->
            if (sortType==SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runSortedByTimeInMils){result ->
            if (sortType==SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType)= when(sortType){

        SortType.DATE ->runSortedByDate.value?.let {runs.value = it  }
        SortType.RUNNING_TIME ->runSortedByTimeInMils.value?.let {runs.value = it  }
        SortType.AVG_SPEED ->runSortedByAvgSpeed.value?.let {runs.value = it  }
        SortType.DISTANCE ->runSortedByDistance.value?.let {runs.value = it  }
        SortType.CALORIES_BURNED ->runSortedByCaloriesBurned.value?.let {runs.value = it  }
    }.also {
        this.sortType = sortType
    }
}















