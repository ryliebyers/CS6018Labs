package com.example.lab4

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class JokeViewModel(private val repository: JokeRepository) : ViewModel() {
    val currentJoke: LiveData<JokeData> = repository.currentJoke

    val allJokes: LiveData<List<JokeData>> = repository.allJokes

    fun checkJokes(joke: String){
        repository.checkJokes(joke)
    }
    fun addData(joke: JokeResult) {
        viewModelScope.launch{
            repository.checkJokes(joke.value)
        }
    }

}

class JokeViewModelFactory(private val repository: JokeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JokeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JokeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}