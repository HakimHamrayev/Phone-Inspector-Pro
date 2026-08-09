package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.DiagnosticViewModel
import java.util.Locale

@Composable
fun HardwareTestsScreen(
    viewModel: DiagnosticViewModel,
    modifier: Modifier = Modifier
) {
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val touchGridPassed by viewModel.touchGridPassed.collectAsState()

    val context = LocalContext.current

    // Live sensor state
    var accelX by remember { mutableStateOf(0f) }
    var accelY by remember { mutableStateOf(0f) }
    var accelZ by remember { mutableStateOf(0f) }

    var gyroX by remember { mutableStateOf(0f) }
    var gyroY by remember { mutableStateOf(0f) }
    var gyroZ by remember { mutableStateOf(0f) }

    var proximityVal by remember { mutableStateOf(5f) }
    var lightLux by remember { mutableStateOf(300f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val prox = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        val light = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelX = event.values[0]
                        accelY = event.values[1]
                        accelZ = event.values[2]
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroX = event.values[0]
                        gyroY = event.values[1]
                        gyroZ = event.values[2]
                    }
                    Sensor.TYPE_PROXIMITY -> proximityVal = event.values[0]
                    Sensor.TYPE_LIGHT -> lightLux = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accel?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        gyro?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        prox?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        light?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "INTERACTIVE HARDWARE SUITE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 1. TOUCH SCREEN DEAD-ZONE GRID TEST
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("touch_grid_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Touch Screen Dead-Zone Grid Test",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Swipe across all cells to verify touch sensor responsiveness.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = { viewModel.resetTouchTest() },
                            modifier = Modifier.testTag("reset_touch_grid_button")
                        ) {
                            Text("Reset", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val rows = 5
                    val cols = 8
                    val totalCells = rows * cols

                    val gridPct = (touchGridPassed.size.toFloat() / totalCells * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Coverage: $gridPct%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gridPct == 100) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                        if (gridPct == 100) {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Touch Display 100% Passed!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5x8 Grid Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val cellWidth = size.width / cols
                                        val cellHeight = size.height / rows
                                        val col = (offset.x / cellWidth)
                                            .toInt()
                                            .coerceIn(0, cols - 1)
                                        val row = (offset.y / cellHeight)
                                            .toInt()
                                            .coerceIn(0, rows - 1)
                                        val index = row * cols + col
                                        viewModel.markTouchCellPassed(index)
                                    },
                                    onDrag = { change, _ ->
                                        val cellWidth = size.width / cols
                                        val cellHeight = size.height / rows
                                        val col = (change.position.x / cellWidth)
                                            .toInt()
                                            .coerceIn(0, cols - 1)
                                        val row = (change.position.y / cellHeight)
                                            .toInt()
                                            .coerceIn(0, rows - 1)
                                        val index = row * cols + col
                                        viewModel.markTouchCellPassed(index)
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val cellWidth = size.width / cols
                                    val cellHeight = size.height / rows
                                    val col = (offset.x / cellWidth)
                                        .toInt()
                                        .coerceIn(0, cols - 1)
                                    val row = (offset.y / cellHeight)
                                        .toInt()
                                        .coerceIn(0, rows - 1)
                                    val index = row * cols + col
                                    viewModel.markTouchCellPassed(index)
                                }
                            }
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    for (c in 0 until cols) {
                                        val cellIdx = r * cols + c
                                        val isPassed = touchGridPassed.contains(cellIdx)
                                        val cellColor = if (isPassed) Color(0xFF10B981) else Color(0xFF94A3B8).copy(alpha = 0.3f)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(1.dp)
                                                .background(cellColor, RoundedCornerShape(4.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. HARDWARE TOGGLE TESTS (Flashlight, Vibration, Speakers)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("hardware_toggles_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Hardware Components Test",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Flashlight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlashlightOn,
                                contentDescription = "Flashlight",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Camera Flash Torch",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isFlashlightOn) "Flashlight ACTIVE" else "Tap to toggle LED flash",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isFlashlightOn,
                            onCheckedChange = { viewModel.toggleFlashlight() },
                            modifier = Modifier.testTag("flashlight_switch")
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Vibration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Vibration",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Vibration Motor",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tests haptic feedback motor",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.testVibration() },
                            modifier = Modifier.testTag("test_vibration_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Vibrate", fontSize = 12.sp)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Speaker Sound Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaker",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Speaker & Sound Test",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tests stereo audio output",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.testSpeakerSound() },
                            modifier = Modifier.testTag("test_speaker_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Play Sound", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. LIVE SENSOR REAL-TIME READOUT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("live_sensors_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Real-Time Sensor Readouts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Accelerometer
                    SensorLiveRow(
                        label = "Accelerometer (3-Axis)",
                        valStr = String.format(Locale.US, "X: %.2f  Y: %.2f  Z: %.2f m/s²", accelX, accelY, accelZ),
                        icon = Icons.Default.ScreenRotation
                    )

                    // Gyroscope
                    SensorLiveRow(
                        label = "Gyroscope (Rotation)",
                        valStr = String.format(Locale.US, "X: %.2f  Y: %.2f  Z: %.2f rad/s", gyroX, gyroY, gyroZ),
                        icon = Icons.Default.Sync
                    )

                    // Proximity & Ambient Light
                    SensorLiveRow(
                        label = "Proximity & Ambient Light",
                        valStr = String.format(Locale.US, "Proximity: %.1f cm  •  Light: %.0f Lux", proximityVal, lightLux),
                        icon = Icons.Default.LightMode
                    )
                }
            }
        }
    }
}

@Composable
fun SensorLiveRow(label: String, valStr: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valStr,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
