package com.pokect.bank.kids.ui.screens.missions.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSecondary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankTertiary
import com.pokect.bank.kids.ui.theme.PokectBankWarning
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Particle(
    val color: Color,
    val startX: Float,
    val delay: Long
)

@Composable
fun MiniConfettiOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val particleCount = 8
    val confettiColors = listOf(
        PokectBankPrimary,
        PokectBankSecondary,
        PokectBankSuccess,
        PokectBankTertiary,
        PokectBankWarning
    )

    val particles = remember {
        List(particleCount) { index ->
            Particle(
                color = confettiColors[Random.nextInt(confettiColors.size)],
                startX = Random.nextFloat(),
                delay = index * 100L
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        particles.forEach { particle ->
            ParticleAnimation(particle = particle)
        }
    }
}

@Composable
private fun ParticleAnimation(particle: Particle) {
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(particle.delay)
        started = true
    }

    if (!started) return

    val yOffset = remember { Animatable(0f) }
    val xOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        val targetY = 300f + Random.nextFloat() * 200f
        val targetX = (Random.nextFloat() - 0.5f) * 100f

        launch {
            yOffset.animateTo(
                targetValue = targetY,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        launch {
            xOffset.animateTo(
                targetValue = targetX,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        launch {
            kotlinx.coroutines.delay(800)
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                IntOffset(
                    x = ((particle.startX * 100) + xOffset.value).toInt(),
                    y = yOffset.value.toInt()
                )
            }
            .alpha(alpha.value)
    ) {
        Canvas(
            modifier = Modifier
                .size((4 + Random.nextFloat() * 4).dp)
                .offset { IntOffset((particle.startX * 100).toInt(), 0) }
        ) {
            drawCircle(color = particle.color)
        }
    }
}
