package com.jd.trakker.Setup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jd.trakker.R
import com.jd.trakker.Wrappers.ApiStatus

// TODO: Fazer as screens e a logicax
enum class Screens(
    val route: String
) {
    WELCOME_SCREEN("welcome"), TMDB("tmdb"), IGDB("igdb"), STEAM("steam"), OPEN_LIBRARY("openlib"), CONFIRM(
        "confirm"
    )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel
) {
    val navHostController = rememberNavController()
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentScreen = Screens.entries.firstOrNull() {
        it.route == backStackEntry?.destination?.route
    } ?: Screens.WELCOME_SCREEN

    // Handle back gesture
    BackHandler(enabled = true) {
        previousScreen(navHostController, currentScreen)
    }

    Scaffold(topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
        ) {
            Text(
                text = "Configuração\nInicial",
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
                        previousScreen(navHostController, currentScreen)
                    }) {
                    Text("Voltar")
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
                            viewModel.completeSetup()
                        } else {
                            nextScreen(navHostController, currentScreen)
                        }
                    }) {
                    if (currentScreen == Screens.CONFIRM) {
                        Text("Terminar")
                    } else {
                        Text("Próximo")
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
            SetupNavHost(navHostController, viewModel)
        }
    }
}

@Composable
fun SetupNavHost(
    controller: NavHostController, viewModel: SetupViewModel
) {
    NavHost(
        startDestination = Screens.WELCOME_SCREEN.route,
        navController = controller,
        enterTransition = {
            val current = Screens.entries.find { it.route == targetState.destination.route }
            val old = Screens.entries.find { it.route == initialState.destination.route }

            val slideDirection =
                if (old == null || current == null || current.ordinal > old.ordinal) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Right
                }

            slideIntoContainer(slideDirection)
        },
        exitTransition = {
            val current = Screens.entries.find { it.route == targetState.destination.route }
            val old = Screens.entries.find { it.route == initialState.destination.route }

            val slideDirection =
                if (old == null || current == null || current.ordinal > old.ordinal) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Right
                }

            slideOutOfContainer(slideDirection)
        }) {
        Screens.entries.forEach { screen ->
            composable(screen.route) {
                when (screen) {
                    Screens.WELCOME_SCREEN -> WelcomeScreen()
                    Screens.TMDB -> TmdbScreen(viewModel)
                    Screens.IGDB -> IgdbScreen()
                    Screens.STEAM -> SteamScreen()
                    Screens.OPEN_LIBRARY -> OpenlibScreen()
                    Screens.CONFIRM -> ConfirmScreen()
                }
            }
        }
    }
}

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
fun TmdbScreen(viewModel: SetupViewModel) {
    val textBoxState = rememberTextFieldState()

    Column(
        modifier = Modifier
            .padding(top = 25.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.tmdb),
                contentDescription = "The Movie Database Logo",
                modifier = Modifier.fillMaxSize()
            )
        }


        Column(
            modifier = Modifier
                .weight(0.70f)
                .padding(top = 15.dp)
        ) {
            OutlinedTextField(
                state = textBoxState,
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.tmdbRequestStatus.status != ApiStatus.LOADING
            )

            Button(
                onClick = {
                    val cleanApiKey = textBoxState.text.trim().toString()
                    if (cleanApiKey.isEmpty()) return@Button

                    viewModel.checkApiKey(cleanApiKey)
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(25),
                enabled = viewModel.tmdbRequestStatus.status != ApiStatus.LOADING
            ) {
                Text("Validar")
            }

            // Api visual feedback
            if ((viewModel.tmdbCheckedKey == textBoxState.text.trim()) && (viewModel.tmdbRequestStatus.status != ApiStatus.INACTIVE && viewModel.tmdbRequestStatus.status != ApiStatus.LOADING)) Text(
                text = when (viewModel.tmdbRequestStatus.status) {
                    ApiStatus.OK -> "API Key: Válida"
                    ApiStatus.NO_INTERNET -> "Api Key: Sem ligação à internet"
                    ApiStatus.UNAUTHORIZED -> "Api Key: Inválida"
                    else -> {
                        "API Key: Erro generico"
                    }
                },
                modifier = Modifier.padding(top = 10.dp),
                color = if (viewModel.tmdbRequestStatus.status != ApiStatus.OK) {
                    Color.Red
                } else {
                    Color.Green
                }
            )
        }
    }
}

@Composable
fun IgdbScreen() {
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.igdb),
                contentDescription = "IGDB Logo",
                modifier = Modifier.fillMaxSize()
            )
        }


        Column(
            modifier = Modifier
                .weight(0.70f)
                .padding(top = 15.dp)
        ) {
            OutlinedTextField(
                state = rememberTextFieldState(),
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    println("TODO: REST REQUEST TO VALIDADE API KEY; CALL API WRAPPER")
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(25),
            ) {
                Text("Validar")
            }

            // Api test feedback
            Text(
                text = "API Key válida",
                modifier = Modifier.padding(top = 10.dp),
                color = Color.Green
            )
        }
    }
}

@Composable
fun SteamScreen() {
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.steam),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = "Steam Logo",
                modifier = Modifier.fillMaxSize()
            )
        }


        Column(
            modifier = Modifier
                .weight(0.70f)
                .padding(top = 15.dp)
        ) {
            OutlinedTextField(
                state = rememberTextFieldState(),
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    println("TODO: REST REQUEST TO VALIDADE API KEY; CALL API WRAPPER")
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(25),
            ) {
                Text("Validar")
            }

            // Api test feedback
            Text(
                text = "API Key válida",
                modifier = Modifier.padding(top = 10.dp),
                color = Color.Green
            )
        }
    }
}

@Composable
fun OpenlibScreen() {
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.openlibrary),
                contentDescription = "Open Library Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }


        Column(
            modifier = Modifier
                .weight(0.70f)
                .padding(top = 15.dp)
        ) {
            OutlinedTextField(
                state = rememberTextFieldState(),
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    println("TODO: REST REQUEST TO VALIDADE API KEY; CALL API WRAPPER")
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(25),
            ) {
                Text("Validar")
            }

            // Api test feedback
            Text(
                text = "API Key válida",
                modifier = Modifier.padding(top = 10.dp),
                color = Color.Green
            )
        }
    }
}

@Composable
fun ConfirmScreen() {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "\tTudo pronto, caso deseje alterar, " + "adicionar ou remover algum serviço " + "poderá fazer nas definições do aplicativo.",
            lineHeight = 25.sp,
        )
    }
}