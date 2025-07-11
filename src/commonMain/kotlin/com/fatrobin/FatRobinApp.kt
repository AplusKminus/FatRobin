package com.fatrobin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatRobinApp() {
    var fatPer100g by remember { mutableStateOf("") }
    var totalPackageWeight by remember { mutableStateOf("") }
    var portionWeight by remember { mutableStateOf("") }
    var calculation by remember { mutableStateOf<PillCalculation?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    
    val calculator = remember { FatRobinCalculator() }
    
    fun calculatePills() {
        try {
            val fat = fatPer100g.toDoubleOrNull()
            val totalWeight = totalPackageWeight.toDoubleOrNull()
            val portion = portionWeight.toDoubleOrNull()
            
            if (fat == null || totalWeight == null || portion == null) {
                errorMessage = "Please enter valid numbers for all fields"
                calculation = null
                return
            }
            
            calculation = calculator.calculatePillsNeeded(fat, totalWeight, portion)
            errorMessage = ""
        } catch (e: Exception) {
            errorMessage = e.message ?: "An error occurred"
            calculation = null
        }
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
                
                OutlinedTextField(
                    value = fatPer100g,
                    onValueChange = { fatPer100g = it },
                    label = { Text("Fat per 100g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = totalPackageWeight,
                    onValueChange = { totalPackageWeight = it },
                    label = { Text("Total package weight (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = portionWeight,
                    onValueChange = { portionWeight = it },
                    label = { Text("Portion weight (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = { calculatePills() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate Pills")
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