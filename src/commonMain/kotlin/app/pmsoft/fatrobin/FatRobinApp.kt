package app.pmsoft.fatrobin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Visual tuning parameter for connection line vertical positioning (as fraction of field height)
private const val CONNECTION_VERTICAL_OFFSET_FRACTION = 0.07f // Adjust this to fine-tune line positioning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatRobinApp() {
  // Core input
  var fatPer100g by remember { mutableStateOf("") }

  // Direct weight method
  var portionWeight by remember { mutableStateOf("") }

  // Package division method
  var totalPackageWeight by remember { mutableStateOf("") }
  var totalPortions by remember { mutableStateOf("") }

  // Food unit method - direct
  var weightPerFoodUnit by remember { mutableStateOf("") }

  // Food unit method - calculated
  var totalFoodUnits by remember { mutableStateOf("") }

  val fatFieldFocusRequester = remember { FocusRequester() }

  // Connection points for visual connections
  var fatFieldConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var packageWeightConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var portionWeightConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var totalPortionsConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var totalPortionsRightConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var weightPerFoodUnitConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var totalFoodUnitsConnectionPoint by remember { mutableStateOf(Offset.Zero) }
  var totalFoodUnitsRightConnectionPoint by remember { mutableStateOf(Offset.Zero) }

  fun filterNumericInput(input: String): String {
    return input.filter { char ->
      char.isDigit() || char == '.' || char == ',' || char == '-'
    }
  }

  // Create calculator with current input values on every composition
  val calculator = remember(fatPer100g, portionWeight, totalPackageWeight, totalPortions, weightPerFoodUnit, totalFoodUnits) {
    FatRobinCalculator().apply {
      this.fatPer100g = fatPer100g.toDoubleOrNull()?.takeIf { it > 0 }
      this.directWeight = portionWeight.toDoubleOrNull()?.takeIf { it > 0 }
      this.packageWeight = totalPackageWeight.toDoubleOrNull()?.takeIf { it > 0 }
      this.portions = totalPortions.toDoubleOrNull()?.takeIf { it > 0 }
      this.unitWeight = weightPerFoodUnit.toDoubleOrNull()?.takeIf { it > 0 }
      this.foodUnits = totalFoodUnits.toDoubleOrNull()?.takeIf { it > 0 }
    }
  }

  // Define pill doses
  val pillDoses = listOf(10000, 35000)

  fun clearAll() {
    fatPer100g = ""
    portionWeight = ""
    totalPackageWeight = ""
    totalPortions = ""
    weightPerFoodUnit = ""
    totalFoodUnits = ""
    fatFieldFocusRequester.requestFocus()
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background,
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Top components with padding
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Header
        Text(
          text = "FatRobin",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )

        // Clear button
        Button(
          onClick = { clearAll() },
          enabled = fatPer100g.isNotEmpty() || portionWeight.isNotEmpty() ||
            totalPackageWeight.isNotEmpty() || totalPortions.isNotEmpty() ||
            weightPerFoodUnit.isNotEmpty() || totalFoodUnits.isNotEmpty(),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text("Clear All")
        }

        // Results section
        ResultsTable(
          calculator = calculator,
          pillDoses = pillDoses,
        )
      }

      // All inputs with visual connections (no outer padding)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
      ) {
        val leftBranchColor = if (calculator.fatPer100g != null) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.outline
        }
        val rightBranchColor = if (calculator.packageWeight != null) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.outline
        }
        // Visual connection lines
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        ) {
          // Draw connection lines for all fields
          // Unified branching system: left branch controlled by fat input, right branch by package weight

          // Fat field connection (left side, to 16dp from left edge)
          if (fatFieldConnectionPoint != Offset.Zero) {
            val targetX = 16.dp.toPx()

            drawLine(
              color = leftBranchColor,
              start = fatFieldConnectionPoint,
              end = Offset(targetX, fatFieldConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Package weight connection (right side, to 16dp from right edge)
          if (packageWeightConnectionPoint != Offset.Zero) {
            val targetX = size.width - 16.dp.toPx()

            drawLine(
              color = rightBranchColor,
              start = packageWeightConnectionPoint,
              end = Offset(targetX, packageWeightConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Portion weight connection (left side, to 16dp from left edge)
          if (portionWeightConnectionPoint != Offset.Zero) {
            val targetX = 16.dp.toPx()

            drawLine(
              color = leftBranchColor,
              start = portionWeightConnectionPoint,
              end = Offset(targetX, portionWeightConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Total portions connections (needs both fat and package weight)
          if (totalPortionsConnectionPoint != Offset.Zero && totalPortionsRightConnectionPoint != Offset.Zero) {
            // Left connection (from fat branch)
            val leftTargetX = 16.dp.toPx()

            drawLine(
              color = leftBranchColor,
              start = totalPortionsConnectionPoint,
              end = Offset(leftTargetX, totalPortionsConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )

            // Right connection (from package weight branch)
            val rightTargetX = size.width - 16.dp.toPx()

            drawLine(
              color = rightBranchColor,
              start = totalPortionsRightConnectionPoint,
              end = Offset(rightTargetX, totalPortionsRightConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Weight per food unit connection (left side, to 16dp from left edge)
          if (weightPerFoodUnitConnectionPoint != Offset.Zero) {
            val targetX = 16.dp.toPx()

            drawLine(
              color = leftBranchColor,
              start = weightPerFoodUnitConnectionPoint,
              end = Offset(targetX, weightPerFoodUnitConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Total food units connections (needs both fat and package weight)
          if (totalFoodUnitsConnectionPoint != Offset.Zero && totalFoodUnitsRightConnectionPoint != Offset.Zero) {
            // Left connection (from fat branch)
            val leftTargetX = 16.dp.toPx()

            drawLine(
              color = leftBranchColor,
              start = totalFoodUnitsConnectionPoint,
              end = Offset(leftTargetX, totalFoodUnitsConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )

            // Right connection (from package weight branch)
            val rightTargetX = size.width - 16.dp.toPx()

            drawLine(
              color = rightBranchColor,
              start = totalFoodUnitsRightConnectionPoint,
              end = Offset(rightTargetX, totalFoodUnitsRightConnectionPoint.y),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Vertical connection lines

          // Left vertical line: from fat field to total food units (via all left-connected fields)
          if (fatFieldConnectionPoint != Offset.Zero && totalFoodUnitsConnectionPoint != Offset.Zero) {
            val leftX = 16.dp.toPx()
            val startY = fatFieldConnectionPoint.y
            val endY = totalFoodUnitsConnectionPoint.y

            drawLine(
              color = leftBranchColor,
              start = Offset(leftX, startY),
              end = Offset(leftX, endY),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }

          // Right vertical line: from package weight to total food units
          if (packageWeightConnectionPoint != Offset.Zero && totalFoodUnitsRightConnectionPoint != Offset.Zero) {
            val rightX = size.width - 16.dp.toPx()
            val startY = packageWeightConnectionPoint.y
            val endY = totalFoodUnitsRightConnectionPoint.y

            drawLine(
              color = rightBranchColor,
              start = Offset(rightX, startY),
              end = Offset(rightX, endY),
              strokeWidth = 3.dp.toPx(),
              cap = StrokeCap.Round,
            )
          }
        }

        // All input fields in one column
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // Product Information
          Text(
            text = "Product Information",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
          ) {
            OutlinedTextField(
              value = fatPer100g,
              onValueChange = { fatPer100g = filterNumericInput(it) },
              label = { Text("Fat per 100g") },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(fatFieldFocusRequester)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection point with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    fatFieldConnectionPoint = Offset(fieldPosition.x, adjustedY)
                  }
                },
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
          ) {
            OutlinedTextField(
              value = totalPackageWeight,
              onValueChange = { totalPackageWeight = filterNumericInput(it) },
              label = { Text("Package weight (g)") },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection point with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    // Package weight uses right edge for connection
                    val rightX = fieldPosition.x + coordinates.size.width
                    packageWeightConnectionPoint = Offset(rightX, adjustedY)
                  }
                },
            )
          }

          // Method 1: Direct Weight
          Text(
            text = "Direct Weight",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
          ) {
            OutlinedTextField(
              value = portionWeight,
              onValueChange = { portionWeight = filterNumericInput(it) },
              label = { Text("Portion weight (g)") },
              enabled = calculator.fatPer100g != null,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection point with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    // Left edge for connection
                    portionWeightConnectionPoint = Offset(fieldPosition.x, adjustedY)
                  }
                },
            )
          }

          // Method 2: Package Division
          Text(
            text = "Package Division",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
          ) {
            OutlinedTextField(
              value = totalPortions,
              onValueChange = { totalPortions = filterNumericInput(it) },
              label = { Text("Number of sub-units") },
              placeholder = { Text("How many portions in the package?") },
              enabled = calculator.fatPer100g != null && calculator.packageWeight != null,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection points with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    // Left and right connection points for dual-input field
                    totalPortionsConnectionPoint = Offset(fieldPosition.x, adjustedY)
                    val rightX = fieldPosition.x + coordinates.size.width
                    totalPortionsRightConnectionPoint = Offset(rightX, adjustedY)
                  }
                },
            )
          }

          // Method 3: Food Units
          Text(
            text = "Food Units",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
          ) {
            OutlinedTextField(
              value = weightPerFoodUnit,
              onValueChange = { newValue ->
                val filtered = filterNumericInput(newValue)
                weightPerFoodUnit = filtered

                // Update calculator and sync UI if auto-calculated
                val weightValue = filtered.toDoubleOrNull()?.takeIf { it > 0 }
                calculator.unitWeight = weightValue
                calculator.foodUnits?.let { calculatedUnits ->
                  totalFoodUnits = String.format("%.1f", calculatedUnits)
                }
              },
              label = { Text("Weight per food unit (g)") },
              enabled = calculator.fatPer100g != null,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection point with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    // Left edge for connection
                    weightPerFoodUnitConnectionPoint = Offset(fieldPosition.x, adjustedY)
                  }
                },
            )
          }

          Text(
            text = "— OR —",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
          ) {
            OutlinedTextField(
              value = totalFoodUnits,
              onValueChange = { newValue ->
                val filtered = filterNumericInput(newValue)
                totalFoodUnits = filtered

                // Update calculator and sync UI if auto-calculated
                val unitsValue = filtered.toDoubleOrNull()?.takeIf { it > 0 }
                calculator.foodUnits = unitsValue
                calculator.unitWeight?.let { calculatedWeight ->
                  weightPerFoodUnit = String.format("%.2f", calculatedWeight)
                }
              },
              label = { Text("Total food units in package") },
              enabled = calculator.fatPer100g != null && calculator.packageWeight != null,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
              ),
              modifier = Modifier
                .fillMaxWidth(0.8f)
                .onGloballyPositioned { coordinates ->
                  // Get position relative to the Box (Canvas parent)
                  var currentParent = coordinates.parentCoordinates
                  // Go up the hierarchy: TextField -> Row -> Column -> Box
                  repeat(4) {
                    currentParent = currentParent?.parentCoordinates
                  }
                  if (currentParent != null) {
                    val fieldPosition = currentParent.localPositionOf(coordinates, Offset.Zero)

                    // Calculate connection points with proportional tuning offset
                    val fieldCenterY = fieldPosition.y + (coordinates.size.height / 2f)
                    val proportionalOffset = coordinates.size.height * CONNECTION_VERTICAL_OFFSET_FRACTION
                    val adjustedY = fieldCenterY + proportionalOffset
                    // Left and right connection points for dual-input field
                    totalFoodUnitsConnectionPoint = Offset(fieldPosition.x, adjustedY)
                    val rightX = fieldPosition.x + coordinates.size.width
                    totalFoodUnitsRightConnectionPoint = Offset(rightX, adjustedY)
                  }
                },
            )
          }
        }
      }
    }
  }
}

@Composable
fun ResultsTable(
  calculator: FatRobinCalculator,
  pillDoses: List<Int>,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = "Results",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
      )
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Header row - always show all columns
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text("", modifier = Modifier.weight(0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("🍽️", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("g/💊", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("💊/📋", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("💊/📦", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("🍎", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Generate rows for each pill type
        pillDoses.forEachIndexed { index, dose ->
          val pillType = when (dose) {
            10000 -> "10k"
            35000 -> "35k"
            else -> "${dose / 1000}k"
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(pillType, modifier = Modifier.weight(0.6f), fontSize = 12.sp, fontWeight = FontWeight.Medium)

            // Portion pills - show any available calculation (prefer direct weight, then sub-package, then food unit)
            val portionPills = calculator.getPortionPills(pillDoses = pillDoses)?.get(index)
              ?: calculator.getSubPackagePills(pillDoses = pillDoses)?.get(index)
              ?: calculator.getFoodUnitPills(pillDoses = pillDoses)?.get(index)
            Text(portionPills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Grams per pill
            val gramsPerPill = calculator.getGramsPerPill(pillDoses = pillDoses)?.get(index)
            Text(gramsPerPill?.toInt()?.let { "${it}g" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Sub-package pills - show if package division is available
            val subPackagePills = calculator.getSubPackagePills(pillDoses = pillDoses)?.get(index)
            Text(subPackagePills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Package pills (always show)
            val packagePills = calculator.getPackagePills(pillDoses = pillDoses)?.get(index)
            Text(packagePills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Food unit pills (always show)
            val foodUnitPills = calculator.getFoodUnitPills(pillDoses = pillDoses)?.get(index)
            val foodUnitsPerPill = calculator.getFoodUnitsPerPill(pillDoses = pillDoses)?.get(index)

            val foodText = when {
              foodUnitPills == null -> "–"
              foodUnitPills > 1 -> "$foodUnitPills 💊"
              foodUnitPills == 1 -> "1 💊"
              foodUnitsPerPill != null -> "${foodUnitsPerPill.toInt()} 🍎"
              else -> "–"
            }
            Text(foodText, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
