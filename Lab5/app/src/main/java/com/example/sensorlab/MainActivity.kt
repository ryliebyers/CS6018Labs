package com.example.sensorlab

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sensorlab.ui.theme.SensorLabTheme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SensorManager service to access sensors
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Get the default gravity sensor
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        // Create a flow to collect gravity data if the sensor is available
        val gravityFlow : Flow<FloatArray>? = gravitySensor?.let { getGravityData(it, sensorManager) }

        // Set the content view with Jetpack Compose
        setContent {
            SensorLabTheme {
                Surface(
                    modifier = Modifier
                        .padding(1.dp)
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        // State variables for x and y positions of the marble
                        var x by remember { mutableStateOf(0.0f) }
                        var y by remember { mutableStateOf(0.0f) }

                        // Collect gravity data in a coroutine
                        LaunchedEffect(key1 = gravityFlow) {
                            if (gravityFlow != null) {
                                gravityFlow.collect { gravityReading ->
                                    // Update x and y positions based on gravity readings
                                    x = gravityReading[0]
                                    y = gravityReading[1]
                                }
                            }
                        }

                        // Render the marble with updated x and y positions
                        Marble(xPos = x, yPos = y)
                    }
                }
            }
        }
    }
}

// Function to get gravity data as a Flow from the sensor
fun getGravityData(gravitySensor: Sensor, sensorManager: SensorManager): Flow<FloatArray> {
    return channelFlow {
        // Create a listener for sensor events
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    // Log the sensor event values
                    Log.e("Sensor event!", event.values.toString())
                    // Try to send the copied values to the channel
                    val success = channel.trySend(event.values.copyOf()).isSuccess
                    Log.e("success?", success.toString())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used in this example
            }
        }
        // Register the listener to receive sensor updates
        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME) // Use game delay for smoother updates

        // Unregister the listener when the flow is closed
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

// Composable function to render a marble based on x and y positions
@Composable
fun Marble(xPos: Float, yPos: Float, modifier: Modifier = Modifier) {

    // State variables for the marble's position offsets
    var xOffset by remember { mutableStateOf(0.0f) }
    var yOffset by remember { mutableStateOf(0.0f) }

    // Update xOffset based on xPos
    if (xPos > 0) {
        xOffset -= 4
    } else if (xPos < 0) {
        xOffset += 4
    }

    // Update yOffset based on yPos
    if (yPos > 0) {
        yOffset += 4
    } else if (yPos < 0) {
        yOffset -= 4
    }

    // Hold the internal box for the marble
    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = modifier
            .fillMaxSize(fraction = 1.0f)
            .align(Alignment.Center)
            .drawBehind {
                drawRect(Color.White) // Background for the marble
            }
        ) {
            BoxWithConstraints {
                // Ensure marble stays within bounds
                if (xOffset < 0) {
                    xOffset = 0.0f
                }
                if (xOffset > (maxWidth.value - 80.0f)) {
                    xOffset = maxWidth.value - 80.0f
                }
                if (yOffset < 0) {
                    yOffset = 0.0f
                }
                if (yOffset > (maxHeight.value - 80.0f)) {
                    yOffset = maxHeight.value - 80.0f
                }
                // Render the marble
                Box(
                    modifier = modifier
                        .offset(
                            xOffset.dp,
                            yOffset.dp
                        )
                        .size(80.dp)
                        .clip(CircleShape) // Make the marble circular
                        .drawBehind {
                            drawRect(Color.Magenta) // Color of the marble
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // You can add content inside the marble if needed
                }
            }
        }
    }
}
