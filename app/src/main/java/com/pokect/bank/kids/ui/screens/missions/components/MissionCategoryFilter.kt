package com.pokect.bank.kids.ui.screens.missions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pokect.bank.R
import com.pokect.bank.kids.ui.screens.missions.MissionFilterCategory
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankOnPrimary
import com.pokect.bank.kids.ui.theme.PokectBankOnSurfaceVariant
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant

@Composable
fun MissionCategoryFilter(
    selectedFilter: MissionFilterCategory,
    onFilterChange: (MissionFilterCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = MissionFilterCategory.entries.toTypedArray()
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterChange(filter) },
                label = {
                    Text(
                        text = when (filter) {
                            MissionFilterCategory.ALL -> stringResource(R.string.missions_filter_all)
                            MissionFilterCategory.DAILY -> stringResource(R.string.missions_filter_daily)
                            MissionFilterCategory.WEEKLY -> stringResource(R.string.missions_filter_weekly)
                            MissionFilterCategory.SPECIAL -> stringResource(R.string.missions_filter_special)
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = PokectBankOnPrimary,
                    containerColor = if (isSelected) Color.Transparent else PokectBankSurfaceVariant,
                    labelColor = if (isSelected) PokectBankOnPrimary else PokectBankOnSurfaceVariant,
                ),
                border = null,
                modifier = Modifier
                    .then(
                        if (isSelected) {
                            Modifier.background(GradientPresets.gradientPrimary)
                        } else {
                            Modifier
                        }
                    )
                    .clip(KidShapes.small)
            )
        }
    }
}
