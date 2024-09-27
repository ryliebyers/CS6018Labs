package com.example.testingdemo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmailViewModel : ViewModel() {


    private val _firstName = MutableLiveData("")
    val firstName  = _firstName as LiveData<String>
    private val _lastName = MutableLiveData("")
    val lastName = _lastName as LiveData<String>

    fun setEmail(email: String, onSuccess: ()-> Unit, onFail: () -> Unit){
        val words = email.split("@")
        if(words.size == 2){
            _firstName.value = words[0]
            _lastName.value = words[1]
            Log.e("viewmodel", "success")
            onSuccess()
        } else {
            Log.e("viewmodel", "failure")
            onFail()
        }
    }


}