# TurboTrack Android — Developer Guide

## Overview
TurboTrack is an aviation turbulence forecasting app for Android.
- **Package:** `com.britetodo.turbotrack`
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35
- **Language:** Kotlin + Jetpack Compose + Material3

## Project Structure
```
app/src/main/java/com/britetodo/turbotrack/
├── TurboTrackApp.kt          # Application class (Hilt + Firebase init)
├── MainActivity.kt           # Single activity, splash screen, onboarding gate
├── ui/
│   ├── AppNavigation.kt      # NavHost: Onboarding → Main
│   ├── onboarding/
│   │   ├── OnboardingScreen.kt       # Steps 0-5 (feature screens)
│   │   ├── OnboardingQuizScreen.kt   # Steps 6-12 (quiz + animation + complete)
│   │   └── OnboardingViewModel.kt    # Quiz state + DataStore save
│   ├── main/
│   │   └── MainScreen.kt             # 4-tab NavigationBar
│   ├── map/
│   │   ├── TurbulenceMapScreen.kt    # Google Maps + PIREP/SIGMET overlays
│   │   └── MapViewModel.kt           # Live aviation data, auto-refresh 5min
│   ├── forecast/
│   │   ├── ForecastTabScreen.kt      # AnimatedContent screen router
│   │   ├── RouteInputScreen.kt       # Airport autocomplete, route input
│   │   ├── ForecastAnalysisScreen.kt # Airplane animation + progress
│   │   ├── ForecastStoryScreen.kt    # 4-page HorizontalPager
│   │   ├── ForecastResultScreen.kt   # Full result with share
│   │   └── RouteViewModel.kt         # Forecast state machine
│   ├── reports/
│   │   ├── ReportsScreen.kt          # PIREP list with search/filter
│   │   └── ReportsViewModel.kt       # Live PIREPs from FAA AWC
│   └── settings/
│       ├── SettingsScreen.kt         # Notifications, units, refresh settings
│       └── SettingsViewModel.kt      # Delegates to prefs + notification service
├── data/
│   ├── model/
│   │   ├── TurbulenceSeverity.kt     # NONE/LIGHT/MODERATE/SEVERE/EXTREME
│   │   ├── TurbulenceType.kt         # CAT/CONVECTIVE/MOUNTAIN_WAVE/COMBINED
│   │   ├── Airport.kt                # 44 airports hardcoded + search()
│   │   ├── TurbulenceForecast.kt     # Forecast result models
│   │   ├── PIREPReport.kt            # FAA PIREP JSON model
│   │   └── AirSigmet.kt              # FAA SIGMET JSON model + coords
│   └── preferences/
│       ├── UserPreferences.kt        # Data class
│       └── UserPreferencesRepository.kt  # DataStore wrapper
├── services/
│   ├── NetworkModule.kt              # Hilt: Aviation + Open-Meteo Retrofit
│   ├── AviationWeatherService.kt     # Retrofit: PIREPs + SIGMETs
│   ├── OpenMeteoService.kt           # Retrofit: wind speed/direction 9 levels
│   ├── TurbulenceForecastService.kt  # Wind shear algorithm + forecast
│   ├── NotificationService.kt        # WorkManager scheduled reminders
│   └── RemoteConfigService.kt        # Firebase Remote Config
└── theme/
    ├── Color.kt    # Brand colors + severity palette
    ├── Type.kt     # Typography
    └── Theme.kt    # TurboTrackTheme (dark only)
```

## Build Commands

```bash
cd ~/turbotrack-android

# Java 17 required
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Debug build
./gradlew assembleDebug

# Release AAB (for Play Store)
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

## Required Setup Before Building

### 1. Google Maps API Key
1. Go to Google Cloud Console → APIs & Services → Credentials
2. Create API Key, restrict to Android with package `com.britetodo.turbotrack`
3. Enable "Maps SDK for Android"
4. Add to `local.properties`:
   ```
   MAPS_API_KEY=AIzaSy...
   ```
5. Or set in `app/build.gradle.kts` defaultConfig:
   ```kotlin
   manifestPlaceholders["MAPS_API_KEY"] = "YOUR_KEY_HERE"
   ```

### 2. Firebase Configuration
1. Go to Firebase Console → TurboTrack project
2. Add Android app: `com.britetodo.turbotrack`
3. Download `google-services.json`
4. Place at: `app/google-services.json`

### 3. Keystore (for release)
```bash
keytool -genkey -v -keystore app/turbotrack.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias turbotrack

# Set env vars for signing:
export KEYSTORE_PASSWORD=your_password
export KEY_PASSWORD=your_key_password
```

## APIs Used

| API | Base URL | Docs |
|-----|----------|------|
| FAA Aviation Weather | `https://aviationweather.gov/` | PIREPs + SIGMETs |
| Open-Meteo | `https://api.open-meteo.com/` | Wind speed/direction, 9 pressure levels |
| Firebase Remote Config | — | `turbulence_close_button_delay` (default 3.0s) |
| Google Maps | — | Map display, markers, polygons |

## Wind Shear Algorithm
Located in `TurbulenceForecastService.kt`:
1. Decompose wind vectors: `u = -speed × sin(dir)`, `v = -speed × cos(dir)`
2. Shear = `√[(Δu)² + (Δv)²] / altDiff_in_1000ft`
3. Jet stream amplification: `>80kt → ×1.3`, `>60kt → ×1.15`
4. Classification: `≥8 = EXTREME`, `6–8 = SEVERE`, `4–6 = MODERATE`, `<4 = LIGHT`

## Deploy to Google Play

```bash
# Upload to Play Console
python3 ~/app-converter/play_console.py upload turbotrack \
  app/build/outputs/bundle/release/app-release.aab

# Or internal track for testing
python3 ~/app-converter/play_console.py upload turbotrack \
  app/build/outputs/bundle/release/app-release.aab --track internal
```

**Note:** First publish must be done manually in Play Console web UI.

## Release 2 (R2) — Deferred
- Google Play Billing 8.0
- Product ID: `turbotrack_weekly` ($2.99/week)
- Package: already `com.britetodo.turbotrack`
