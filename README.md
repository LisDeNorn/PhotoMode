# PhotoMode

Android app with short lessons on photography basics: light, horizon, angle, framing, and shooting scenarios (cafe portrait, group photo). Each lesson has theory steps, instructions, and practice; progress is saved on device.

## Screenshots

_Add screenshots of the home screen and a lesson screen (e.g. from a `screenshots/` folder)._

## Features

- **Lesson of the day** and **Fundamentals** / **Scenarios** sections on the home screen
- **Lesson steps:** theory (good vs bad examples with interactive images), instruction (text + example image), practice (task)
- **Progress:** completed lessons saved locally (DataStore)
- **Current mission** shown in the top bar

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** Clean Architecture (domain / data / app)
- **DI:** Koin
- **Navigation:** Navigation Compose
- **Persistence:** DataStore (Preferences)
- **Images:** Coil
- **Data:** lesson content from `data` module assets (`lessons.json`)

## Project Structure

```
PhotoMode/
├── app/          # UI, screens, ViewModels, Compose, Koin setup
├── data/         # Repository impl, LocalLessonStorage, DataStore, assets (lessons.json)
├── domain/       # Models, repository interfaces, use cases
└── README.md
```

## Requirements

- Android Studio (latest stable recommended)
- JDK 11+
- minSdk 24, targetSdk 36

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/PhotoMode.git
   cd PhotoMode
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run on a device or emulator (**Run** → Run 'app' or `Shift+F10`).

To build a debug APK from the command line:

```bash
./gradlew assembleDebug
```

The APK will be in `app/build/outputs/apk/debug/app-debug.apk`. You can upload it to [Releases](https://github.com/YOUR_USERNAME/PhotoMode/releases) so others can install without building.

## Content

Lesson content is in `data/src/main/assets/lessons.json`. After editing, use **Build → Clean Project**, then **Run** so the app picks up changes.
