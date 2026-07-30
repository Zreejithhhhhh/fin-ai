package com.moneymoment.ai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moneymoment.ai.ui.theme.AccentGreen
import com.moneymoment.ai.ui.theme.TextMuted
import com.moneymoment.ai.ui.theme.TextSecondary

@Composable
fun CategoryProgressBar(
    category: String,
    total: Double,
    percentage: Float,
    barColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = String.format("\u20B9%.0f", total),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = String.format("%.0f%%", percentage * 100),
                style = MaterialTheme.typography.labelMedium,
                color = barColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = percentage.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun SavingsProgressBar(
    saved: Double,
    target: Double,
    modifier: Modifier = Modifier
) {
    val percentage = if (target > 0) (saved / target).toFloat().coerceIn(0f, 1f) else 0f
    val remaining = (target - saved).coerceAtLeast(0.0)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.0f%%", percentage * 100),
                style = MaterialTheme.typography.titleLarge,
                color = AccentGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = String.format("\u20B9%.0f / \u20B9%.0f", saved, target),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                if (remaining > 0) {
                    Text(
                        text = String.format("\u20B9%.0f remaining", remaining),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = AccentGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}
