package idnull.znz.losing_weight_is_easy.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idnull.znz.losing_weight_is_easy.data.database.MainRepository
import idnull.znz.losing_weight_is_easy.domain.Run
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
   val repository :MainRepository
) : ViewModel() {

    fun insertRun(run: Run) = viewModelScope.launch {
     repository.insertRun(run)
    }

}