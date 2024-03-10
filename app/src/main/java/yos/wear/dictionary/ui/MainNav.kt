package yos.wear.dictionary.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MainNav() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(navController, startDestination = "main") {
        composable("main") {
            Home(navController)
        }
        composable("translate/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            TranslateResult(query)
        }
        composable("dic/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            YoudaoDicResult(query, navController)
        }
    }
}