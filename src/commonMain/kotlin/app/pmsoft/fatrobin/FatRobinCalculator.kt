package app.pmsoft.fatrobin

import kotlin.math.ceil
import kotlin.math.floor

data class PillCalculation(
    val pills10k: Int,
    val pills35k: Int,
    val gramsFor10k: Double,
    val gramsFor35k: Double,
    val pillsPerPackage10k: Int,
    val pillsPerPackage35k: Int
)

class FatRobinCalculator {
    companion object {
        private const val UNITS_PER_GRAM_FAT = 2000.0
        private const val UNITS_10K_PILL = 10000.0
        private const val UNITS_35K_PILL = 35000.0
    }
    
    fun calculatePillsNeededByWeight(
        fatPer100g: Double,
        totalPackageWeight: Double,
        portionWeight: Double
    ): PillCalculation {
        require(fatPer100g >= 0) { "Fat per 100g must be non-negative" }
        require(totalPackageWeight > 0) { "Total package weight must be positive" }
        require(portionWeight > 0) { "Portion weight must be positive" }
        require(portionWeight <= totalPackageWeight) { "Portion weight cannot exceed total package weight" }
        
        val fatInPortion = (fatPer100g / 100.0) * portionWeight
        val unitsNeeded = fatInPortion * UNITS_PER_GRAM_FAT
        
        val pills10k = ceil(unitsNeeded / UNITS_10K_PILL).toInt()
        val pills35k = ceil(unitsNeeded / UNITS_35K_PILL).toInt()
        
        val gramsFor10k = floor(UNITS_10K_PILL / UNITS_PER_GRAM_FAT)
        val gramsFor35k = floor(UNITS_35K_PILL / UNITS_PER_GRAM_FAT)
        
        // Calculate pills needed for entire package
        val fatInPackage = (fatPer100g / 100.0) * totalPackageWeight
        val unitsNeededForPackage = fatInPackage * UNITS_PER_GRAM_FAT
        val pillsPerPackage10k = ceil(unitsNeededForPackage / UNITS_10K_PILL).toInt()
        val pillsPerPackage35k = ceil(unitsNeededForPackage / UNITS_35K_PILL).toInt()
        
        return PillCalculation(
            pills10k = pills10k,
            pills35k = pills35k,
            gramsFor10k = gramsFor10k,
            gramsFor35k = gramsFor35k,
            pillsPerPackage10k = pillsPerPackage10k,
            pillsPerPackage35k = pillsPerPackage35k
        )
    }
    
    fun calculatePillsNeededByCount(
        fatPer100g: Double,
        totalPackageWeight: Double,
        totalPortionsInPackage: Double
    ): PillCalculation {
        require(fatPer100g >= 0) { "Fat per 100g must be non-negative" }
        require(totalPackageWeight > 0) { "Total package weight must be positive" }
        require(totalPortionsInPackage > 0) { "Total portions in package must be positive" }
        
        val portionWeight = totalPackageWeight / totalPortionsInPackage
        
        return calculatePillsNeededByWeight(fatPer100g, totalPackageWeight, portionWeight)
    }
}