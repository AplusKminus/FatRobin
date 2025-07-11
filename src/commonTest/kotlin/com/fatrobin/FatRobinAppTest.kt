package com.fatrobin

import kotlin.test.*

class FatRobinAppTest {
    
    @Test
    fun `PillCalculation data class should store values correctly`() {
        val calculation = PillCalculation(
            pills10k = 2,
            pills35k = 1,
            gramsFor10k = 5.0,
            gramsFor35k = 17.0,
            pillsPerPackage10k = 4,
            pillsPerPackage35k = 2
        )
        
        assertEquals(2, calculation.pills10k)
        assertEquals(1, calculation.pills35k)
        assertEquals(5.0, calculation.gramsFor10k)
        assertEquals(17.0, calculation.gramsFor35k)
    }
    
    @Test
    fun `PillCalculation should support equality comparison`() {
        val calc1 = PillCalculation(
            pills10k = 2,
            pills35k = 1,
            gramsFor10k = 5.0,
            gramsFor35k = 17.0,
            pillsPerPackage10k = 4,
            pillsPerPackage35k = 2
        )
        
        val calc2 = PillCalculation(
            pills10k = 2,
            pills35k = 1,
            gramsFor10k = 5.0,
            gramsFor35k = 17.0,
            pillsPerPackage10k = 4,
            pillsPerPackage35k = 2
        )
        
        assertEquals(calc1, calc2)
    }
    
    @Test
    fun `PillCalculation should handle different values`() {
        val calc1 = PillCalculation(
            pills10k = 2,
            pills35k = 1,
            gramsFor10k = 5.0,
            gramsFor35k = 17.0,
            pillsPerPackage10k = 4,
            pillsPerPackage35k = 2
        )
        
        val calc2 = PillCalculation(
            pills10k = 3,
            pills35k = 1,
            gramsFor10k = 5.0,
            gramsFor35k = 17.0,
            pillsPerPackage10k = 4,
            pillsPerPackage35k = 2
        )
        
        assertNotEquals(calc1, calc2)
    }
}