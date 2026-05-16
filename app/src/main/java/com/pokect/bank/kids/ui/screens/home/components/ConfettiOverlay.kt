package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSecondary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankTertiary
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Particle(
    val type: String,
    val color: Color,
    val isEmoji: Boolean,
)

private val particleTypes = listOf(
    Particle("🪙", Color.Unspecified, true),
    Particle("⭐", Color.Unspecified, true),
    Particle("🪙", Color.Unspecified, true),
    Particle("⭐", Color.Unspecified, true),
    Particle("🪙", Color.Unspecified, true),
)

private val circleColors = listOf(
    PokectBankPrimary,
    PokectBankSecondary,
    PokectBankTertiary,
    PokectBankSuccess,
)

@Composable
fun ConfettiOverlay(
    isActive: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val density = LocalDensity.current
    val particles = remember {
        List(AnimationSpecs.CONFETTI_PARTICLE_COUNT) { index ->
            val useEmoji = index < 10
            if (useEmoji) {
                particleTypes[index % particleTypes.size]
            } else {
                Particle("", circleColors[index % circleColors.size], false)
            }
        }
    }

    val particleStates = remember {
        particles.map {
            ParticleAnimationState(
                x = Animatable(0f),
                y = Animatable(0f),
                rotation = Animatable(0f),
                alpha = Animatable(1f),
            )
        }
    }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { containerSize = it.size }
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        particles.forEachIndexed { index, particle ->
            val state = particleStates[index]
            LaunchedEffect(index, containerSize) {
                if (containerSize.width == 0 || containerSize.height == 0) return@LaunchedEffect
                val targetX = Random.nextFloat() * containerSize.width - containerSize.width / 2f
                val targetY = Random.nextFloat() * containerSize.height - containerSize.height / 2f
                val targetRotation = Random.nextFloat() * 1440f - 720f

                state.x.snapTo(0f)
                state.y.snapTo(0f)
                state.rotation.snapTo(0f)
                state.alpha.snapTo(1f)

                state.x.animateTo(targetX)
                state.y.animateTo(targetY)
                state.rotation.animateTo(targetRotation)
                state.alpha.animateTo(0f)

                if (index == particles.lastIndex) {
                    onFinished()
                }
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            state.x.value.roundToInt(),
                            state.y.value.roundToInt()
                        )
                    }
                    .graphicsLayer(
                        rotationZ = state.rotation.value,
                        alpha = state.alpha.value.coerceIn(0f, 1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (particle.isEmoji) {
                    Text(
                        text = particle.type,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(particle.color)
                    )
                }
            }
        }
    }
}

private class ParticleAnimationState(
    val x: Animatable<Float, *>,
    val y: Animatable<Float, *>,
    val rotation: Animatable<Float, *>,
    val alpha: Animatable<Float, *>,
)
