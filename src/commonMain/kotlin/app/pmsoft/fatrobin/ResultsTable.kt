package app.pmsoft.fatrobin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

            // Portion pills
            val portionPills = calculator.getPortionPills(pillDoses = pillDoses)?.get(index)
            Text(portionPills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Grams per pill
            val gramsPerPill = calculator.getGramsPerPill(pillDoses = pillDoses)?.get(index)
            Text(gramsPerPill?.toInt()?.let { "${it}g" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Sub-package pills
            val subPackagePills = calculator.getSubPackagePills(pillDoses = pillDoses)?.get(index)
            Text(subPackagePills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Package pills
            val packagePills = calculator.getPackagePills(pillDoses = pillDoses)?.get(index)
            Text(packagePills?.let { "$it 💊" } ?: "–", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)

            // Food items per pill or pills per food item
            val pillsPerFoodItem = calculator.getPillsPerFoodItem(pillDoses = pillDoses)?.get(index)
            val foodItemsPerPill = calculator.getFoodItemsPerPill(pillDoses = pillDoses)?.get(index)

            val foodText = when {
              pillsPerFoodItem == null || foodItemsPerPill == null -> "–"
              foodItemsPerPill > 1 -> "$foodItemsPerPill 🍎"
              else -> "$pillsPerFoodItem 💊"
            }
            Text(foodText, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
