# TurboTrack Android — Developer Guide

## Overview
TurboTrack is an aviation turbulence forecasting app for Android.
- **Package:** `com.britetodo.turbotrack`
- **Play Store:** Turbulence Forecast - Flight+
- **Current version:** 1.0.1 (versionCode 2)
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35
- **Language:** Kotlin + Jetpack Compose + Material3
- **GitHub:** `misikovbrite/turbotrack-android`

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
│   │   ├── Airport.kt                # 250+ airports worldwide + search()
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

# JAVA_HOME required (Android Studio JBR)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Debug build
./gradlew assembleDebug

# Release AAB (signed)
./gradlew bundleRelease \
  -Pandroid.injected.signing.store.file=$(pwd)/app/turbotrack.jks \
  -Pandroid.injected.signing.store.password="TurboTrack2024!" \
  -Pandroid.injected.signing.key.alias=turbotrack \
  -Pandroid.injected.signing.key.password="TurboTrack2024!"

# Output: app/build/outputs/bundle/release/app-release.aab
```

## Keystore
- **File:** `app/turbotrack.jks` (excluded from git)
- **Alias:** `turbotrack`
- **Password:** `TurboTrack2024!` (store + key)
- **GitHub Secrets:** KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
- **Note:** R8 minification disabled (AGP 8.5.2 bug) — re-enable after AGP upgrade in R2

## Firebase Configuration
- **Project:** TurboTrack (same Firebase project as iOS)
- **Android app:** `com.britetodo.turbotrack`
- **Config file:** `app/google-services.json` (tracked in git)
- **Services used:** Analytics, Remote Config
- **Remote Config key:** `turbulence_close_button_delay` (default 3.0s)

## Google Maps
- API Key set via `manifestPlaceholders["MAPS_API_KEY"]` in `build.gradle.kts`
- Restrict key to package `com.britetodo.turbotrack` in Google Cloud Console

## APIs Used

| API | Base URL | Purpose |
|-----|----------|---------|
| FAA Aviation Weather | `https://aviationweather.gov/` | PIREPs + SIGMETs |
| Open-Meteo | `https://api.open-meteo.com/` | Wind speed/direction, 9 pressure levels |
| Firebase Remote Config | — | `turbulence_close_button_delay` |
| Google Maps | — | Map display, PIREP markers, SIGMET polygons |

## Wind Shear Algorithm
Located in `TurbulenceForecastService.kt`:
1. Decompose wind vectors: `u = -speed × sin(dir)`, `v = -speed × cos(dir)`
2. Shear = `√[(Δu)² + (Δv)²] / altDiff_in_1000ft`
3. Jet stream amplification: `>80kt → ×1.3`, `>60kt → ×1.15`
4. Classification: `≥8 = EXTREME`, `6–8 = SEVERE`, `4–6 = MODERATE`, `<4 = LIGHT`

## Deploy to Google Play

```bash
# Upload new AAB to production
python3 ~/app-converter/play_console.py upload turbotrack \
  app/build/outputs/bundle/release/app-release.aab --track production

# Upload to internal track only
python3 ~/app-converter/play_console.py upload turbotrack \
  app/build/outputs/bundle/release/app-release.aab --track internal

# Update store listing
python3 ~/app-converter/play_console.py update-listing turbotrack en-US \
  --title "Turbulence Forecast" \
  --short "Know turbulence before you fly..."
```

## CI/CD — GitHub Actions
Workflow: `.github/workflows/release.yml`
- **Trigger:** push tag `v*` or manual dispatch
- **Secrets required:** KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD, PLAY_STORE_JSON_KEY
- **Output:** signed AAB uploaded to Play Store internal track

Tag a release:
```bash
git tag v1.0.1 && git push origin v1.0.1
```

## Store Listing Assets
Located in `fastlane/metadata/android/en-US/`:
- `title.txt` — "Turbulence Forecast"
- `short_description.txt`
- `full_description.txt`
- `images/icon/icon.png` — 512×512
- `images/featureGraphic/feature_graphic.png` — 1024×500
- `images/phoneScreenshots/1-6.png` — 1282×2778

## Version History

| versionCode | versionName | Notes |
|-------------|-------------|-------|
| 1 | 1.0.0 | First upload (package registration in Play Console) |
| 2 | 1.0.1 | Firebase google-services.json added |
| 3 | 1.0.2 | Remove broken Recent Forecasts dead code, fix debug package — uploaded via API |
| 4 | 1.0.3 | **Current** — ready for production submission |

## Current Status (2026-02-25)

### Done ✅
- Full app built and compiling (Kotlin + Jetpack Compose)
- iOS icon converted → Android adaptive icon (all densities)
- iOS App Store screenshots (6×) → Google Play metadata
- Feature graphic generated (1024×500)
- Firebase connected (`google-services.json` in repo)
- Keystore created (`app/turbotrack.jks`), GitHub Secrets set
- GitHub Actions CI workflow (`.github/workflows/release.yml`)
- Store listing uploaded via API: title, description, icon, feature graphic, 6 screenshots
- AAB v1.0.3 (build 4) built and ready

### In Progress / Needs Manual Steps ⏳
- Play Console app still in **draft** status — must complete these to publish:
  1. **App content → Content rating** — complete questionnaire
  2. **App content → Target audience** — select 18+
  3. **App content → Data safety** — fill in (no personal data collected)
  4. **Store listing → Category** — Travel
  5. **Store listing → Contact email** — hello@britetodo.com
  6. **Production release** — upload AAB v1.0.3 (versionCode=4) and submit for review

### Play Console Info
- **Package:** `com.britetodo.turbotrack`
- **App name in console:** Turbulence Forecast - Flight+
- **Service account:** `play-console-api-access@brite-ads-automation.iam.gserviceaccount.com`
- **API key:** `~/Downloads/brite-ads-automation-33f3693602ca.json`

## Release 2 (R2) — Deferred
- Google Play Billing 8.0
- Product ID: `turbotrack_weekly` ($2.99/week)
- Re-enable R8 minification after AGP upgrade to 8.7+
- Add 22-language localization
