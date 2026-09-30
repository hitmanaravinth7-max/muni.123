package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.repository.BloodCompatibility
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.AmberUrgent
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.BlueLight
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.GreenAvailable
import com.example.ui.theme.GreenLight
import com.example.ui.theme.RedCritical
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedSoftBackground

@Composable
fun EducationalScreen(
    language: AppLanguage
) {
    var checkAge by remember { mutableStateOf(false) }
    var checkWeight by remember { mutableStateOf(false) }
    var checkInterval by remember { mutableStateOf(false) }
    var checkHealth by remember { mutableStateOf(false) }

    val eligibilityScore = listOf(checkAge, checkWeight, checkInterval, checkHealth).count { it }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Blood Donation Guide & Education",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = RedDark
                )
                Text(
                    text = "Medical criteria, debunking myths, and compatibility facts",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Eligibility Self-Check
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("eligibility_self_check_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = RedSoftBackground,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = RedEmergency,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Interactive Eligibility Self-Check",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Tick each criterion to test your readiness today:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CheckItem(
                        checked = checkAge,
                        onCheckedChange = { checkAge = it },
                        text = "I am between 18 and 65 years old"
                    )
                    CheckItem(
                        checked = checkWeight,
                        onCheckedChange = { checkWeight = it },
                        text = "I weigh at least 50 kg (110 lbs)"
                    )
                    CheckItem(
                        checked = checkInterval,
                        onCheckedChange = { checkInterval = it },
                        text = "I have not donated whole blood in the last 90 days"
                    )
                    CheckItem(
                        checked = checkHealth,
                        onCheckedChange = { checkHealth = it },
                        text = "No recent infections, fever, major surgery, or tattoos in last 6 months"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (eligibilityScore == 4) GreenLight else RedSoftBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (eligibilityScore == 4)
                                "Awesome! You meet all standard donor criteria for blood donation today."
                            else
                                "Checked $eligibilityScore of 4 criteria. Please ensure all 4 are met before donating.",
                            color = if (eligibilityScore == 4) GreenAvailable else RedDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Blood Compatibility Matrix
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Blood Compatibility Matrix (Hematology)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Red blood cell compatibility guidelines",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val rows = listOf(
                        Triple("O-", "Everyone (Universal Donor)", "O- Only"),
                        Triple("O+", "O+, A+, B+, AB+", "O+, O-"),
                        Triple("A-", "A+, A-, AB+, AB-", "A-, O-"),
                        Triple("A+", "A+, AB+", "A+, A-, O+, O-"),
                        Triple("B-", "B+, B-, AB+, AB-", "B-, O-"),
                        Triple("B+", "B+, AB+", "B+, B-, O+, O-"),
                        Triple("AB-", "AB+, AB-", "AB-, A-, B-, O-"),
                        Triple("AB+", "AB+ Only", "Everyone (Universal Recipient)")
                    )

                    rows.forEach { (group, canDonateTo, canReceiveFrom) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GrayBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (group == "O-") RedDark else RedEmergency,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = group,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Can Donate To: $canDonateTo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Can Receive From: $canReceiveFrom",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Myths vs Facts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Myths vs Facts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    MythFactItem(
                        myth = "Myth: Donating blood makes you physically weak or drains your energy.",
                        fact = "Fact: Your body replenishes blood volume (plasma) within 24 to 48 hours, and red blood cells are fully restored within 4 to 6 weeks. Most donors feel completely normal right after resting and drinking fluids."
                    )
                    MythFactItem(
                        myth = "Myth: You can contract diseases or infections like HIV from donating.",
                        fact = "Fact: It is 100% safe. Every needle, tube, and blood bag used is sterile, disposable, and used only once before medical incineration."
                    )
                    MythFactItem(
                        myth = "Myth: People with high blood pressure or diabetes can never donate.",
                        fact = "Fact: As long as your blood pressure and blood sugar are stable and controlled by routine medication, you are generally eligible to donate blood."
                    )
                    MythFactItem(
                        myth = "Myth: Vegetarians have low iron and cannot donate blood.",
                        fact = "Fact: Iron levels vary individually. Hemoglobin is checked before every donation with a painless finger prick; many vegetarians have excellent iron levels!"
                    )
                }
            }
        }

        // Privacy and Consent Policy Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = BlueInfo,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LifeLink Donor Privacy & Consent Guarantee",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BlueInfo
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "LifeLink protects donor identities. Contact numbers are masked from public view and only accessible when emergency requesters initiate a critical contact request. Donors can pause their availability anytime via the Donor Dashboard.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = GreenAvailable)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun MythFactItem(myth: String, fact: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = GrayBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = myth,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = RedCritical,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = fact,
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
