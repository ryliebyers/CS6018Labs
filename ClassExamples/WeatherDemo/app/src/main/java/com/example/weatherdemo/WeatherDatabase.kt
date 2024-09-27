package com.example.weatherdemo

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow


@Database(entities= [WeatherData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun weatherDao(): WeatherDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}


@Dao
interface WeatherDAO {

    @Insert
    suspend fun addWeatherData(data: WeatherData)

    @Query("SELECT * from weather ORDER BY timestamp DESC LIMIT 1")
    fun latestWeather() : Flow<WeatherData>

    @Query("SELECT * from weather ORDER BY timestamp DESC")
    fun allWeather() : Flow<List<WeatherData>>


}