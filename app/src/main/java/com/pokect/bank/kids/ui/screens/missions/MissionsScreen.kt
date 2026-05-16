package com.pokect.bank.kids.ui.screens.missions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokect.bank.R
import com.pokect.bank.kids.ui.screens.missions.components.MissionCard
import com.pokect.bank.kids.ui.screens.missions.components.MissionCategoryFilter
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.PokectBankBackground
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground

@Composable
fun MissionsScreen(
    viewModel: MissionsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PokectBankBackground),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            item {
                Text(
                    text = stringResource(R.string.missions_screen_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokectBankOnBackground
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Filter chips
            item {
                MissionCategoryFilter(
                    selectedFilter = uiState.selectedFilter,
                    onFilterChange = viewModel::selectFilter
                )
            }

            // Empty state or mission list
            if (uiState.filteredMissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "🌟",
                                fontSize = 48.sp
                            )
                            Text(
                                text = stringResource(R.string.missions_empty_heading),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PokectBankOnBackground
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.missions_empty_body),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PokectBankMutedForeground
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    uiState.filteredMissions,
                    key = { it.id }
                ) { mission ->
                    MissionCard(
                        mission = mission,
                        modifier = Modifier.animateItem(
                            fadeInSpec = AnimationSpecs.screenTransition,
                            fadeOutSpec = tween(200),
                            placementSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    )
                }
            }
        }
    }
}
