package com.pokect.bank.kids.data.models

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class RewardModelTest {

    @Test
    fun `Reward has redeemed field defaulting to false`() {
        val reward = Reward(
            id = "r1",
            title = "Test Reward",
            description = "A test reward",
            icon = "🎁",
            cost = 50,
            isNew = true
        )
        assertFalse(reward.redeemed, "redeemed should default to false")
    }

    @Test
    fun `Reward can be created with redeemed set to true`() {
        val reward = Reward(
            id = "r1",
            title = "Test Reward",
            description = "A test reward",
            icon = "🎁",
            cost = 50,
            isNew = true,
            redeemed = true
        )
        assertTrue(reward.redeemed, "redeemed should be true when explicitly set")
    }

    @Test
    fun `Reward has all 7 fields`() {
        val reward = Reward(
            id = "r1",
            title = "Test",
            description = "Desc",
            icon = "🎁",
            cost = 50,
            isNew = true,
            redeemed = false
        )
        assertEquals("r1", reward.id)
        assertEquals("Test", reward.title)
        assertEquals("Desc", reward.description)
        assertEquals("🎁", reward.icon)
        assertEquals(50, reward.cost)
        assertTrue(reward.isNew)
        assertFalse(reward.redeemed)
    }
}
