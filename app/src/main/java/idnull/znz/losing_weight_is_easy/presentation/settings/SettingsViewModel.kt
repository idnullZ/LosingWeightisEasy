package idnull.znz.losing_weight_is_easy.presentation.settings

import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idnull.znz.losing_weight_is_easy.dataStore
import idnull.znz.losing_weight_is_easy.utils.APP
import idnull.znz.losing_weight_is_easy.utils.APP_ACTIVITY
import idnull.znz.losing_weight_is_easy.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    val sharedPref: SharedPreferences
) : BaseViewModel<SettingsViewState, SettingsAction, SettingsEvent>() {


    private fun setData() {
        var value: String? = null
        viewModelScope.launch {
            value = readMaxKCalInPreferences().toString()
        }.onJoin
        viewState =
            SettingsViewState(
                fetchStatus = FetchStatus.Load,
                nameText = sharedPref.getString(Constants.KEY_NAME, "").toString(),
                weightText = sharedPref.getFloat(Constants.KEY_WEIGHT, 80f).toString(),
                kCal = value.toString()
            )
    }


    override fun obtainEvent(viewEvent: SettingsEvent) {
        when (viewEvent) {
            SettingsEvent.ScreenShown -> setData()
            SettingsEvent.ClickUpdate -> clickUpdate()
        }
    }


    fun applyChangesToSharedPref(nameText: String, weightText: String, kCal: String) {

        if (nameText.isEmpty() || weightText.isEmpty() || kCal.isEmpty()) {

            viewAction = SettingsAction.ShowToast("Please fill out all fields")
        }
        viewModelScope.launch {
            try {
                saveMaxKCalInPreferences(kCal.toInt())
            } catch (e: Exception) {
                viewAction = SettingsAction.ShowToast("input KCal")
            }
        }
        sharedPref.edit()
            .putString(Constants.KEY_NAME, nameText)
            .putFloat(Constants.KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go, $nameText!"

        APP_ACTIVITY.mBinding.tvToolbarTitle.text = toolbarText
        viewAction = SettingsAction.ShowToast("Saved changes")
        setData()
    }


    private suspend fun readMaxKCalInPreferences(): Int {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        val data = APP.dataStore.data.firstOrNull()?.get(dataStoreKey)

        return data?.toInt() ?: 0
    }

    private suspend fun saveMaxKCalInPreferences(value: Int) {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        APP.dataStore.edit {
            it[dataStoreKey] = value.toString()
        }
    }

    private fun clickUpdate() {

    }
}