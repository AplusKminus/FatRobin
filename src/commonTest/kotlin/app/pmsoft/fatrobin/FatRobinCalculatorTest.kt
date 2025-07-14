package app.pmsoft.fatrobin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FatRobinCalculatorTest {

  private val pillDoses = listOf(10000, 35000)

  private fun freshCalculator() = FatRobinCalculator()

  @Test
  fun `should calculate portion pills correctly for basic case`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.directWeight = 50.0

    val result = calculator.getPortionPills(pillDoses = pillDoses)!!

    // 50g with 10% fat = 5g fat = 10000 units needed
    assertEquals(1, result[0]) // 10k pill: 10000/10000 = 1
    assertEquals(1, result[1]) // 35k pill: 10000/35000 = 0.29, rounds up to 1
  }

  @Test
  fun `should calculate grams per pill correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0

    val result = calculator.getGramsPerPill(pillDoses = pillDoses)!!

    // 10k pill covers: 10000/2000/(10/100) = 50g
    // 35k pill covers: 35000/2000/(10/100) = 175g
    assertEquals(50.0, result[0])
    assertEquals(175.0, result[1])
  }

  @Test
  fun `should calculate sub-package pills correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0
    calculator.portions = 3.0

    val result = calculator.getSubPackagePills(pillDoses = pillDoses)!!

    // Each sub-package: 40g with 4g fat = 8000 units needed
    assertEquals(1, result[0]) // 10k pill: 8000/10000 = 0.8, rounds up to 1
    assertEquals(1, result[1]) // 35k pill: 8000/35000 = 0.23, rounds up to 1
  }

  @Test
  fun `should calculate package pills correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0

    val result = calculator.getPackagePills(pillDoses = pillDoses)!!

    // Entire package: 120g with 12g fat = 24000 units needed
    assertEquals(3, result[0]) // 10k pill: 24000/10000 = 2.4, rounds up to 3
    assertEquals(1, result[1]) // 35k pill: 24000/35000 = 0.69, rounds up to 1
  }

  @Test
  fun `should calculate food item pills correctly with direct food item weight`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.foodItemWeight = 25.0

    val result = calculator.getPillsPerFoodItem(pillDoses = pillDoses)!!

    // Each item: 25g with 2.5g fat = 5000 units needed
    assertEquals(1, result[0]) // 10k pill: 5000/10000 = 0.5, rounds up to 1
    assertEquals(1, result[1]) // 35k pill: 5000/35000 = 0.14, rounds up to 1
  }

  @Test
  fun `should calculate food item pills correctly with calculated food item weight`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0
    calculator.foodItems = 4.0

    val result = calculator.getPillsPerFoodItem(pillDoses = pillDoses)!!

    // Each item: 30g with 3g fat = 6000 units needed
    assertEquals(1, result[0]) // 10k pill: 6000/10000 = 0.6, rounds up to 1
    assertEquals(1, result[1]) // 35k pill: 6000/35000 = 0.17, rounds up to 1
  }

  @Test
  fun `should calculate food items per pill correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.foodItemWeight = 20.0

    val result = calculator.getFoodItemsPerPill(pillDoses = pillDoses)!!

    // Each item: 20g with 2g fat = 4000 units needed
    assertEquals(2, result[0]) // 10k pill covers: 10000/4000 = 2 items (floored)
    assertEquals(8, result[1]) // 35k pill covers: 35000/4000 = 8 items (floored)
  }

  @Test
  fun `should return null when insufficient input for portion pills`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    // Missing directWeight

    val result = calculator.getPortionPills(pillDoses = pillDoses)
    assertNull(result)
  }

  @Test
  fun `should return null when insufficient input for sub-package pills`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0
    // Missing portions

    val result = calculator.getSubPackagePills(pillDoses = pillDoses)
    assertNull(result)
  }

  @Test
  fun `should return null when insufficient input for pills per food item`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    // Missing foodItemWeight or calculated weight

    val result = calculator.getPillsPerFoodItem(pillDoses = pillDoses)
    assertNull(result)
  }

  @Test
  fun `should handle zero fat correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 0.0
    calculator.directWeight = 50.0

    val result = calculator.getPortionPills(pillDoses = pillDoses)!!

    assertEquals(0, result[0])
    assertEquals(0, result[1])
  }

  @Test
  fun `should handle large portions correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 50.0
    calculator.directWeight = 200.0

    val result = calculator.getPortionPills(pillDoses = pillDoses)!!

    // 200g with 50% fat = 100g fat = 200000 units needed
    assertEquals(20, result[0]) // 10k pill: 200000/10000 = 20
    assertEquals(6, result[1]) // 35k pill: 200000/35000 = 5.71, rounds up to 6
  }

  @Test
  fun `should handle decimal values correctly`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 12.5
    calculator.directWeight = 30.0

    val result = calculator.getPortionPills(pillDoses = pillDoses)!!

    // 30g with 12.5% fat = 3.75g fat = 7500 units needed
    assertEquals(1, result[0]) // 10k pill: 7500/10000 = 0.75, rounds up to 1
    assertEquals(1, result[1]) // 35k pill: 7500/35000 = 0.21, rounds up to 1
  }

  @Test
  fun `should ensure rounding is correct for edge cases`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 25.0
    calculator.directWeight = 70.0

    val result = calculator.getPortionPills(pillDoses = pillDoses)!!

    // 70g with 25% fat = 17.5g fat = 35000 units needed
    assertEquals(4, result[0]) // 10k pill: 35000/10000 = 3.5, rounds up to 4
    assertEquals(1, result[1]) // 35k pill: 35000/35000 = 1.0, exact
  }

  @Test
  fun `should calculate effective unit weight correctly`() {
    val calculator = freshCalculator()
    // First test calculated weight
    calculator.packageWeight = 120.0
    calculator.foodItems = 4.0

    assertEquals(30.0, calculator.effectiveFoodItemWeight)

    // Setting foodItemWeight auto-calculates foodItems when packageWeight is available
    calculator.foodItemWeight = 25.0
    assertEquals(4.8, calculator.foodItems) // Auto-calculated: 120.0 / 25.0
    assertEquals(25.0, calculator.effectiveFoodItemWeight) // Uses calculated weight from new foodItems

    // Test direct item weight when no calculation possible
    calculator.clear()
    calculator.foodItemWeight = 25.0
    assertEquals(25.0, calculator.effectiveFoodItemWeight)
  }

  @Test
  fun `should calculate portion weight from package correctly`() {
    val calculator = freshCalculator()
    calculator.packageWeight = 120.0
    calculator.portions = 3.0

    assertEquals(40.0, calculator.portionWeightFromPackage)
  }

  @Test
  fun `should auto-calculate foodItems when foodItemWeight is set`() {
    val calculator = freshCalculator()
    calculator.packageWeight = 120.0
    calculator.foodItemWeight = 30.0

    assertEquals(4.0, calculator.foodItems) // Auto-calculated: 120.0 / 30.0
  }

  @Test
  fun `should auto-calculate foodItemWeight when foodItems is set`() {
    val calculator = freshCalculator()
    calculator.packageWeight = 120.0
    calculator.foodItems = 4.0

    assertEquals(30.0, calculator.foodItemWeight) // Auto-calculated: 120.0 / 4.0
  }

  @Test
  fun `should not auto-calculate when packageWeight is null`() {
    val calculator = freshCalculator()
    calculator.foodItemWeight = 30.0

    assertNull(calculator.foodItems) // No auto-calculation without packageWeight

    calculator.foodItems = 4.0

    assertEquals(30.0, calculator.foodItemWeight) // Original value preserved, no auto-calculation
  }

  @Test
  fun `should not auto-calculate when values are zero or negative`() {
    val calculator = freshCalculator()
    calculator.packageWeight = 120.0
    calculator.foodItemWeight = 0.0

    assertNull(calculator.foodItems)

    calculator.foodItemWeight = -5.0

    assertNull(calculator.foodItems)
  }

  @Test
  fun `should calculate sub-package pills when all required inputs are provided`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0
    calculator.portions = 3.0

    val result = calculator.getSubPackagePills(pillDoses = pillDoses)

    assertNotNull(result)
    assertEquals(1, result[0]) // 10k pill
    assertEquals(1, result[1]) // 35k pill
  }

  @Test
  fun `should return null for sub-package pills when package weight is missing`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.portions = 3.0
    // Missing packageWeight

    val result = calculator.getSubPackagePills(pillDoses = pillDoses)

    assertNull(result)
  }

  @Test
  fun `should return null for sub-package pills when portions is missing`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    calculator.packageWeight = 120.0
    // Missing portions

    val result = calculator.getSubPackagePills(pillDoses = pillDoses)

    assertNull(result)
  }

  @Test
  fun `should calculate grams per pill with only fat content`() {
    val calculator = freshCalculator()
    calculator.fatPer100g = 10.0
    // No other data needed

    val result = calculator.getGramsPerPill(pillDoses = pillDoses)

    assertNotNull(result)
    assertEquals(50.0, result[0]) // 10k pill covers 50g
    assertEquals(175.0, result[1]) // 35k pill covers 175g
  }
}
