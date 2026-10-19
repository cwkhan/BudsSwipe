# Google Play Protect Warning Resolution & Release APK Rebuild

Resolution plan to fix the Google Play Protect block (*"This app can request access to sensitive data. This can increase the risk of identity theft or financial fraud"*) when sideloading the BudSwipe Release APK.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following choices were confirmed during interactive clarification:
> - **Security Pruning**: Strip all potential keylogger and window-inspection flags (`canRequestFilterKeyEvents`, `flagRetrieveInteractiveWindows`, `typeWindowContentChanged`) from the accessibility configuration so the service is strictly a gesture simulation engine.
> - **Artifact Output**: Build and place the optimized **Release APK only** in the `/apk` folder (`/apk/BudSwipe-release.apk` and `/apk/app-release.apk`).
> - **Installation Guidance**: Provide complete instructions for bypassing Play Protect and enabling Android 13/14 restricted settings for sideloaded accessibility utilities.

- **Confirmed Decision 1**: Prune invasive flags from `accessibility_service_config.xml` to minimize security risk triggers in Play Protect heuristics.
- **Confirmed Decision 2**: Build and update the signed Release APK in `/apk` and provide clear steps to install smoothly on Android.

---

### 1. Root Cause Analysis

- **Why Play Protect Blocked the APK**:
  1. **Keystroke Interception Flag (`canRequestFilterKeyEvents="true"`)**: In Android security models, requesting permission to intercept all system key events is flagged by Google Play Protect as a high-risk capability associated with keyloggers and credential harvesters (*"risk of identity theft or financial fraud"*).
  2. **Window Content Inspection (`flagRetrieveInteractiveWindows`)**: Indicated to the OS that the app could read on-screen text in banking or messaging apps.
  3. **Why BudSwipe Doesn't Need These**:
     - Earbud media key events are already captured cleanly through Android's standard **`MediaSession`** and **`MediaButtonReceiver`** APIs in `BudSwipeMediaSessionService`.
     - The Accessibility Service only needs **`canPerformGestures="true"`** to execute vertical swipe gestures (`dispatchGesture()`) and **`typeWindowStateChanged`** to identify when Instagram or Facebook enters the foreground.
  4. **Browser Sideloading Restrictions (Android 13/14+)**: Sideloading APKs declaring accessibility services directly through Chrome triggers automated fraud protection blocks unless installed via a file manager or with Play Protect temporarily paused.

---

### 2. User Experience & Verification Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                   Play Protect Mitigation Flow                   │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
                                  ▼
      ┌────────────────────────────────────────────────────────┐
      │ 1. Security Pruning in accessibility_service_config.xml│
      │    - Keep: canPerformGestures="true"                   │
      │    - Keep: typeWindowStateChanged                      │
      │    - Remove: canRequestFilterKeyEvents                 │
      │    - Remove: flagRetrieveInteractiveWindows            │
      │    - Remove: typeWindowContentChanged                  │
      └───────────────────────────┬────────────────────────────┘
                                  │
                                  ▼
      ┌────────────────────────────────────────────────────────┐
      │ 2. Rebuild Signed Release APK                          │
      │    - gradle :app:assembleRelease                       │
      │    - Copy updated binary to /apk/BudSwipe-release.apk  │
      └───────────────────────────┬────────────────────────────┘
                                  │
                                  ▼
      ┌────────────────────────────────────────────────────────┐
      │ 3. User Installation Guide                             │
      │    - Step A: Temporarily toggle Play Protect scanning   │
      │    - Step B: Install APK via Files app or direct tap   │
      │    - Step C: Enable "Allow restricted settings" in OS  │
      └────────────────────────────────────────────────────────┘
```

---

### 3. Key Technical Decisions & Changes

- **Decision 1: Streamline Accessibility Config to Minimum Privilege**
  - *Target File*: `app/src/main/res/xml/accessibility_service_config.xml`
  - *New Config*:
    ```xml
    <accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
        android:description="@string/accessibility_service_description"
        android:accessibilityEventTypes="typeWindowStateChanged"
        android:accessibilityFeedbackType="feedbackGeneric"
        android:notificationTimeout="100"
        android:canPerformGestures="true"
        android:accessibilityFlags="flagDefault" />
    ```
  - *Rationale*: Eliminates all sensitive permission flags that trigger the financial fraud warning while preserving 100% of gesture dispatch functionality.

- **Decision 2: Clean Up `BudSwipeAccessibilityService.kt`**
  - Retain `performSwipe()` and foreground package tracking.
  - Simplify `onKeyEvent()` to default delegation since hardware media buttons are handled by `MediaSession`.

- **Decision 3: Rebuild and Deliver Release APK**
  - Execute `STORE_PASSWORD=android KEY_PASSWORD=android gradle :app:assembleRelease`.
  - Copy new release APK to `/apk/BudSwipe-release.apk` and `/apk/app-release.apk`.
