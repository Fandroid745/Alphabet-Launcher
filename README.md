# Alphabet Launcher-Assignment App

A minimal Android home screen built with Kotlin and Jetpack Compose. The launcher displays the current time/date, favourite apps, and an interactive curved A–Z alphabet bar for quickly browsing installed apps.

## Features

### Core
* Live clock and date.
* 5–7 apps on the home screen.
* Vertical A–Z alphabet bar with a star at the top and dot at the bottom.
* Real installed launchable apps loaded through PackageManager.
* Android 11+ package visibility support.
* Curved alphabet animation following the finger.
* Enlarged selected-letter bubble beside the finger.
* Apps filtered by selected starting letter and sorted alphabetically.
* Clear "No apps" state for empty letters.
* Release animation returning the alphabet bar to its resting position.
* Tapping an app launches it.
* App list is cached outside the touch path.

### Bonus Features
* Default launcher support.
* Haptic feedback on letter selection.
* Spring-based release animation.
* System light/dark theme support.
* Unit-test support.

## Tech Stack
* **Language**: Kotlin
* **UI**: Jetpack Compose & Material 3
* **DI**: Koin (`koin-android`, `koin-androidx-compose`)
* **Architecture**: ViewModel & Kotlin Flow / StateFlow
* **Icon Loading**: Accompanist DrawablePainter

## Requirements
* Android Studio (Ladybug / 2024.2.1+).
* Android SDK supporting API 37 (Compile SDK).
* Android device or emulator running API 24 or newer.

## Setup & Build
1. Clone the repository.
2. Open the project in Android Studio and allow Gradle to sync.
3. Build the debug APK:
   * **Linux/macOS**: `./gradlew assembleDebug`
   * **Windows**: `gradlew.bat assembleDebug`

## Run Tests
* **Linux/macOS**: `./gradlew test`
* **Windows**: `gradlew.bat test`

## Curve Animation
The alphabet is rendered as a vertical Compose Column.

During a vertical drag, the finger's Y position is converted into a normalized value between 0 and 1. The distance between the finger and each letter is used with a Gaussian curve formula ($X = -110\text{dp} \cdot e^{-\frac{d^2}{2\sigma^2}}$) to calculate the horizontal offset. Letters closest to the finger move furthest left, while letters farther away move less.

This creates the curved/bulged alphabet effect without using a third-party curve-animation library.

When the finger is released, the alphabet returns to its resting position using spring-based animation.

## Libraries

| Library | Version | Purpose |
| :--- | :--- | :--- |
| **Koin Android** | `4.2.2` | Pure Kotlin dependency injection framework for application singletons (`AppRepository`). |
| **Koin Compose** | `4.2.2` | Integrates Koin ViewModels into Jetpack Compose screens (`koinViewModel()`). |
| **Accompanist DrawablePainter** | `0.37.3` | Renders native `PackageManager` `Drawable` app icons inside Compose `Image` composables. |
| **Material Icons Extended** | BOM-managed | Material design vector icons (Star `★`, Dot `•`). |
| **AndroidX Core KTX** | `1.19.0` | Kotlin extensions for core Android APIs. |
| **AndroidX Activity Compose** | `1.13.0` | Activity integration for Jetpack Compose `setContent`. |
| **AndroidX Lifecycle Runtime KTX** | `2.11.0` | Lifecycle runtime support and `collectAsStateWithLifecycle()`. |
| **Jetpack Compose (BOM)** | `2026.02.01` | Declarative UI toolkit. |
| **KotlinX Coroutines Test** | `1.11.0` | Testing dispatcher for executing coroutines in JVM unit tests. |
| **Turbine** | `1.2.1` | Flow testing library for Kotlin StateFlow stream assertions. |
| **JUnit** | `4.13.2` | Local JVM unit testing framework. |

## AI / Assistant Disclosure
Android Studio AI Agent was used as a development assistant during this assignment for research, implementation guidance and debugging.

The final implementation was integrated, reviewed, tested, and run by me.

## Project Structure

```
app/
├── src/main/java/com/example/alphabetlauncher/
│   ├── AlphabetLauncherApp.kt
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── AppInfo.kt
│   │   └── AppRepository.kt
│   ├── di/
│   │   └── AppModule.kt
│   └── ui/
│       ├── HomeViewModel.kt
│       ├── MainHomeScreen.kt
│       └── components/
│           ├── AlphabetSideBar.kt
│           └── AppItem.kt
│
└── src/test/java/com/example/alphabetlauncher/
    └── HomeViewModelTest.kt
```
