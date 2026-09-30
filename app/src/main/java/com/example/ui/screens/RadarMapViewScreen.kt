package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodStockItem
import com.example.data.model.UserAccount
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.openDirections
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.GreenAvailable
import com.example.ui.theme.RedCritical
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedSoftBackground

data class MapPinItem(
    val id: Long,
    val isBloodBank: Boolean,
    val title: String,
    val subtitle: String,
    val bloodGroup: String,
    val distanceKm: Double,
    val phone: String,
    val address: String,
    // Relative offset on radar (-1f to 1f)
    val relX: Float,
    val relY: Float
)

@Composable
fun RadarMapViewScreen(
    language: AppLanguage,
    stockItems: List<BloodStockItem>,
    donors: List<UserAccount>
) {
    val context = LocalContext.current

    // Build pins
    val pins = remember(stockItems, donors) {
        val list = mutableListOf<MapPinItem>()
        stockItems.take(5).forEachIndexed { i, s ->
            val angle = (i * 72) * (Math.PI / 180.0)
            val dist = 0.35f + (i * 0.12f)
            list.add(
                MapPinItem(
                    id = s.id,
                    isBloodBank = true,
                    title = s.bloodBankName,
                    subtitle = "${s.units} units available • ${s.component}",
                    bloodGroup = s.bloodGroup,
                    distanceKm = s.distanceKm,
                    phone = s.phone,
                    address = s.address,
                    relX = (Math.cos(angle) * dist).toFloat(),
                    relY = (Math.sin(angle) * dist).toFloat()
                )
            )
        }
        donors.take(4).forEachIndexed { j, d ->
            val angle = ((j * 90) + 45) * (Math.PI / 180.0)
            val dist = 0.5f + (j * 0.1f)
            list.add(
                MapPinItem(
                    id = 1000L + d.id,
                    isBloodBank = false,
                    title = "${d.name} (Donor)",
                    subtitle = "Age ${d.age} • ${d.city}",
                    bloodGroup = d.bloodGroup,
                    distanceKm = 1.2 + (j * 1.1),
                    phone = d.phone,
                    address = "${d.city}, Sector ${j + 1}",
                    relX = (Math.cos(angle) * dist).toFloat(),
                    relY = (Math.sin(angle) * dist).toFloat()
                )
            )
        }
        list
    }

    var selectedPin by remember { mutableStateOf<MapPinItem?>(pins.firstOrNull()) }

    val transition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Nearby Emergency Radar Map",
                fontWeight = FontWeight.Black,
                fontSize = 19.sp,
                color = RedDark
            )
            Text(
                text = "Tap on pins to inspect blood banks and eligible nearby donors",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Radar Canvas Map
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2124)),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pins) {
                        detectTapGestures { tapOffset ->
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val radius = Math.min(cx, cy) * 0.85f

                            var closest: MapPinItem? = null
                            var minDist = 48f // tap radius in px

                            for (pin in pins) {
                                val px = cx + (pin.relX * radius)
                                val py = cy + (pin.relY * radius)
                                val dx = tapOffset.x - px
                                val dy = tapOffset.y - py
                                val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                                if (dist < minDist) {
                                    minDist = dist
                                    closest = pin
                                }
                            }
                            if (closest != null) {
                                selectedPin = closest
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val maxR = Math.min(cx, cy) * 0.85f

                    // Concentric range circles
                    val circleColors = Color.White.copy(alpha = 0.15f)
                    drawCircle(color = circleColors, radius = maxR * 0.33f, center = Offset(cx, cy), style = Stroke(1.5f))
                    drawCircle(color = circleColors, radius = maxR * 0.66f, center = Offset(cx, cy), style = Stroke(1.5f))
                    drawCircle(color = circleColors, radius = maxR, center = Offset(cx, cy), style = Stroke(2f))

                    // Cross hairs
                    drawLine(color = circleColors, start = Offset(cx - maxR, cy), end = Offset(cx + maxR, cy), strokeWidth = 1f)
                    drawLine(color = circleColors, start = Offset(cx, cy - maxR), end = Offset(cx, cy + maxR), strokeWidth = 1f)

                    // User Center Location Pin
                    drawCircle(color = BlueInfo.copy(alpha = 0.35f), radius = 18f, center = Offset(cx, cy))
                    drawCircle(color = Color.White, radius = 6f, center = Offset(cx, cy))

                    // Draw pins
                    pins.forEach { pin ->
                        val px = cx + (pin.relX * maxR)
                        val py = cy + (pin.relY * maxR)
                        val isSelected = selectedPin?.id == pin.id

                        val pinColor = if (pin.isBloodBank) RedCritical else GreenAvailable
                        if (isSelected) {
                            drawCircle(color = Color.White, radius = 14f, center = Offset(px, py), style = Stroke(2f))
                        }
                        drawCircle(color = pinColor.copy(alpha = 0.3f), radius = 12f, center = Offset(px, py))
                        drawCircle(color = pinColor, radius = 7f, center = Offset(px, py))
                    }
                }

                // Legend at top-right
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(RedCritical, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Blood Bank", color = Color.White, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(GreenAvailable, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active Donor", color = Color.White, fontSize = 9.sp)
                        }
                    }
                }

                // Center Label
                Text(
                    text = "You (Location)",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 22.dp)
                )
            }
        }

        // Selected Pin Details Card
        selectedPin?.let { pin ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_selected_pin_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (pin.isBloodBank) RedSoftBackground else Color(0xFFE8F5E9),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (pin.isBloodBank) Icons.Default.LocalHospital else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (pin.isBloodBank) RedEmergency else GreenAvailable,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = pin.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${pin.distanceKm} km away • ${pin.subtitle}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RedEmergency
                        ) {
                            Text(
                                text = pin.bloodGroup,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pin.address,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { openDirections(context, pin.address, 12.9716, 77.5946) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Directions", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { dialPhoneNumber(context, pin.phone) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
