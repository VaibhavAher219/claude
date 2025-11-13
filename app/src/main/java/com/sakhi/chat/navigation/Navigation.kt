package com.sakhi.chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sakhi.chat.data.PreferencesManager
import com.sakhi.chat.model.BotType
import com.sakhi.chat.repository.FirebaseRepository
import com.sakhi.chat.ui.*
import com.sakhi.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object BotSelection : Screen("bot_selection")
    object ChatHistory : Screen("chat_history/{botType}") {
        fun createRoute(botType: BotType) = "chat_history/${botType.name}"
    }
    object Chat : Screen("chat/{botType}/{conversationId}") {
        fun createRoute(botType: BotType, conversationId: String) =
            "chat/${botType.name}/$conversationId"
    }
    object Settings : Screen("settings")
}

@Composable
fun SakhiNavigation(
    navController: NavHostController,
    firebaseRepository: FirebaseRepository,
    preferencesManager: PreferencesManager
) {
    val coroutineScope = rememberCoroutineScope()

    val startDestination = if (firebaseRepository.currentUser != null) {
        Screen.BotSelection.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                firebaseRepository = firebaseRepository,
                onLoginSuccess = {
                    navController.navigate(Screen.BotSelection.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                firebaseRepository = firebaseRepository,
                onSignUpSuccess = {
                    navController.navigate(Screen.BotSelection.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BotSelection.route) {
            BotSelectionScreen(
                firebaseRepository = firebaseRepository,
                onBotSelected = { botType ->
                    navController.navigate(Screen.ChatHistory.createRoute(botType))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onSignOut = {
                    firebaseRepository.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ChatHistory.route,
            arguments = listOf(
                navArgument("botType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val botTypeString = backStackEntry.arguments?.getString("botType") ?: return@composable
            val botType = BotType.valueOf(botTypeString)

            ChatHistoryScreen(
                botType = botType,
                firebaseRepository = firebaseRepository,
                onBackClick = {
                    navController.popBackStack()
                },
                onConversationSelected = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(botType, conversationId))
                },
                onNewChat = {
                    coroutineScope.launch {
                        val result = firebaseRepository.createConversation(botType)
                        result.onSuccess { conversationId ->
                            navController.navigate(Screen.Chat.createRoute(botType, conversationId))
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("botType") { type = NavType.StringType },
                navArgument("conversationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val botTypeString = backStackEntry.arguments?.getString("botType") ?: return@composable
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            val botType = BotType.valueOf(botTypeString)

            val chatViewModel: ChatViewModel = remember(conversationId) {
                ChatViewModel(
                    firebaseRepository = firebaseRepository,
                    apiKeyProvider = { preferencesManager.apiKeyFlow.first() },
                    conversationId = conversationId,
                    botType = botType
                )
            }

            ChatScreen(
                viewModel = chatViewModel,
                botType = botType,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                preferencesManager = preferencesManager,
                firebaseRepository = firebaseRepository,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
