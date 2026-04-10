package com.example.spendsense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spendsense.ui.screen.AddIncomeScreen
import com.example.spendsense.ui.screen.HomeScreen
import com.example.spendsense.ui.screen.SignupScreen
import com.example.spendsense.ui.screens.AddExpenseScreen
import com.example.spendsense.ui.screens.BudgetScreen
import com.example.spendsense.ui.screens.LoginScreen
import com.example.spendsense.ui.screens.ProfileScreen
import com.example.spendsense.ui.screens.SplashScreen
import com.example.spendsense.viewmodel.TransactionViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val transactionViewModel: TransactionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }

        // Pass viewModel using named parameter
        composable("home") { HomeScreen(navController, transactionViewModel = transactionViewModel) }
        composable("addExpense") { AddExpenseScreen(navController, transactionViewModel) }
        composable("addIncome") { AddIncomeScreen(navController, transactionViewModel) }
        composable("profile") { ProfileScreen(navController, transactionViewModel) }
        composable("budget") { BudgetScreen(navController, transactionViewModel) }

    }
}