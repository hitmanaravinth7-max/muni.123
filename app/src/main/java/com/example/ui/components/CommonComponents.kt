package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguageManager
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberUrgent
import com.example.ui.theme.GreenAvailable
import com.example.ui.theme.GreenLight
import com.example.ui.theme.RedCritical
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedLight
import com.example.ui.theme.RedSoftBackground
import com.example.ui.viewmodel.AppScreen

fun dialPhoneNumber(context: Context, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open phone dialer: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed. Launching dialer...", Toast.LENGTH_SHORT).show()
        dialPhoneNumber(context, phoneNumber)
    }
}

fun openDirections(context: Context, address: String, lat: Double, lng: Double) {
    try {
        val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Directions to: $address", Toast.LENGTH_LONG).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeLinkTopBar(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    activeUser: UserAccount?,
    onNavigateBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenSos: () -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    onLogout: () -> Unit
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = RedDark,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "LifeLink Icon",
                            tint = RedEmergency,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "LifeLink",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = LanguageManager.getString("app_subtitle", currentLanguage),
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            if (currentScreen != AppScreen.HOME) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            // SOS quick button in app bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { onOpenSos() }
                    .testTag("appbar_sos_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "SOS",
                        tint = RedCritical,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SOS",
                        fontWeight = FontWeight.Bold,
                        color = RedCritical,
                        fontSize = 12.sp
                    )
                }
            }

            // Language Switcher
            IconButton(
                onClick = onToggleLanguage,
                modifier = Modifier.testTag("language_toggle_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "HI" else "EN",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Profile / Auth Menu
            Box {
                IconButton(
                    onClick = { showProfileMenu = true },
                    modifier = Modifier.testTag("profile_menu_button")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (activeUser != null) GreenAvailable else Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (activeUser != null) {
                                Text(
                                    text = activeUser.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Account",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                DropdownMenu(
                    expanded = showProfileMenu,
                    onDismissRequest = { showProfileMenu = false }
                ) {
                    if (activeUser != null) {
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = activeUser.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${activeUser.role.name} • ${activeUser.bloodGroup}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                showProfileMenu = false
                                when (activeUser.role) {
                                    UserRole.DONOR -> onNavigateTo(AppScreen.DONOR_DASHBOARD)
                                    UserRole.BLOOD_BANK_ADMIN -> onNavigateTo(AppScreen.ADMIN_STOCK)
                                    UserRole.REQUESTER -> onNavigateTo(AppScreen.EMERGENCY_REQUESTS)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showProfileMenu = false
                                onLogout()
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Sign In") },
                            onClick = {
                                showProfileMenu = false
                                onNavigateTo(AppScreen.LOGIN)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Register Account") },
                            onClick = {
                                showProfileMenu = false
                                onNavigateTo(AppScreen.REGISTER)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SosBroadcastBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("sos_broadcast_banner"),
        colors = CardDefaults.cardColors(containerColor = RedCritical),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SosEmergencyDialog(
    isOpen: Boolean,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onTriggerBroadcast: (bloodGroup: String, city: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedGroup by remember { mutableStateOf("O-") }
    var selectedCity by remember { mutableStateOf("Metro Central") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = RedCritical,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LanguageManager.getString("sos_button", language),
                        fontWeight = FontWeight.Black,
                        color = RedCritical,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Instant 1-Tap Emergency Broadcast",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "If this is a life-threatening crisis, call emergency medical services immediately or broadcast an alert to all nearby donors in your area.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Direct Hotline Call
                Button(
                    onClick = { dialPhoneNumber(context, "108") },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sos_call_hotline_button")
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Emergency Ambulance (108 / 911)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Or broadcast urgently needed blood group:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Group Chips
                val groups = listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    groups.take(4).forEach { group ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedGroup == group) RedEmergency else RedSoftBackground,
                            border = if (selectedGroup == group) null else androidx.compose.foundation.BorderStroke(1.dp, RedLight),
                            modifier = Modifier
                                .clickable { selectedGroup = group }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = group,
                                color = if (selectedGroup == group) Color.White else RedDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    groups.drop(4).forEach { group ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedGroup == group) RedEmergency else RedSoftBackground,
                            border = if (selectedGroup == group) null else androidx.compose.foundation.BorderStroke(1.dp, RedLight),
                            modifier = Modifier
                                .clickable { selectedGroup = group }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = group,
                                color = if (selectedGroup == group) Color.White else RedDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onTriggerBroadcast(selectedGroup, selectedCity) },
                colors = ButtonDefaults.buttonColors(containerColor = RedCritical),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("sos_confirm_broadcast_button")
            ) {
                Text("Broadcast Urgent $selectedGroup Request", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = RedEmergency,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = bloodGroup,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun UrgencyBadge(urgency: String) {
    val (bgColor, textColor, label) = when (urgency.lowercase()) {
        "critical" -> Triple(RedSoftBackground, RedCritical, "CRITICAL")
        "within 24h" -> Triple(AmberLight, AmberUrgent, "WITHIN 24H")
        else -> Triple(GreenLight, GreenAvailable, "PLANNED")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        modifier = Modifier.border(0.5.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun BigSosPulseButton(
    language: AppLanguage,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Outer glowing pulse ring
        Box(
            modifier = Modifier
                .scale(scale)
                .size(136.dp)
                .clip(CircleShape)
                .background(RedCritical.copy(alpha = 0.2f))
        )

        // Core Red SOS Button
        Surface(
            shape = CircleShape,
            color = RedCritical,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(120.dp)
                .clickable { onClick() }
                .testTag("big_red_sos_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "SOS",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "SOS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = "EMERGENCY",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
