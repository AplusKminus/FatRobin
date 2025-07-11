package com.fatrobin

import kotlin.test.*

class FatRobinCalculatorTest {
    
    private val calculator = FatRobinCalculator()
    
    @Test
    fun `should calculate pills needed correctly for basic case`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 10.0,
            totalPackageWeight = 100.0,
            portionWeight = 50.0
        )
        
        val fatInPortion = 5.0
        val unitsNeeded = 10000.0
        
        assertEquals(1, result.pills10k)
        assertEquals(1, result.pills35k)
        assertEquals(5.0, result.gramsFor10k)
        assertEquals(17.0, result.gramsFor35k)
    }
    
    @Test
    fun `should round up pills needed when not exact`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 15.0,
            totalPackageWeight = 100.0,
            portionWeight = 40.0
        )
        
        val fatInPortion = 6.0
        val unitsNeeded = 12000.0
        
        assertEquals(2, result.pills10k)
        assertEquals(1, result.pills35k)
    }
    
    @Test
    fun `should handle zero fat correctly`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 0.0,
            totalPackageWeight = 100.0,
            portionWeight = 50.0
        )
        
        assertEquals(0, result.pills10k)
        assertEquals(0, result.pills35k)
    }
    
    @Test
    fun `should handle small portions correctly`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 20.0,
            totalPackageWeight = 100.0,
            portionWeight = 10.0
        )
        
        val fatInPortion = 2.0
        val unitsNeeded = 4000.0
        
        assertEquals(1, result.pills10k)
        assertEquals(1, result.pills35k)
    }
    
    @Test
    fun `should handle large portions correctly`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 50.0,
            totalPackageWeight = 200.0,
            portionWeight = 200.0
        )
        
        val fatInPortion = 100.0
        val unitsNeeded = 200000.0
        
        assertEquals(20, result.pills10k)
        assertEquals(6, result.pills35k)
    }
    
    @Test
    fun `should calculate grams per pill correctly`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 10.0,
            totalPackageWeight = 100.0,
            portionWeight = 50.0
        )
        
        assertEquals(5.0, result.gramsFor10k)
        assertEquals(17.0, result.gramsFor35k)
    }
    
    @Test
    fun `should throw exception for negative fat`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = -1.0,
                totalPackageWeight = 100.0,
                portionWeight = 50.0
            )
        }
    }
    
    @Test
    fun `should throw exception for zero total weight`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = 10.0,
                totalPackageWeight = 0.0,
                portionWeight = 50.0
            )
        }
    }
    
    @Test
    fun `should throw exception for negative total weight`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = 10.0,
                totalPackageWeight = -10.0,
                portionWeight = 50.0
            )
        }
    }
    
    @Test
    fun `should throw exception for zero portion weight`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = 10.0,
                totalPackageWeight = 100.0,
                portionWeight = 0.0
            )
        }
    }
    
    @Test
    fun `should throw exception for negative portion weight`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = 10.0,
                totalPackageWeight = 100.0,
                portionWeight = -10.0
            )
        }
    }
    
    @Test
    fun `should throw exception when portion weight exceeds total weight`() {
        assertFailsWith<IllegalArgumentException> {
            calculator.calculatePillsNeeded(
                fatPer100g = 10.0,
                totalPackageWeight = 100.0,
                portionWeight = 150.0
            )
        }
    }
    
    @Test
    fun `should handle decimal values correctly`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 12.5,
            totalPackageWeight = 80.0,
            portionWeight = 30.0
        )
        
        val fatInPortion = 3.75
        val unitsNeeded = 7500.0
        
        assertEquals(1, result.pills10k)
        assertEquals(1, result.pills35k)
    }
    
    @Test
    fun `should handle edge case requiring multiple 35k pills`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 80.0,
            totalPackageWeight = 100.0,
            portionWeight = 100.0
        )
        
        val fatInPortion = 80.0
        val unitsNeeded = 160000.0
        
        assertEquals(16, result.pills10k)
        assertEquals(5, result.pills35k)
    }
    
    @Test
    fun `should ensure rounding is correct for edge cases`() {
        val result = calculator.calculatePillsNeeded(
            fatPer100g = 25.0,
            totalPackageWeight = 100.0,
            portionWeight = 70.0
        )
        
        val fatInPortion = 17.5
        val unitsNeeded = 35000.0
        
        assertEquals(4, result.pills10k)
        assertEquals(1, result.pills35k)
    }
}