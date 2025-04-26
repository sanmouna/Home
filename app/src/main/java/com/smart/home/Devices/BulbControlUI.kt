package com.smart.home.Devices

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun BulbControlUI() {
    var isBulbOn by remember { mutableStateOf(true) }
    var bulbColor by remember { mutableStateOf(Color.Yellow) }
    var brightness by remember { mutableStateOf(0.5f) }

    // Animate bulb color and brightness
    val animatedColor by animateColorAsState(
        targetValue = if (isBulbOn) bulbColor else Color.Gray, // Gray when off
        animationSpec = tween(durationMillis = 500)
    )

    val animatedBrightness by animateFloatAsState(
        targetValue = if (isBulbOn) brightness else 0f, // No brightness when off
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Bulb Icon with Animated Color and Brightness
        Icon(
            imageVector = Icons.Filled.Lightbulb,
            contentDescription = "Bulb Icon",
            tint = animatedColor.copy(alpha = animatedBrightness),  // Adjust color and brightness
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bulb on/off toggle button with animated label
        Button(onClick = { isBulbOn = !isBulbOn }) {
            Text(text = if (isBulbOn) "Turn Off" else "Turn On")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Brightness Slider with smooth animation
        Text(text = "Brightness", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Slider(
            value = brightness,
            onValueChange = { brightness = it },
            valueRange = 0f..1f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Color Buttons with animations for changing bulb color
        Text(text = "Choose Color", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ColorButton(Color.Yellow) { bulbColor = it }
            ColorButton(Color.Red) { bulbColor = it }
            ColorButton(Color.Green) { bulbColor = it }
        }
    }
}

@Composable
fun ColorButton(color: Color, onClick: (Color) -> Unit) {
    Button(
        onClick = { onClick(color) },
        modifier = Modifier
            .size(50.dp)
            .background(color),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Empty button with background color
    }
}


