# Izem - Tamazight Language Learning App

**Izem** (ⵉⵣⵎ - meaning "Lion" in Tamazight) is a modern, interactive Android application designed to help users learn the Tamazight (Amazigh) language through fun quizzes, structured vocabulary, grammar, audio pronunciations, and dynamic leaderboards.

---

## Features

- **Rich Vocabulary & Categories**: Explore words, phrases, verbs, and alphabet categories with clean cards and definitions.
- **Native Audio Pronunciation**: Listen to authentic Tamazight audio pronunciations for words and phrases.
- **Interactive Quizzes**: Test your knowledge with 10 randomized, non-repeating questions per session, complete with victory audio effects.
- **Global Leaderboard & Roar Challenges**: Earn points from quizzes, climb the leaderboard, and send interactive "Roars" (FCM Push Notifications) to challenge other learners.
- **Modern Jetpack Compose UI**: Built with Material 3 design system, responsive layouts, RTL support (Arabic & English UI options), and smooth animations.
- **Firebase Auth**: Secure Google Sign-In & guest profiles.

---

## Tech Stack & Architecture

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: Dagger Hilt + KSP
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Backend Services**:
  - **Cloud Firestore**: Real-time database for content, leaderboards, and user interactions.
  - **Firebase Authentication**: Google Auth & User session management.
  - **Firebase Cloud Messaging (FCM)**: Push notifications for social interactions.
  - **Firebase Cloud Functions**: Node.js backend logic for spam-checked notification dispatching.
- **Image & Audio**: Coil Compose, Android MediaPlayer
- **Monetization**: Google Mobile Ads (AdMob)

---

## Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Jellyfish or newer
- **JDK**: Version 17+
- **Android SDK**: API level 35+ (Target SDK 37)

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/RachidNichan/Izem.git
   cd Izem
   ```

2. **Configure Firebase**
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `com.relyvo.izem`.
   - Download `google-services.json` and place it in the `app/` directory:
     ```bash
     cp app/google-services.json.example app/google-services.json
     # Replace with your actual Firebase config
     ```

3. **Build & Run**
   - Open the project in Android Studio.
   - Sync Gradle dependencies.
   - Run on an emulator or connected physical Android device.

---

## Project Structure

```text
Izem/
├── app/
│   ├── src/main/java/com/relyvo/izem/
│   │   ├── data/           # Repositories & Data Sources
│   │   ├── di/             # Hilt Dependency Injection Modules
│   │   ├── model/          # Data Models (Word, Quiz, UserProfile, etc.)
│   │   ├── service/        # FCM & Background Workers
│   │   ├── ui/             # Jetpack Compose Screens, Components & Modals
│   │   ├── utils/          # Audio Player, Ad Manager, Utilities
│   │   └── viewmodel/      # Application ViewModels
├── functions/              # Firebase Cloud Functions (Node.js)
├── scripts/                # Database Backup & Restore Utilities
└── google-services.json.example
```

---

## Contributing

Contributions, issues, and feature requests are welcome! Feel free to check out the [issues page](https://github.com/RachidNichan/Izem/issues).

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
