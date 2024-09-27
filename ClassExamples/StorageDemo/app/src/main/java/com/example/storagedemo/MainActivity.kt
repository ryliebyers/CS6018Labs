package com.example.storagedemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.storagedemo.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files

//extension property, in this package, Context objects will now have a datastore property
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferenceFilename")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val FILENAME = "myfile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val prefs: Flow<Preferences> = dataStore.data
        val keyForData: Preferences.Key<Int> = intPreferencesKey("my_key") //"type safety"


        //periodically write to the data store
        lifecycleScope.launch{
            for(i in 1..10){
                delay(1000)
                Log.e("iteration", i.toString())
                    //suspend function
                dataStore.edit {
                    val currentVal = it[keyForData] ?: 0
                    it[keyForData] = currentVal + 1
                }
            }
        }

        //watch for updates.  This is pretty similar to livedata.observe
        lifecycleScope.launch {
            prefs.collect() {
                val currentVal = it[keyForData]?.toString() ?: "unknown"
                MainScope().launch {
                    binding.text.text = "preferences value: $currentVal"
                }
            }
        }

        updateFileView()

        binding.deleteButton.setOnClickListener(){
            val filename = File(filesDir, FILENAME)
            Files.deleteIfExists(filename.toPath())
            updateFileView()
        }

        binding.saveButton.setOnClickListener{
            val filename = File(filesDir, FILENAME)
            filename.writeText("This is some text that's saved in a file")
            updateFileView()

        }


        setContentView(binding.root)
    }

    private fun updateFileView(){
        val filename = File(filesDir, FILENAME)
        binding.fileContents.text = if(filename.exists()){
            filename.readText()
        } else {
            "File doesn't exist"
        }
    }

}