package com.example.shakedetector

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shakedetector.ui.theme.ShakeDetectorTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

const val SHAKE_THRESHOLD = 3.0f

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val accelMagFlow = getAccelMagnitude(accelerometer, sensorManager)
        val shakingFlow : Flow<Boolean> = shakeDetector(accelerometer, sensorManager)
        setContent {
            ShakeDetectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        val accelMag by accelMagFlow.collectAsStateWithLifecycle(0.0f)
                        if (accelMag > SHAKE_THRESHOLD) {
                            Text(
                                "ACCELERATING A LOT",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        val shaking by shakingFlow.collectAsStateWithLifecycle(initialValue = false)
                        if(shaking){
                            Text(
                                "SHAKING",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.fillMaxWidth().background(Color.Blue)

                            )
                        }
                    }
                }
            }
        }
    }
}
fun getAccelMagnitude(accelerometer: Sensor, sensorManager: SensorManager): Flow<Float> {
    return channelFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event !== null) {
                    channel.trySend(event.values.map{x -> x*x}.reduce(Float::plus))
                }

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //
            }
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}


fun shakeDetector(accelerometer: Sensor, sensorManager: SensorManager): Flow<Boolean> {
    return channelFlow {
        val listener = object : SensorEventListener {

            //store some previous values here
            val history = mutableListOf<FloatArray>()

            override fun onSensorChanged(event: SensorEvent?) {
                if (event !== null) {
                    if(history.size > 10) { //store a few previous vectors
                        history.removeAt(0) //inefficient but :shrug:
                    }
                    history.add(event.values.copyOf())
                    //detect "shaking" by acceleration readings in opposite directions in the history
                    //use the dot product to compute that
                    Log.e("ACCEL", "${event.values[0]} ${event.values[1]} ${event.values[2]}")
                    val minDotProduct = history.minOf {
                        it.zip(event.values).map { pair -> //dot product x*x + y*y + z*z
                            pair.first * pair.second
                        }.reduce(Float::plus)
                    }
                    Log.e("MDP", "${history.size} ${minDotProduct.toString()}")
                    channel.trySend(minDotProduct < -2.0)

                }

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //
            }
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}