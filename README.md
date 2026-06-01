# PhotoMode

[![Tests](https://github.com/LisDeNorn/PhotoMode/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/LisDeNorn/PhotoMode/actions/workflows/ci.yml)

| | app | domain | data |
|---|:---:|:---:|:---:|
| **Coverage** | [![app](https://codecov.io/gh/LisDeNorn/PhotoMode/graph/badge.svg?flag=app)](https://codecov.io/gh/LisDeNorn/PhotoMode?flag=app) | [![domain](https://codecov.io/gh/LisDeNorn/PhotoMode/graph/badge.svg?flag=domain)](https://codecov.io/gh/LisDeNorn/PhotoMode?flag=domain) | [![data](https://codecov.io/gh/LisDeNorn/PhotoMode/graph/badge.svg?flag=data)](https://codecov.io/gh/LisDeNorn/PhotoMode?flag=data) |

**Kotlin • Jetpack Compose • Clean Architecture • Koin • Ktor Client • Room • DataStore**

PhotoMode is a modular Android app built around structured lessons and missions: offline-capable content delivery, mission-aware lists, and local progress, with a Compose UI driven by ViewModels and use cases. Lessons and missions are stored as JSON, can be updated from a remote source, and remain available offline through a local cache and bundled asset fallback.

## Screenshots

| Home | Lesson |
|---|---|
| ![](screenshots/home.png) | ![](screenshots/lesson.png) |

## Overview

The app renders lessons from a typed step model (theory, instruction, and related variants) and applies mission and completion state when ordering and highlighting work on the home flow. That keeps navigation and screens stable while content and mission definitions change in assets.

**Content delivery:** localized pairs `lessons_ru.json` / `lessons_en.json` and `missions_ru.json` / `missions_en.json` ship with the app as bundled assets and are used as a fallback. The app can manually synchronize fresh lesson and mission JSON from a remote source using Ktor Client, validate the downloaded content, and store it in a local file cache. Repositories read from the local content source first and fall back to bundled assets when no downloaded content is available.

**User-specific state** is separate: lesson-of-the-day id is stored in DataStore, completed lessons in Room, and app language in DataStore.

Content is intentionally compact: the value shown here is the architecture, persistence boundaries, and how UI consumes immutable state—not the depth of editorial copy.

## Key Features

- **Lesson of the day** — deterministic per-calendar-day choice, persisted so it stays stable until midnight
- **Content model** — fundamentals vs scenario-style categories; steps mapped to composables from data, not hard-coded screens
- **Remote content sync** — lessons and missions can be manually updated from remote JSON via Ktor Client, validated, cached locally, and served offline with bundled asset fallback
- **Mission-aware ordering** — home and lists prioritize work tied to the active mission and completion state
- **Visual steps** — theory/instruction steps can show paired reference imagery (see lesson screenshot)
- **Progress** — completed lessons stored locally via Room
- **Profile** — active mission, progress summary, and navigation to the next required lesson
- **Locales** — UI strings (`values` / `values-ru`) plus matching lesson/mission JSON; language (RU / EN only) is persisted in DataStore and controls both bundled and remote content selection

## Architecture

### Modules

```text
app     -> presentation: Compose UI, navigation, ViewModels, DI (Koin)
data    -> repositories, local/remote content sources, JSON parsing, file cache, in-memory parsed cache, Room, DataStore
domain  -> models, repository contracts, use cases
```

### Design decisions

- UI state is owned by ViewModels and exposed as immutable state
- Business rules live in use cases instead of screens
- Repository abstractions separate domain logic from data sources
- Lessons and missions are modeled as localized JSON content; parsing, validation, and mapping stay in the data layer
- Remote content synchronization is separated from content reading: repositories read local cached JSON or bundled assets, while `SyncContentUseCase` updates the local cache from the remote source
- Downloaded content is validated before replacing the local cache, so invalid remote JSON does not break the offline experience
- Parsed lessons and current mission are cached in memory per active `AppLocale`; successful content sync invalidates this cache so repositories reload updated JSON from the local file cache
- Domain use cases and models avoid Android framework APIs

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | Clean Architecture, 3 Gradle modules |
| DI | Koin |
| Navigation | Navigation Compose, centralized route strings |
| Networking | Ktor Client, OkHttp engine |
| Persistence | **Room** — completed lessons · **DataStore Preferences** — lesson-of-the-day id, app language · **File cache** — downloaded lesson/mission JSON · **Assets** — bundled fallback content · **In-memory** — parsed lessons/missions for current locale |
| Images | Coil |
| Testing | JUnit, MockK, coroutines test utilities |

## Testing and CI

- Unit tests are configured for `app`, `domain`, and `data` modules
- GitHub Actions runs tests on push and pull request
- Coverage reports are generated per module and uploaded to Codecov

### Local commands

```bash
./gradlew :app:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest
./gradlew :app:createDebugUnitTestCoverageReport :domain:createDebugUnitTestCoverageReport :data:createDebugUnitTestCoverageReport
```

Coverage reports are generated under:

- `app/build/reports/coverage/test/debug/`
- `domain/build/reports/coverage/test/debug/`
- `data/build/reports/coverage/test/debug/`

## Running the project

**Requirements**
- Android Studio
- JDK 11+
- minSdk 24

### Run locally

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle
4. Run the `app` configuration on an emulator or device

### Build APK

```bash
./gradlew assembleDebug
```

## Content Configuration

Bundled fallback content:
- `data/src/main/assets/lessons_ru.json`
- `data/src/main/assets/lessons_en.json`
- `data/src/main/assets/missions_ru.json`
- `data/src/main/assets/missions_en.json`

Remote content sync:
- `GistContentRemoteDataSource` downloads localized lesson and mission JSON from remote raw URLs using Ktor Client
- `ContentSyncRepositoryImpl` validates downloaded JSON before saving it
- `ContentLocalDataSource` stores downloaded content in the app’s internal `files/content_cache/` directory
- If downloaded content is unavailable, the app falls back to bundled assets
