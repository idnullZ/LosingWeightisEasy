package idnull.znz.losing_weight_is_easy.data.database

import idnull.znz.losing_weight_is_easy.domain.Run
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeInMills() = runDao.getAllRunsSortedByTimeInMills()

    fun getAllRunsSortedByTCaloriesBurned() = runDao.getAllRunsSortedByTCaloriesBurned()

    fun getAllRunsSortedByAvgSpeedInKMH() = runDao.getAllRunsSortedByAvgSpeedInKMH()

    fun getAllRunsSortedByDistanceInMeters() = runDao.getAllRunsSortedByDistanceInMeters()


}