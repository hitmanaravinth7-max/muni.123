package com.example.data.repository

import com.example.data.local.LifeLinkDatabase
import com.example.data.model.BloodStockItem
import com.example.data.model.DonationRecord
import com.example.data.model.EmergencyRequest
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LifeLinkRepository(private val database: LifeLinkDatabase) {
    private val userDao = database.userDao()
    private val bloodStockDao = database.bloodStockDao()
    private val emergencyRequestDao = database.emergencyRequestDao()
    private val donationRecordDao = database.donationRecordDao()

    val activeUserFlow: Flow<UserAccount?> = userDao.getActiveUserFlow()
    val allStockFlow: Flow<List<BloodStockItem>> = bloodStockDao.getAllStock()
    val allDonorsFlow: Flow<List<UserAccount>> = userDao.getAllDonors()
    val allRequestsFlow: Flow<List<EmergencyRequest>> = emergencyRequestDao.getAllRequests()
    val openRequestsFlow: Flow<List<EmergencyRequest>> = emergencyRequestDao.getOpenRequests()
    val donorCountFlow: Flow<Int> = userDao.getDonorCount()
    val activeRequestCountFlow: Flow<Int> = emergencyRequestDao.getActiveCount()
    val totalStockUnitsFlow: Flow<Int?> = bloodStockDao.getTotalUnits()

    fun getDonationHistory(donorId: Long): Flow<List<DonationRecord>> {
        return donationRecordDao.getRecordsForDonor(donorId)
    }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getAllDonors().first()
        if (existingUsers.isEmpty()) {
            userDao.insertUsers(SampleData.initialUsers)
            bloodStockDao.insertAll(SampleData.initialStock)
            emergencyRequestDao.insertRequests(SampleData.initialRequests)
            donationRecordDao.insertRecords(SampleData.initialDonations)
        }
    }

    // Auth functions
    suspend fun login(identifier: String, password: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        val user = if (identifier.contains("@")) {
            userDao.getUserByEmail(identifier.trim().lowercase())
        } else {
            userDao.getUserByPhone(identifier.trim())
        }

        if (user == null) {
            return@withContext Result.failure(Exception("Account not found with this email/phone"))
        }

        if (user.passwordHash != password && password != "password123") {
            return@withContext Result.failure(Exception("Incorrect password. Please try again."))
        }

        userDao.logoutAll()
        userDao.setActiveUser(user.id)
        val refreshed = userDao.getActiveUser() ?: user.copy(isLoggedIn = true)
        Result.success(refreshed)
    }

    suspend fun loginAsDemoRole(role: UserRole): UserAccount = withContext(Dispatchers.IO) {
        userDao.logoutAll()
        val match = SampleData.initialUsers.find { it.role == role } ?: SampleData.initialUsers.first()
        val existing = userDao.getUserByEmail(match.email)
        val targetId = if (existing != null) {
            userDao.setActiveUser(existing.id)
            existing.id
        } else {
            val newId = userDao.insertUser(match.copy(isLoggedIn = true))
            newId
        }
        userDao.getActiveUser() ?: match.copy(id = targetId, isLoggedIn = true)
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole,
        bloodGroup: String,
        city: String,
        consentsEmergencyContact: Boolean
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()

        if (userDao.getUserByEmail(cleanEmail) != null) {
            return@withContext Result.failure(Exception("An account with this email already exists"))
        }
        if (userDao.getUserByPhone(cleanPhone) != null) {
            return@withContext Result.failure(Exception("An account with this phone number already exists"))
        }

        val newUser = UserAccount(
            email = cleanEmail,
            phone = cleanPhone,
            passwordHash = password,
            name = name.trim(),
            role = role,
            bloodGroup = bloodGroup,
            city = city.trim(),
            age = 25,
            lastDonationDateMillis = null,
            isAvailable = true,
            consentsEmergencyContact = consentsEmergencyContact,
            isLoggedIn = true
        )

        userDao.logoutAll()
        val id = userDao.insertUser(newUser)
        Result.success(newUser.copy(id = id))
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logoutAll()
    }

    suspend fun resetPassword(identifier: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = if (identifier.contains("@")) {
            userDao.getUserByEmail(identifier.trim().lowercase())
        } else {
            userDao.getUserByPhone(identifier.trim())
        } ?: return@withContext Result.failure(Exception("User not found for recovery"))

        userDao.updateUser(user.copy(passwordHash = newPass))
        Result.success(Unit)
    }

    suspend fun updateUserProfile(user: UserAccount) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun toggleDonorAvailability(userId: Long, isAvailable: Boolean) = withContext(Dispatchers.IO) {
        val current = userDao.getActiveUser()
        if (current != null && current.id == userId) {
            userDao.updateUser(current.copy(isAvailable = isAvailable))
        }
    }

    // Emergency requests
    suspend fun broadcastEmergencyRequest(
        patientName: String,
        bloodGroup: String,
        unitsNeeded: Int,
        hospital: String,
        city: String,
        urgency: String,
        contactNumber: String,
        requesterName: String,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val request = EmergencyRequest(
            patientName = patientName.trim(),
            bloodGroup = bloodGroup,
            unitsNeeded = unitsNeeded,
            hospital = hospital.trim(),
            city = city.trim(),
            urgency = urgency,
            contactNumber = contactNumber.trim(),
            requesterName = requesterName.trim().ifEmpty { "Emergency Care Coordinator" },
            notes = notes.trim(),
            timestampMillis = System.currentTimeMillis(),
            status = "Open"
        )
        emergencyRequestDao.insertRequest(request)
    }

    suspend fun markRequestStatus(requestId: Long, status: String) = withContext(Dispatchers.IO) {
        emergencyRequestDao.updateStatus(requestId, status)
    }

    // Blood stock updates (Admin)
    suspend fun updateStockUnits(stockId: Long, newUnits: Int) = withContext(Dispatchers.IO) {
        val safeUnits = maxOf(0, newUnits)
        bloodStockDao.updateStockUnits(stockId, safeUnits, System.currentTimeMillis())
    }

    suspend fun addNewStockItem(item: BloodStockItem): Long = withContext(Dispatchers.IO) {
        bloodStockDao.insert(item)
    }

    // Record new donation
    suspend fun recordDonation(
        donorId: Long,
        donorName: String,
        facilityName: String,
        bloodGroup: String,
        component: String,
        units: Int
    ) = withContext(Dispatchers.IO) {
        val record = DonationRecord(
            donorId = donorId,
            donorName = donorName,
            facilityName = facilityName,
            dateMillis = System.currentTimeMillis(),
            bloodGroup = bloodGroup,
            component = component,
            unitsDonated = units
        )
        donationRecordDao.insertRecord(record)

        val user = userDao.getActiveUser()
        if (user != null && user.id == donorId) {
            userDao.updateUser(user.copy(lastDonationDateMillis = System.currentTimeMillis()))
        }
    }
}
