package com.example.data.repository

import com.example.data.model.BloodStockItem
import com.example.data.model.DonationRecord
import com.example.data.model.EmergencyRequest
import com.example.data.model.UserAccount
import com.example.data.model.UserRole

object SampleData {
    val initialUsers = listOf(
        UserAccount(
            id = 1L,
            email = "donor@lifelink.org",
            phone = "9876543210",
            passwordHash = "password123",
            name = "Sarah Jenkins",
            role = UserRole.DONOR,
            bloodGroup = "O-",
            city = "Metro Central",
            age = 29,
            lastDonationDateMillis = System.currentTimeMillis() - (110L * 24 * 60 * 60 * 1000L), // 110 days ago (Eligible!)
            isAvailable = true,
            consentsEmergencyContact = true,
            isLoggedIn = false
        ),
        UserAccount(
            id = 2L,
            email = "arjun.sharma@lifelink.org",
            phone = "9123456780",
            passwordHash = "password123",
            name = "Dr. Arjun Sharma",
            role = UserRole.BLOOD_BANK_ADMIN,
            bloodGroup = "B+",
            city = "Metro Central",
            age = 42,
            lastDonationDateMillis = null,
            isAvailable = true,
            consentsEmergencyContact = true,
            isLoggedIn = false
        ),
        UserAccount(
            id = 3L,
            email = "requester@lifelink.org",
            phone = "9845123456",
            passwordHash = "password123",
            name = "Maya Patel",
            role = UserRole.REQUESTER,
            bloodGroup = "A+",
            city = "Metro Central",
            age = 34,
            lastDonationDateMillis = null,
            isAvailable = false,
            consentsEmergencyContact = true,
            isLoggedIn = false
        ),
        UserAccount(
            id = 4L,
            email = "david.chen@lifelink.org",
            phone = "9876500112",
            passwordHash = "password123",
            name = "David Chen",
            role = UserRole.DONOR,
            bloodGroup = "A+",
            city = "Metro Central",
            age = 31,
            lastDonationDateMillis = System.currentTimeMillis() - (35L * 24 * 60 * 60 * 1000L), // 35 days ago (Ineligible - 55 days left)
            isAvailable = false,
            consentsEmergencyContact = true,
            isLoggedIn = false
        ),
        UserAccount(
            id = 5L,
            email = "priya.nair@lifelink.org",
            phone = "9898765432",
            passwordHash = "password123",
            name = "Priya Nair",
            role = UserRole.DONOR,
            bloodGroup = "B+",
            city = "North District",
            age = 26,
            lastDonationDateMillis = System.currentTimeMillis() - (150L * 24 * 60 * 60 * 1000L), // Eligible
            isAvailable = true,
            consentsEmergencyContact = true,
            isLoggedIn = false
        ),
        UserAccount(
            id = 6L,
            email = "michael.ross@lifelink.org",
            phone = "9711223344",
            passwordHash = "password123",
            name = "Michael Ross",
            role = UserRole.DONOR,
            bloodGroup = "AB-",
            city = "South Harbor",
            age = 36,
            lastDonationDateMillis = null, // Never donated (Eligible)
            isAvailable = true,
            consentsEmergencyContact = true,
            isLoggedIn = false
        )
    )

    val initialStock = listOf(
        // Red Cross Blood Center
        BloodStockItem(
            id = 1L,
            bloodBankId = 101L,
            bloodBankName = "City Red Cross Blood Center",
            city = "Metro Central",
            address = "742 Evergreen Healthcare Blvd, Central District",
            phone = "+1 (800) 733-2767",
            bloodGroup = "O-",
            component = "Whole Blood",
            units = 3, // LOW STOCK
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 45,
            expiryDays = 7,
            distanceKm = 1.8,
            latitude = 12.9716,
            longitude = 77.5946
        ),
        BloodStockItem(
            id = 2L,
            bloodBankId = 101L,
            bloodBankName = "City Red Cross Blood Center",
            city = "Metro Central",
            address = "742 Evergreen Healthcare Blvd, Central District",
            phone = "+1 (800) 733-2767",
            bloodGroup = "O+",
            component = "Whole Blood",
            units = 24,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 30,
            expiryDays = 28,
            distanceKm = 1.8,
            latitude = 12.9716,
            longitude = 77.5946
        ),
        BloodStockItem(
            id = 3L,
            bloodBankId = 101L,
            bloodBankName = "City Red Cross Blood Center",
            city = "Metro Central",
            address = "742 Evergreen Healthcare Blvd, Central District",
            phone = "+1 (800) 733-2767",
            bloodGroup = "A+",
            component = "Plasma",
            units = 14,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 90,
            expiryDays = 180,
            distanceKm = 1.8,
            latitude = 12.9716,
            longitude = 77.5946
        ),
        BloodStockItem(
            id = 4L,
            bloodBankId = 101L,
            bloodBankName = "City Red Cross Blood Center",
            city = "Metro Central",
            address = "742 Evergreen Healthcare Blvd, Central District",
            phone = "+1 (800) 733-2767",
            bloodGroup = "B-",
            component = "Platelets",
            units = 2, // CRITICAL LOW STOCK
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 15,
            expiryDays = 3,
            distanceKm = 1.8,
            latitude = 12.9716,
            longitude = 77.5946
        ),
        // Metropolitan General Hospital Blood Bank
        BloodStockItem(
            id = 5L,
            bloodBankId = 102L,
            bloodBankName = "Metropolitan General Hospital Blood Bank",
            city = "Metro Central",
            address = "1200 Avenue of Health, Near Metro Station",
            phone = "+1 (555) 019-2834",
            bloodGroup = "A-",
            component = "Whole Blood",
            units = 4, // LOW STOCK
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 20,
            expiryDays = 14,
            distanceKm = 3.2,
            latitude = 12.9780,
            longitude = 77.6010
        ),
        BloodStockItem(
            id = 6L,
            bloodBankId = 102L,
            bloodBankName = "Metropolitan General Hospital Blood Bank",
            city = "Metro Central",
            address = "1200 Avenue of Health, Near Metro Station",
            phone = "+1 (555) 019-2834",
            bloodGroup = "AB+",
            component = "Whole Blood",
            units = 18,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 60,
            expiryDays = 25,
            distanceKm = 3.2,
            latitude = 12.9780,
            longitude = 77.6010
        ),
        BloodStockItem(
            id = 7L,
            bloodBankId = 102L,
            bloodBankName = "Metropolitan General Hospital Blood Bank",
            city = "Metro Central",
            address = "1200 Avenue of Health, Near Metro Station",
            phone = "+1 (555) 019-2834",
            bloodGroup = "B+",
            component = "Whole Blood",
            units = 31,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 40,
            expiryDays = 32,
            distanceKm = 3.2,
            latitude = 12.9780,
            longitude = 77.6010
        ),
        // Apollo Emergency Blood Services
        BloodStockItem(
            id = 8L,
            bloodBankId = 103L,
            bloodBankName = "Apollo Emergency Blood Services",
            city = "North District",
            address = "45 Ring Road, Medical Tech Zone",
            phone = "+1 (555) 789-0123",
            bloodGroup = "O-",
            component = "Plasma",
            units = 8,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 12,
            expiryDays = 120,
            distanceKm = 5.6,
            latitude = 12.9900,
            longitude = 77.5850
        ),
        BloodStockItem(
            id = 9L,
            bloodBankId = 103L,
            bloodBankName = "Apollo Emergency Blood Services",
            city = "North District",
            address = "45 Ring Road, Medical Tech Zone",
            phone = "+1 (555) 789-0123",
            bloodGroup = "AB-",
            component = "Platelets",
            units = 1, // VERY CRITICAL
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 5,
            expiryDays = 2,
            distanceKm = 5.6,
            latitude = 12.9900,
            longitude = 77.5850
        ),
        // Mercy Care Blood Bank
        BloodStockItem(
            id = 10L,
            bloodBankId = 104L,
            bloodBankName = "Mercy Care Rotary Blood Bank",
            city = "South Harbor",
            address = "88 Harbor Port Rd, Sector 4",
            phone = "+1 (555) 432-1098",
            bloodGroup = "O+",
            component = "Whole Blood",
            units = 19,
            lastUpdatedMillis = System.currentTimeMillis() - 1000L * 60 * 80,
            expiryDays = 21,
            distanceKm = 6.4,
            latitude = 12.9550,
            longitude = 77.6100
        )
    )

    val initialRequests = listOf(
        EmergencyRequest(
            id = 1L,
            patientName = "Elena Vance (ICU Bed 4)",
            bloodGroup = "O-",
            unitsNeeded = 3,
            hospital = "City Trauma Center",
            city = "Metro Central",
            urgency = "Critical",
            contactNumber = "+1 (555) 012-9876",
            requesterName = "Dr. Marcus Brody",
            notes = "Emergency surgery scheduled in 1 hour. Immediate O- red cells required.",
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 25,
            status = "Open"
        ),
        EmergencyRequest(
            id = 2L,
            patientName = "Rohan Verma",
            bloodGroup = "B-",
            unitsNeeded = 2,
            hospital = "Metropolitan General Hospital",
            city = "Metro Central",
            urgency = "Within 24h",
            contactNumber = "+1 (555) 234-5678",
            requesterName = "Sunita Verma (Sister)",
            notes = "Platelet transfusion needed by tonight for dengue fever patient.",
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 120,
            status = "Open"
        ),
        EmergencyRequest(
            id = 3L,
            patientName = "Chloe Bennett",
            bloodGroup = "A+",
            unitsNeeded = 1,
            hospital = "North District Health Institute",
            city = "North District",
            urgency = "Planned",
            contactNumber = "+1 (555) 345-6789",
            requesterName = "James Bennett",
            notes = "Elective orthopedic surgery preparation.",
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 360,
            status = "Fulfilled"
        )
    )

    val initialDonations = listOf(
        DonationRecord(
            id = 1L,
            donorId = 1L,
            donorName = "Sarah Jenkins",
            facilityName = "City Red Cross Blood Center",
            dateMillis = System.currentTimeMillis() - (110L * 24 * 60 * 60 * 1000L),
            bloodGroup = "O-",
            component = "Whole Blood",
            unitsDonated = 1,
            certificateId = "LL-DON-8841"
        ),
        DonationRecord(
            id = 2L,
            donorId = 1L,
            donorName = "Sarah Jenkins",
            facilityName = "Mercy Care Rotary Blood Bank",
            dateMillis = System.currentTimeMillis() - (220L * 24 * 60 * 60 * 1000L),
            bloodGroup = "O-",
            component = "Whole Blood",
            unitsDonated = 1,
            certificateId = "LL-DON-6120"
        ),
        DonationRecord(
            id = 3L,
            donorId = 1L,
            donorName = "Sarah Jenkins",
            facilityName = "Metropolitan General Hospital",
            dateMillis = System.currentTimeMillis() - (330L * 24 * 60 * 60 * 1000L),
            bloodGroup = "O-",
            component = "Whole Blood",
            unitsDonated = 1,
            certificateId = "LL-DON-4019"
        )
    )
}
