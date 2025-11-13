# Sakhi - Quick Reference Guide

## 🚀 60-Second Setup

```bash
# 1. Clone
git clone <repo-url> && cd claude

# 2. Firebase
# - Go to console.firebase.google.com
# - Create project → Add Android app (com.sakhi.chat)
# - Download google-services.json → place in app/
# - Enable Auth (Email/Password) & Firestore

# 3. OpenAI
# - Get API key: platform.openai.com/api-keys
# - Copy the sk-... key

# 4. Run
# Open in Android Studio → Run → In app: Settings → Paste API key
```

## 📝 Essential Commands

```bash
# Build
./gradlew assembleDebug

# Clean
./gradlew clean

# Install
./gradlew installDebug

# Logs
adb logcat | grep -i sakhi
```

## 🔥 Firebase URLs

- **Console:** https://console.firebase.google.com
- **Auth:** Project → Build → Authentication
- **Firestore:** Project → Build → Firestore Database

## 🤖 OpenAI URLs

- **API Keys:** https://platform.openai.com/api-keys
- **Billing:** https://platform.openai.com/account/billing
- **Docs:** https://platform.openai.com/docs/

## 📱 Testing Checklist

- [ ] Sign up works
- [ ] Login works
- [ ] API key saves
- [ ] AI responds
- [ ] History persists
- [ ] Sign out works

## ⚡ Common Fixes

| Problem | Fix |
|---------|-----|
| Build error | Check `app/google-services.json` exists |
| Auth fails | Enable Email/Password in Firebase Console |
| AI no response | Add API key in Settings |
| 401 error | Invalid API key |
| 429 error | Add payment method to OpenAI |

## 🎯 Key Files

```
app/
├── google-services.json          ← Add this from Firebase
├── build.gradle.kts              ← Dependencies
└── src/main/
    ├── AndroidManifest.xml       ← Permissions
    └── java/com/sakhi/chat/
        ├── MainActivity.kt       ← Entry point
        ├── api/OpenAIService.kt  ← AI integration
        ├── repository/
        │   └── FirebaseRepository.kt  ← Backend
        ├── ui/
        │   ├── ChatScreen.kt     ← Main UI
        │   ├── LoginScreen.kt
        │   ├── SettingsScreen.kt
        │   └── SignUpScreen.kt
        └── viewmodel/
            └── ChatViewModel.kt  ← Logic
```

## 💰 Costs

**Firebase:** FREE (typical usage)
**OpenAI:** ~$0.001 per message (~$1 for 1000 messages)

## 🔐 Security Rules (Firestore)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

## 📞 Support

- Detailed Guide: [SETUP_AND_TESTING.md](SETUP_AND_TESTING.md)
- Firebase: https://firebase.google.com/support
- OpenAI: https://help.openai.com

## 🎨 Customization

**Change App Name:**
```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">YourName</string>

<!-- app/src/main/res/values-mr/strings.xml -->
<string name="app_name">तुमचे नाव</string>
```

**Change Colors:**
```kotlin
// app/src/main/java/com/sakhi/chat/ui/theme/Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFYourColor)
)
```

## 🧪 Test Messages

Try these in the app:

1. `नमस्कार! तू कोण आहेस?`
2. `मराठी भाषेबद्दल सांग`
3. `माझं नाव [Name] आहे` (then ask: `माझं नाव काय?`)
4. `Hello, how are you?` (AI will respond in Marathi)

## ⚠️ Important Notes

- API key stored locally (secure)
- Each user's chats are private
- Internet required
- OpenAI billing needed for API
- Free Firebase tier is sufficient

---

**Need help?** See full guide: [SETUP_AND_TESTING.md](SETUP_AND_TESTING.md)
