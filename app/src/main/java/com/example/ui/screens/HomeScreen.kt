package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EmergencyRequest
import com.example.ui.components.BigSosPulseButton
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguageManager
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
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
    language: AppLanguage,
    openRequestsCount: Int,
    availableUnitsCount: Int,
    donorsCount: Int,
    recentRequests: List<EmergencyRequest>,
    onOpenSosDialog: () -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    onQuickSearchGroup: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Visual Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RedDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_blood_donation),
                        contentDescription = "Hero blood donation banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, RedDark.copy(alpha = 0.88f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = LanguageManager.getString("app_subtitle", language),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Every donation can save up to 3 lives. Connect instantly.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // BIG RED SOS EMERGENCY BUTTON
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LanguageManager.getString("sos_desc", language),
                        fontSize = 13.sp,
                        color = RedCritical,
                        fontWeight = FontWeight.SemiBold
                    )

                    BigSosPulseButton(language = language, onClick = onOpenSosDialog)

                    Text(
                        text = "Public Emergency Access • No Login Required",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Real-Time Network Counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Active Requests",
                    value = "$openRequestsCount",
                    sub = "Emergency Needs",
                    icon = Icons.Default.AddAlert,
                    color = RedCritical,
                    bgColor = RedSoftBackground,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo(AppScreen.EMERGENCY_REQUESTS) }
                )
                MetricCard(
                    title = "Units in Banks",
                    value = "$availableUnitsCount",
                    sub = "Verified Stock",
                    icon = Icons.Default.LocalHospital,
                    color = GreenAvailable,
                    bgColor = GreenLight,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo(AppScreen.SEARCH_AVAILABILITY) }
                )
                MetricCard(
                    title = "Eligible Donors",
                    value = "$donorsCount",
                    sub = "Nearby On-Call",
                    icon = Icons.Default.Favorite,
                    color = AmberUrgent,
                    bgColor = AmberLight,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo(AppScreen.DONOR_FINDER) }
                )
            }
        }

        // Quick Blood Group Selection
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Blood Availability",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "View All →",
                            color = RedEmergency,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateTo(AppScreen.SEARCH_AVAILABILITY) }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val bloodGroups = listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bloodGroups) { group ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RedSoftBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedLight),
                                modifier = Modifier
                                    .clickable {
                                        onQuickSearchGroup(group)
                                        onNavigateTo(AppScreen.SEARCH_AVAILABILITY)
                                    }
                                    .testTag("home_quick_group_$group")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = group,
                                        fontWeight = FontWeight.Black,
                                        color = RedEmergency,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = if (group == "O-") "Universal" else "Search",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Core Action Portals Grid
        item {
            Text(
                text = "Services & Emergency Network",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PortalCard(
                        title = "Blood Availability",
                        desc = "Hospitals & stock levels",
                        icon = Icons.Default.Search,
                        color = RedEmergency,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_availability",
                        onClick = { onNavigateTo(AppScreen.SEARCH_AVAILABILITY) }
                    )
                    PortalCard(
                        title = "Broadcast Need",
                        desc = "Alert nearby donors",
                        icon = Icons.Default.AddAlert,
                        color = RedCritical,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_request",
                        onClick = { onNavigateTo(AppScreen.EMERGENCY_REQUESTS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PortalCard(
                        title = "Donor Finder",
                        desc = "Compatibility & distance",
                        icon = Icons.Default.PersonSearch,
                        color = AmberUrgent,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_donors",
                        onClick = { onNavigateTo(AppScreen.DONOR_FINDER) }
                    )
                    PortalCard(
                        title = "Radar Map",
                        desc = "Nearby banks & donors",
                        icon = Icons.Default.Map,
                        color = BlueInfo,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_map",
                        onClick = { onNavigateTo(AppScreen.RADAR_MAP) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PortalCard(
                        title = "Donor Dashboard",
                        desc = "Eligibility & history",
                        icon = Icons.Default.Favorite,
                        color = GreenAvailable,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_donor_dash",
                        onClick = { onNavigateTo(AppScreen.DONOR_DASHBOARD) }
                    )
                    PortalCard(
                        title = "Bank Stock Admin",
                        desc = "Update hospital stock",
                        icon = Icons.Default.Inventory,
                        color = RedDark,
                        modifier = Modifier.weight(1f),
                        testTag = "nav_portal_admin",
                        onClick = { onNavigateTo(AppScreen.ADMIN_STOCK) }
                    )
                }
            }
        }

        // Live Emergency Requests Feed Preview
        if (recentRequests.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Emergency Broadcasts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "All Requests →",
                        color = RedEmergency,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateTo(AppScreen.EMERGENCY_REQUESTS) }
                    )
                }
            }

            items(recentRequests.take(2)) { request ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BloodGroupBadge(bloodGroup = request.bloodGroup)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${request.unitsNeeded} units needed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            UrgencyBadge(urgency = request.urgency)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Patient: ${request.patientName}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${request.hospital}, ${request.city}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { dialPhoneNumber(context, request.contactNumber) },
                                colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Requester", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Education Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BlueLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTo(AppScreen.EDUCATION) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = BlueInfo,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Blood Donation Guide & Myths",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BlueInfo
                        )
                        Text(
                            text = "Check eligibility criteria, 90-day recovery cycle, and compatibility.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = BlueInfo
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = color)
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
            Text(text = sub, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PortalCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}
