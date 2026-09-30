package com.example

import com.example.data.repository.BloodCompatibility
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testUniversalDonorCompatibility() {
    // O- is universal red cell donor
    assertTrue(BloodCompatibility.isCompatible(donorGroup = "O-", recipientGroup = "A+"))
    assertTrue(BloodCompatibility.isCompatible(donorGroup = "O-", recipientGroup = "B-"))
    assertTrue(BloodCompatibility.isCompatible(donorGroup = "O-", recipientGroup = "AB+"))
    assertTrue(BloodCompatibility.isCompatible(donorGroup = "O-", recipientGroup = "O-"))

    // AB+ cannot donate to O-
    assertFalse(BloodCompatibility.isCompatible(donorGroup = "AB+", recipientGroup = "O-"))
  }

  @Test
  fun testUniversalRecipientCompatibility() {
    // AB+ can receive from everyone
    val allGroups = BloodCompatibility.ALL_BLOOD_GROUPS
    allGroups.forEach { donor ->
      assertTrue(BloodCompatibility.isCompatible(donorGroup = donor, recipientGroup = "AB+"))
    }
  }

  @Test
  fun test90DayEligibilityCycle() {
    // Never donated -> eligible
    val resultNever = BloodCompatibility.checkEligibility(null)
    assertTrue(resultNever.isEligible)
    assertEquals(0, resultNever.daysRemaining)

    // Donated 30 days ago -> ineligible, 60 days remaining
    val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000L)
    val resultRecent = BloodCompatibility.checkEligibility(thirtyDaysAgo)
    assertFalse(resultRecent.isEligible)
    assertEquals(60, resultRecent.daysRemaining)

    // Donated 100 days ago -> eligible
    val hundredDaysAgo = System.currentTimeMillis() - (100L * 24 * 60 * 60 * 1000L)
    val resultPast = BloodCompatibility.checkEligibility(hundredDaysAgo)
    assertTrue(resultPast.isEligible)
    assertEquals(0, resultPast.daysRemaining)
  }
}

