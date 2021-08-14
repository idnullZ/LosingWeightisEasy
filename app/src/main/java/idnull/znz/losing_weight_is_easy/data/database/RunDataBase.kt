package idnull.znz.losing_weight_is_easy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import idnull.znz.losing_weight_is_easy.domain.Run
import idnull.znz.losing_weight_is_easy.utils.Converters

@Database(entities = [Run::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunDataBase : RoomDatabase() {
    abstract fun getRunDao(): RunDao
}