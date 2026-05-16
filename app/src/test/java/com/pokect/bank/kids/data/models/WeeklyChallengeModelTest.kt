package com.pokect.bank.kids.data.models

import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeeklyChallengeModelTest {

    @Test
    fun `WeeklyChallenge can be created with all fields`() {
        val endDate = Instant.now().plus(java.time.Duration.ofDays(7))
        val challenge = WeeklyChallenge(
            id = "wc1",
            title = "Test Challenge",
            description = "A test challenge",
            rewardXp = 200,
            rewardCoins = 40,
            endDate = endDate,
            participants = listOf("fm2", "fm4"),
            hasJoined = false
        )
        assertEquals("wc1", challenge.id)
        assertEquals("Test Challenge", challenge.title)
        assertEquals("A test challenge", challenge.description)
        assertEquals(200, challenge.rewardXp)
        assertEquals(40, challenge.rewardCoins)
        assertEquals(endDate, challenge.endDate)
        assertEquals(listOf("fm2", "fm4"), challenge.participants)
        assertFalse(challenge.hasJoined)
    }

    @Test
    fun `WeeklyChallenge hasJoined defaults to false`() {
        val endDate = Instant.now()
        val challenge = WeeklyChallenge(
            id = "wc2",
            title = "Default Test",
            description = "Test",
            rewardXp = 100,
            rewardCoins = 20,
            endDate = endDate
        )
        assertFalse(challenge.hasJoined, "hasJoined should default to false")
        assertTrue(challenge.participants.isEmpty(), "participants should default to empty")
    }
}
