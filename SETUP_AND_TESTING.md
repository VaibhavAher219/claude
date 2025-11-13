# Sakhi Full-Stack Setup and Testing Guide

This guide will walk you through setting up and testing the Sakhi AI Chat App with Firebase backend and OpenAI integration.

## 🏗️ Architecture Overview

**Backend:**
- **Firebase Authentication** - User login/signup with email/password
- **Cloud Firestore** - Real-time chat history storage
- **DataStore** - Secure local storage for OpenAI API key

**AI Integration:**
- **OpenAI GPT-3.5-turbo** - Complete integration, just add API key
- System prompt in Marathi for native language responses

**Frontend:**
- **Jetpack Compose** - Modern declarative UI
- **MVVM Architecture** - Clean separation of concerns
- **Navigation Component** - Screen navigation

---

## 📋 Prerequisites

Before starting, ensure you have:

1. **Android Studio** (Hedgehog 2023.1.1 or later)
2. **JDK 8 or higher**
3. **Android SDK** with API 34
4. **Google Account** (for Firebase)
5. **OpenAI Account** (for API key)

---

## 🔥 Part 1: Firebase Setup (10 minutes)

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter project name: `sakhi-chat` (or your preferred name)
4. **Disable Google Analytics** (optional, can be enabled later)
5. Click **"Create project"**

### Step 2: Add Android App to Firebase

1. In Firebase Console, click **"Add app"** → Select **Android** icon
2. Fill in the details:
   - **Android package name**: `com.sakhi.chat` (must match exactly)
   - **App nickname**: Sakhi (optional)
   - **Debug signing certificate SHA-1**: Leave blank for now
3. Click **"Register app"**

### Step 3: Download google-services.json

1. Download the `google-services.json` file
2. Move it to: `app/google-services.json` (in your project's app folder)
   ```bash
   # From your downloads folder
   mv ~/Downloads/google-services.json /path/to/claude/app/
   ```

### Step 4: Enable Firebase Authentication

1. In Firebase Console, go to **Build** → **Authentication**
2. Click **"Get started"**
3. Click **"Sign-in method"** tab
4. Enable **"Email/Password"**
   - Toggle the first "Email/Password" option to **Enabled**
   - Click **"Save"**

### Step 5: Enable Cloud Firestore

1. In Firebase Console, go to **Build** → **Firestore Database**
2. Click **"Create database"**
3. Select **"Start in test mode"** (for development)
4. Choose a location (preferably closest to you)
5. Click **"Enable"**

**Security Rules (Optional - for production):**
After testing, update Firestore rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 🤖 Part 2: OpenAI API Setup (5 minutes)

### Step 1: Create OpenAI Account

1. Go to [OpenAI Platform](https://platform.openai.com/)
2. Sign up or log in
3. Go to [API Keys](https://platform.openai.com/api-keys)
4. Click **"Create new secret key"**
5. Name it: `sakhi-app`
6. **IMPORTANT**: Copy the key immediately (you won't see it again!)
   - Format: `sk-proj-...` or `sk-...`

### Step 2: Add Billing (Required)

OpenAI requires a payment method for API access:

1. Go to [Billing](https://platform.openai.com/account/billing/overview)
2. Click **"Add payment method"**
3. Add your credit/debit card
4. Set a usage limit (recommended: $5-10 for testing)

**Cost Estimate:**
- GPT-3.5-turbo: ~$0.002 per 1,000 tokens
- Average conversation: ~500 tokens = $0.001
- 1000 messages: ~$1

---

## 🛠️ Part 3: Build and Run the App (10 minutes)

### Step 1: Open Project in Android Studio

```bash
# Clone if you haven't already
git clone <repository-url>
cd claude

# Open in Android Studio
# File → Open → Select the 'claude' folder
```

### Step 2: Sync Gradle

1. Android Studio will show "Gradle sync" prompt
2. Click **"Sync Now"**
3. Wait for sync to complete (3-5 minutes first time)

**Common Issues:**
- If sync fails, check internet connection
- Make sure `google-services.json` is in `app/` folder
- Try **File → Invalidate Caches → Restart**

### Step 3: Build the App

**Option A: Android Studio**
1. Click **Build → Make Project** (or Ctrl+F9 / Cmd+F9)
2. Wait for build to complete

**Option B: Command Line**
```bash
./gradlew assembleDebug
```

### Step 4: Run on Emulator

**Create Emulator:**
1. Click **Device Manager** (phone icon in toolbar)
2. Click **"Create Device"**
3. Select **Pixel 6** (or any device)
4. Download system image: **Android 14 (API 34)**
5. Click **"Finish"**

**Run App:**
1. Select your emulator from device dropdown
2. Click **Run** button (green play icon)
3. Wait for emulator to start and app to install

### Step 5: Run on Physical Device

1. Enable **Developer Options** on your Android phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging**:
   - Settings → System → Developer Options → USB Debugging
3. Connect phone via USB
4. Click **Run** in Android Studio
5. Select your device and click **OK**

---

## 🧪 Part 4: Testing the App (Complete Walkthrough)

### Test 1: User Registration

1. **Launch the app**
   - You should see the Login screen with "सखी" title

2. **Click "Sign Up करा"** (Sign Up button)

3. **Fill in registration form:**
   - Email: `test@example.com` (use your email)
   - Password: `test1234` (minimum 6 characters)
   - Confirm Password: `test1234`

4. **Click "Sign Up"**
   - Wait for loading indicator
   - Should navigate to Chat screen automatically

**Expected Result:** ✅ Account created, logged in, showing chat screen

**Troubleshooting:**
- ❌ "Network error" → Check internet connection
- ❌ "Email already in use" → Use different email or test login
- ❌ "Weak password" → Use at least 6 characters

### Test 2: Configure OpenAI API Key

1. **Click Settings icon** (gear icon in top-right)

2. **Paste your OpenAI API Key:**
   - In the "OpenAI API Key" field
   - Paste the key you copied earlier: `sk-...`

3. **Click "API Key जतन करा"** (Save API Key)

4. **Verify success message:**
   - Should see: "✓ API Key यशस्वीरित्या जतन झाली!"

5. **Click Back arrow** to return to chat

**Expected Result:** ✅ API key saved successfully

### Test 3: Chat with AI

1. **You should see welcome message:**
   ```
   नमस्कार! मी सखी आहे, तुमची AI सहाय्यक. आज मी तुम्हाला कशी मदत करू शकते?
   ```

2. **Test Message 1 - Greeting (English):**
   - Type: `Hello, how are you?`
   - Click Send button
   - Wait for AI response (3-5 seconds)

**Expected Result:** ✅ AI responds in Marathi (because of system prompt)

3. **Test Message 2 - Greeting (Marathi):**
   - Type: `नमस्कार! तुझं नाव काय आहे?`
   - Send

**Expected Result:** ✅ AI responds in Marathi

4. **Test Message 3 - Question:**
   - Type: `मराठी भाषेबद्दल काही सांग`
   - Send

**Expected Result:** ✅ AI provides information in Marathi

5. **Test Message 4 - Help Request:**
   - Type: `मला Python शिकायचं आहे, मदत कर`
   - Send

**Expected Result:** ✅ AI provides helpful response in Marathi

### Test 4: Chat History Persistence

1. **Send a few messages** (3-4 messages)
2. **Close the app completely**:
   - Press Back button to close
   - Or swipe away from Recent Apps
3. **Reopen the app**
4. **Login again** with same credentials

**Expected Result:** ✅ All previous messages are loaded from Firebase

### Test 5: Clear Chat

1. **Click Delete icon** (trash icon in top-right)
2. **Verify chat is cleared:**
   - All messages should disappear
   - Welcome message should reappear

**Expected Result:** ✅ Chat cleared, welcome message shown

### Test 6: Sign Out and Login

1. **Click Settings → "Sign Out"**
2. **Should return to Login screen**
3. **Login again** with your credentials:
   - Email: `test@example.com`
   - Password: `test1234`
4. **Click "Login"**

**Expected Result:** ✅ Successfully logged in, chat history restored

### Test 7: Multi-Turn Conversation

Test the conversation context (AI remembers previous messages):

1. **Message 1:** `माझं नाव राहुल आहे`
2. **Message 2:** `मी पुणे येथे राहतो`
3. **Message 3:** `माझं नाव काय आहे?`
   - AI should remember your name: "राहुल"
4. **Message 4:** `मी कुठे राहतो?`
   - AI should remember: "पुणे"

**Expected Result:** ✅ AI remembers context from previous messages

### Test 8: Error Handling

**Test 8a: No API Key**
1. Go to Settings → Clear the API key → Save
2. Try sending a message
3. **Expected:** Message in Marathi asking to add API key

**Test 8b: Invalid API Key**
1. Settings → Enter fake key: `sk-invalidkey123`
2. Try sending a message
3. **Expected:** Error message about invalid API key

**Test 8c: No Internet**
1. Turn off WiFi/Mobile data
2. Try sending a message
3. **Expected:** Network error message

---

## 📱 Part 5: Testing on Different Scenarios

### Test Marathi Keyboard Support

1. **Install Marathi keyboard** (if not already):
   - Settings → System → Languages & Input
   - Add "Marathi" language
   - Enable Marathi keyboard

2. **Switch to Marathi keyboard in app**
3. **Type message in Marathi script**
4. **Send and verify AI responds**

**Expected Result:** ✅ Full Marathi input/output support

### Test Dark Mode

1. **Enable dark mode on your device:**
   - Settings → Display → Dark theme
2. **Reopen Sakhi app**
3. **Verify dark theme is applied**

**Expected Result:** ✅ App switches to dark color scheme

### Test Rotation

1. **Send a few messages**
2. **Rotate device** (landscape mode)
3. **Verify messages are preserved**
4. **Rotate back to portrait**

**Expected Result:** ✅ No data loss on rotation

---

## 🎯 Part 6: Verify Firebase Console

### Check Authentication

1. Go to Firebase Console → Authentication → Users
2. **Expected:** Your test account should be listed with email

### Check Firestore Data

1. Go to Firebase Console → Firestore → Data
2. Navigate to: `users → [your-user-id] → messages`
3. **Expected:** All your chat messages stored with:
   - `id`: Timestamp
   - `text`: Message content
   - `isFromUser`: true/false
   - `timestamp`: Number

---

## 🔍 Debugging Common Issues

### App Won't Build

```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Or in Android Studio:
# Build → Clean Project
# Build → Rebuild Project
```

### "google-services.json missing" Error

```bash
# Check file location
ls app/google-services.json

# Should show the file. If not:
# Re-download from Firebase Console
```

### Firebase Authentication Not Working

1. Check internet connection
2. Verify Firebase Auth is enabled in console
3. Check logcat for detailed errors:
   ```
   Android Studio → Logcat → Filter: "Firebase"
   ```

### OpenAI API Errors

| Error | Solution |
|-------|----------|
| 401 Unauthorized | Invalid API key, check Settings |
| 429 Rate limit | You've exceeded free tier, add payment method |
| Network timeout | Check internet connection |
| Invalid model | API key might not have access to GPT-3.5 |

### App Crashes on Startup

1. **Check Logcat** for error messages
2. **Common causes:**
   - Missing google-services.json
   - Invalid Firebase configuration
   - Network permission denied

---

## 📊 Performance Testing

### Check API Response Time

1. Send message → Note time to get response
2. **Expected:** 2-5 seconds for typical responses
3. **If slower:** Check internet speed

### Check Chat History Load Time

1. Create 50+ messages
2. Close and reopen app
3. **Expected:** Messages load within 1-2 seconds

### Check Memory Usage

1. Android Studio → Profiler → Memory
2. Run app and chat for 5 minutes
3. **Expected:** Memory stays under 150MB

---

## 🚀 Going to Production

### Security Checklist

- [ ] Update Firestore security rules (restrict to authenticated users only)
- [ ] Remove debug logs from OpenAI service
- [ ] Enable Firebase App Check (prevent API abuse)
- [ ] Add ProGuard rules for release build
- [ ] Store API keys more securely (consider backend proxy)

### Before Publishing

```bash
# Create release build
./gradlew assembleRelease

# Sign with keystore
# Follow Android's signing guide
```

### Firebase Security Rules (Production)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }

    // Prevent unauthorized access
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

---

## 💡 Tips for Best Experience

1. **API Cost Management:**
   - Set OpenAI usage limits
   - Monitor spending in OpenAI dashboard
   - Consider caching common responses

2. **Better AI Responses:**
   - Ask questions in Marathi for best results
   - Be specific in your queries
   - Use complete sentences

3. **Data Management:**
   - Regularly clear old chats
   - Backup important conversations
   - Monitor Firestore usage

4. **Network Optimization:**
   - Use WiFi for initial testing
   - Test on different network conditions
   - Handle offline scenarios gracefully

---

## 🆘 Support and Troubleshooting

### Where to Get Help

- **Firebase Issues**: [Firebase Support](https://firebase.google.com/support)
- **OpenAI Issues**: [OpenAI Help Center](https://help.openai.com/)
- **Android Issues**: [Stack Overflow - Android Tag](https://stackoverflow.com/questions/tagged/android)

### Useful Logs

**View Android Logs:**
```bash
# Terminal/Command Prompt
adb logcat | grep -i "sakhi\|firebase\|openai"
```

**Firebase Debugging:**
```kotlin
// Enable Firebase debug logging
FirebaseFirestore.setLoggingEnabled(true)
```

---

## ✅ Testing Checklist

Print this checklist and mark items as you test:

- [ ] Firebase project created
- [ ] google-services.json added
- [ ] Firebase Auth enabled
- [ ] Firestore enabled
- [ ] OpenAI API key obtained
- [ ] OpenAI billing configured
- [ ] App builds successfully
- [ ] User registration works
- [ ] User login works
- [ ] API key saved successfully
- [ ] AI responds to messages
- [ ] Responses are in Marathi
- [ ] Chat history persists
- [ ] Clear chat works
- [ ] Sign out works
- [ ] Multi-turn conversations work
- [ ] Error handling works
- [ ] Dark mode works
- [ ] Marathi keyboard works
- [ ] App works on physical device

---

## 🎓 What You've Built

Congratulations! You now have a **production-ready full-stack AI chat application** with:

✅ **Backend:** Firebase Authentication + Firestore Database
✅ **AI:** OpenAI GPT-3.5 with Marathi language support
✅ **Frontend:** Modern Android app with Jetpack Compose
✅ **Features:**
- User authentication
- Real-time chat history
- Persistent storage
- Native Marathi language
- Context-aware conversations
- Beautiful Material Design UI

---

**Total Setup Time:** ~25-30 minutes
**Total Testing Time:** ~20-30 minutes

Happy Coding! 🚀 मराठी भाषिकांसाठी प्रेमाने बनवलेले!
