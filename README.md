# Sakhi - AI Chat App (सखी)

**Sakhi** is a native Android AI chat application built with Marathi language support. The app provides a beautiful and intuitive interface for users to interact with an AI assistant in Marathi.

## Features

- 🇮🇳 **Native Marathi Language Support** - Complete UI and conversation in Marathi
- 💬 **Modern Chat Interface** - Beautiful Material Design 3 UI with Jetpack Compose
- 🤖 **AI-Powered Conversations** - Placeholder for AI integration (ready for API integration)
- 🎨 **Beautiful Theming** - Support for both light and dark themes
- ⚡ **Fast and Responsive** - Built with modern Android architecture (MVVM)
- 📱 **Adaptive UI** - Works seamlessly on all Android devices

## Screenshots

The app features:
- Clean chat interface with message bubbles
- Marathi keyboard support
- Time stamps for messages
- Auto-scroll to latest messages
- Clear chat functionality

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL

### Dependencies

- AndroidX Core KTX
- Jetpack Compose (Material 3)
- ViewModel & Lifecycle
- Coroutines
- Retrofit (ready for AI API integration)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/sakhi/chat/
│   │   ├── MainActivity.kt           # Main entry point
│   │   ├── model/
│   │   │   └── Message.kt            # Message data model
│   │   ├── viewmodel/
│   │   │   └── ChatViewModel.kt      # Chat logic & state management
│   │   └── ui/
│   │       ├── ChatScreen.kt         # Main chat UI
│   │       └── theme/                # App theming
│   │           ├── Theme.kt
│   │           └── Type.kt
│   └── res/
│       ├── values/                   # Default (English) strings
│       ├── values-mr/                # Marathi strings
│       └── drawable/                 # App icons and graphics
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or higher
- Android SDK with API 34
- Gradle 8.2 or higher

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd claude
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Click on "Open an Existing Project"
   - Navigate to the cloned repository
   - Select the project root directory

3. **Sync Gradle**
   - Android Studio will automatically start syncing Gradle
   - Wait for the sync to complete
   - If prompted, accept any SDK licenses

4. **Build the project**
   ```bash
   ./gradlew build
   ```
   Or use Android Studio: `Build > Make Project`

5. **Run on Device/Emulator**
   - Connect an Android device with USB debugging enabled, or start an emulator
   - Click the "Run" button in Android Studio
   - Select your target device

## Language Support

The app is designed with Marathi as the primary language. All UI elements, messages, and interactions are in Marathi.

### Supported Languages

- **Marathi (मराठी)** - Primary language
- **English** - Fallback language

To add more languages, create a new `values-{language-code}/strings.xml` file.

## AI Integration

Currently, the app uses placeholder AI responses. To integrate with a real AI service:

### Option 1: OpenAI Integration

1. Add your OpenAI API key to `local.properties`:
   ```properties
   OPENAI_API_KEY=your-api-key-here
   ```

2. Update `ChatViewModel.kt` to call OpenAI API:
   ```kotlin
   // Use Retrofit to call OpenAI Chat Completions API
   // Endpoint: https://api.openai.com/v1/chat/completions
   ```

### Option 2: Google Gemini Integration

1. Get API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Add to `local.properties`:
   ```properties
   GEMINI_API_KEY=your-api-key-here
   ```
3. Update `ChatViewModel.kt` with Gemini API calls

### Option 3: Custom AI Backend

Update the `generateAIResponse()` function in `ChatViewModel.kt` to call your custom API endpoint.

## Customization

### Change App Name

Edit `app/src/main/res/values/strings.xml` and `values-mr/strings.xml`:
```xml
<string name="app_name">YourAppName</string>
```

### Change Theme Colors

Edit `app/src/main/java/com/sakhi/chat/ui/theme/Theme.kt`:
```kotlin
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFYourColor),
    // ... other colors
)
```

### Change Package Name

1. Update `namespace` in `app/build.gradle.kts`
2. Rename package directories
3. Update imports throughout the project

## Building APK

### Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

**Note**: You'll need to configure signing for release builds.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## Future Enhancements

- [ ] Real AI API integration (OpenAI, Gemini, Claude, etc.)
- [ ] Voice input support in Marathi
- [ ] Text-to-speech for AI responses
- [ ] Chat history persistence
- [ ] Multiple conversation threads
- [ ] Image/file sharing in chat
- [ ] Offline mode with cached responses
- [ ] User preferences and settings
- [ ] Multi-language support beyond Marathi

## License

This project is open source and available under the MIT License.

## Contact

For questions or support, please open an issue on GitHub.

---

**Made with ❤️ for Marathi speakers**

**मराठी भाषिकांसाठी प्रेमाने बनवलेले**
