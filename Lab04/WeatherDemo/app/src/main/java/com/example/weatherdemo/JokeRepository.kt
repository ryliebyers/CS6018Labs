package com.example.weatherdemo

import androidx.lifecycle.asLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random



class JokeRepository( val scope: CoroutineScope, val dao: JokeDAO) {

    val currentJoke = dao.latestJoke().asLiveData()

    val allJokes = dao.allJokes().asLiveData()
    fun checkJoke(joke: String){
        scope.launch {
            delay(1000) // pretend this is a slow network call
            dao.addJokeData(
                JokeData(Date(), joke))
        }
    }
}