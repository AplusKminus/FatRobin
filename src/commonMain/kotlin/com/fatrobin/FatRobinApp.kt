package com.fatrobin

import androidx.compose.foundation.layout.*
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
import com.fatrobin.ui.MultipleChoiceButtonGroup
import com.fatrobin.ui.MultipleChoiceButtonOption

enum class PortionMode {
    BY_WEIGHT, BY_COUNT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatRobinApp() {
    var fatPer100g by remember { mutableStateOf("") }
    var totalPackageWeight by remember { mutableStateOf("") }
    var portionWeight by remember { mutableStateOf("") }
    var totalPortions by remember { mutableStateOf("") }
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
    
    val calculation = remember(fat, totalWeight, portion, totalPrt, portionMode) {
        try {
            if (fat != null) {
                when (portionMode) {
                    PortionMode.BY_WEIGHT -> {
                        if (portion != null) {
                            // For by weight, we don't need total package weight, so use portion weight as total
                            calculator.calculatePillsNeededByWeight(fat, portion, portion)
                        } else null
                    }
                    PortionMode.BY_COUNT -> {
                        if (totalWeight != null && totalPrt != null) {
                            calculator.calculatePillsNeededByCount(fat, totalWeight, totalPrt)
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
    
    val errorMessage = remember(fat, totalWeight, portion, totalPrt, portionMode, calculation) {
        when {
            fat == null && fatPer100g.isNotEmpty() -> "Please enter a valid number for fat per 100g"
            portionMode == PortionMode.BY_COUNT && totalWeight == null && totalPackageWeight.isNotEmpty() -> "Please enter a valid number for total package weight"
            portionMode == PortionMode.BY_WEIGHT && portion == null && portionWeight.isNotEmpty() -> "Please enter a valid number for portion weight"
            portionMode == PortionMode.BY_COUNT && totalPrt == null && totalPortions.isNotEmpty() -> "Please enter a valid number for total portions"
            fat != null && calculation == null -> "Invalid input values"
            else -> {
                val missing = mutableListOf<String>()
                if (fat == null) missing.add("fat per 100g")
                when (portionMode) {
                    PortionMode.BY_WEIGHT -> {
                        if (portion == null) missing.add("portion weight")
                    }
                    PortionMode.BY_COUNT -> {
                        if (totalPrt == null) missing.add("total portions in package")
                        if (totalWeight == null) missing.add("total package weight")
                    }
                }
                if (missing.isNotEmpty()) "Please enter: ${missing.joinToString(", ")}" else ""
            }
        }
    }
    
    fun clearAll() {
        fatPer100g = ""
        totalPackageWeight = ""
        portionWeight = ""
        totalPortions = ""
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
                    enabled = fatPer100g.isNotEmpty() || totalPackageWeight.isNotEmpty() || portionWeight.isNotEmpty() || totalPortions.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear")
                }
                
                MultipleChoiceButtonGroup(
                    selectedOption = portionMode,
                    options = listOf(
                        MultipleChoiceButtonOption(PortionMode.BY_WEIGHT, "By Weight"),
                        MultipleChoiceButtonOption(PortionMode.BY_COUNT, "By Count")
                    ),
                    onSelectionChanged = { portionMode = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
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
                    PortionMode.BY_COUNT -> {
                        OutlinedTextField(
                            value = totalPortions,
                            onValueChange = { totalPortions = filterNumericInput(it) },
                            label = { Text("Total portions in package") },
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
                }
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                calculation?.let { calc ->
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
                            Text(
                                text = if (portionMode == PortionMode.BY_COUNT) "Pills per Portion:" else "Pills Needed for Portion:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text("10k units: ${calc.pills10k} pills")
                            Text("35k units: ${calc.pills35k} pills")
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Pills Needed for Entire Package:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text("10k units: ${calc.pillsPerPackage10k} pills")
                            Text("35k units: ${calc.pillsPerPackage35k} pills")
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Grams per pill:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text("10k pill covers: ${calc.gramsFor10k}g fat")
                            Text("35k pill covers: ${calc.gramsFor35k}g fat")
                        }
                    }
                }
            }
        }
    }
}