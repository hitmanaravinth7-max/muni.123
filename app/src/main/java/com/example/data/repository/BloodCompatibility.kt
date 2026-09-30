package com.example.data.repository

object BloodCompatibility {
    val ALL_BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val ALL_COMPONENTS = listOf("Whole Blood", "Plasma", "Platelets")

    // Who can donate to whom (Donor -> List of compatible Recipients)
    private val canDonateToMap = mapOf(
        "O-" to listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
        "O+" to listOf("O+", "A+", "B+", "AB+"),
        "A-" to listOf("A-", "A+", "AB-", "AB+"),
        "A+" to listOf("A+", "AB+"),
        "B-" to listOf("B-", "B+", "AB-", "AB+"),
        "B+" to listOf("B+", "AB+"),
        "AB-" to listOf("AB-", "AB+"),
        "AB+" to listOf("AB+")
    )

    // Who can receive from whom (Recipient -> List of compatible Donors)
    private val canReceiveFromMap = mapOf(
        "AB+" to listOf("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"),
        "AB-" to listOf("AB-", "A-", "B-", "O-"),
        "A+" to listOf("A+", "A-", "O+", "O-"),
        "A-" to listOf("A-", "O-"),
        "B+" to listOf("B+", "B-", "O+", "O-"),
        "B-" to listOf("B-", "O-"),
        "O+" to listOf("O+", "O-"),
        "O-" to listOf("O-")
    )

    fun getCompatibleDonorsForRecipient(recipientGroup: String): List<String> {
        return canReceiveFromMap[recipientGroup] ?: listOf(recipientGroup)
    }

    fun getCompatibleRecipientsForDonor(donorGroup: String): List<String> {
        return canDonateToMap[donorGroup] ?: listOf(donorGroup)
    }

    fun isCompatible(donorGroup: String, recipientGroup: String, component: String = "Whole Blood"): Boolean {
        if (component == "Plasma") {
            // Plasma is reverse
            return when (donorGroup.replace("+", "").replace("-", "")) {
                "AB" -> true
                "A" -> recipientGroup.startsWith("A") || recipientGroup.startsWith("O")
                "B" -> recipientGroup.startsWith("B") || recipientGroup.startsWith("O")
                "O" -> recipientGroup.startsWith("O")
                else -> donorGroup == recipientGroup
            }
        }
        val compatibleDonors = canReceiveFromMap[recipientGroup] ?: return false
        return compatibleDonors.contains(donorGroup)
    }

    data class EligibilityResult(
        val isEligible: Boolean,
        val daysRemaining: Int,
        val message: String
    )

    fun checkEligibility(lastDonationMillis: Long?): EligibilityResult {
        if (lastDonationMillis == null) {
            return EligibilityResult(
                isEligible = true,
                daysRemaining = 0,
                message = "Eligible to donate! No prior donations recorded."
            )
        }
        val currentTime = System.currentTimeMillis()
        val diffDays = ((currentTime - lastDonationMillis) / (1000L * 60 * 60 * 24)).toInt()
        val requiredGap = 90

        return if (diffDays >= requiredGap) {
            EligibilityResult(
                isEligible = true,
                daysRemaining = 0,
                message = "Eligible to donate! Last donated $diffDays days ago."
            )
        } else {
            val remaining = requiredGap - diffDays
            EligibilityResult(
                isEligible = false,
                daysRemaining = remaining,
                message = "Ineligible: 90-day recovery required. Eligible in $remaining days."
            )
        }
    }
}
