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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatRobinApp() {
    var fatPer100g by remember { mutableStateOf("") }
    var totalPackageWeight by remember { mutableStateOf("") }
    var portionWeight by remember { mutableStateOf("") }
    
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
    
    val calculation = remember(fat, totalWeight, portion) {
        try {
            if (fat != null && totalWeight != null && portion != null) {
                calculator.calculatePillsNeeded(fat, totalWeight, portion)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    val errorMessage = remember(fat, totalWeight, portion, calculation) {
        when {
            fat == null && fatPer100g.isNotEmpty() -> "Please enter a valid number for fat per 100g"
            totalWeight == null && totalPackageWeight.isNotEmpty() -> "Please enter a valid number for total package weight"
            portion == null && portionWeight.isNotEmpty() -> "Please enter a valid number for portion weight"
            fat != null && totalWeight != null && portion != null && calculation == null -> "Invalid input values"
            fat == null || totalWeight == null || portion == null -> {
                val missing = mutableListOf<String>()
                if (fat == null) missing.add("fat per 100g")
                if (totalWeight == null) missing.add("total package weight")
                if (portion == null) missing.add("portion weight")
                "Please enter: ${missing.joinToString(", ")}"
            }
            else -> ""
        }
    }
    
    fun clearAll() {
        fatPer100g = ""
        totalPackageWeight = ""
        portionWeight = ""
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
                    enabled = fatPer100g.isNotEmpty() || totalPackageWeight.isNotEmpty() || portionWeight.isNotEmpty(),
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
                
                OutlinedTextField(
                    value = totalPackageWeight,
                    onValueChange = { totalPackageWeight = filterNumericInput(it) },
                    label = { Text("Total package weight (g)") },
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
                                text = "Pills Needed:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text("10k units: ${calc.pills10k} pills")
                            Text("35k units: ${calc.pills35k} pills")
                            
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