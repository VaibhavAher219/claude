package com.sakhi.chat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakhi.chat.data.PreferencesManager
import com.sakhi.chat.repository.FirebaseRepository
import com.sakhi.chat.ui.*
import com.sakhi.chat.viewmodel.ChatViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Chat : Screen("chat")
    object Settings : Screen("settings")
}

@Composable
fun SakhiNavigation(
    navController: NavHostController,
    firebaseRepository: FirebaseRepository,
    preferencesManager: PreferencesManager,
    chatViewModel: ChatViewModel
) {
    val startDestination = if (firebaseRepository.currentUser != null) {
        Screen.Chat.route
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
                    navController.navigate(Screen.Chat.route) {
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
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                preferencesManager = preferencesManager,
                firebaseRepository = firebaseRepository,
                onBackClick = {
                    chatViewModel.refreshApiKey()
                    navController.popBackStack()
                }
            )
        }
    }
}
