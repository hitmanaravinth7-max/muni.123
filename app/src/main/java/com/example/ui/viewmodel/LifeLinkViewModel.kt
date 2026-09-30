package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LifeLinkDatabase
import com.example.data.model.BloodStockItem
import com.example.data.model.DonationRecord
import com.example.data.model.EmergencyRequest
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.repository.BloodCompatibility
import com.example.data.repository.LifeLinkRepository
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SEARCH_AVAILABILITY,
    EMERGENCY_REQUESTS,
    DONOR_FINDER,
    DONOR_DASHBOARD,
    ADMIN_STOCK,
    RADAR_MAP,
    EDUCATION,
    LOGIN,
    REGISTER
}

class LifeLinkViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LifeLinkDatabase.getDatabase(application)
    private val repository = LifeLinkRepository(database)

    // Language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Navigation screen
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation history for BackHandler
    private val screenBackStack = mutableListOf(AppScreen.HOME)

    // Database flows
    val activeUser: StateFlow<UserAccount?> = repository.activeUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allStock: StateFlow<List<BloodStockItem>> = repository.allStockFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDonors: StateFlow<List<UserAccount>> = repository.allDonorsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyRequests: StateFlow<List<EmergencyRequest>> = repository.allRequestsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openRequestsCount: StateFlow<Int> = repository.activeRequestCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val donorsCount: StateFlow<Int> = repository.donorCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStockUnits: StateFlow<Int?> = repository.totalStockUnitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Donation history for active user
    private val _activeUserDonations = MutableStateFlow<List<DonationRecord>>(emptyList())
    val activeUserDonations: StateFlow<List<DonationRecord>> = _activeUserDonations.asStateFlow()

    // Blood Search Filters
    val selectedBloodGroupFilter = MutableStateFlow("All")
    val selectedComponentFilter = MutableStateFlow("All")
    val selectedCityFilter = MutableStateFlow("All")

    val filteredBloodStock: StateFlow<List<BloodStockItem>> = combine(
        allStock,
        selectedBloodGroupFilter,
        selectedComponentFilter,
        selectedCityFilter
    ) { stock, group, comp, city ->
        stock.filter { item ->
            val matchGroup = (group == "All") || (item.bloodGroup == group)
            val matchComp = (comp == "All") || (item.component.equals(comp, ignoreCase = true))
            val matchCity = (city == "All") || (item.city.equals(city, ignoreCase = true))
            matchGroup && matchComp && matchCity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Donor Finder Filters & Compatibility
    val donorSearchBloodGroup = MutableStateFlow("All")
    val donorSearchCity = MutableStateFlow("All")
    val enableCompatibilityMatching = MutableStateFlow(true)

    val filteredDonors: StateFlow<List<Pair<UserAccount, String>>> = combine(
        allDonors,
        donorSearchBloodGroup,
        donorSearchCity,
        enableCompatibilityMatching
    ) { donors, targetGroup, city, useCompat ->
        donors.filter { donor ->
            val matchCity = (city == "All") || donor.city.equals(city, ignoreCase = true)
            matchCity
        }.mapNotNull { donor ->
            if (targetGroup == "All") {
                Pair(donor, "Registered Donor")
            } else if (donor.bloodGroup == targetGroup) {
                Pair(donor, "Direct Exact Match")
            } else if (useCompat && BloodCompatibility.isCompatible(donor.bloodGroup, targetGroup)) {
                if (donor.bloodGroup == "O-") {
                    Pair(donor, "Universal Donor Compatible")
                } else {
                    Pair(donor, "Compatible Donor (${donor.bloodGroup} → $targetGroup)")
                }
            } else {
                null
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Privacy Contact Disclosure
    private val _revealedContactDonorIds = MutableStateFlow<Set<Long>>(emptySet())
    val revealedContactDonorIds: StateFlow<Set<Long>> = _revealedContactDonorIds.asStateFlow()

    // SOS State
    private val _sosDialogOpen = MutableStateFlow(false)
    val sosDialogOpen: StateFlow<Boolean> = _sosDialogOpen.asStateFlow()

    private val _sosBroadcastBanner = MutableStateFlow<String?>(null)
    val sosBroadcastBanner: StateFlow<String?> = _sosBroadcastBanner.asStateFlow()

    // Authentication UI State
    val authEmailOrPhone = MutableStateFlow("")
    val authPassword = MutableStateFlow("")
    val authConfirmPassword = MutableStateFlow("")
    val authName = MutableStateFlow("")
    val authRole = MutableStateFlow(UserRole.DONOR)
    val authBloodGroup = MutableStateFlow("O+")
    val authCity = MutableStateFlow("Metro Central")
    val authConsent = MutableStateFlow(true)
    val authRememberMe = MutableStateFlow(true)

    val authIsLoading = MutableStateFlow(false)
    val authErrorMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)
    val showPassword = MutableStateFlow(false)
    val forgotPasswordDialogOpen = MutableStateFlow(false)
    val forgotPasswordIdentifier = MutableStateFlow("")
    val forgotPasswordOtp = MutableStateFlow("")
    val forgotPasswordNewPass = MutableStateFlow("")
    val forgotPasswordStep = MutableStateFlow(1) // 1 = identifier, 2 = OTP + new pass

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }

        viewModelScope.launch {
            activeUser.collect { user ->
                if (user != null) {
                    repository.getDonationHistory(user.id).collect { records ->
                        _activeUserDonations.value = records
                    }
                } else {
                    _activeUserDonations.value = emptyList()
                }
            }
        }
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.HINDI
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        return if (screenBackStack.isNotEmpty()) {
            val previous = screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = previous
            true
        } else if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            true
        } else {
            false
        }
    }

    fun revealContact(donorId: Long) {
        _revealedContactDonorIds.value = _revealedContactDonorIds.value + donorId
    }

    fun openSosDialog() {
        _sosDialogOpen.value = true
    }

    fun closeSosDialog() {
        _sosDialogOpen.value = false
    }

    fun dismissSosBanner() {
        _sosBroadcastBanner.value = null
    }

    fun triggerSosBroadcast(bloodGroup: String, city: String) {
        viewModelScope.launch {
            repository.broadcastEmergencyRequest(
                patientName = "URGENT SOS PATIENT",
                bloodGroup = bloodGroup,
                unitsNeeded = 3,
                hospital = "Emergency Medical Trauma Center",
                city = city,
                urgency = "Critical",
                contactNumber = "108 / +1 (800) 555-0199",
                requesterName = "LifeLink Emergency Dispatch",
                notes = "HIGH PRIORITY SOS BROADCAST. Immediate donors requested."
            )
            _sosBroadcastBanner.value = "EMERGENCY BROADCAST TRANSMITTED: $bloodGroup needed immediately in $city. Matching donors alerted!"
            _sosDialogOpen.value = false
        }
    }

    // Auth Actions
    fun login(onSuccess: (UserRole) -> Unit) {
        val id = authEmailOrPhone.value.trim()
        val pass = authPassword.value

        if (id.isEmpty()) {
            authErrorMessage.value = "Please enter your email or 10-digit phone number"
            return
        }
        if (pass.isEmpty()) {
            authErrorMessage.value = "Please enter your password"
            return
        }

        authIsLoading.value = true
        authErrorMessage.value = null

        viewModelScope.launch {
            val result = repository.login(id, pass)
            authIsLoading.value = false
            result.onSuccess { user ->
                authSuccessMessage.value = "Welcome back, ${user.name}!"
                // Redirect user to the right dashboard by role
                when (user.role) {
                    UserRole.DONOR -> navigateTo(AppScreen.DONOR_DASHBOARD)
                    UserRole.BLOOD_BANK_ADMIN -> navigateTo(AppScreen.ADMIN_STOCK)
                    UserRole.REQUESTER -> navigateTo(AppScreen.EMERGENCY_REQUESTS)
                }
                onSuccess(user.role)
            }.onFailure { err ->
                authErrorMessage.value = err.message ?: "Authentication failed"
            }
        }
    }

    fun loginWithGoogle(onSuccess: (UserRole) -> Unit) {
        authIsLoading.value = true
        authErrorMessage.value = null
        viewModelScope.launch {
            // Instant Google Sign-In demonstration
            val user = repository.loginAsDemoRole(UserRole.DONOR)
            authIsLoading.value = false
            authSuccessMessage.value = "Signed in with Google as ${user.name}"
            navigateTo(AppScreen.DONOR_DASHBOARD)
            onSuccess(user.role)
        }
    }

    fun loginAsRole(role: UserRole) {
        viewModelScope.launch {
            val user = repository.loginAsDemoRole(role)
            authSuccessMessage.value = "Switched to ${user.name} (${role.name})"
            when (role) {
                UserRole.DONOR -> navigateTo(AppScreen.DONOR_DASHBOARD)
                UserRole.BLOOD_BANK_ADMIN -> navigateTo(AppScreen.ADMIN_STOCK)
                UserRole.REQUESTER -> navigateTo(AppScreen.EMERGENCY_REQUESTS)
            }
        }
    }

    fun register(onSuccess: (UserRole) -> Unit) {
        val name = authName.value.trim()
        val email = authEmailOrPhone.value.trim()
        val phone = authEmailOrPhone.value.trim()
        val pass = authPassword.value
        val confirmPass = authConfirmPassword.value
        val role = authRole.value
        val bloodGroup = authBloodGroup.value
        val city = authCity.value
        val consent = authConsent.value

        if (name.length < 2) {
            authErrorMessage.value = "Please enter your full name"
            return
        }

        // Validate email format
        val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPhone = phone.replace(Regex("[^0-9]"), "").length >= 10

        if (!isEmail && !isPhone) {
            authErrorMessage.value = "Enter a valid email address or 10-digit mobile number"
            return
        }

        if (pass.length < 8) {
            authErrorMessage.value = "Password must be at least 8 characters long"
            return
        }

        if (pass != confirmPass) {
            authErrorMessage.value = "Passwords do not match"
            return
        }

        if (role == UserRole.DONOR && !consent) {
            authErrorMessage.value = "You must accept the emergency contact consent to register as a donor"
            return
        }

        authIsLoading.value = true
        authErrorMessage.value = null

        viewModelScope.launch {
            val cleanEmail = if (isEmail) email else "${phone.replace(Regex("[^0-9]"), "")}@lifelink.org"
            val cleanPhone = if (isPhone) phone.replace(Regex("[^0-9]"), "") else "9876543210"

            val result = repository.register(
                name = name,
                email = cleanEmail,
                phone = cleanPhone,
                password = pass,
                role = role,
                bloodGroup = bloodGroup,
                city = city,
                consentsEmergencyContact = consent
            )
            authIsLoading.value = false
            result.onSuccess { user ->
                authSuccessMessage.value = "Registration successful! Welcome to LifeLink."
                when (user.role) {
                    UserRole.DONOR -> navigateTo(AppScreen.DONOR_DASHBOARD)
                    UserRole.BLOOD_BANK_ADMIN -> navigateTo(AppScreen.ADMIN_STOCK)
                    UserRole.REQUESTER -> navigateTo(AppScreen.EMERGENCY_REQUESTS)
                }
                onSuccess(user.role)
            }.onFailure { err ->
                authErrorMessage.value = err.message ?: "Registration failed"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _activeUserDonations.value = emptyList()
            navigateTo(AppScreen.HOME)
        }
    }

    fun sendPasswordResetOtp() {
        val id = forgotPasswordIdentifier.value.trim()
        if (id.isEmpty()) {
            authErrorMessage.value = "Please enter your registered email or phone"
            return
        }
        forgotPasswordStep.value = 2
        authSuccessMessage.value = "OTP code [482910] sent to $id"
    }

    fun confirmPasswordReset() {
        val otp = forgotPasswordOtp.value.trim()
        val newPass = forgotPasswordNewPass.value

        if (otp != "482910" && otp.length < 4) {
            authErrorMessage.value = "Invalid OTP code. Please enter 482910"
            return
        }
        if (newPass.length < 8) {
            authErrorMessage.value = "New password must be at least 8 characters"
            return
        }

        viewModelScope.launch {
            val result = repository.resetPassword(forgotPasswordIdentifier.value, newPass)
            result.onSuccess {
                forgotPasswordDialogOpen.value = false
                forgotPasswordStep.value = 1
                authSuccessMessage.value = "Password successfully reset! Please sign in with your new password."
            }.onFailure {
                authErrorMessage.value = it.message ?: "Password reset failed"
            }
        }
    }

    // Donor actions
    fun toggleAvailability(isAvailable: Boolean) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.toggleDonorAvailability(user.id, isAvailable)
        }
    }

    fun scheduleQuickDonation(facilityName: String, bloodGroup: String, component: String) {
        val user = activeUser.value ?: return
        viewModelScope.launch {
            repository.recordDonation(
                donorId = user.id,
                donorName = user.name,
                facilityName = facilityName,
                bloodGroup = bloodGroup,
                component = component,
                units = 1
            )
            _sosBroadcastBanner.value = "Thank you! Donation recorded at $facilityName. You saved up to 3 lives!"
        }
    }

    // Emergency Request Form Actions
    fun submitEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        unitsNeeded: Int,
        hospital: String,
        city: String,
        urgency: String,
        contactNumber: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val user = activeUser.value
        val requesterName = user?.name ?: "Emergency Requester"

        viewModelScope.launch {
            repository.broadcastEmergencyRequest(
                patientName = patientName,
                bloodGroup = bloodGroup,
                unitsNeeded = unitsNeeded,
                hospital = hospital,
                city = city,
                urgency = urgency,
                contactNumber = contactNumber,
                requesterName = requesterName,
                notes = notes
            )
            _sosBroadcastBanner.value = "Emergency broadcast live! Alert dispatched to all matching $bloodGroup donors in $city."
            onSuccess()
        }
    }

    fun markRequestFulfilled(requestId: Long) {
        viewModelScope.launch {
            repository.markRequestStatus(requestId, "Fulfilled")
        }
    }

    // Admin Stock Actions
    fun updateStockLevel(stockId: Long, currentUnits: Int, delta: Int) {
        val newUnits = maxOf(0, currentUnits + delta)
        viewModelScope.launch {
            repository.updateStockUnits(stockId, newUnits)
        }
    }

    fun addNewStockEntry(
        bloodBankName: String,
        city: String,
        address: String,
        phone: String,
        bloodGroup: String,
        component: String,
        units: Int
    ) {
        viewModelScope.launch {
            val item = BloodStockItem(
                bloodBankId = (activeUser.value?.id ?: 100L) + 50L,
                bloodBankName = bloodBankName,
                city = city,
                address = address,
                phone = phone,
                bloodGroup = bloodGroup,
                component = component,
                units = units,
                expiryDays = if (component == "Platelets") 5 else if (component == "Plasma") 180 else 35,
                distanceKm = 2.5
            )
            repository.addNewStockItem(item)
            _sosBroadcastBanner.value = "Stock updated: $units units of $bloodGroup $component added."
        }
    }
}
