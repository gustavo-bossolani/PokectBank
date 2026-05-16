package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.data.models.Mission
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnSurface
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant

@Composable
fun MissionSummaryCard(
    missions: List<Mission>,
    onMissionTap: (Mission) -> Unit,
    onViewAllTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Missões Ativas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PokectBankOnSurface
                    )
                )
                Text(
                    text = "Ver todas →",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankPrimary
                    ),
                    modifier = Modifier.clickable { onViewAllTap() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (missions.isEmpty()) {
                Text(
                    text = "Nenhuma missão ativa no momento 😴",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankMutedForeground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    missions.forEach { mission ->
                        MissionRow(
                            mission = mission,
                            onClick = { onMissionTap(mission) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionRow(
    mission: Mission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressPercent = if (mission.maxProgress > 0) {
        (mission.progress.toFloat() / mission.maxProgress.toFloat() * 100f).coerceIn(0f, 100f)
    } else {
        0f
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(KidShapes.small)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mission.icon,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mission.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = PokectBankOnSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(KidShapes.small)
                    .background(PokectBankSurfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercent / 100f)
                        .fillMaxHeight()
                        .clip(KidShapes.small)
                        .background(PokectBankPrimary)
                )
            }
        }

        Text(
            text = "${mission.progress}/${mission.maxProgress}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = PokectBankMutedForeground
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
