package app.pmsoft.fatrobin

import kotlin.math.ceil
import kotlin.math.floor

/**
 * Unified calculator for FatRobin pill calculations.
 * Contains all input values as nullable vars and provides calculated outputs via getters.
 */
class FatRobinCalculator {
  // Input variables
  var fatPer100g: Double? = null
  var directWeight: Double? = null
  var packageWeight: Double? = null
  var portions: Double? = null

  // Private backing fields for interdependent properties
  private var _foodItemWeight: Double? = null
  private var _foodItems: Double? = null

  var foodItemWeight: Double?
    get() = _foodItemWeight
    set(value) {
      _foodItemWeight = value
      // Auto-calculate foodItems if packageWeight is available
      if (value != null && value > 0 && packageWeight != null && packageWeight!! > 0) {
        _foodItems = packageWeight!! / value
      }
    }

  var foodItems: Double?
    get() = _foodItems
    set(value) {
      _foodItems = value
      // Auto-calculate foodItemWeight if packageWeight is available
      if (value != null && value > 0 && packageWeight != null && packageWeight!! > 0) {
        _foodItemWeight = packageWeight!! / value
      }
    }

  // Calculated properties
  val effectiveFoodItemWeight: Double?
    get() = if (packageWeight != null && foodItems != null && foodItems!! > 0) {
      packageWeight!! / foodItems!!
    } else {
      foodItemWeight
    }

  val portionWeightFromPackage: Double?
    get() = if (packageWeight != null && portions != null && portions!! > 0) {
      packageWeight!! / portions!!
    } else {
      null
    }

  /**
   * Get pills needed for current portion/serving
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units (e.g., [10000, 35000])
   * @return List of pills needed for each dose, or null if insufficient input
   */
  fun getPortionPills(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Int>? {
    val fat = fatPer100g ?: return null
    val weight = directWeight ?: return null

    val fatInPortion = (fat / 100.0) * weight
    val unitsNeeded = fatInPortion * dosingFactor

    return pillDoses.map { dose ->
      ceil(unitsNeeded / dose).toInt()
    }
  }

  /**
   * Get grams of product covered by each pill type
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units
   * @return List of grams covered per pill, or null if insufficient input
   */
  fun getGramsPerPill(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Double>? {
    val fat = fatPer100g ?: return null

    return pillDoses.map { dose ->
      floor((dose / dosingFactor) / (fat / 100.0))
    }
  }

  /**
   * Get pills needed for sub-packaging unit (portion from package division)
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units
   * @return List of pills needed per sub-package, or null if insufficient input
   */
  fun getSubPackagePills(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Int>? {
    val fat = fatPer100g ?: return null
    val weight = portionWeightFromPackage ?: return null

    val fatInPortion = (fat / 100.0) * weight
    val unitsNeeded = fatInPortion * dosingFactor

    return pillDoses.map { dose ->
      ceil(unitsNeeded / dose).toInt()
    }
  }

  /**
   * Get pills needed for entire package
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units
   * @return List of pills needed for entire package, or null if insufficient input
   */
  fun getPackagePills(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Int>? {
    val fat = fatPer100g ?: return null
    val weight = packageWeight ?: return null

    val fatInPackage = (fat / 100.0) * weight
    val unitsNeeded = fatInPackage * dosingFactor

    return pillDoses.map { dose ->
      ceil(unitsNeeded / dose).toInt()
    }
  }

  /**
   * Get pills needed per food item
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units
   * @return List of pills needed per food item, or null if insufficient input
   */
  fun getPillsPerFoodItem(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Int>? {
    val fat = fatPer100g ?: return null
    val weight = effectiveFoodItemWeight ?: return null

    val fatInItem = (fat / 100.0) * weight
    val unitsNeeded = fatInItem * dosingFactor

    return pillDoses.map { dose ->
      ceil(unitsNeeded / dose).toInt()
    }
  }

  /**
   * Get food items covered by each pill (inverse of getPillsPerFoodItem for ratios < 1)
   * @param dosingFactor Units per gram of fat (default 2000)
   * @param pillDoses List of pill doses in units
   * @return List of food items covered per pill, or null if insufficient input
   */
  fun getFoodItemsPerPill(dosingFactor: Double = 2000.0, pillDoses: List<Int>): List<Int>? {
    val fat = fatPer100g ?: return null
    val weight = effectiveFoodItemWeight ?: return null

    val fatInItem = (fat / 100.0) * weight
    val unitsNeeded = fatInItem * dosingFactor

    return pillDoses.map { dose ->
      floor(dose / unitsNeeded).toInt()
    }
  }

  /**
   * Check if direct weight calculation is possible
   */
  val hasDirectWeight: Boolean
    get() = fatPer100g != null && directWeight != null

  /**
   * Check if package division calculation is possible
   */
  val hasPackageDivision: Boolean
    get() = fatPer100g != null && packageWeight != null && portions != null && portions!! > 0

  /**
   * Check if food item calculation is possible
   */
  val hasFoodItem: Boolean
    get() = fatPer100g != null && effectiveFoodItemWeight != null

  /**
   * Check if package calculation is possible
   */
  val hasPackage: Boolean
    get() = fatPer100g != null && packageWeight != null

  /**
   * Get description for package division method
   */
  val packageDivisionDescription: String?
    get() = portionWeightFromPackage?.let { weight ->
      "Per sub-unit (${String.format("%.1f", weight)}g each)"
    }

  /**
   * Get description for food item method
   */
  val foodItemDescription: String?
    get() = effectiveFoodItemWeight?.let { weight ->
      val source = if (packageWeight != null && foodItems != null && foodItems!! > 0) "calculated" else "direct"
      "Per food item (${String.format("%.2f", weight)}g each, $source)"
    }

  /**
   * Get description for direct weight method
   */
  val directWeightDescription: String?
    get() = directWeight?.let { weight ->
      "Direct weight (${String.format("%.1f", weight)}g)"
    }

  /**
   * Clear all input values
   */
  fun clear() {
    fatPer100g = null
    directWeight = null
    packageWeight = null
    portions = null
    _foodItemWeight = null
    _foodItems = null
  }
}
