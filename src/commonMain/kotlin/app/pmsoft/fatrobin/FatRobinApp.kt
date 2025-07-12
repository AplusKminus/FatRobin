package app.pmsoft.fatrobin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pmsoft.fatrobin.ui.MultipleChoiceButtonGroup
import app.pmsoft.fatrobin.ui.MultipleChoiceButtonOption

enum class PortionMode {
    BY_WEIGHT, BY_SUB_PACKAGING_UNIT, BY_FOOD_UNIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatRobinApp() {
    var fatPer100g by remember { mutableStateOf("") }
    var totalPackageWeight by remember { mutableStateOf("") }
    var portionWeight by remember { mutableStateOf("") }
    var totalPortions by remember { mutableStateOf("") }
    var weightPerFoodUnit by remember { mutableStateOf("") }
    var portionMode by remember { mutableStateOf(PortionMode.BY_WEIGHT) }
    
    val calculator = remember { FatRobinCalculator() }
    val focusManager = LocalFocusManager.current
    val firstFieldFocusRequester = remember { FocusRequester() }
    
    fun filterNumericInput(input: String): String {
        return input.filter { char ->
            char.isDigit() || char == '.' || char == ',' || char == '-'
        }
    }
    
    val fat = fatPer100g.toDoubleOrNull()
    val totalWeight = totalPackageWeight.toDoubleOrNull()
    val portion = portionWeight.toDoubleOrNull()
    val totalPrt = totalPortions.toDoubleOrNull()
    val weightPerUnit = weightPerFoodUnit.toDoubleOrNull()
    
    val calculation = remember(fat, totalWeight, portion, totalPrt, weightPerUnit, portionMode) {
        try {
            if (fat != null) {
                when (portionMode) {
                    PortionMode.BY_WEIGHT -> {
                        if (portion != null) {
                            // For by weight, we don't need total package weight, so use portion weight as total
                            calculator.calculatePillsNeededByWeight(fat, portion, portion)
                        } else null
                    }
                    PortionMode.BY_SUB_PACKAGING_UNIT -> {
                        if (totalWeight != null && totalPrt != null) {
                            calculator.calculatePillsNeededByCount(fat, totalWeight, totalPrt)
                        } else null
                    }
                    PortionMode.BY_FOOD_UNIT -> {
                        if (weightPerUnit != null) {
                            calculator.calculatePillsNeededByWeight(fat, weightPerUnit, weightPerUnit)
                        } else null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    
    fun clearAll() {
        fatPer100g = ""
        totalPackageWeight = ""
        portionWeight = ""
        totalPortions = ""
        weightPerFoodUnit = ""
        firstFieldFocusRequester.requestFocus()
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "FatRobin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Button(
                    onClick = { clearAll() },
                    enabled = fatPer100g.isNotEmpty() || totalPackageWeight.isNotEmpty() || portionWeight.isNotEmpty() || totalPortions.isNotEmpty() || weightPerFoodUnit.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear")
                }
                
                OutlinedTextField(
                    value = fatPer100g,
                    onValueChange = { fatPer100g = filterNumericInput(it) },
                    label = { Text("Fat per 100g") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(firstFieldFocusRequester)
                )
                
                Text(
                    text = "Portion Size Method",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                MultipleChoiceButtonGroup(
                    selectedOption = portionMode,
                    options = listOf(
                        MultipleChoiceButtonOption(PortionMode.BY_WEIGHT, "Weight"),
                        MultipleChoiceButtonOption(PortionMode.BY_SUB_PACKAGING_UNIT, "Sub-packaging Unit"),
                        MultipleChoiceButtonOption(PortionMode.BY_FOOD_UNIT, "Food Unit")
                    ),
                    onSelectionChanged = { portionMode = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                when (portionMode) {
                    PortionMode.BY_WEIGHT -> {
                        OutlinedTextField(
                            value = portionWeight,
                            onValueChange = { portionWeight = filterNumericInput(it) },
                            label = { Text("Portion weight (g)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PortionMode.BY_SUB_PACKAGING_UNIT -> {
                        OutlinedTextField(
                            value = totalPortions,
                            onValueChange = { totalPortions = filterNumericInput(it) },
                            label = { Text("Total sub-packaging units in package") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        OutlinedTextField(
                            value = totalPackageWeight,
                            onValueChange = { totalPackageWeight = filterNumericInput(it) },
                            label = { Text("Total package weight (g)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PortionMode.BY_FOOD_UNIT -> {
                        OutlinedTextField(
                            value = weightPerFoodUnit,
                            onValueChange = { weightPerFoodUnit = filterNumericInput(it) },
                            label = { Text("Weight per food unit (g)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (portionMode == PortionMode.BY_FOOD_UNIT) {
                            // Special display for food unit mode
                            calculation?.let { calc ->
                                if (fat != null && weightPerUnit != null) {
                                    // Calculate actual fractional pills needed per food unit
                                    val fatInFoodUnit = (fat / 100.0) * weightPerUnit
                                    val unitsNeededPerFoodUnit = fatInFoodUnit * 2000.0 // UNITS_PER_GRAM_FAT
                                    
                                    val actualPills10kPerUnit = unitsNeededPerFoodUnit / 10000.0 // UNITS_10K_PILL
                                    val actualPills35kPerUnit = unitsNeededPerFoodUnit / 35000.0 // UNITS_35K_PILL
                                    
                                    // For 10k pills
                                    if (actualPills10kPerUnit > 1) {
                                        Text("10k units: ${calc.pills10k} pills per food unit")
                                    } else if (actualPills10kPerUnit == 1.0) {
                                        Text("10k units: 1 pill per food unit")
                                    } else {
                                        // Calculate how many food units one pill covers
                                        val unitsPerPill = (1.0 / actualPills10kPerUnit).toInt()
                                        Text("10k units: $unitsPerPill food units per pill")
                                    }
                                    
                                    // For 35k pills  
                                    if (actualPills35kPerUnit > 1) {
                                        Text("35k units: ${calc.pills35k} pills per food unit")
                                    } else if (actualPills35kPerUnit == 1.0) {
                                        Text("35k units: 1 pill per food unit")
                                    } else {
                                        // Calculate how many food units one pill covers
                                        val unitsPerPill = (1.0 / actualPills35kPerUnit).toInt()
                                        Text("35k units: $unitsPerPill food units per pill")
                                    }
                                } else {
                                    Text("10k units: --")
                                    Text("35k units: --")
                                }
                            } ?: run {
                                Text("10k units: --")
                                Text("35k units: --")
                            }
                        } else {
                            // Normal display for other modes
                            Text(
                                text = when (portionMode) {
                                    PortionMode.BY_SUB_PACKAGING_UNIT -> "Pills Per Sub-packaging Unit:"
                                    else -> "Pills Needed For Portion:"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text("10k units: ${calculation?.let { "${it.pills10k} pills" } ?: "--"}")
                            Text("35k units: ${calculation?.let { "${it.pills35k} pills" } ?: "--"}")
                            
                            // Only show "Pills Needed for Entire Package" for BY_SUB_PACKAGING_UNIT mode
                            if (portionMode == PortionMode.BY_SUB_PACKAGING_UNIT) {
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Pills Needed For Entire Package:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text("10k units: ${calculation?.let { "${it.pillsPerPackage10k} pills" } ?: "--"}")
                                Text("35k units: ${calculation?.let { "${it.pillsPerPackage35k} pills" } ?: "--"}")
                                
                                Spacer(modifier = Modifier.height(8.dp))
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // Only show "Grams Per Pill" for BY_WEIGHT mode
                            if (portionMode == PortionMode.BY_WEIGHT) {
                                Text(
                                    text = "Grams Per Pill:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text("10k pill covers: ${calculation?.let { "${it.gramsFor10k}g of product" } ?: "--"}")
                                Text("35k pill covers: ${calculation?.let { "${it.gramsFor35k}g of product" } ?: "--"}")
                            }
                        }
                    }
                }
            }
        }
    }
}