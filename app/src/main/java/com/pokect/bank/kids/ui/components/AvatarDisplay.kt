package com.pokect.bank.kids.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AvatarSize(val dp: Dp) { SM(48.dp), MD(80.dp), LG(112.dp), XL(144.dp) }

@Composable
fun AvatarDisplay(
    level: Int,
    size: AvatarSize,
    showLevel: Boolean = true,
    modifier: Modifier = Modifier
) {
    val avatarData = remember(level) { getAvatarData(level) }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 300f
        ),
        label = "avatarTapScale"
    )

    val circleSize = size.dp
            val eyeRadius = circleSize * 0.04f
            val eyeOffsetY = circleSize * 0.35f
            val eyeSpacing = circleSize * 0.12f
    val smilePath = remember { Path() }

    val sparkleSize = when (size) {
        AvatarSize.SM -> 8.sp
        AvatarSize.MD -> 12.sp
        AvatarSize.LG -> 14.sp
        AvatarSize.XL -> 16.sp
    }
    val accessorySize = when (size) {
        AvatarSize.SM -> 12.sp
        AvatarSize.MD -> 16.sp
        AvatarSize.LG -> 20.sp
        AvatarSize.XL -> 24.sp
    }
    val badgeSize = when (size) {
        AvatarSize.SM -> 20.dp
        AvatarSize.MD -> 24.dp
        AvatarSize.LG -> 28.dp
        AvatarSize.XL -> 32.dp
    }
    val badgeFontSize = when (size) {
        AvatarSize.SM -> 10.sp
        AvatarSize.MD -> 12.sp
        AvatarSize.LG -> 14.sp
        AvatarSize.XL -> 16.sp
    }

    Box(
        modifier = modifier
            .size(circleSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { isPressed = !isPressed },
        contentAlignment = Alignment.Center
    ) {
        // Gradient circle background
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(avatarData.gradient),
            contentAlignment = Alignment.Center
        ) {
            // Face: eyes + smile
            Canvas(modifier = Modifier.size(circleSize)) {
                val canvasSize = this.size
                val cx = canvasSize.width / 2f
                val cy = canvasSize.height / 2f

                // Eyes (two black dots)
                drawCircle(
                    color = Color(0xFF1A1A2E),
                    radius = eyeRadius.toPx(),
                    center = Offset(cx - eyeSpacing.toPx(), eyeOffsetY.toPx())
                )
                drawCircle(
                    color = Color(0xFF1A1A2E),
                    radius = eyeRadius.toPx(),
                    center = Offset(cx + eyeSpacing.toPx(), eyeOffsetY.toPx())
                )

                // Smile (curved arc)
                smilePath.reset()
                val smileStartX = cx - circleSize.toPx() * 0.15f
                val smileEndX = cx + circleSize.toPx() * 0.15f
                val smileY = eyeOffsetY.toPx() + circleSize.toPx() * 0.2f
                val controlY = smileY + circleSize.toPx() * 0.12f
                smilePath.moveTo(smileStartX, smileY)
                smilePath.quadraticBezierTo(cx, controlY, smileEndX, smileY)
                drawPath(
                    path = smilePath,
                    color = Color(0xFF1A1A2E),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Accessory (level 3+)
        if (avatarData.accessory.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer { translationY = -(circleSize.toPx() * 0.1f) }
            ) {
                androidx.compose.material3.Text(
                    text = avatarData.accessory,
                    fontSize = accessorySize
                )
            }
        }

        // Sparkles (level 5+)
        if (avatarData.hasSparkles) {
            // Top-right sparkle
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .graphicsLayer { translationX = circleSize.toPx() * 0.05f }
            ) {
                androidx.compose.material3.Text(
                    text = "✨",
                    fontSize = sparkleSize
                )
            }
            // Bottom-left sparkle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .graphicsLayer { translationX = -(circleSize.toPx() * 0.05f) }
            ) {
                androidx.compose.material3.Text(
                    text = "✨",
                    fontSize = sparkleSize
                )
            }
        }

        // Level badge (bottom-right)
        if (showLevel) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .graphicsLayer {
                        translationX = badgeSize.toPx() * 0.3f
                        translationY = badgeSize.toPx() * 0.3f
                    }
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = level.toString(),
                    color = Color.White,
                    fontSize = badgeFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class AvatarDisplayData(
    val gradient: Brush,
    val accessory: String,
    val hasSparkles: Boolean
)

private fun getAvatarData(level: Int): AvatarDisplayData {
    return when {
        level >= 10 -> AvatarDisplayData(
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF6366F1),
                    Color(0xFFEC4899),
                    Color(0xFFFBBF24),
                    Color(0xFF10B981),
                    Color(0xFF6366F1)
                )
            ),
            accessory = "👑",
            hasSparkles = true
        )
        level >= 7 -> AvatarDisplayData(
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFFFBBF24), Color(0xFFF97316))
            ),
            accessory = "🎩",
            hasSparkles = true
        )
        level >= 5 -> AvatarDisplayData(
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
            ),
            accessory = "🎀",
            hasSparkles = true
        )
        level >= 3 -> AvatarDisplayData(
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFFEC4899), Color(0xFFF472B6))
            ),
            accessory = "🧢",
            hasSparkles = false
        )
        else -> AvatarDisplayData(
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFF6366F1), Color(0xFF818CF8))
            ),
            accessory = "",
            hasSparkles = false
        )
    }
}
