package com.example.sodacanshaker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var shakeCount = 0
    private val shakeThreshold = 10

    // State to manage explosion state
    private var isExploded by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content to the Jetpack Compose UI
        setContent {
            SodaCanShakerApp()
        }

        // Initialize SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Check for accelerometer availability
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            Toast.makeText(this, "No accelerometer found on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            if (acceleration > shakeThreshold) {
                shakeCount++
                Log.d("ShakeDetection", "Shake Count: $shakeCount")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun explodeCan() {
        try {
            // Set exploded state to true
            isExploded = true
            shakeCount = 0 // Reset shake count
            Toast.makeText(this, "Boom! The can exploded!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ExplodeCan", "Error during explosion: ${e.message}")
        }
    }

    private fun resetGame() {
        isExploded = false // Reset explosion state
        shakeCount = 0 // Reset shake count
    }

    @Composable
    fun SodaCanShakerApp() {
        // Using Scaffold to add a TopAppBar
        Scaffold(
            topBar = {
                TopAppBar()
            }
        ) { innerPadding ->
            // Content of the app
            Surface(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                color = MaterialTheme.colorScheme.background) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Display the appropriate image based on the explosion state
                    if (isExploded) {
                        Image(
                            painter = painterResource(id = R.drawable.exploding_can), // Exploding can image
                            contentDescription = "Exploding Can"
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.can), // Coke can image
                            contentDescription = "Coke Can"
                        )
                    }

                    Button(onClick = {
                        if (isExploded) {
                            resetGame() // Reset the game if the can has exploded
                        } else {
                            if (shakeCount > shakeThreshold) {
                                explodeCan()
                            } else {
                                Toast.makeText(this@MainActivity, "Shake the can more!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text(text = if (isExploded) "Get A New Can" else "Open Can", fontSize = 20.sp)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBar(modifier: Modifier = Modifier) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                )
            },
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewSodaCanShakerApp() {
        SodaCanShakerApp()
    }
}


