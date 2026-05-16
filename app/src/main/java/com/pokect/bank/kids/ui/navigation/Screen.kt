package com.pokect.bank.kids.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Goals : Screen("goals", "Metas", Icons.Default.TrackChanges)
    object Missions : Screen("missions", "Missões", Icons.Default.Star)
    object Rewards : Screen("rewards", "Prêmios", Icons.Default.CardGiftcard)
    object Ranking : Screen("ranking", "Ranking", Icons.Default.EmojiEvents)
    object Login : Screen("login", "Login", Icons.Default.Person)
}

val bottomNavTabs = listOf(Screen.Home, Screen.Goals, Screen.Missions, Screen.Rewards, Screen.Ranking)
