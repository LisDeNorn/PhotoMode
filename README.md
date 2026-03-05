# PhotoMode

**Kotlin • Jetpack Compose • Clean Architecture • Koin • DataStore**

A modular Android app for structured photography lessons — built with Jetpack Compose and a clear separation of UI, domain, and data.

Each lesson follows a step-based learning flow: **Theory → Instruction → Practice**. Users see visual examples, receive simple shooting instructions, and get a practice task (coming soon). Progress is saved locally. The codebase is structured for readability and maintainability: single-responsibility use cases, repository abstractions, and state-driven Compose UI.

---

## Product Vision

This project aims to teach mobile photography through short, visual, and practical lessons.

Instead of long tutorials, each lesson focuses on a single concept and follows a structured flow:

Theory → Instruction → Practice

Users first see visual examples, then receive simple shooting instructions, and finally get a practice task (coming soon).

The goal is to make photography learning fast, practical, and accessible directly from a mobile device.

---

## What this project demonstrates

- **Clean Architecture** — `domain` (models, contracts, use cases), `data` (repos, storage, assets), `app` (Compose, ViewModels, navigation)
- **Modern Android** — Kotlin, Jetpack Compose, Material 3, Navigation Compose
- **State & DI** — Unidirectional data flow, Koin for dependency injection
- **Persistence** — DataStore for progress; lesson content from JSON assets
- **Reusable UI** — Composable step cards (theory, instruction, practice), shared components

---

## Overview

The app teaches photography basics: light, horizon, angle, framing, and real-world scenarios (e.g. cafe portrait, group photo). Users move through steps, see good vs bad examples, and complete lessons; progress is saved locally.

| Home Screen | Lesson Screen |
|-------------|---------------|
| ![](screenshots/home.png) | ![](screenshots/lesson.png) |

_Screenshots from the running application._

---

## Features

- Step-based lesson engine (Theory / Instruction / Practice)
- Interactive image comparison (tap to reveal labels, auto-dismiss)
- Lesson of the day + categories (Fundamentals, Scenarios)
- Priority-based lesson sorting based on mission and completion state
- Local progress persistence (DataStore)
- Current mission in the app bar

---

## Architecture

### Architecture layers

```
app (presentation)
   ↓
data (repositories, storage)
   ↓
domain (models, use cases)
```

The domain layer is pure Kotlin and does not depend on Android APIs, which keeps business logic isolated and testable.

**Module layout:**

```
PhotoMode/
├── app/        Compose UI, ViewModels, navigation, Koin module
├── data/       Repository implementations, DataStore, LocalLessonStorage, lessons.json
├── domain/     Models, repository interfaces, use cases
└── screenshots/
```

- **domain** — No Android dependencies. Defines `Lesson`, `LessonStep`, repository contracts, and use cases.
- **data** — Implements repositories, parses `lessons.json`, persists progress with DataStore.
- **app** — Compose screens, ViewModels (state + events), single Activity, Koin DI.

UI state is held in ViewModels. Business logic is implemented in dedicated use cases rather than inside ViewModels. Composables are stateless and receive data + callbacks. Navigation is type-safe via routes and arguments.

---

## Tech stack

| Layer        | Choice |
|-------------|--------|
| Language    | Kotlin |
| UI          | Jetpack Compose, Material 3 |
| Architecture| Clean Architecture (3 modules) |
| DI          | Koin |
| Navigation  | Navigation Compose |
| Persistence | DataStore (Preferences) |
| Images      | Coil |

---

## Running the project

**Requirements:** Android Studio (latest stable), JDK 11+, minSdk 24.

1. Clone and open:
   ```bash
   git clone https://github.com/<your-username>/PhotoMode.git
   cd PhotoMode
   ```
   Open the project in Android Studio and sync Gradle.

2. Run on a device or emulator (Run → Run 'app').

**Build debug APK from terminal:**

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`. You can upload this to GitHub Releases so others can install without building.

---

Lesson content is defined in `data/src/main/assets/lessons.json`. After editing: **Build → Clean Project**, then run the app again.
