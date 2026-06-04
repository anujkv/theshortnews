package com.a3solution.theshortnews.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a3solution.theshortnews.viewmodel.NewsViewModel

sealed class Screen(val route: String) {
    object NewsList : Screen("news_list")
    object NewsDetail : Screen("news_detail")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: NewsViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NewsList.route
    ) {
        composable(Screen.NewsList.route) {
            NewsScreen(
                viewModel = viewModel,
                onArticleClick = { article ->
                    viewModel.selectArticle(article)
                    navController.navigate(Screen.NewsDetail.route)
                }
            )
        }
        composable(Screen.NewsDetail.route) {
            val selectedArticle by viewModel.selectedArticle.collectAsState()
            selectedArticle?.let { article ->
                NewsDetailScreen(
                    article = article,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
