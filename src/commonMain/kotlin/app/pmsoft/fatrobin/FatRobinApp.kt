package app.pmsoft.fatrobin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    
    val calculator = remember { FatRobinCalculator() }
    val fatFieldFocusRequester = remember { FocusRequester() }
    
    fun filterNumericInput(input: String): String {
        return input.filter { char ->
            char.isDigit() || char == '.' || char == ',' || char == '-'
        }
    }
    
    // Parse all inputs
    val fat = fatPer100g.toDoubleOrNull()
    val directWeight = portionWeight.toDoubleOrNull()
    val packageWeight = totalPackageWeight.toDoubleOrNull()
    val portions = totalPortions.toDoubleOrNull()
    val unitWeight = weightPerFoodUnit.toDoubleOrNull()
    val foodUnits = totalFoodUnits.toDoubleOrNull()
    
    // Smart food unit calculation - calculate missing field from the other two
    val calculatedWeightPerUnit = if (foodUnits != null && packageWeight != null && foodUnits > 0) {
        packageWeight / foodUnits
    } else null
    
    val effectiveUnitWeight = calculatedWeightPerUnit ?: unitWeight
    
    // Determine which calculation methods are available
    data class CalculationResult(
        val type: String,
        val calculation: PillCalculation,
        val description: String
    )
    
    val availableCalculations = remember(fat, directWeight, packageWeight, portions, effectiveUnitWeight) {
        mutableListOf<CalculationResult>().apply {
            if (fat != null) {
                // Method 1: Direct weight
                if (directWeight != null) {
                    try {
                        val calc = calculator.calculatePillsNeededByWeight(fat, directWeight, directWeight)
                        add(CalculationResult("direct", calc, "For ${directWeight}g portion"))
                    } catch (_: Exception) { }
                }
                
                // Method 2: Package division
                if (packageWeight != null && portions != null && portions > 0) {
                    try {
                        val calc = calculator.calculatePillsNeededByCount(fat, packageWeight, portions)
                        add(CalculationResult("package", calc, "Per sub-unit (${String.format("%.1f", packageWeight/portions)}g each)"))
                    } catch (_: Exception) { }
                }
                
                // Method 3: Food units
                if (effectiveUnitWeight != null) {
                    try {
                        val calc = calculator.calculatePillsNeededByWeight(fat, effectiveUnitWeight, effectiveUnitWeight)
                        val source = if (calculatedWeightPerUnit != null) "calculated" else "direct"
                        add(CalculationResult("food_unit", calc, "Per food unit (${String.format("%.2f", effectiveUnitWeight)}g each, $source)"))
                    } catch (_: Exception) { }
                }
            }
        }
    }
    
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
                
                // Core input - always visible and prominent
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fatPer100g,
                            onValueChange = { fatPer100g = filterNumericInput(it) },
                            label = { Text("Fat per 100g") },
                            placeholder = { Text("Required for all calculations") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(fatFieldFocusRequester)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = totalPackageWeight,
                            onValueChange = { totalPackageWeight = filterNumericInput(it) },
                            label = { Text("Package weight (g)") },
                            placeholder = { Text("Optional - used for package division and food unit calculations") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
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
                        isActive = fat != null && directWeight != null,
                        content = {
                            if (fat == null) {
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
                                enabled = fat != null,
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
                        isActive = fat != null && packageWeight != null && portions != null,
                        content = {
                            if (fat == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this method",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            } else if (packageWeight == null) {
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
                                enabled = fat != null && packageWeight != null,
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
                        isActive = fat != null && effectiveUnitWeight != null,
                        content = {
                            if (fat == null) {
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
                                    
                                    // Auto-update total units when weight per unit changes
                                    val weightValue = filtered.toDoubleOrNull()
                                    if (weightValue != null && weightValue > 0 && packageWeight != null) {
                                        val calculatedUnits = packageWeight / weightValue
                                        totalFoodUnits = String.format("%.1f", calculatedUnits)
                                    }
                                },
                                label = { Text("Weight per food unit (g)") },
                                enabled = fat != null,
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
                            
                            if (fat == null) {
                                Text(
                                    text = "Enter fat per 100g above to use this field",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            } else if (packageWeight == null) {
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
                                    
                                    // Auto-update weight per unit when total units changes
                                    val unitsValue = filtered.toDoubleOrNull()
                                    if (unitsValue != null && unitsValue > 0 && packageWeight != null) {
                                        val calculatedWeight = packageWeight / unitsValue
                                        weightPerFoodUnit = String.format("%.2f", calculatedWeight)
                                    }
                                },
                                label = { Text("Total food units in package") },
                                enabled = fat != null && packageWeight != null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
                
                // Results section
                if (availableCalculations.isNotEmpty() || fat != null) {
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
                            
                            if (availableCalculations.isEmpty()) {
                                Text(
                                    text = if (fat == null) "Enter fat content to see calculations" else "Complete any calculation method above to see results",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            } else {
                                availableCalculations.forEach { result ->
                                    ResultSection(
                                        title = result.description,
                                        calculation = result.calculation,
                                        type = result.type,
                                        fat = fat,
                                        effectiveWeight = when(result.type) {
                                            "food_unit" -> effectiveUnitWeight
                                            "direct" -> directWeight
                                            "package" -> if (packageWeight != null && portions != null && portions > 0) packageWeight / portions else null
                                            else -> null
                                        }
                                    )
                                    
                                    if (result != availableCalculations.last()) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
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
fun ResultSection(
    title: String,
    calculation: PillCalculation,
    type: String,
    fat: Double? = null,
    effectiveWeight: Double? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (type == "food_unit" && fat != null && effectiveWeight != null) {
            // Special food unit display logic with smart ratios
            val fatInFoodUnit = (fat / 100.0) * effectiveWeight
            val unitsNeeded = fatInFoodUnit * 2000.0
            
            val actualPills10k = unitsNeeded / 10000.0
            val actualPills35k = unitsNeeded / 35000.0
            
            // For 10k pills
            if (actualPills10k > 1) {
                Text("10k units: ${calculation.pills10k} pills per food unit")
            } else if (actualPills10k == 1.0) {
                Text("10k units: 1 pill per food unit")
            } else {
                val unitsPerPill = (1.0 / actualPills10k).toInt()
                Text("10k units: $unitsPerPill food units per pill")
            }
            
            // For 35k pills
            if (actualPills35k > 1) {
                Text("35k units: ${calculation.pills35k} pills per food unit")
            } else if (actualPills35k == 1.0) {
                Text("35k units: 1 pill per food unit")
            } else {
                val unitsPerPill = (1.0 / actualPills35k).toInt()
                Text("35k units: $unitsPerPill food units per pill")
            }
        } else {
            Text("10k units: ${calculation.pills10k} ${if (calculation.pills10k == 1) "pill" else "pills"}")
            Text("35k units: ${calculation.pills35k} ${if (calculation.pills35k == 1) "pill" else "pills"}")
            
            if (type == "package") {
                Text(
                    text = "For entire package:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text("10k units: ${calculation.pillsPerPackage10k} ${if (calculation.pillsPerPackage10k == 1) "pill" else "pills"}")
                Text("35k units: ${calculation.pillsPerPackage35k} ${if (calculation.pillsPerPackage35k == 1) "pill" else "pills"}")
            }
            
            if (type == "direct") {
                Text(
                    text = "Coverage per pill:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text("10k pill covers: ${calculation.gramsFor10k}g of product")
                Text("35k pill covers: ${calculation.gramsFor35k}g of product")
            }
        }
    }
}