package idnull.znz.losing_weight_is_easy

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
//import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.HiltAndroidApp
import idnull.znz.losing_weight_is_easy.utils.APP
import timber.log.Timber


///
val Context.dataStore: DataStore<Preferences> by preferencesDataStore( name = "KCal")
///
@HiltAndroidApp
class App : Application() {



    override fun onCreate() {
        super.onCreate()
        APP = this
       // dataStore = createDataStore("KCal")
        Timber.plant(Timber.DebugTree())
    }
}