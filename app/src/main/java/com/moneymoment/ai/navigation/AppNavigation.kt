package com.moneymoment.ai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moneymoment.ai.ui.screens.DashboardScreen
import com.moneymoment.ai.ui.screens.DecisionScreen
import com.moneymoment.ai.ui.screens.DigestScreen
import com.moneymoment.ai.ui.screens.GoalsScreen
import com.moneymoment.ai.ui.screens.JournalScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") { DashboardScreen() }
        composable("decision") { DecisionScreen() }
        composable("journal") { JournalScreen() }
        composable("goals") { GoalsScreen() }
        composable("digest") { DigestScreen() }
    }
}
