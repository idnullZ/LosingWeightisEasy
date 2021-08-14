package idnull.znz.losing_weight_is_easy.presentation.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import idnull.znz.losing_weight_is_easy.dataStore
import idnull.znz.losing_weight_is_easy.utils.APP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor( ) : ViewModel() {

    private val _kCal: MutableStateFlow<Int> = MutableStateFlow(0)
    val kCal: StateFlow<Int> = _kCal.asStateFlow()

    private val _lostValueKCal: MutableStateFlow<Int> = MutableStateFlow(0)
    val lostValueKCal: StateFlow<Int> = _lostValueKCal.asStateFlow()

    private var onePercent = 0
    private var maxKCalValue = 0


    private val _kCalPercent: MutableStateFlow<Int> = MutableStateFlow(0)
    val kCalPercent: StateFlow<Int> = _kCalPercent.asStateFlow()

    init {
        initDay()
    }

    fun addKCal(value: Int) {
        setKCal(_kCal.value + value)
    }

    private fun setKCal(kCal: Int) {
        viewModelScope.launch {
            _kCal.value = saveDataKCal(kCal)
            if (_kCalPercent.value > 0) {
                _kCalPercent.value = _kCal.value / onePercent
            }
            setLostValueKCal()
        }
    }

    private suspend fun saveDataKCal(value: Int): Int {
        val dataStoreKey = stringPreferencesKey("key1")
        APP.dataStore.edit {
            it[dataStoreKey] = value.toString()
        }
        return getKCalOfPreferences()
    }

    private suspend fun getKCalOfPreferences(): Int {
        val dataStoreKey = stringPreferencesKey("key1")
        val data = APP.dataStore.data.first()
        return if (data[dataStoreKey] == null) 0 else data[dataStoreKey].toString().toInt()
    }

    fun setInitialValue() {
        viewModelScope.launch {
            val maxValue = readMaxKCalInPreferences()
            maxKCalValue = maxValue

            if (maxValue > 0) {
                onePercent = maxValue / 100
            }
            val value = getKCalOfPreferences()
            _kCal.value = value
            if (value > 0) {
                _kCalPercent.value = value / (readMaxKCalInPreferences() / 100)
            }
            setLostValueKCal()
        }
    }

    private suspend fun saveDataInCalendar(value: Int) {
        val dataStoreKey = stringPreferencesKey("calendar")
        APP.dataStore.edit {
            it[dataStoreKey] = value.toString()
        }
    }

   private suspend fun getLastDayOfPreferences(): Int {
        val dataStoreKey = stringPreferencesKey("calendar")
        val data = APP.dataStore.data.first()
        return if (data[dataStoreKey] == null) -1 else data[dataStoreKey].toString().toInt()
    }

    private fun getToDay(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    private fun initDay() {
        val nowDay = getToDay()
        var lastDayOfPreferences = -1
        viewModelScope.launch {
            lastDayOfPreferences = getLastDayOfPreferences()
            if (nowDay != lastDayOfPreferences) {
                setKCal(0)
                saveDataInCalendar(nowDay)
            }
        }
    }

   suspend fun readMaxKCalInPreferences(): Int {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        val data = APP.dataStore.data.first()
        return if (data[dataStoreKey] == null) 0 else data[dataStoreKey]?.toInt()!!
    }

    private fun setLostValueKCal() {
        _lostValueKCal.value = maxKCalValue - _kCal.value

    }


}

