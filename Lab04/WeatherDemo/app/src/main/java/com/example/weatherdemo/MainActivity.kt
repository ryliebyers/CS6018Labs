package com.example.weatherdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import android.net.Uri
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.weatherdemo.ui.theme.WeatherDemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ElevatedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.weatherdemo.JokeData
import com.example.weatherdemo.ui.theme.WeatherDemoTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: JokeViewModel by viewModels{  JokeViewModelFactory((application as JokeApplication).jokeRepository) }

        setContent {
            WeatherDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val currentJoke by vm.currentJoke.observeAsState()

                    Column() {

                        val scope = rememberCoroutineScope()
                        Text("Current Joke:",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            textDecoration = TextDecoration.Underline)
                        Spacer(modifier = Modifier.padding(10.dp))
                        JokeDataDisplay(data = currentJoke, modifier = Modifier.padding(5.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.padding(10.dp))

                        Row() {
                            ElevatedButton(onClick = {
                                scope.launch {
                                    delay(500) //give me the chance to cancel
                                    val response = getJokeCoroutine()
                                    vm.addJoke(response)
                                }
                            }) {
                                Text("Another Joke", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                        }
                        Spacer(modifier = Modifier.padding(10.dp))

                        val allJokes by vm.allJokes.observeAsState()

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier
                            .padding(5.dp)
                            .border(
                                2.dp, Color.Black
                            ), content = {
                            item {
                                Text("Previous Joke Data:",
                                    fontSize = 5.em,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 0.dp))
                            }
                            for(data in allJokes ?: listOf()){
                                item{
                                    JokeDataDisplay(data = data, modifier = Modifier.padding(10.dp), fontSize = 16.sp, fontWeight = FontWeight.Normal)
                                }
                            }
                        })

                    }
                }
            }
        }
    }
}




data class JokeResult(var value: String)



suspend fun getJokeCoroutine(): JokeResult {
    return withContext(Dispatchers.IO) {

        val url: Uri = Uri.Builder().scheme("https")
            .authority("api.chucknorris.io")
            .appendPath("jokes")
            .appendPath("random").build()

        val conn = URL(url.toString()).openConnection() as HttpURLConnection
        conn.connect()
        val gson = Gson()
        val result = gson.fromJson(
            InputStreamReader(conn.inputStream, "UTF-8"),
            JokeResult::class.java
        )
        Log.e("data!", gson.toJson(result).toString())
        result
    }
}

@Composable
fun JokeDataDisplay(data: JokeData?, modifier: Modifier = Modifier, fontSize: TextUnit, fontWeight: FontWeight) {
    Surface(color=MaterialTheme.colorScheme.surface) {
        Text(
            text = if (data != null) "${data.value}" else "NO JOKE DATA YET",
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}