package com.jd.trakker.ui.setup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jd.trakker.R
import com.jd.trakker.ui.theme.ThemeType
import com.jd.trakker.ui.theme.getPrimaryAndSecondayColors
import com.jd.trakker.ui.theme.isDynamicColorAvailable

enum class Screens(
    val route: String
) {
    WELCOME_SCREEN("welcome"),
    THEME("theme"),
    FEED("feed"),
    CONFIRM("confirm")
}

private fun nextScreen(controller: NavHostController, currentScreen: Screens) {
    val nextScreen = Screens.entries.getOrNull(currentScreen.ordinal + 1) ?: return
    controller.navigate(nextScreen.route)
}

private fun previousScreen(controller: NavController, currentScreen: Screens) {
    if (currentScreen.ordinal == Screens.entries.first().ordinal) return

    val previousScreen = Screens.entries.getOrNull(currentScreen.ordinal - 1) ?: return
    controller.navigate(previousScreen.route) {
        popUpTo(currentScreen.route) {
            inclusive = true
        }

        launchSingleTop = true
    }
}

private fun transitionDirection(
    old: Screens?,
    current: Screens?
): AnimatedContentTransitionScope.SlideDirection {
    return if (old == null || current == null || current.ordinal > old.ordinal) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

@Composable
fun SetupNavHost(
    controller: NavHostController, uiState: SetupState, onEvent: (SetupEvent) -> Unit
) {
    NavHost(
        startDestination = Screens.WELCOME_SCREEN.route,
        navController = controller,
        enterTransition = {
            slideIntoContainer(
                transitionDirection(
                    Screens.entries.find { it.route == initialState.destination.route },
                    Screens.entries.find { it.route == targetState.destination.route })
            )
        }, exitTransition = {
            slideOutOfContainer(
                (transitionDirection(
                    Screens.entries.find { it.route == initialState.destination.route },
                    Screens.entries.find { it.route == targetState.destination.route }))
            )
        }) {
        Screens.entries.forEach { screen ->
            composable(screen.route) {
                when (screen) {
                    Screens.WELCOME_SCREEN -> WelcomeScreen()
                    Screens.THEME -> ThemeScreen(uiState.theme, uiState.useSystemColor, onEvent)
                    Screens.FEED -> FeedScreen(uiState, onEvent)
                    Screens.CONFIRM -> ConfirmScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(
    uiState: SetupState,
    onEvent: (SetupEvent) -> Unit,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screens.entries.firstOrNull() {
        it.route == backStackEntry?.destination?.route
    } ?: Screens.WELCOME_SCREEN

    // Interact only by the provided buttons
    BackHandler(enabled = true) {}

    Scaffold(topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
        ) {
            Text(
                text = stringResource(R.string.setup_title),
                textAlign = TextAlign.Left,
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            val progress by animateFloatAsState(
                targetValue = currentScreen.ordinal.toFloat() / (Screens.entries.size.toFloat() - 1.0f),
                animationSpec = WavyProgressIndicatorDefaults.ProgressAnimationSpec
            )

            LinearWavyProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                stopSize = 0.dp
            )

        }
    }, bottomBar = {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(bottom = 25.dp)
                .padding(horizontal = 25.dp)
        ) {
            // This is to avoid buggy animation when one button expands
            val spaceBetweenButtons = 15.dp
            val backButtonWidth = maxWidth * 0.3f

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxHeight()
                        .animateContentSize()
                        .width(
                            if (currentScreen != Screens.WELCOME_SCREEN) backButtonWidth
                            else 0.dp
                        ), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ), onClick = {
                        previousScreen(navController, currentScreen)
                    }) {
                    Text(stringResource(R.string.setup_back))
                }

                Spacer(
                    modifier = Modifier
                        .animateContentSize()
                        .width(
                            if (currentScreen != Screens.WELCOME_SCREEN) spaceBetweenButtons
                            else 0.dp
                        )
                )


                // Next button
                Button(
                    modifier = Modifier.fillMaxSize(), onClick = {
                        if (currentScreen == Screens.CONFIRM) {
                            onEvent(SetupEvent.finish)
                        } else {
                            nextScreen(navController, currentScreen)
                        }
                    }) {
                    if (currentScreen == Screens.CONFIRM) {
                        Text(stringResource(R.string.setup_finish))
                    } else {
                        Text(stringResource(R.string.setup_next))
                    }
                }
            }

        }
    }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 25.dp)
                .padding(horizontal = 25.dp)
                .fillMaxSize()
        ) {
            SetupNavHost(navController, uiState, onEvent)
        }
    }
}

// Setup stages
@Composable
fun WelcomeScreen() {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "\tBem vindo ao Trakker, afim de utilizar o aplicativo " + "ao seu melhor deverá conectar-se a varios serviços " + "provenientes de metadados.\n\tCaso não queira poderá sempre " + "importar e criar metadados de media localmente no seu dispositvo.",
            lineHeight = 25.sp,
        )
    }
}

@Composable
fun ThemeScreen(currentTheme: ThemeType, dynColor: Boolean, onEvent: (SetupEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Dynamic colors setting
        if (isDynamicColorAvailable()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setup_useSystemColors),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Usar as cores do sistema",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = dynColor,
                        onCheckedChange = {
                            onEvent(SetupEvent.useSystemColor(it))
                        }
                    )
                }
            }
        }

        // Theme selection
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tema da aplicação",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Escolhe o aspeto da aplicação.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeType.entries.forEach { theme ->
                    val selected = theme == currentTheme
                    val (primary, secondary) =
                        getPrimaryAndSecondayColors(theme, dynColor)

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onEvent(SetupEvent.changeTheme(theme))
                            },
                        shape = RoundedCornerShape(16.dp),
                        border = if (selected) {
                            BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary
                            )
                        } else {
                            null
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Color preview
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(primary)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(secondary)
                                )
                            }

                            // Theme name
                            Text(
                                text = theme.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )

                            // Selection indicator
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selected) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedScreen(onEvent: SetupState, onEvent1: (SetupEvent) -> Unit) {
    Text("TODO :)")
}

@Composable
fun ConfirmScreen() {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = R.string.setup_confirm_screen_text.toString(),
            lineHeight = 25.sp,
        )
    }
}