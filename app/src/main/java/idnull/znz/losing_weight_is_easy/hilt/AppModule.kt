package idnull.znz.losing_weight_is_easy.hilt

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import idnull.znz.losing_weight_is_easy.data.database.RunDataBase
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_FIRST_TIME_TOGGLE
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_NAME
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_WEIGHT
import idnull.znz.losing_weight_is_easy.utils.Constants.RUN_DATABASE_NAME
import idnull.znz.losing_weight_is_easy.utils.Constants.SHARED_PREFERENCES_NAME
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunDataBase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, RunDataBase::class.java, RUN_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunDataBase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Provides
    @Singleton
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 0f)

    @Provides
    @Singleton
    fun provideFirsTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true
    )
}













