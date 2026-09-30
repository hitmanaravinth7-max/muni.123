package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.LifeLinkTopBar
import com.example.ui.components.SosBroadcastBanner
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.i18n.LanguageManager
import com.example.ui.screens.AdminStockScreen
import com.example.ui.screens.BloodAvailabilityScreen
import com.example.ui.screens.DonorDashboardScreen
import com.example.ui.screens.EducationalScreen
import com.example.ui.screens.EmergencyDonorFinderScreen
import com.example.ui.screens.EmergencyRequestScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RadarMapViewScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedEmergency
import com.example.ui.theme.RedSoftBackground
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LifeLinkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: LifeLinkViewModel = viewModel()
                LifeLinkApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LifeLinkApp(viewModel: LifeLinkViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()

    val openRequestsCount by viewModel.openRequestsCount.collectAsStateWithLifecycle()
    val donorsCount by viewModel.donorsCount.collectAsStateWithLifecycle()
    val totalStockUnits by viewModel.totalStockUnits.collectAsStateWithLifecycle()
    val emergencyRequests by viewModel.emergencyRequests.collectAsStateWithLifecycle()
    val allStock by viewModel.allStock.collectAsStateWithLifecycle()
    val allDonors by viewModel.allDonors.collectAsStateWithLifecycle()

    val filteredBloodStock by viewModel.filteredBloodStock.collectAsStateWithLifecycle()
    val selectedGroupFilter by viewModel.selectedBloodGroupFilter.collectAsStateWithLifecycle()
    val selectedCompFilter by viewModel.selectedComponentFilter.collectAsStateWithLifecycle()
    val selectedCityFilter by viewModel.selectedCityFilter.collectAsStateWithLifecycle()

    val filteredDonors by viewModel.filteredDonors.collectAsStateWithLifecycle()
    val donorSearchGroup by viewModel.donorSearchBloodGroup.collectAsStateWithLifecycle()
    val donorSearchCity by viewModel.donorSearchCity.collectAsStateWithLifecycle()
    val enableCompat by viewModel.enableCompatibilityMatching.collectAsStateWithLifecycle()
    val revealedDonorIds by viewModel.revealedContactDonorIds.collectAsStateWithLifecycle()

    val userDonations by viewModel.activeUserDonations.collectAsStateWithLifecycle()

    val isSosDialogOpen by viewModel.sosDialogOpen.collectAsStateWithLifecycle()
    val sosBannerMessage by viewModel.sosBroadcastBanner.collectAsStateWithLifecycle()

    // Auth states
    val authEmailOrPhone by viewModel.authEmailOrPhone.collectAsStateWithLifecycle()
    val authPassword by viewModel.authPassword.collectAsStateWithLifecycle()
    val authConfirmPassword by viewModel.authConfirmPassword.collectAsStateWithLifecycle()
    val authName by viewModel.authName.collectAsStateWithLifecycle()
    val authRole by viewModel.authRole.collectAsStateWithLifecycle()
    val authBloodGroup by viewModel.authBloodGroup.collectAsStateWithLifecycle()
    val authCity by viewModel.authCity.collectAsStateWithLifecycle()
    val authConsent by viewModel.authConsent.collectAsStateWithLifecycle()
    val authRememberMe by viewModel.authRememberMe.collectAsStateWithLifecycle()
    val authIsLoading by viewModel.authIsLoading.collectAsStateWithLifecycle()
    val authErrorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()
    val showPassword by viewModel.showPassword.collectAsStateWithLifecycle()
    val forgotPasswordOpen by viewModel.forgotPasswordDialogOpen.collectAsStateWithLifecycle()
    val forgotIdentifier by viewModel.forgotPasswordIdentifier.collectAsStateWithLifecycle()
    val forgotOtp by viewModel.forgotPasswordOtp.collectAsStateWithLifecycle()
    val forgotNewPass by viewModel.forgotPasswordNewPass.collectAsStateWithLifecycle()
    val forgotStep by viewModel.forgotPasswordStep.collectAsStateWithLifecycle()

    // BackHandler support
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.handleBack()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            LifeLinkTopBar(
                currentScreen = currentScreen,
                currentLanguage = currentLanguage,
                activeUser = activeUser,
                onNavigateBack = { viewModel.handleBack() },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenSos = { viewModel.openSosDialog() },
                onNavigateTo = { viewModel.navigateTo(it) },
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            // Standard M3 Bottom Navigation Bar
            NavigationBar(
                containerColor = Color.White,
                contentColor = RedDark,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val navItems = listOf(
                    Triple(AppScreen.HOME, LanguageManager.getString("nav_home", currentLanguage), Icons.Default.Home),
                    Triple(AppScreen.SEARCH_AVAILABILITY, LanguageManager.getString("nav_search", currentLanguage), Icons.Default.Search),
                    Triple(AppScreen.EMERGENCY_REQUESTS, LanguageManager.getString("nav_emergency", currentLanguage), Icons.Default.AddAlert),
                    Triple(AppScreen.DONOR_FINDER, LanguageManager.getString("nav_donors", currentLanguage), Icons.Default.Favorite),
                    Triple(AppScreen.EDUCATION, LanguageManager.getString("nav_education", currentLanguage), Icons.Default.MenuBook)
                )

                navItems.forEach { (screen, label, icon) ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(screen) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedEmergency,
                            selectedTextColor = RedEmergency,
                            indicatorColor = RedSoftBackground,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("bottom_nav_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GrayBackground)
        ) {
            // SOS Emergency Alert Broadcast Banner
            sosBannerMessage?.let { msg ->
                SosBroadcastBanner(
                    message = msg,
                    onDismiss = { viewModel.dismissSosBanner() }
                )
            }

            // Main Active Screen Router
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        language = currentLanguage,
                        openRequestsCount = openRequestsCount,
                        availableUnitsCount = totalStockUnits ?: 0,
                        donorsCount = donorsCount,
                        recentRequests = emergencyRequests,
                        onOpenSosDialog = { viewModel.openSosDialog() },
                        onNavigateTo = { viewModel.navigateTo(it) },
                        onQuickSearchGroup = { group ->
                            viewModel.selectedBloodGroupFilter.value = group
                        }
                    )

                    AppScreen.SEARCH_AVAILABILITY -> BloodAvailabilityScreen(
                        language = currentLanguage,
                        stockItems = filteredBloodStock,
                        selectedGroup = selectedGroupFilter,
                        selectedComponent = selectedCompFilter,
                        selectedCity = selectedCityFilter,
                        onSelectGroup = { viewModel.selectedBloodGroupFilter.value = it },
                        onSelectComponent = { viewModel.selectedComponentFilter.value = it },
                        onSelectCity = { viewModel.selectedCityFilter.value = it }
                    )

                    AppScreen.EMERGENCY_REQUESTS -> EmergencyRequestScreen(
                        language = currentLanguage,
                        requests = emergencyRequests,
                        onSubmitRequest = { name, group, units, hospital, city, urgency, phone, notes ->
                            viewModel.submitEmergencyRequest(
                                patientName = name,
                                bloodGroup = group,
                                unitsNeeded = units,
                                hospital = hospital,
                                city = city,
                                urgency = urgency,
                                contactNumber = phone,
                                notes = notes,
                                onSuccess = {}
                            )
                        },
                        onMarkFulfilled = { viewModel.markRequestFulfilled(it) }
                    )

                    AppScreen.DONOR_FINDER -> EmergencyDonorFinderScreen(
                        language = currentLanguage,
                        matchingDonors = filteredDonors,
                        targetBloodGroup = donorSearchGroup,
                        selectedCity = donorSearchCity,
                        enableCompatibility = enableCompat,
                        revealedDonorIds = revealedDonorIds,
                        onSelectTargetGroup = { viewModel.donorSearchBloodGroup.value = it },
                        onSelectCity = { viewModel.donorSearchCity.value = it },
                        onToggleCompatibility = { viewModel.enableCompatibilityMatching.value = it },
                        onRevealContact = { viewModel.revealContact(it) }
                    )

                    AppScreen.DONOR_DASHBOARD -> DonorDashboardScreen(
                        language = currentLanguage,
                        activeUser = activeUser,
                        donations = userDonations,
                        onToggleAvailability = { viewModel.toggleAvailability(it) },
                        onRecordQuickDonation = { facility, group, comp ->
                            viewModel.scheduleQuickDonation(facility, group, comp)
                        },
                        onNavigateTo = { viewModel.navigateTo(it) },
                        onDemoLoginDonor = { viewModel.loginAsRole(UserRole.DONOR) }
                    )

                    AppScreen.ADMIN_STOCK -> AdminStockScreen(
                        language = currentLanguage,
                        activeUser = activeUser,
                        stockList = allStock,
                        onUpdateUnits = { id, current, delta ->
                            viewModel.updateStockLevel(id, current, delta)
                        },
                        onAddNewStock = { bank, city, addr, phone, group, comp, units ->
                            viewModel.addNewStockEntry(bank, city, addr, phone, group, comp, units)
                        },
                        onNavigateTo = { viewModel.navigateTo(it) },
                        onDemoLoginAdmin = { viewModel.loginAsRole(UserRole.BLOOD_BANK_ADMIN) }
                    )

                    AppScreen.RADAR_MAP -> RadarMapViewScreen(
                        language = currentLanguage,
                        stockItems = allStock,
                        donors = allDonors
                    )

                    AppScreen.EDUCATION -> EducationalScreen(
                        language = currentLanguage
                    )

                    AppScreen.LOGIN -> LoginScreen(
                        language = currentLanguage,
                        identifier = authEmailOrPhone,
                        pass = authPassword,
                        rememberMe = authRememberMe,
                        isLoading = authIsLoading,
                        errorMessage = authErrorMessage,
                        successMessage = authSuccessMessage,
                        showPass = showPassword,
                        forgotPasswordOpen = forgotPasswordOpen,
                        onIdentifierChange = { viewModel.authEmailOrPhone.value = it },
                        onPasswordChange = { viewModel.authPassword.value = it },
                        onRememberMeChange = { viewModel.authRememberMe.value = it },
                        onToggleShowPassword = { viewModel.showPassword.value = !viewModel.showPassword.value },
                        onLoginClick = { viewModel.login(onSuccess = {}) },
                        onGoogleLoginClick = { viewModel.loginWithGoogle(onSuccess = {}) },
                        onDemoRoleLogin = { viewModel.loginAsRole(it) },
                        onOpenForgotPassword = { viewModel.forgotPasswordDialogOpen.value = true },
                        onCloseForgotPassword = { viewModel.forgotPasswordDialogOpen.value = false },
                        onSendResetOtp = { viewModel.sendPasswordResetOtp() },
                        onConfirmResetPass = { viewModel.confirmPasswordReset() },
                        forgotIdentifier = forgotIdentifier,
                        forgotOtp = forgotOtp,
                        forgotNewPass = forgotNewPass,
                        forgotStep = forgotStep,
                        onForgotIdentifierChange = { viewModel.forgotPasswordIdentifier.value = it },
                        onForgotOtpChange = { viewModel.forgotPasswordOtp.value = it },
                        onForgotNewPassChange = { viewModel.forgotPasswordNewPass.value = it },
                        onNavigateToRegister = { viewModel.navigateTo(AppScreen.REGISTER) }
                    )

                    AppScreen.REGISTER -> RegisterScreen(
                        language = currentLanguage,
                        name = authName,
                        identifier = authEmailOrPhone,
                        pass = authPassword,
                        confirmPass = authConfirmPassword,
                        role = authRole,
                        bloodGroup = authBloodGroup,
                        city = authCity,
                        consent = authConsent,
                        isLoading = authIsLoading,
                        errorMessage = authErrorMessage,
                        showPass = showPassword,
                        onNameChange = { viewModel.authName.value = it },
                        onIdentifierChange = { viewModel.authEmailOrPhone.value = it },
                        onPasswordChange = { viewModel.authPassword.value = it },
                        onConfirmPasswordChange = { viewModel.authConfirmPassword.value = it },
                        onRoleChange = { viewModel.authRole.value = it },
                        onBloodGroupChange = { viewModel.authBloodGroup.value = it },
                        onCityChange = { viewModel.authCity.value = it },
                        onConsentChange = { viewModel.authConsent.value = it },
                        onToggleShowPassword = { viewModel.showPassword.value = !viewModel.showPassword.value },
                        onRegisterClick = { viewModel.register(onSuccess = {}) },
                        onNavigateToLogin = { viewModel.navigateTo(AppScreen.LOGIN) }
                    )
                }
            }
        }
    }

    // SOS Emergency Broadcast Dialog
    SosEmergencyDialog(
        isOpen = isSosDialogOpen,
        language = currentLanguage,
        onDismiss = { viewModel.closeSosDialog() },
        onTriggerBroadcast = { group, city ->
            viewModel.triggerSosBroadcast(group, city)
        }
    )
}
