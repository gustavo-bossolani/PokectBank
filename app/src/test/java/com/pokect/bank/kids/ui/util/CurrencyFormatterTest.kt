package com.pokect.bank.kids.ui.util

import com.pokect.bank.kids.util.formatCurrency
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class CurrencyFormatterTest {

    @Test
    fun formatsPositiveAmount() {
        val result = formatCurrency(10.0)
        assertTrue(result.contains("10"))
        assertTrue(result.contains("R$"))
    }

    @Test
    fun formatsDecimalAmount() {
        val result = formatCurrency(47.50)
        assertTrue(result.contains("47"))
        assertTrue(result.contains("50"))
    }

    @Test
    fun formatsLargeAmount() {
        val result = formatCurrency(1234.56)
        assertTrue(result.contains("1"))
        assertTrue(result.contains("234"))
        assertTrue(result.contains("56"))
    }

    @Test
    fun formatsZero() {
        val result = formatCurrency(0.0)
        assertTrue(result.contains("0"))
        assertTrue(result.contains("R$"))
    }

    @Test
    fun formatsNegativeAmount() {
        val result = formatCurrency(-10.0)
        assertTrue(result.contains("-"))
        assertTrue(result.contains("10"))
    }

    @Test
    fun formatsSmallAmount() {
        val result = formatCurrency(0.01)
        assertTrue(result.contains("0"))
        assertTrue(result.contains("01"))
    }
}
