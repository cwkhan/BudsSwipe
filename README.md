# BudSwipe 🎧📱

**BudSwipe** is a modern, high-precision Android application that empowers you to browse Instagram Reels and Facebook Reels completely hands-free using your Bluetooth earbuds.

---

## 🌟 Key Features

1. **Hands-Free Vertical Reels Navigation**
   - **Left Earbud Tap** → Swipe **UP** to next Reel.
   - **Right Earbud Tap** → Swipe **DOWN** to previous Reel.
   - Fallback single-button action for earbuds with single media code.

2. **Real-time Bluetooth & Media Key Telemetry**
   - Live hardware event monitor showing `keyCode` name, numerical ID, event action (`ACTION_DOWN` vs `ACTION_UP`), timestamp, and debounce status.
   - Earbud Signal Calibration wizard to test left and right earbuds independently and diagnose if your buds output stereo-distinct signals.

3. **Android Accessibility Gesture Simulation**
   - Employs the official Android `AccessibilityService` `dispatchGesture()` API with configurable vertical travel distance (35%–85% of screen height), duration/flick speed (120ms–500ms), and haptic vibration feedback.
   - Safe foreground gating: automatically restricts swipes to Instagram (`com.instagram.android`) and Facebook (`com.facebook.katana`, `com.facebook.lite`) so gestures never trigger on your home screen or inside other apps.

4. **100% Privacy & Security**
   - **Zero Internet Permission**: BudSwipe does not declare or use `android.permission.INTERNET`. No network requests are made.
   - **Zero Screen Scraping**: The accessibility service only injects swipe gestures; it never reads, logs, or intercepts screen contents.
   - All settings are saved locally using Android Jetpack DataStore Preferences.

---

## 🎧 Realme & Earbud Configuration Guide

Android receives standard Bluetooth media button events (`KEYCODE_MEDIA_NEXT`, `KEYCODE_MEDIA_PREVIOUS`, `KEYCODE_MEDIA_PLAY_PAUSE`). By default, many earbuds assign generic Play/Pause to both buds.

To enable independent left and right earbud swiping:
1. Open your earbud companion app (e.g., **Realme Link**, **HeyMelody**, **Galaxy Wearable**, or **Sony Headphones Connect**).
2. Go to **Touch / Gesture Settings**.
3. Set **Left Earbud (Double Tap or Triple Tap)** → **Previous Track**.
4. Set **Right Earbud (Double Tap or Triple Tap)** → **Next Track**.
5. When tapped, your earbuds will emit distinct `KEYCODE_MEDIA_PREVIOUS` and `KEYCODE_MEDIA_NEXT` events. BudSwipe intercepts these and triggers the respective upward and downward Reel swipes seamlessly!

---

## 🛠️ Permissions Overview

| Permission | Purpose |
|---|---|
| `BIND_ACCESSIBILITY_SERVICE` | Required to simulate screen gestures via `dispatchGesture()`. |
| `BLUETOOTH_CONNECT` | Detects connected audio device name on Android 12+. |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Maintains active MediaSession in the background. |
| `POST_NOTIFICATIONS` | Displays the persistent status notification required by Android. |
| `VIBRATE` | Optional subtle haptic feedback upon successful gesture dispatch. |

---

## 🚀 Building the Project

### Prerequisites
- JDK 17 or higher
- Android SDK 36 (Minimum API 26 / Android 8.0)
- Gradle 8.x+ / AGP 9.1+

### Build Debug APK via Command Line
```bash
gradle assembleDebug
```
The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Run Unit & Robolectric Tests
```bash
gradle testDebugUnitTest
```
