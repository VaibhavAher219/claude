package com.sakhi.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sakhi.chat.data.PreferencesManager
import com.sakhi.chat.repository.FirebaseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    firebaseRepository: FirebaseRepository,
    onBackClick: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Load existing API key
    LaunchedEffect(Unit) {
        preferencesManager.apiKeyFlow.collect { savedKey ->
            apiKey = savedKey ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("सेटिंग्ज") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "परत जा")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "OpenAI API Configuration",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "तुमची OpenAI API key प्रविष्ट करा. ही key तुमच्या डिव्हाइसवर सुरक्षितपणे संग्रहित केली जाईल.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = {
                    apiKey = it
                    showSavedMessage = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("OpenAI API Key") },
                placeholder = { Text("sk-...") },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            "API Key दाखवा/लपवा"
                        )
                    }
                },
                supportingText = {
                    Text("API key मिळवण्यासाठी: https://platform.openai.com/api-keys")
                }
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        preferencesManager.saveApiKey(apiKey)
                        showSavedMessage = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank()
            ) {
                Text("API Key जतन करा")
            }

            if (showSavedMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "✓ API Key यशस्वीरित्या जतन झाली!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Account",
                style = MaterialTheme.typography.headlineSmall
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "लॉग इन केलेले: ${firebaseRepository.currentUser?.email ?: "None"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = {
                    firebaseRepository.signOut()
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sign Out")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version 1.0.0 - Made with ❤️ for Marathi speakers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
