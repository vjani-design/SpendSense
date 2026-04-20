package com.example.spendsense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendsense.ui.screen.AddIncomeScreen
import com.example.spendsense.ui.screen.HomeScreen
import com.example.spendsense.ui.screens.AddExpenseScreen
import com.example.spendsense.ui.screens.BudgetScreen
import com.example.spendsense.ui.screens.EditTransactionScreen
import com.example.spendsense.ui.screens.LoginScreen
import com.example.spendsense.ui.screens.ProfileScreen
import com.example.spendsense.ui.screens.ReportsScreen
import com.example.spendsense.ui.screens.SignupScreen
import com.example.spendsense.ui.screens.SplashScreen
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.screens.ForgotPasswordEmailScreen
import com.example.spendsense.ui.screens.ResetPasswordScreen

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
        composable("add_expense") { AddExpenseScreen(navController, transactionViewModel) }
        composable("add_income") { AddIncomeScreen(navController, transactionViewModel) }
        composable("profile") { ProfileScreen(navController, transactionViewModel) }
        composable("budget") { BudgetScreen(navController, transactionViewModel) }
        composable("reports") {
            ReportsScreen(navController, transactionViewModel)
        }

        composable("forgot_password") {
            ForgotPasswordEmailScreen(navController)
        }

        composable(
            route = "reset_password/{docId}",
            arguments = listOf(navArgument("docId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->

            val docId = backStackEntry.arguments?.getString("docId") ?: ""

            ResetPasswordScreen(
                docId = docId,
                navController = navController
            )
        }

        composable(
            route = "editTransaction/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""

            EditTransactionScreen(
                transactionId = id,
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }
    }
}
