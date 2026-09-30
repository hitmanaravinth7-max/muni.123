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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.repository.BloodCompatibility
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.openWhatsApp
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberUrgent
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.BlueLight
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.GreenAvailable
import com.example.ui.theme.GreenLight
import com.example.ui.theme.RedCritical
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedLight
import com.example.ui.theme.RedSoftBackground

@Composable
fun EmergencyDonorFinderScreen(
    language: AppLanguage,
    matchingDonors: List<Pair<UserAccount, String>>,
    targetBloodGroup: String,
    selectedCity: String,
    enableCompatibility: Boolean,
    revealedDonorIds: Set<Long>,
    onSelectTargetGroup: (String) -> Unit,
    onSelectCity: (String) -> Unit,
    onToggleCompatibility: (Boolean) -> Unit,
    onRevealContact: (Long) -> Unit
) {
    val context = LocalContext.current
    val allBloodGroups = listOf("All") + BloodCompatibility.ALL_BLOOD_GROUPS
    val cities = listOf("All", "Metro Central", "North District", "South Harbor")
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Emergency Donor Finder",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = RedDark
                )
                Text(
                    text = "Locate compatible volunteer blood donors based on hematology rules",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Blood Group Selection
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Recipient Needed Blood Group",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allBloodGroups) { group ->
                            val isSel = targetBloodGroup == group
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) RedEmergency else RedSoftBackground,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) RedEmergency else RedLight
                                ),
                                modifier = Modifier
                                    .clickable { onSelectTargetGroup(group) }
                                    .testTag("donor_filter_group_$group")
                            ) {
                                Text(
                                    text = group,
                                    color = if (isSel) Color.White else RedDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compatibility Mode Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Include Compatible Blood Groups",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Includes universal donors (e.g. O-) & compatible red cells",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = enableCompatibility,
                            onCheckedChange = onToggleCompatibility,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RedEmergency
                            ),
                            modifier = Modifier.testTag("toggle_compatibility_matching")
                        )
                    }
                }
            }
        }

        // City & Compatibility Notice
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${matchingDonors.size} Donors Available",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .clickable { cityDropdownExpanded = true }
                            .testTag("donor_city_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "City: $selectedCity",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city, fontSize = 12.sp) },
                                onClick = {
                                    onSelectCity(city)
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Privacy info card
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = BlueLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BlueInfo,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy Protected: Donor phone numbers are masked until an emergency contact request is made.",
                        fontSize = 11.sp,
                        color = BlueInfo,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Donor list
        if (matchingDonors.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AmberUrgent,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching donors found in $selectedCity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Try enabling 'Include Compatible Blood Groups' or select 'All' cities.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(matchingDonors) { (donor, matchReason) ->
            val eligibility = BloodCompatibility.checkEligibility(donor.lastDonationDateMillis)
            val isRevealed = revealedDonorIds.contains(donor.id)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("donor_card_${donor.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top: Name, Group, Age
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = RedSoftBackground,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = RedDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = donor.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${donor.age} yrs • ${donor.city}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        BloodGroupBadge(bloodGroup = donor.bloodGroup)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Match Reason Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (matchReason.contains("Exact")) GreenLight else RedSoftBackground
                    ) {
                        Text(
                            text = matchReason,
                            color = if (matchReason.contains("Exact")) GreenAvailable else RedDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 90-Day Eligibility Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (eligibility.isEligible) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenAvailable,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Eligible to Donate (Passed 90-day recovery)",
                                color = GreenAvailable,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberUrgent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ineligible: ${eligibility.daysRemaining} days remaining in 90-day cycle",
                                color = AmberUrgent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact Section: Privacy Protected vs Revealed
                    if (!isRevealed) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GrayBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "+1 (555) •••• ••${donor.phone.takeLast(2)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = { onRevealContact(donor.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("reveal_contact_button_${donor.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reveal Contact", fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Revealed: Call and WhatsApp buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    openWhatsApp(
                                        context,
                                        donor.phone,
                                        "Hello ${donor.name}, LifeLink urgent blood request for ${donor.bloodGroup}. Can you help donate?"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("whatsapp_donor_button_${donor.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = GreenAvailable
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 11.sp, color = GreenAvailable)
                            }

                            Button(
                                onClick = { dialPhoneNumber(context, donor.phone) },
                                colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("call_donor_button_${donor.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call ${donor.phone}", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
