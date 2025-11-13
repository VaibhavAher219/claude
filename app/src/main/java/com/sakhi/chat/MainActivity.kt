package com.sakhi.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sakhi.chat.data.PreferencesManager
import com.sakhi.chat.navigation.SakhiNavigation
import com.sakhi.chat.repository.FirebaseRepository
import com.sakhi.chat.ui.theme.SakhiTheme
import com.sakhi.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repositories
        firebaseRepository = FirebaseRepository()
        preferencesManager = PreferencesManager(applicationContext)

        // Initialize ViewModel
        chatViewModel = ChatViewModel(
            firebaseRepository = firebaseRepository,
            apiKeyProvider = {
                preferencesManager.apiKeyFlow.first()
            }
        )

        setContent {
            SakhiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SakhiNavigation(
                        navController = navController,
                        firebaseRepository = firebaseRepository,
                        preferencesManager = preferencesManager,
                        chatViewModel = chatViewModel
                    )
                }
            }
        }
    }
}
