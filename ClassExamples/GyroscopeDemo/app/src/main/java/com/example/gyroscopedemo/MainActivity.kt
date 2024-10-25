package com.example.gyroscopedemo

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gyroscopedemo.ui.theme.GyroscopeDemoTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.emptyFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val gyroFlow : Flow<FloatArray> = getGyroData(gyroscope, sensorManager)

        setContent {
            GyroscopeDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var text by remember { mutableStateOf("") }

                    LaunchedEffect(key1 = gyroFlow)
                    {
                        gyroFlow.collect { gyroReading ->
                            text = "Gyro reading: ${gyroReading[0]} ${gyroReading[1]} ${gyroReading[2]}"
                        }
                    }
                        Text(text = text)
                    }
//                    val gyroReading by gyroFlow.collectAsStateWithLifecycle(
//                        floatArrayOf(0.0f, 0.0f, 0.0f),
//                        lifecycleOwner = this@MainActivity
//                    )
//                    Text("Gyro reading: ${gyroReading[0]} ${gyroReading[1]} ${gyroReading[2]}")
                }
            }
        }
    }



fun getGyroData(gyroscope: Sensor, sensorManager: SensorManager): Flow<FloatArray> {
    return channelFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event !== null) {
                    Log.e("Sensor event!", event.values.toString())
                    var success = channel.trySend(event.values).isSuccess
                   Log.e("success?", success.toString())
                }

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            
            }
        }
        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}