package com.example.workmanagerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workmanagerdemo.ui.theme.WorkManagerDemoTheme

//ADAPTED FROM THE AOSP WorkManager codelab

enum class BlurAmount(val amount: Int) {
    Some(1),
    Lots(2) ,
    Tons(3)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkManagerDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel { BlurViewModelFactory(this@MainActivity.application).create(BlurViewModel::class.java) }
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.android_cupcake),
                            contentDescription ="a cupcake"
                        )
                        Text("Select Blur Amount", style = MaterialTheme.typography.headlineMedium)
                        var buttonState by remember { mutableStateOf(BlurAmount.Some) }

                        Column(Modifier.selectableGroup()) {

                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {buttonState = BlurAmount.Some}){
                                RadioButton(
                                    selected = buttonState == BlurAmount.Some,
                                    onClick = {buttonState = BlurAmount.Some},
                                    modifier = Modifier.semantics { contentDescription = "Some Blur" }
                                )
                                Text("Some Blur")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = buttonState == BlurAmount.Lots,
                                    onClick = { buttonState = BlurAmount.Lots },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Lots of Blur"
                                    }
                                )
                                Text("Lots of Blur")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = buttonState == BlurAmount.Tons,
                                    onClick = { buttonState = BlurAmount.Tons },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Tons of Blur"
                                    }
                                )
                                Text("Tons of Blur")
                            }

                            val workInfos by viewModel.outputWorkInfos.observeAsState()
                            val workInfo = if(workInfos?.isNotEmpty() == true) workInfos!![0] else null

                            Row {
                                Button({
                                    viewModel.applyBlur(buttonState.amount)
                                }){Text("Go")}
                                if(workInfo != null && !workInfo.state.isFinished) {
                                    Button({ viewModel.cancelWork() }) { Text("Cancel") }
                                }
                                if(workInfo != null && workInfo.state.isFinished) {
                                    val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                                    // If there is an output file show "See File" button
                                    if (!outputImageUri.isNullOrEmpty()) {
                                        viewModel.setOutputUri(outputImageUri)

                                        Button({

                                            viewModel.outputUri?.let { currentUri ->
                                                Log.e("see image", currentUri.toString())
                                                val actionView =
                                                    Intent(Intent.ACTION_VIEW, currentUri)
                                                actionView.resolveActivity(packageManager)?.run {
                                                    startActivity(actionView)
                                                }
                                            }
                                        }) { Text("See File") }
                                    }
                                }
                            }
                            if(workInfo != null && !workInfo.state.isFinished) {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    progress = 0.5f
                                )
                            }

                        } //Row
                    }
                }
            }
        }
    }
}

