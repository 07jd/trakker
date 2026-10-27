package com.jd.trakker.ui.setup

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jd.trakker.R
import com.jd.trakker.ui.setup.components.ConfirmScreen
import com.jd.trakker.ui.setup.components.FeedScreen
import com.jd.trakker.ui.setup.components.ThemeScreen
import com.jd.trakker.ui.setup.components.WelcomeScreen

private enum class Screens(
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
private fun SetupNavHost(
    controller: NavHostController,
    uiState: SetupState,
    onEvent: (SetupEvent) -> Unit
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
        },
        exitTransition = {
            slideOutOfContainer(
                (transitionDirection(
                    Screens.entries.find { it.route == initialState.destination.route },
                    Screens.entries.find { it.route == targetState.destination.route }))
            )
        })
    {
        Screens.entries.forEach { screen ->
            composable(screen.route) {
                when (screen) {
                    Screens.WELCOME_SCREEN -> WelcomeScreen()
                    Screens.THEME -> ThemeScreen(uiState.theme, uiState.useSystemColor, uiState.useVibrations) { onEvent(it) }
                    Screens.FEED -> FeedScreen(uiState) { onEvent(it) }
                    Screens.CONFIRM -> ConfirmScreen()
                }
            }
        }
    }
}

@Composable
fun SetupScreen(
    uiState: SetupState,
    onEvent: (SetupEvent) -> Unit,
    navController: NavHostController
) {
    val haptics = LocalHapticFeedback.current
    val backStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val currentScreen = Screens.entries.firstOrNull {
        it.route == backStackEntry?.destination?.route
    } ?: Screens.WELCOME_SCREEN

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

            val progressAnimated by animateFloatAsState(
                targetValue = currentScreen.ordinal.toFloat() / (Screens.entries.size.toFloat() - 1.0f),
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )

            LinearProgressIndicator(
                progress = { progressAnimated },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                drawStopIndicator = {}
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

            Row(modifier = Modifier.fillMaxSize()) {
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
                        if(uiState.useVibrations) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        if(uiState.useVibrations) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
        Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
            SetupNavHost(navController, uiState, onEvent)
        }
    }
}