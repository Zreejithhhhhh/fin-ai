package com.moneymoment.ai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moneymoment.ai.domain.engine.CategoryStats
import com.moneymoment.ai.ui.components.CategoryProgressBar
import com.moneymoment.ai.ui.components.StatCard
import com.moneymoment.ai.ui.theme.AccentGreen
import com.moneymoment.ai.ui.theme.AccentRed
import com.moneymoment.ai.ui.theme.AccentYellow
import com.moneymoment.ai.ui.theme.TextMuted
import com.moneymoment.ai.ui.theme.TextSecondary
import com.moneymoment.ai.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var incomeInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (monthlyIncome.isBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Set Monthly Income",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = incomeInput,
                        onValueChange = { incomeInput = it },
                        label = { Text("Monthly Income") },
                        prefix = { Text("\u20B9") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.setIncome(incomeInput)
                            incomeInput = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = incomeInput.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (monthlyStats != null) {
            val stats = monthlyStats!!

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.AccountBalance,
                        value = String.format("\u20B9%.0f", stats.totalIncome),
                        label = "Income"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.ShoppingCart,
                        value = String.format("\u20B9%.0f", stats.totalSpent),
                        label = "Spent"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val regretColor = when {
                    stats.regretRate >= 70 -> AccentRed
                    stats.regretRate >= 35 -> AccentYellow
                    else -> AccentGreen
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.TrendingDown,
                        value = "${stats.regretRate}%",
                        label = "Regret Rate",
                        valueColor = regretColor
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(
                        icon = Icons.Default.Savings,
                        value = "${stats.savingsRate}%",
                        label = "Savings Rate",
                        valueColor = AccentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Spending by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (categoryBreakdown.isEmpty()) {
                Text(
                    text = "No spending data yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                val totalSpent = categoryBreakdown.values.sumOf { it.total }
                categoryBreakdown.forEach { (category, catStats: CategoryStats) ->
                    val percentage = if (totalSpent > 0) (catStats.total / totalSpent).toFloat() else 0f
                    val barColor = when {
                        catStats.count > 0 && catStats.regretted.toDouble() / catStats.count >= 0.5 -> AccentRed
                        catStats.count > 0 && catStats.regretted.toDouble() / catStats.count >= 0.25 -> AccentYellow
                        else -> AccentGreen
                    }
                    CategoryProgressBar(
                        category = category,
                        total = catStats.total,
                        percentage = percentage,
                        barColor = barColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Regret Rate Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            RegretRateTrendChart()

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Check Purchase")
                }
                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Rate Journal")
                }
            }
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RegretRateTrendChart() {
    val weekLabels = listOf("3w ago", "2w ago", "1w ago", "This week")
    val mockRates = listOf(45, 38, 52, 30)
    val maxRate = mockRates.maxOrNull()?.coerceAtLeast(1) ?: 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weekLabels.forEachIndexed { index, label ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        val rate = mockRates[index]
                        val barColor = when {
                            rate >= 70 -> AccentRed
                            rate >= 35 -> AccentYellow
                            else -> AccentGreen
                        }
                        val barHeight = (rate.toFloat() / maxRate * 100).dp.coerceAtLeast(8.dp)

                        Text(
                            text = "${rate}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = barColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            modifier = Modifier
                                .width(24.dp)
                                .height(barHeight),
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = barColor
                            )
                        ) {}
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
