package se.umu.cs.phbo0006.parkLens

import org.junit.Test
import org.junit.Assert.*
import se.umu.cs.phbo0006.parkLens.controller.util.extractRuleFromLine
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType

class ParkingRuleExtractorTest {

    @Test
    fun `test PAID pattern matching`() {
        val result1 = extractRuleFromLine("avgift")
        assertNotNull(result1)
        assertEquals(SymbolType.PAID, result1?.type)

        val result2 = extractRuleFromLine("BETALA")
        assertNotNull(result2)
        assertEquals(SymbolType.PAID, result2?.type)

        val result3 = extractRuleFromLine("Betald")
        assertNotNull(result3)
        assertEquals(SymbolType.PAID, result3?.type)

        val result4 = extractRuleFromLine("avgiftsfritt")
        assertEquals(SymbolType.UNKNOWN, result4?.type)
    }

    @Test
    fun `test PARKING pattern matching`() {
        val result1 = extractRuleFromLine("p")
        assertNotNull(result1)
        assertEquals(SymbolType.PARKING, result1?.type)

        val result2 = extractRuleFromLine("P")
        assertNotNull(result2)
        assertEquals(SymbolType.PARKING, result2?.type)

        val result3 = extractRuleFromLine("parking")
        assertEquals(SymbolType.UNKNOWN, result3?.type)
    }

    @Test
    fun `test WEEKDAY pattern matching - basic`() {
        val result1 = extractRuleFromLine("1-5")
        assertNotNull(result1)
        assertEquals(SymbolType.WEEKDAY, result1?.type)
        assertEquals(1, result1?.startHour)
        assertEquals(5, result1?.endHour)

        val result2 = extractRuleFromLine("0-6")
        assertNotNull(result2)
        assertEquals(SymbolType.WEEKDAY, result2?.type)
        assertEquals(0, result2?.startHour)
        assertEquals(6, result2?.endHour)
    }

    @Test
    fun `test WEEKDAY pattern matching - with O and o prefixes`() {
        val result1 = extractRuleFromLine("O7-18")
        assertNotNull(result1)
        assertEquals(SymbolType.WEEKDAY, result1?.type)
        assertEquals(7, result1?.startHour)
        assertEquals(18, result1?.endHour)

        val result2 = extractRuleFromLine("o7-18")
        assertNotNull(result2)
        assertEquals(SymbolType.WEEKDAY, result2?.type)
        assertEquals(7, result2?.startHour)
        assertEquals(18, result2?.endHour)

        val result3 = extractRuleFromLine("O9-O17")
        assertNotNull(result3)
        assertEquals(SymbolType.WEEKDAY, result3?.type)
        assertEquals(9, result3?.startHour)
        assertEquals(17, result3?.endHour)
    }

    @Test
    fun `test WEEKDAY pattern matching - standalone O and o`() {
        val result1 = extractRuleFromLine("O-5")
        assertNotNull(result1)
        assertEquals(SymbolType.WEEKDAY, result1?.type)
        assertEquals(0, result1?.startHour)
        assertEquals(5, result1?.endHour)

        val result2 = extractRuleFromLine("1-o")
        assertNotNull(result2)
        assertEquals(SymbolType.WEEKDAY, result2?.type)
        assertEquals(1, result2?.startHour)
        assertEquals(0, result2?.endHour)
    }

    @Test
    fun `test WEEKDAY pattern matching - with spaces and different dashes`() {
        val result1 = extractRuleFromLine("1 - 5")
        assertNotNull(result1)
        assertEquals(SymbolType.WEEKDAY, result1?.type)
        assertEquals(1, result1?.startHour)
        assertEquals(5, result1?.endHour)

        val result2 = extractRuleFromLine("2–6") // em dash
        assertNotNull(result2)
        assertEquals(SymbolType.WEEKDAY, result2?.type)
        assertEquals(2, result2?.startHour)
        assertEquals(6, result2?.endHour)

        val result3 = extractRuleFromLine("O7 – 18")
        assertNotNull(result3)
        assertEquals(SymbolType.WEEKDAY, result3?.type)
        assertEquals(7, result3?.startHour)
        assertEquals(18, result3?.endHour)

        val result4 = extractRuleFromLine("O7     –      18")
        assertNotNull(result4)
        assertEquals(SymbolType.WEEKDAY, result4?.type)
        assertEquals(7, result4?.startHour)
        assertEquals(18, result4?.endHour)

        val result5 = extractRuleFromLine("O7–18")
        assertNotNull(result5)
        assertEquals(SymbolType.WEEKDAY, result5?.type)
        assertEquals(7, result5?.startHour)
        assertEquals(18, result5?.endHour)
    }

    @Test
    fun `test WEEKDAY pattern matching - large numbers`() {
        val result1 = extractRuleFromLine("15-20")
        assertNotNull(result1)
        assertEquals(SymbolType.WEEKDAY, result1?.type)
        assertEquals(15, result1?.startHour)
        assertEquals(20, result1?.endHour)

        val result2 = extractRuleFromLine("O8-23")
        assertNotNull(result2)
        assertEquals(SymbolType.WEEKDAY, result2?.type)
        assertEquals(8, result2?.startHour)
        assertEquals(23, result2?.endHour)
    }

    @Test
    fun `test WEEKDAY pattern matching - negative lookbehind`() {
        val result1 = extractRuleFromLine("(1-5)")

        assertNotNull(result1)
        assertEquals(SymbolType.PRE_HOLIDAY, result1?.type)
        assertEquals(1, result1?.startHour)
        assertEquals(5, result1?.endHour)
    }

    @Test
    fun `test PRE_HOLIDAY pattern matching`() {
        val result1 = extractRuleFromLine("(1-5)")
        assertNotNull(result1)
        assertEquals(SymbolType.PRE_HOLIDAY, result1?.type)
        assertEquals(1, result1?.startHour)
        assertEquals(5, result1?.endHour)

        val result2 = extractRuleFromLine("( 12 – 18 )")  // em dash
        assertNotNull(result2)
        assertEquals(SymbolType.PRE_HOLIDAY, result2?.type)
        assertEquals(12, result2?.startHour)
        assertEquals(18, result2?.endHour)

        val result3 = extractRuleFromLine("( 07 - 23 )")
        assertNotNull(result3)
        assertEquals(SymbolType.PRE_HOLIDAY, result3?.type)
        assertEquals(7, result3?.startHour)
        assertEquals(23, result3?.endHour)
    }

    @Test
    fun `test TIME_RANGE pattern matching`() {
        val result1 = extractRuleFromLine("2 tim")
        assertNotNull(result1)
        assertEquals(SymbolType.TIME_RANGE, result1?.type)
        assertEquals(2, result1?.startHour)
        assertEquals(2, result1?.endHour)

    }

    @Test
    fun `test pattern priority`() {
        val result = extractRuleFromLine("p 1-5")
        assertEquals(SymbolType.WEEKDAY, result?.type)
    }

    @Test
    fun `test unknown pattern`() {
        val result1 = extractRuleFromLine("random text")
        assertNotNull(result1)
        assertEquals(SymbolType.UNKNOWN, result1?.type)

        val result2 = extractRuleFromLine("123abc")
        assertNotNull(result2)
        assertEquals(SymbolType.UNKNOWN, result2?.type)
    }

    @Test
    fun `test edge cases`() {
        val result1 = extractRuleFromLine("X-Y") // Non-numeric
        assertEquals(SymbolType.UNKNOWN, result1?.type)

        val result2 = extractRuleFromLine("1-") // Incomplete range
        assertEquals(SymbolType.UNKNOWN, result2?.type)
    }

    @Test
    fun `test empty and null`() {
        val result1 = extractRuleFromLine("")
        assertNotNull(result1)
        assertEquals(SymbolType.UNKNOWN, result1?.type)

        val result2 = extractRuleFromLine("   ")
        assertNotNull(result2)
        assertEquals(SymbolType.UNKNOWN, result2?.type)
    }

    @Test
    fun `test complex strings with multiple potential matches`() {
        val result1 = extractRuleFromLine("Avgift 1-5 (2-4) 3 tim")
        assertEquals(SymbolType.PAID, result1?.type)

        val result2 = extractRuleFromLine("Parkering 1-5 måndag-fredag")
        assertEquals(SymbolType.WEEKDAY, result2?.type)
    }
}