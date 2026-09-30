package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DonationRecord
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.repository.BloodCompatibility
import com.example.ui.components.BloodGroupBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberUrgent
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.GreenAvailable
import com.example.ui.theme.GreenLight
import com.example.ui.theme.RedCritical
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedLight
import com.example.ui.theme.RedSoftBackground
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DonorDashboardScreen(
    language: AppLanguage,
    activeUser: UserAccount?,
    donations: List<DonationRecord>,
    onToggleAvailability: (Boolean) -> Unit,
    onRecordQuickDonation: (facility: String, group: String, comp: String) -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    onDemoLoginDonor: () -> Unit
) {
    var showRecordDonationDialog by remember { mutableStateOf(false) }
    var donationFacility by remember { mutableStateOf("City Red Cross Blood Center") }
    var donationComponent by remember { mutableStateOf("Whole Blood") }

    if (activeUser == null || activeUser.role != UserRole.DONOR) {
        // Access control: Guest / Non-donor prompt
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayBackground)
                .padding(16.dp),
            contentPadding = PaddingValues(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RedSoftBackground,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = RedEmergency,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Donor Dashboard & Log",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sign in as a registered volunteer blood donor to manage your availability, view your 90-day eligibility cycle, and view your verified donation history.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDemoLoginDonor,
                            colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("demo_donor_login_button")
                        ) {
                            Text("Quick Sign In as Sarah Jenkins (Donor O-)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { onNavigateTo(AppScreen.LOGIN) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_go_to_login_button")
                        ) {
                            Text("Sign In with My Email / Phone")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = { onNavigateTo(AppScreen.REGISTER) },
                            modifier = Modifier.testTag("donor_go_to_register_button")
                        ) {
                            Text("New Donor? Register here", color = RedEmergency, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    // Active Donor is logged in!
    val eligibility = BloodCompatibility.checkEligibility(activeUser.lastDonationDateMillis)
    val totalDonations = donations.size
    val livesSaved = totalDonations * 3
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = RedDark,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = activeUser.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeUser.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "${activeUser.city} • Age ${activeUser.age}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        BloodGroupBadge(bloodGroup = activeUser.bloodGroup)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Emergency Availability Toggle
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeUser.isAvailable) GreenLight else GrayBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeUser.isAvailable) GreenAvailable else Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (activeUser.isAvailable) "Emergency On-Call: ACTIVE" else "Emergency On-Call: PAUSED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (activeUser.isAvailable) GreenAvailable else Color.Gray
                                )
                                Text(
                                    text = if (activeUser.isAvailable) "You can receive critical blood alerts" else "You will not receive urgent emergency calls",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = activeUser.isAvailable,
                                onCheckedChange = onToggleAvailability,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GreenAvailable
                                ),
                                modifier = Modifier.testTag("donor_availability_switch")
                            )
                        }
                    }
                }
            }
        }

        // 90-Day Auto Eligibility Meter
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (eligibility.isEligible) GreenLight else AmberLight
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (eligibility.isEligible) GreenAvailable.copy(alpha = 0.5f) else AmberUrgent.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (eligibility.isEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (eligibility.isEligible) GreenAvailable else AmberUrgent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (eligibility.isEligible) "90-Day Eligibility: ELIGIBLE TODAY" else "90-Day Eligibility: INELIGIBLE",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (eligibility.isEligible) GreenAvailable else AmberUrgent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = eligibility.message,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!eligibility.isEligible) {
                        val progress = (90 - eligibility.daysRemaining) / 90f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = AmberUrgent,
                            trackColor = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Donated", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${eligibility.daysRemaining} days until recovery complete",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberUrgent
                            )
                        }
                    } else {
                        Text(
                            text = "Standard 90-day whole blood interval met. Ready to donate again!",
                            fontSize = 11.sp,
                            color = GreenAvailable,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Impact Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = RedEmergency,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$livesSaved",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = RedEmergency
                        )
                        Text(
                            text = "Estimated Lives Saved",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardMembership,
                            contentDescription = null,
                            tint = GreenAvailable,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalDonations",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = GreenAvailable
                        )
                        Text(
                            text = "Total Donations",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Donation History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Donation History (${donations.size})",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Button(
                    onClick = { showRecordDonationDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("record_donation_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Donation", fontSize = 11.sp)
                }
            }
        }

        if (donations.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No donation records yet.", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Log your recent blood donation to keep your 90-day cycle accurate.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(donations) { record ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RedSoftBackground,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = RedEmergency,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = record.facilityName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${record.bloodGroup} • ${record.component} (1 Unit)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Cert: ${record.certificateId} • ${dateFormat.format(Date(record.dateMillis))}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GreenLight
                    ) {
                        Text(
                            text = "VERIFIED",
                            color = GreenAvailable,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }

    // Log Donation Dialog
    if (showRecordDonationDialog) {
        AlertDialog(
            onDismissRequest = { showRecordDonationDialog = false },
            title = { Text("Log Completed Donation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recording a donation will automatically update your 90-day recovery countdown.",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = donationFacility,
                        onValueChange = { donationFacility = it },
                        label = { Text("Blood Bank or Hospital Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_donation_facility")
                    )
                    OutlinedTextField(
                        value = donationComponent,
                        onValueChange = { donationComponent = it },
                        label = { Text("Component (Whole Blood / Platelets / Plasma)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRecordQuickDonation(donationFacility, activeUser.bloodGroup, donationComponent)
                        showRecordDonationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                    modifier = Modifier.testTag("confirm_record_donation_button")
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecordDonationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
