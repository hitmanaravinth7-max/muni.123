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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyRequest
import com.example.data.repository.BloodCompatibility
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguageManager
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

@Composable
fun EmergencyRequestScreen(
    language: AppLanguage,
    requests: List<EmergencyRequest>,
    onSubmitRequest: (
        patientName: String,
        bloodGroup: String,
        unitsNeeded: Int,
        hospital: String,
        city: String,
        urgency: String,
        contactNumber: String,
        notes: String
    ) -> Unit,
    onMarkFulfilled: (Long) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Post Broadcast, 1 = Live Requests List

    var patientName by remember { mutableStateOf("") }
    var selectedBloodGroup by remember { mutableStateOf("O-") }
    var unitsNeeded by remember { mutableIntStateOf(2) }
    var hospitalName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Metro Central") }
    var urgency by remember { mutableStateOf("Critical") }
    var contactPhone by remember { mutableStateOf("") }
    var clinicalNotes by remember { mutableStateOf("") }
    var formErrorMessage by remember { mutableStateOf<String?>(null) }

    val urgencies = listOf("Critical", "Within 24h", "Planned")
    val bloodGroups = BloodCompatibility.ALL_BLOOD_GROUPS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = RedEmergency
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Broadcast Request", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_post_request")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    val openCount = requests.count { it.status == "Open" }
                    Text("Live Requests ($openCount)", fontWeight = FontWeight.Bold)
                },
                modifier = Modifier.testTag("tab_live_requests")
            )
        }

        if (selectedTab == 0) {
            // Form to Broadcast Emergency Request
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RedSoftBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = null,
                                tint = RedCritical,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency requests are immediately broadcasted to all nearby registered donors & blood bank coordinators.",
                                fontSize = 12.sp,
                                color = RedDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Emergency Blood Request Form",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            // Patient Name
                            OutlinedTextField(
                                value = patientName,
                                onValueChange = { patientName = it },
                                label = { Text("Patient Name & Bed/Ward") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_input_patient_name")
                            )

                            // Blood Group Selector
                            Column {
                                Text(
                                    text = "Blood Group Needed *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(bloodGroups) { group ->
                                        val isSel = selectedBloodGroup == group
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) RedEmergency else RedSoftBackground,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSel) RedEmergency else RedLight
                                            ),
                                            modifier = Modifier
                                                .clickable { selectedBloodGroup = group }
                                                .testTag("request_group_$group")
                                        ) {
                                            Text(
                                                text = group,
                                                color = if (isSel) Color.White else RedDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Units Needed Stepper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Units of Blood Needed:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = { if (unitsNeeded > 1) unitsNeeded-- },
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "$unitsNeeded",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = RedEmergency
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    OutlinedButton(
                                        onClick = { if (unitsNeeded < 10) unitsNeeded++ },
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Hospital
                            OutlinedTextField(
                                value = hospitalName,
                                onValueChange = { hospitalName = it },
                                label = { Text("Hospital / Medical Center *") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_input_hospital")
                            )

                            // City
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City / Area *") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_input_city")
                            )

                            // Urgency Selector
                            Column {
                                Text(
                                    text = "Urgency Level *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    urgencies.forEach { level ->
                                        val isSel = urgency == level
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) {
                                                when (level) {
                                                    "Critical" -> RedCritical
                                                    "Within 24h" -> AmberUrgent
                                                    else -> GreenAvailable
                                                }
                                            } else Color.White,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { urgency = level }
                                                .testTag("urgency_level_$level")
                                        ) {
                                            Text(
                                                text = level,
                                                color = if (isSel) Color.White else Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            // Contact Phone Number
                            OutlinedTextField(
                                value = contactPhone,
                                onValueChange = { contactPhone = it },
                                label = { Text("Contact Phone (10 Digits) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_input_phone")
                            )

                            // Clinical Notes
                            OutlinedTextField(
                                value = clinicalNotes,
                                onValueChange = { clinicalNotes = it },
                                label = { Text("Additional Clinical Notes (Optional)") },
                                maxLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_input_notes")
                            )

                            if (formErrorMessage != null) {
                                Text(
                                    text = formErrorMessage ?: "",
                                    color = RedCritical,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Large Broadcast Button
                            Button(
                                onClick = {
                                    if (patientName.isBlank()) {
                                        formErrorMessage = "Please specify patient name"
                                        return@Button
                                    }
                                    if (hospitalName.isBlank()) {
                                        formErrorMessage = "Please enter hospital name"
                                        return@Button
                                    }
                                    if (contactPhone.replace(Regex("[^0-9]"), "").length < 10) {
                                        formErrorMessage = "Please enter a valid 10-digit contact number"
                                        return@Button
                                    }
                                    formErrorMessage = null
                                    onSubmitRequest(
                                        patientName,
                                        selectedBloodGroup,
                                        unitsNeeded,
                                        hospitalName,
                                        city,
                                        urgency,
                                        contactPhone,
                                        clinicalNotes
                                    )
                                    // Reset form and switch to live list
                                    patientName = ""
                                    hospitalName = ""
                                    contactPhone = ""
                                    clinicalNotes = ""
                                    selectedTab = 1
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RedCritical),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("broadcast_emergency_request_button")
                            ) {
                                Icon(imageVector = Icons.Default.AddAlert, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Broadcast Emergency Request",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Live Requests List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val openList = requests.filter { it.status == "Open" }
                val fulfilledList = requests.filter { it.status != "Open" }

                item {
                    Text(
                        text = "Active Emergency Requests (${openList.size})",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = RedDark
                    )
                }

                if (openList.isEmpty()) {
                    item {
                        Card(
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
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GreenAvailable,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No open emergency requests at this moment!",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                items(openList) { req ->
                    RequestCard(
                        request = req,
                        onCall = { dialPhoneNumber(context, req.contactNumber) },
                        onMarkFulfilled = { onMarkFulfilled(req.id) }
                    )
                }

                if (fulfilledList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recently Fulfilled Requests (${fulfilledList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(fulfilledList) { req ->
                        RequestCard(
                            request = req,
                            onCall = { dialPhoneNumber(context, req.contactNumber) },
                            onMarkFulfilled = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: EmergencyRequest,
    onCall: () -> Unit,
    onMarkFulfilled: (() -> Unit)?
) {
    val isFulfilled = request.status != "Open"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("request_item_card_${request.id}")
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
                        text = "${request.unitsNeeded} Units Needed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                if (isFulfilled) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GreenLight
                    ) {
                        Text(
                            text = "FULFILLED",
                            color = GreenAvailable,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    UrgencyBadge(urgency = request.urgency)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Patient: ${request.patientName}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${request.hospital}, ${request.city}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${request.notes}\"",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${request.requesterName}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onMarkFulfilled != null && !isFulfilled) {
                        OutlinedButton(
                            onClick = onMarkFulfilled,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("mark_fulfilled_button_${request.id}")
                        ) {
                            Text("Mark Fulfilled", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = onCall,
                        colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("call_contact_button_${request.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
