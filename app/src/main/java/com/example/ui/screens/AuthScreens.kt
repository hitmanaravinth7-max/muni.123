package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.BloodCompatibility
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
fun LoginScreen(
    language: AppLanguage,
    identifier: String,
    pass: String,
    rememberMe: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    showPass: Boolean,
    forgotPasswordOpen: Boolean,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onDemoRoleLogin: (UserRole) -> Unit,
    onOpenForgotPassword: () -> Unit,
    onCloseForgotPassword: () -> Unit,
    onSendResetOtp: () -> Unit,
    onConfirmResetPass: () -> Unit,
    forgotIdentifier: String,
    forgotOtp: String,
    forgotNewPass: String,
    forgotStep: Int,
    onForgotIdentifierChange: (String) -> Unit,
    onForgotOtpChange: (String) -> Unit,
    onForgotNewPassChange: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Centered LifeLink Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card_container")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RedEmergency,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sign In to LifeLink",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = RedDark
                    )
                    Text(
                        text = "Access your donor dashboard, active requests, or bank stock",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RedSoftBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedCritical),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = RedCritical,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (successMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = successMessage,
                                color = GreenAvailable,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Email / Phone field
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = onIdentifierChange,
                        label = { Text("Email or 10-digit Phone") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_input_identifier")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password field with toggle
                    OutlinedTextField(
                        value = pass,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = onToggleShowPassword) {
                                Icon(
                                    imageVector = if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_input_password")
                    )

                    // Remember Me & Forgot Password row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = onRememberMeChange,
                                colors = CheckboxDefaults.colors(checkedColor = RedEmergency),
                                modifier = Modifier.testTag("remember_me_checkbox")
                            )
                            Text("Remember me", fontSize = 12.sp)
                        }

                        Text(
                            text = "Forgot password?",
                            color = RedEmergency,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onOpenForgotPassword() }
                                .testTag("forgot_password_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sign In Button
                    Button(
                        onClick = onLoginClick,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google Sign-In
                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("login_google_button")
                    ) {
                        Text("Sign In with Google", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register link
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Don't have an account?", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Register Now",
                            color = RedEmergency,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { onNavigateToRegister() }
                                .testTag("goto_register_link")
                        )
                    }
                }
            }
        }

        // Quick Demo Role Login shortcuts
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Demo Role Sign-In (1-Tap):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDemoRoleLogin(UserRole.DONOR) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Donor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onDemoRoleLogin(UserRole.REQUESTER) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Requester", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onDemoRoleLogin(UserRole.BLOOD_BANK_ADMIN) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Bank Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Forgot Password OTP Dialog
    if (forgotPasswordOpen) {
        AlertDialog(
            onDismissRequest = onCloseForgotPassword,
            title = { Text("Reset Password (OTP Flow)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (forgotStep == 1) {
                        Text(
                            text = "Enter your registered email or phone to receive a verification OTP code:",
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = forgotIdentifier,
                            onValueChange = onForgotIdentifierChange,
                            label = { Text("Registered Email / Phone") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_identifier_input")
                        )
                    } else {
                        Text(
                            text = "Enter the 6-digit OTP code sent to your device and enter a new password (8+ chars):",
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = forgotOtp,
                            onValueChange = onForgotOtpChange,
                            label = { Text("Enter OTP [Use: 482910]") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = forgotNewPass,
                            onValueChange = onForgotNewPassChange,
                            label = { Text("New Password (8+ chars)") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotStep == 1) onSendResetOtp() else onConfirmResetPass()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedEmergency)
                ) {
                    Text(if (forgotStep == 1) "Send OTP" else "Update Password")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseForgotPassword) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RegisterScreen(
    language: AppLanguage,
    name: String,
    identifier: String,
    pass: String,
    confirmPass: String,
    role: UserRole,
    bloodGroup: String,
    city: String,
    consent: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    showPass: Boolean,
    onNameChange: (String) -> Unit,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onBloodGroupChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onConsentChange: (Boolean) -> Unit,
    onToggleShowPassword: () -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val bloodGroups = BloodCompatibility.ALL_BLOOD_GROUPS

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_card_container")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create LifeLink Account",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = RedDark
                    )
                    Text(
                        text = "Join our emergency life-saving network",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RedSoftBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedCritical),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = RedCritical,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Choose Role First (Donor / Requester / Blood Bank Admin)
                    Text(
                        text = "Choose Your Role First *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.values().forEach { r ->
                            val isSel = role == r
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) RedEmergency else RedSoftBackground,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onRoleChange(r) }
                                    .testTag("select_role_${r.name}")
                            ) {
                                Text(
                                    text = when (r) {
                                        UserRole.DONOR -> "Donor"
                                        UserRole.REQUESTER -> "Requester"
                                        UserRole.BLOOD_BANK_ADMIN -> "Admin"
                                    },
                                    color = if (isSel) Color.White else RedDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Full Name *") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_input_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email / Phone
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = onIdentifierChange,
                        label = { Text("Email or 10-Digit Mobile *") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_input_identifier")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // For Donors: Blood Group & City
                    if (role == UserRole.DONOR) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Your Blood Group *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                bloodGroups.take(4).forEach { g ->
                                    val isSel = bloodGroup == g
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) RedEmergency else RedSoftBackground,
                                        modifier = Modifier.clickable { onBloodGroupChange(g) }
                                    ) {
                                        Text(
                                            text = g,
                                            color = if (isSel) Color.White else RedDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                bloodGroups.drop(4).forEach { g ->
                                    val isSel = bloodGroup == g
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) RedEmergency else RedSoftBackground,
                                        modifier = Modifier.clickable { onBloodGroupChange(g) }
                                    ) {
                                        Text(
                                            text = g,
                                            color = if (isSel) Color.White else RedDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = city,
                            onValueChange = onCityChange,
                            label = { Text("Your City / Area") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_input_city")
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Password
                    OutlinedTextField(
                        value = pass,
                        onValueChange = onPasswordChange,
                        label = { Text("Password (8+ characters) *") },
                        singleLine = true,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = onToggleShowPassword) {
                                Icon(
                                    imageVector = if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_input_password")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text("Confirm Password *") },
                        singleLine = true,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_input_confirm_password")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Donor Consent Checkbox
                    if (role == UserRole.DONOR) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConsentChange(!consent) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = consent,
                                onCheckedChange = onConsentChange,
                                colors = CheckboxDefaults.colors(checkedColor = RedEmergency),
                                modifier = Modifier.testTag("consent_checkbox")
                            )
                            Text(
                                text = "I consent to sharing contact details with hospital coordinators and patients during verified emergency blood crises.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Create Account Button
                    Button(
                        onClick = onRegisterClick,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = RedEmergency),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("register_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already registered?", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In here",
                            color = RedEmergency,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { onNavigateToLogin() }
                                .testTag("goto_login_link")
                        )
                    }
                }
            }
        }
    }
}
