package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    DONOR,
    REQUESTER,
    BLOOD_BANK_ADMIN
}

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val name: String,
    val role: UserRole,
    val bloodGroup: String = "O+",
    val city: String = "Central City",
    val age: Int = 28,
    val lastDonationDateMillis: Long? = null,
    val isAvailable: Boolean = true,
    val consentsEmergencyContact: Boolean = true,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "blood_stock")
data class BloodStockItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bloodBankId: Long,
    val bloodBankName: String,
    val city: String,
    val address: String,
    val phone: String,
    val bloodGroup: String, // A+, A-, B+, B-, AB+, AB-, O+, O-
    val component: String, // Whole Blood, Plasma, Platelets
    val units: Int,
    val lastUpdatedMillis: Long = System.currentTimeMillis(),
    val expiryDays: Int = 35, // typical red cell shelf life
    val distanceKm: Double = 2.4,
    val latitude: Double = 12.9716,
    val longitude: Double = 77.5946
)

@Entity(tableName = "emergency_requests")
data class EmergencyRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val bloodGroup: String,
    val unitsNeeded: Int,
    val hospital: String,
    val city: String,
    val urgency: String, // Critical, Within 24h, Planned
    val contactNumber: String,
    val requesterName: String,
    val notes: String = "",
    val timestampMillis: Long = System.currentTimeMillis(),
    val status: String = "Open" // Open, Fulfilled
)

@Entity(tableName = "donation_records")
data class DonationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val donorId: Long,
    val donorName: String,
    val facilityName: String,
    val dateMillis: Long,
    val bloodGroup: String,
    val component: String,
    val unitsDonated: Int = 1,
    val certificateId: String = "LL-DON-${System.currentTimeMillis() % 10000}"
)
