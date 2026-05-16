package com.pokect.bank.kids.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pokect.bank.kids.di.AppContainer
import com.pokect.bank.kids.ui.screens.goals.GoalsScreen
import com.pokect.bank.kids.ui.screens.goals.GoalsViewModelFactory
import com.pokect.bank.kids.ui.screens.home.HomeScreen
import com.pokect.bank.kids.ui.screens.home.HomeViewModelFactory
import com.pokect.bank.kids.ui.screens.login.LoginScreen
import com.pokect.bank.kids.ui.screens.missions.MissionsScreen
import com.pokect.bank.kids.ui.screens.missions.MissionsViewModelFactory
import com.pokect.bank.kids.ui.screens.ranking.RankingScreen
import com.pokect.bank.kids.ui.screens.ranking.RankingViewModelFactory
import com.pokect.bank.kids.ui.screens.rewards.RewardsScreen
import com.pokect.bank.kids.ui.screens.rewards.RewardsViewModelFactory

@Composable
fun PokectBankNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Home.route,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            val viewModel: com.pokect.bank.kids.ui.screens.home.HomeViewModel = viewModel(
                factory = HomeViewModelFactory(container.repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToMissions = { navController.navigate(Screen.Missions.route) }
            )
        }
        composable(
            route = Screen.Goals.route,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            val viewModel: com.pokect.bank.kids.ui.screens.goals.GoalsViewModel = viewModel(
                factory = GoalsViewModelFactory(container.repository)
            )
            GoalsScreen(viewModel = viewModel)
        }
        composable(
            route = Screen.Missions.route,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            val viewModel: com.pokect.bank.kids.ui.screens.missions.MissionsViewModel = viewModel(
                factory = MissionsViewModelFactory(container.repository)
            )
            MissionsScreen(viewModel = viewModel)
        }
        composable(
            route = Screen.Rewards.route,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            val viewModel: com.pokect.bank.kids.ui.screens.rewards.RewardsViewModel = viewModel(
                factory = RewardsViewModelFactory(container.repository)
            )
            RewardsScreen(viewModel = viewModel)
        }
        composable(
            route = Screen.Ranking.route,
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            val viewModel: com.pokect.bank.kids.ui.screens.ranking.RankingViewModel = viewModel(
                factory = RankingViewModelFactory(container.repository)
            )
            RankingScreen(viewModel = viewModel)
        }
    }
}
