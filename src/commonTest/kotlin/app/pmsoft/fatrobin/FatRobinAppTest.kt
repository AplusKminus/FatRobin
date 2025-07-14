package app.pmsoft.fatrobin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FatRobinAppTest {

  @Test
  fun `FatRobinCalculator should store input values correctly`() {
    val calculator = FatRobinCalculator()
    calculator.fatPer100g = 10.0
    calculator.directWeight = 50.0
    calculator.packageWeight = 100.0
    calculator.portions = 2.0

    assertEquals(10.0, calculator.fatPer100g)
    assertEquals(50.0, calculator.directWeight)
    assertEquals(100.0, calculator.packageWeight)
    assertEquals(2.0, calculator.portions)
  }

  @Test
  fun `FatRobinCalculator should have correct calculation availability flags`() {
    val calculator = FatRobinCalculator()

    // Initially nothing available
    assertFalse(calculator.hasDirectWeight)
    assertFalse(calculator.hasPackageDivision)
    assertFalse(calculator.hasFoodItem)
    assertFalse(calculator.hasPackage)

    // With fat only
    calculator.fatPer100g = 10.0
    assertFalse(calculator.hasDirectWeight)
    assertFalse(calculator.hasPackageDivision)
    assertFalse(calculator.hasFoodItem)
    assertFalse(calculator.hasPackage)

    // With fat and direct weight
    calculator.directWeight = 50.0
    assertTrue(calculator.hasDirectWeight)
    assertFalse(calculator.hasPackageDivision)
    assertFalse(calculator.hasFoodItem)
    assertFalse(calculator.hasPackage)

    // With fat, package weight and portions
    calculator.packageWeight = 100.0
    calculator.portions = 2.0
    assertTrue(calculator.hasDirectWeight)
    assertTrue(calculator.hasPackageDivision)
    assertFalse(calculator.hasFoodItem)
    assertTrue(calculator.hasPackage)

    // With food item weight
    calculator.foodItemWeight = 25.0
    assertTrue(calculator.hasDirectWeight)
    assertTrue(calculator.hasPackageDivision)
    assertTrue(calculator.hasFoodItem)
    assertTrue(calculator.hasPackage)
  }

  @Test
  fun `FatRobinCalculator clear should reset all values`() {
    val calculator = FatRobinCalculator()
    calculator.fatPer100g = 10.0
    calculator.directWeight = 50.0
    calculator.packageWeight = 100.0
    calculator.portions = 2.0
    calculator.foodItemWeight = 25.0
    calculator.foodItems = 4.0

    calculator.clear()

    assertNull(calculator.fatPer100g)
    assertNull(calculator.directWeight)
    assertNull(calculator.packageWeight)
    assertNull(calculator.portions)
    assertNull(calculator.foodItemWeight)
    assertNull(calculator.foodItems)
  }
}
