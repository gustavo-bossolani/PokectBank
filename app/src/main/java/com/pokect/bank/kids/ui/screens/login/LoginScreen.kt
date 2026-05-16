package com.pokect.bank.kids.ui.screens.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import com.pokect.bank.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokect.bank.kids.data.models.AvatarOption
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import kotlin.math.roundToInt

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // When login succeeds (isLoggedIn transitions true), trigger navigation
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // Show snackbar on error
    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Layer 1: Decorative blurred circles background
        DecorativeCirclesBackground()

        // Layer 2: Floating emoji background
        FloatingEmojiBackground()

        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Layer 3: Logo section
            LogoSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Layer 4: Auth card
            AuthCard(
                uiState = uiState,
                onToggleTab = { viewModel.toggleTab(it) },
                onUpdateName = { viewModel.updateName(it) },
                onUpdateEmail = { viewModel.updateEmail(it) },
                onUpdatePassword = { viewModel.updatePassword(it) },
                onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                onSelectAvatar = { viewModel.selectAvatar(it) },
                onNextStep = { viewModel.nextStep() },
                onPreviousStep = { viewModel.previousStep() },
                onLogin = { viewModel.login() },
                onForgotPassword = { viewModel.forgotPassword() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Layer 5: Footer
            FooterSection()
        }

        // Layer 6: Error Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(2f)
        )
    }
}

// ============================================================================
// Layer 1: Decorative blurred circles background
// ============================================================================
@Composable
private fun DecorativeCirclesBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-right circle - primary/20
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFF6366F1).copy(alpha = 0.15f))
        )
        // Bottom-left circle - secondary/20
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFFEC4899).copy(alpha = 0.15f))
        )
        // Middle-left circle - tertiary/30
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-30).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFFBBF24).copy(alpha = 0.2f))
        )
        // Bottom-right circle - success/20
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = 0.15f))
        )
    }
}

// ============================================================================
// Layer 2: Floating emoji background (InfiniteTransition)
// ============================================================================
@Composable
private fun FloatingEmojiBackground() {
    val emojis = listOf("🪙", "⭐", "🎯", "🏆", "💎", "🎁")

    // Track positions and rotations with mutable states
    val yOffsets = remember {
        List(emojis.size) { mutableFloatStateOf(-100f) }
    }
    val rotations = remember {
        List(emojis.size) { mutableFloatStateOf(0f) }
    }

    // Animate floating emojis with LaunchedEffect
    for (i in emojis.indices) {
        val idx = i
        LaunchedEffect(Unit) {
            val delay = idx * 500L
            kotlinx.coroutines.delay(delay)
            while (true) {
                // Float from top to bottom
                yOffsets[idx].floatValue = -100f
                rotations[idx].floatValue = 0f
                val steps = 60
                for (step in 0 until steps) {
                    yOffsets[idx].floatValue = -100f + (1100f + 100f) * step / steps
                    rotations[idx].floatValue = 360f * step / steps
                    kotlinx.coroutines.delay((4000L / steps))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        emojis.forEachIndexed { index, emoji ->
            val startX = 50f + (index * 60f) % 300f
            Box(
                modifier = Modifier
                    .offset { IntOffset(startX.roundToInt(), yOffsets[index].floatValue.roundToInt()) }
                    .graphicsLayer {
                        rotationZ = rotations[index].floatValue
                        alpha = 0.35f
                    }
            ) {
                Text(emoji, fontSize = 28.sp)
            }
        }
    }
}

// ============================================================================
// Layer 3: Logo section with bouncing piggy bank + sparkles
// ============================================================================
@Composable
private fun LogoSection() {
    var bounceOffset by remember { mutableFloatStateOf(0f) }
    var sparkleScale by remember { mutableFloatStateOf(1f) }
    var starScale by remember { mutableFloatStateOf(1f) }

    // Bounce animation
    LaunchedEffect(Unit) {
        while (true) {
            bounceOffset = 0f
            repeat(2) {
                for (i in 0 until 20) {
                    bounceOffset = -8f * i / 20
                    kotlinx.coroutines.delay(50)
                }
                for (i in 0 until 20) {
                    bounceOffset = -8f * (20 - i) / 20
                    kotlinx.coroutines.delay(50)
                }
            }
        }
    }

    // Sparkle pulse
    LaunchedEffect(Unit) {
        while (true) {
            for (step in 0 until 30) {
                sparkleScale = 1f + 0.3f * step / 30
                kotlinx.coroutines.delay(25)
            }
            for (step in 0 until 30) {
                sparkleScale = 1.3f - 0.3f * step / 30
                kotlinx.coroutines.delay(25)
            }
        }
    }

    // Star pulse
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        while (true) {
            for (step in 0 until 36) {
                starScale = 1f + 0.2f * step / 36
                kotlinx.coroutines.delay(25)
            }
            for (step in 0 until 36) {
                starScale = 1.2f - 0.2f * step / 36
                kotlinx.coroutines.delay(25)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Piggy bank logo circle with bounce
        Box(modifier = Modifier.size(96.dp)) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .offset(y = bounceOffset.dp)
                    .clip(CircleShape)
                    .background(GradientPresets.gradientPrimary)
                    .graphicsLayer {
                        shadowElevation = 8f
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🐷", fontSize = 48.sp)
            }

            // Sparkle ✨ at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .graphicsLayer {
                        scaleX = sparkleScale
                        scaleY = sparkleScale
                    }
            ) {
                Text("✨", fontSize = 20.sp)
            }

            // Star ⭐ at bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-4).dp, y = 4.dp)
                    .graphicsLayer {
                        scaleX = starScale
                        scaleY = starScale
                    }
            ) {
                Text("⭐", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title: "Pocket" in onSurface, "Bank" in primary
        Text(
            text = stringResource(R.string.login_brand_pocket),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.login_brand_bank_kids),
            style = MaterialTheme.typography.displaySmall,
            color = PokectBankPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// Layer 4: Auth card
// ============================================================================
@Composable
private fun AuthCard(
    uiState: LoginUiState,
    onToggleTab: (Boolean) -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSelectAvatar: (Int) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .graphicsLayer {
                    shadowElevation = 8f
                    shape = RoundedCornerShape(24.dp)
                    clip = true
                }
                .padding(24.dp)
        ) {
            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { it / 4 } + fadeOut())
                    }
                },
                label = "stepTransition"
            ) { step ->
                if (step == 1) {
                    Step1Credentials(
                        uiState = uiState,
                        onToggleTab = onToggleTab,
                        onUpdateName = onUpdateName,
                        onUpdateEmail = onUpdateEmail,
                        onUpdatePassword = onUpdatePassword,
                        onTogglePasswordVisibility = onTogglePasswordVisibility,
                        onNextStep = onNextStep,
                        onForgotPassword = onForgotPassword
                    )
                } else {
                    Step2AvatarSelection(
                        uiState = uiState,
                        onSelectAvatar = onSelectAvatar,
                        onPreviousStep = onPreviousStep,
                        onLogin = onLogin
                    )
                }
            }
        }

        // Parental note
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${stringResource(R.string.parental_note)} ${stringResource(R.string.learn_more)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============================================================================
// Step 1: Credentials form
// ============================================================================
@Composable
private fun Step1Credentials(
    uiState: LoginUiState,
    onToggleTab: (Boolean) -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onNextStep: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column {
        // Tab switcher
        TabSwitcher(
            isLogin = uiState.isLoginTab,
            onToggle = onToggleTab
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Name field (signup only)
        AnimatedVisibility(
            visible = !uiState.isLoginTab,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Column {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onUpdateName,
                    label = { Text(stringResource(R.string.signup_name_label)) },
                    placeholder = { Text(stringResource(R.string.signup_name_placeholder)) },
                    leadingIcon = { Text("👋", fontSize = 20.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Email field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onUpdateEmail,
            label = { Text(stringResource(R.string.login_email)) },
            placeholder = { Text(stringResource(R.string.login_email_placeholder)) },
            leadingIcon = { Text("📧", fontSize = 20.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onUpdatePassword,
            label = { Text(stringResource(R.string.login_password)) },
            placeholder = { Text(stringResource(R.string.login_password_placeholder)) },
            leadingIcon = { Text("🔐", fontSize = 20.sp) },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.showPassword)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (uiState.showPassword) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Forgot password (login mode only)
        if (uiState.isLoginTab) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    stringResource(R.string.forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CTA Button
        LoginButton(
            isLoading = uiState.isLoading,
            label = if (uiState.isLoginTab) stringResource(R.string.login_button) else stringResource(R.string.signup_continue),
            showIcon = uiState.isLoginTab,
            onClick = onNextStep
        )
    }
}

// ============================================================================
// Step 2: Avatar selection
// ============================================================================
@Composable
private fun Step2AvatarSelection(
    uiState: LoginUiState,
    onSelectAvatar: (Int) -> Unit,
    onPreviousStep: () -> Unit,
    onLogin: () -> Unit
) {
    Column {
        // Back button
        TextButton(
            onClick = onPreviousStep,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(stringResource(R.string.avatar_back), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Title
        Text(
            text = stringResource(R.string.avatar_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.avatar_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar 3x2 grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AvatarOption.options) { avatar ->
                AvatarGridItem(
                    avatar = avatar,
                    isSelected = uiState.selectedAvatar == avatar.id,
                    onClick = { onSelectAvatar(avatar.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar preview
        val selectedAvatar = AvatarOption.options.find { it.id == uiState.selectedAvatar }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(selectedAvatar?.emoji ?: "🐷", fontSize = 40.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${uiState.name} o(a) ${selectedAvatar?.name ?: "Leão"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.avatar_level_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CTA Button
        LoginButton(
            isLoading = uiState.isLoading,
            label = stringResource(R.string.avatar_start),
            showIcon = false,
            onClick = onLogin
        )
    }
}

// ============================================================================
// Tab Switcher
// ============================================================================
@Composable
private fun TabSwitcher(
    isLogin: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Entrar tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isLogin) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onToggle(true) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.login_tab),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isLogin) FontWeight.Bold else FontWeight.Normal,
                    color = if (isLogin) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Criar Conta tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (!isLogin) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onToggle(false) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.signup_tab),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (!isLogin) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isLogin) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// Avatar Grid Item
// ============================================================================
@Composable
private fun AvatarGridItem(
    avatar: AvatarOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 300f
        ),
        label = "avatarScale"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                else Modifier.border(2.dp, Color.Transparent, RoundedCornerShape(16.dp))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(avatar.emoji, fontSize = 28.sp)
            Text(
                avatar.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Checkmark overlay when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
            }
        }
    }
}

// ============================================================================
// Login Button with spring animation and loading state
// ============================================================================
@Composable
private fun LoginButton(
    isLoading: Boolean,
    label: String,
    showIcon: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 300f
        ),
        label = "buttonScale"
    )

    var rotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            while (true) {
                rotation += 15f
                if (rotation >= 360f) rotation = 0f
                kotlinx.coroutines.delay(42) // ~24fps for smooth 360°/s spin
            }
        } else {
            rotation = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(GradientPresets.gradientPrimary)
            .clickable(enabled = !isLoading) {
                isPressed = true
                onClick()
                isPressed = false
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            // Coin-spin loader
            Text(
                "🪙",
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (showIcon) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✨", fontSize = 20.sp)
                }
            }
        }
    }
}

// ============================================================================
// Layer 5: Footer with decorative characters
// ============================================================================
@Composable
private fun FooterSection() {
    // Use mutable states updated via LaunchedEffect for wiggle animations
    var foxY by remember { mutableFloatStateOf(0f) }
    var foxR by remember { mutableFloatStateOf(0f) }
    var rabbitY by remember { mutableFloatStateOf(0f) }
    var rabbitR by remember { mutableFloatStateOf(0f) }
    var bearY by remember { mutableFloatStateOf(0f) }
    var bearR by remember { mutableFloatStateOf(0f) }

    // Animate wiggles
    LaunchedEffect(Unit) {
        while (true) {
            // Fox wiggle
            foxY = -4f; foxR = -5f
            kotlinx.coroutines.delay(300)
            foxY = 4f; foxR = 5f
            kotlinx.coroutines.delay(300)
            // Rabbit wiggle (staggered)
            rabbitY = -4f; rabbitR = -5f
            kotlinx.coroutines.delay(300)
            rabbitY = 4f; rabbitR = 5f
            kotlinx.coroutines.delay(300)
            // Bear wiggle (staggered)
            bearY = -4f; bearR = -5f
            kotlinx.coroutines.delay(300)
            bearY = 4f; bearR = 5f
            kotlinx.coroutines.delay(300)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Fox
        Text(
            "🦊",
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-80).dp)
                .graphicsLayer {
                    translationY = foxY
                    rotationZ = foxR
                }
        )

        // Rabbit
        Text(
            "🐰",
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 80.dp)
                .graphicsLayer {
                    translationY = rabbitY
                    rotationZ = rabbitR
                }
        )

        // Bear
        Text(
            "🐻",
            fontSize = 44.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    translationY = bearY
                    rotationZ = bearR
                }
        )
    }
}
