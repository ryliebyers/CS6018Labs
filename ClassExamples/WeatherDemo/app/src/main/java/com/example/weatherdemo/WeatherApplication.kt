package com.example.weatherdemo

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class WeatherApplication : Application() {

    val scope = CoroutineScope(SupervisorJob())
    val db by lazy {WeatherDatabase.getDatabase(applicationContext)}
    val weatherRepository by lazy {WeatherRepository(scope, db.weatherDao())}
}