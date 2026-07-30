package com.moneymoment.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moneymoment.ai.domain.model.Goal
import com.moneymoment.ai.ui.components.SavingsProgressBar
import com.moneymoment.ai.ui.theme.AccentGreen
import com.moneymoment.ai.ui.theme.AccentRed
import com.moneymoment.ai.ui.theme.TextMuted
import com.moneymoment.ai.ui.theme.TextSecondary
import com.moneymoment.ai.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = viewModel()) {
    val goals by viewModel.goals.collectAsState()
    val showAddForm by viewModel.showAddForm.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Goals",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.toggleForm() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showAddForm) {
            AddGoalForm(
                onSave = { name, target, icon, savings ->
                    viewModel.addGoal(name, target, icon)
                },
                onCancel = { viewModel.toggleForm() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No goals yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to create your first savings goal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onAddSavings = { amount -> viewModel.addSavings(goal.id, amount) },
                        onToggleActive = { viewModel.toggleActive(goal.id) },
                        onDelete = { viewModel.deleteGoal(goal.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onAddSavings: (Double) -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    var savingsInput by remember { mutableStateOf("") }
    val alpha = if (goal.isActive) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = AccentGreen.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForGoal(goal.icon),
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = String.format("Target: \u20B9%.0f", goal.targetAmount),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row {
                    IconButton(onClick = onToggleActive) {
                        Icon(
                            imageVector = if (goal.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (goal.isActive) "Pause goal" else "Resume goal",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete goal",
                            tint = AccentRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SavingsProgressBar(
                saved = goal.savedAmount,
                target = goal.targetAmount
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = savingsInput,
                    onValueChange = { savingsInput = it },
                    label = { Text("Add savings") },
                    prefix = { Text("\u20B9") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val amount = savingsInput.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            onAddSavings(amount)
                            savingsInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add")
                }
            }

            if (!goal.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Goal paused",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGoalForm(
    onSave: (name: String, target: Double, icon: String, savings: Double) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var savings by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("dart") }

    val goalIcons = listOf(
        "dart" to Icons.Outlined.AutoAwesome,
        "home" to Icons.Outlined.Home,
        "car" to Icons.Outlined.DirectionsCar,
        "school" to Icons.Outlined.School,
        "phone" to Icons.Outlined.PhoneIphone,
        "flight" to Icons.Outlined.Flight,
        "shopping" to Icons.Outlined.ShoppingBag,
        "gift" to Icons.Outlined.CardGiftcard,
        "favorite" to Icons.Outlined.FavoriteBorder,
        "ac" to Icons.Outlined.AcUnit
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "New Goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Goal name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text("Target amount") },
                prefix = { Text("\u20B9") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose icon",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goalIcons) { (iconName, iconVector) ->
                    val isSelected = selectedIcon == iconName
                    OutlinedCard(
                        onClick = { selectedIcon = iconName },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = iconName,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = savings,
                onValueChange = { savings = it },
                label = { Text("Initial savings") },
                prefix = { Text("\u20B9") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val targetVal = target.toDoubleOrNull()
                        if (name.isNotBlank() && targetVal != null && targetVal > 0) {
                            val savingsVal = savings.toDoubleOrNull() ?: 0.0
                            onSave(name, targetVal, selectedIcon, savingsVal)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

private fun getIconForGoal(icon: String): ImageVector {
    return when (icon) {
        "home" -> Icons.Outlined.Home
        "car" -> Icons.Outlined.DirectionsCar
        "school" -> Icons.Outlined.School
        "phone" -> Icons.Outlined.PhoneIphone
        "flight" -> Icons.Outlined.Flight
        "shopping" -> Icons.Outlined.ShoppingBag
        "gift" -> Icons.Outlined.CardGiftcard
        "favorite" -> Icons.Outlined.FavoriteBorder
        "ac" -> Icons.Outlined.AcUnit
        else -> Icons.Outlined.AutoAwesome
    }
}
