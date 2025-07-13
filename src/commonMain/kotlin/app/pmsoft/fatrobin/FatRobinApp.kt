package app.pmsoft.fatrobin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "FatRobin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Clear button
                Button(
                    onClick = { clearAll() },
                    enabled = fatPer100g.isNotEmpty() || portionWeight.isNotEmpty() ||
                              totalPackageWeight.isNotEmpty() || totalPortions.isNotEmpty() ||
                              weightPerFoodUnit.isNotEmpty() || totalFoodUnits.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All")
                }

                // Results section
                ResultsTable(
                    calculator = calculator,
                    pillDoses = pillDoses
                )

                // Product Information
                MethodCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Product Information",
                    isActive = calculator.fatPer100g != null || calculator.packageWeight != null,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = fatPer100g,
                                onValueChange = { fatPer100g = filterNumericInput(it) },
                                label = { Text("Fat per 100g") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(fatFieldFocusRequester)
                            )
                            OutlinedTextField(
                                value = totalPackageWeight,
                                onValueChange = { totalPackageWeight = filterNumericInput(it) },
                                label = { Text("Package weight (g)") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                )

                // Calculation methods grid
                Text(
                    text = "Choose Your Method",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Fill in fields for any method you prefer. Multiple calculations will show if you provide enough information.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Method cards - vertical layout for better readability
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Method 1: Direct Weight
                    MethodCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Direct Weight",
                        isActive = calculator.fatPer100g != null && calculator.directWeight != null,
                        content = {
                            if (calculator.fatPer100g == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this method",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            OutlinedTextField(
                                value = portionWeight,
                                onValueChange = { portionWeight = filterNumericInput(it) },
                                label = { Text("Portion weight (g)") },
                                enabled = calculator.fatPer100g != null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )

                    // Method 2: Package Division
                    MethodCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Package Division",
                        isActive = calculator.fatPer100g != null && calculator.packageWeight != null && calculator.portions != null,
                        content = {
                            if (calculator.fatPer100g == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this method",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            } else if (calculator.packageWeight == null) {
                                Text(
                                    text = "Enter package weight above to use this method",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            OutlinedTextField(
                                value = totalPortions,
                                onValueChange = { totalPortions = filterNumericInput(it) },
                                label = { Text("Number of sub-units") },
                                placeholder = { Text("How many portions in the package?") },
                                enabled = calculator.fatPer100g != null && calculator.packageWeight != null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )

                    // Method 3: Food Units
                    MethodCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Food Units",
                        isActive = calculator.fatPer100g != null && calculator.effectiveUnitWeight != null,
                        content = {
                            if (calculator.fatPer100g == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this method",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

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
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "OR specify total count:",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (calculator.fatPer100g == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this field",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            } else if (calculator.packageWeight == null) {
                                Text(
                                    text = "Enter package weight above to use this field",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

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
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MethodCard(
    modifier: Modifier = Modifier,
    title: String,
    isActive: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isActive) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            content()
        }
    }
}

@Composable
fun ResultsTable(
    calculator: FatRobinCalculator,
    pillDoses: List<Int>
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Results",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Header row - always show all columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        else -> "${dose/1000}k"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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