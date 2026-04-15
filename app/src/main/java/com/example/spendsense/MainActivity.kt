package com.example.spendsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendsense.ui.navigation.AppNavigation
import com.example.spendsense.ui.theme.SpendSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpendSenseTheme {
                AppNavigation()
            }
        }
    }
}