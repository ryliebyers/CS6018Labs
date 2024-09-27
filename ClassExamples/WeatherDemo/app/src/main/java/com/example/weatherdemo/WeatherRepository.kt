package com.example.weatherdemo


import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class WeatherRepository( val scope: CoroutineScope, val dao: WeatherDAO) {

    val currentWeather = dao.latestWeather().asLiveData()
    val allWeather = dao.allWeather().asLiveData()


    fun checkWeather(city: String){
        scope.launch {
            delay(1000) // pretend this is a slow network call
            // add http request to get the weather from remote server
            dao.addWeatherData(
                WeatherData(Date(), city, Random.Default.nextDouble(0.0,105.0)))
        }
    }
}