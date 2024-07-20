package com.xbot.currencyconverter.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xbot.currencyconverter.ui.feature.converter.ConverterScreen
import com.xbot.currencyconverter.ui.feature.result.ResultScreen
import kotlinx.serialization.Serializable

@Composable
fun CurrencyConverterNavHost(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier.background(backgroundColor),
        navController = navController,
        startDestination = Routes.ConverterScreen
    ) {
        composable<Routes.ConverterScreen> {
            ConverterScreen {
                navController.navigate(Routes.ResultScreen(it)) {
                    restoreState = true
                }
            }
        }
        composable<Routes.ResultScreen> {
            ResultScreen {
                navController.navigateUp()
            }
        }
    }
}

@Serializable
sealed class Routes {
    @Serializable
    data object ConverterScreen : Routes()

    @Serializable
    data class ResultScreen(
        val result: String
    ) : Routes()
}
