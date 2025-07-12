package app.pmsoft.fatrobin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape

class MultipleChoiceButtonOption<T>(
    val value: T,
    val text: String,
)

@Composable
fun <T> MultipleChoiceButtonGroup(
    selectedOption: T?,
    options: List<MultipleChoiceButtonOption<T>>,
    onSelectionChanged: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (options.size < 2) {
        return
    }
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(shape = RoundedCornerShape(percent = 50), color = Color.Transparent)
            .border(
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(percent = 50),
            ),
    ) {
        val firstOption = options.first()
        val lastOption = options.last()
        val middleOptions = options.subList(1, options.size - 1)
        MultipleChoiceButton(
            onClick = { onSelectionChanged(firstOption.value) },
            text = firstOption.text,
            selected = selectedOption == firstOption.value,
            first = true,
        )
        middleOptions.forEach { option ->
            MultipleChoiceButton(
                onClick = { onSelectionChanged(option.value) },
                text = option.text,
                selected = selectedOption == option.value,
            )
        }
        MultipleChoiceButton(
            onClick = { onSelectionChanged(lastOption.value) },
            text = lastOption.text,
            selected = selectedOption == lastOption.value,
            last = true,
        )
    }
}

@Composable
private fun RowScope.MultipleChoiceButton(
    onClick: () -> Unit,
    text: String,
    selected: Boolean,
    first: Boolean = false,
    last: Boolean = false,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = when {
                    first && last -> ButtonDefaults.outlinedShape
                    first         -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
                    last          -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
                    else          -> RectangleShape
                },
            ),
        enabled = !selected,
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        )
    }
}