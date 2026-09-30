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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.BloodStockItem
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
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
import com.example.ui.theme.RedSoftBackground
import com.example.ui.viewmodel.AppScreen

@Composable
fun AdminStockScreen(
    language: AppLanguage,
    activeUser: UserAccount?,
    stockList: List<BloodStockItem>,
    onUpdateUnits: (stockId: Long, currentUnits: Int, delta: Int) -> Unit,
    onAddNewStock: (
        bankName: String,
        city: String,
        address: String,
        phone: String,
        group: String,
        component: String,
        units: Int
    ) -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    onDemoLoginAdmin: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newBankName by remember { mutableStateOf("City Red Cross Blood Center") }
    var newCity by remember { mutableStateOf("Metro Central") }
    var newAddress by remember { mutableStateOf("742 Evergreen Healthcare Blvd") }
    var newPhone by remember { mutableStateOf("+1 (800) 733-2767") }
    var newBloodGroup by remember { mutableStateOf("O-") }
    var newComponent by remember { mutableStateOf("Whole Blood") }
    var newUnits by remember { mutableIntStateOf(5) }

    // Access control check: Only Blood Bank Admin
    if (activeUser == null || activeUser.role != UserRole.BLOOD_BANK_ADMIN) {
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
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = RedEmergency,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Blood Bank Admin Portal",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Access is restricted to authorized hospital and blood bank administrators for inventory management, stock increment/decrement, and expiry tracking.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDemoLoginAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("demo_admin_login_button")
                        ) {
                            Text("Sign In as Dr. Arjun Sharma (Admin)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { onNavigateTo(AppScreen.LOGIN) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_go_to_login_button")
                        ) {
                            Text("Sign In with Admin Credentials")
                        }
                    }
                }
            }
        }
        return
    }

    // Logged in as Admin
    val lowStockItems = stockList.filter { it.units < 5 }
    val totalUnits = stockList.sumOf { it.units }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Blood Bank Stock Manager",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = RedDark
                    )
                    Text(
                        text = "Admin: ${activeUser.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_stock_batch_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Batch", fontSize = 12.sp)
                }
            }
        }

        // Low-stock alerts banner
        if (lowStockItems.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberUrgent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AmberUrgent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CRITICAL LOW-STOCK ALERT (${lowStockItems.size} items < 5 units)",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = RedCritical
                            )
                            Text(
                                text = lowStockItems.joinToString(", ") { "${it.bloodGroup} ${it.component} (${it.units} units left)" },
                                fontSize = 11.sp,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Stats summary
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$totalUnits", fontWeight = FontWeight.Black, fontSize = 20.sp, color = RedDark)
                        Text(text = "Total Units", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${stockList.size}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = GreenAvailable)
                        Text(text = "Stock Batches", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${lowStockItems.size}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = RedCritical)
                        Text(text = "Low Stock Alerts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Stock list with unit modifier controls (+ / -)
        items(stockList) { item ->
            val isLow = item.units < 5

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_stock_item_${item.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BloodGroupBadge(bloodGroup = item.bloodGroup)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.component,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.bloodBankName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Expires in: ${item.expiryDays} days",
                            fontSize = 10.sp,
                            color = if (item.expiryDays <= 5) RedCritical else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (item.expiryDays <= 5) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Stepper modifier
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { onUpdateUnits(item.id, item.units, -1) },
                            shape = CircleShape,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("decrement_stock_${item.id}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "${item.units}",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = if (isLow) RedCritical else GreenAvailable
                            )
                            Text(
                                text = "Units",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { onUpdateUnits(item.id, item.units, 1) },
                            shape = CircleShape,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("increment_stock_${item.id}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Add Stock Batch Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Blood Stock Batch") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newBankName,
                        onValueChange = { newBankName = it },
                        label = { Text("Blood Bank Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_input_bank_name")
                    )
                    OutlinedTextField(
                        value = newBloodGroup,
                        onValueChange = { newBloodGroup = it },
                        label = { Text("Blood Group (e.g. O-, A+, etc.)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_input_blood_group")
                    )
                    OutlinedTextField(
                        value = newComponent,
                        onValueChange = { newComponent = it },
                        label = { Text("Component (Whole Blood / Platelets / Plasma)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Units Added:", fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (newUnits > 1) newUnits-- },
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-")
                            }
                            Text(
                                text = "$newUnits",
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            OutlinedButton(
                                onClick = { newUnits++ },
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddNewStock(
                            newBankName,
                            newCity,
                            newAddress,
                            newPhone,
                            newBloodGroup,
                            newComponent,
                            newUnits
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                    modifier = Modifier.testTag("admin_confirm_add_stock_button")
                ) {
                    Text("Add to Inventory")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
