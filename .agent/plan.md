# Project Plan

FilterYou: An Android app to intercept harassing, promotional, fraudulent, and bombing calls and SMS. The app should have a clean, fresh Material You design, display intercepted logs, and allow custom interception rules. It must follow Now in Android architecture and style, using a single-module structure and the latest recommended technologies.

## Project Brief

# Project Brief: FilterYou

FilterYou is a modern Android application designed to provide a secure and peaceful communication environment by intercepting harassing, promotional, fraudulent, and "bombing" calls and SMS. Built with the latest Android standards, it offers a clean, energetic, and highly functional experience.

## Features

- **Real-time Interception**: Automatically filters and blocks unwanted calls and SMS using system-level roles and APIs.

- **Intercepted Logs**: A detailed, searchable history of all blocked communications, allowing users to review and restore legitimate messages if necessary.
- **Custom Filtering Rules**: An intuitive interface to define personal blocklists based on specific phone numbers, keywords, or sender patterns.
- **Adaptive Dashboard**: A vibrant, Material You-compliant home screen that provides a quick overview of protection status and interception statistics.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (Material You)
- **Navigation**: Jetpack Navigation 3 (state-driven architecture)
- **Adaptive Layouts**: Compose Material Adaptive library for seamless support across different screen sizes and form factors.
- **Concurrency**: Kotlin Coroutines & Flow for reactive, non-blocking operations.
- **System Integration**: Android `RoleManager` and `Telecom`/`Telephony` APIs for call and SMS filtering.

## Implementation Steps

### Task_1_Foundation_Data_Roles: Configure Android Roles (SMS/Call Screening), initialize Room database for logs/rules, and setup DataStore for settings.
- **Status:** COMPLETED
- **Updates:** Room Database initialized (InterceptedLog, FilterRule), DataStore setup for settings, RoleManagerHelper implemented for ROLE_SMS and ROLE_CALL_SCREENING, and project builds successfully.
- **Acceptance Criteria:**
  - RoleManager correctly requests and handles SMS/Call Screening roles
  - Room database schema for InterceptedLog and FilterRule is defined
  - DataStore is initialized for protection toggles
  - Project builds successfully

### Task_2_Interception_Engine: Implement CallScreeningService and SMS BroadcastReceiver to intercept communication based on Room rules and log events.
- **Status:** COMPLETED
- **Updates:** CallScreeningService and SmsReceiver implemented. They check Room rules and respect DataStore settings to block and log calls/SMS. Support for ROLE_SMS (default SMS app requirements) added. Project builds successfully.
- **Acceptance Criteria:**
  - CallScreeningService blocks numbers based on rules
  - SMS BroadcastReceiver intercepts and blocks messages
  - Intercepted events are persisted to Room database
  - Real-time filtering logic works without UI

### Task_3_UI_Navigation_Dashboard: Implement Navigation 3 and the core UI: a Material 3 Adaptive Dashboard showing stats and the Intercepted Logs list.
- **Status:** COMPLETED
- **Updates:** Navigation 3 integrated for state-driven navigation. Dashboard implemented with stats and protection toggles. Searchable Intercepted Logs screen with swipe-to-delete and clear-all functionality. Adaptive UI using Material 3 components. Role-based setup screen included.
- **Acceptance Criteria:**
  - Navigation 3 manages state-driven screen transitions
  - Dashboard displays interception statistics correctly
  - Intercepted logs list is searchable and displays data from Room
  - UI uses Material 3 components and adapts to screen sizes

### Task_4_Rules_UI_Theme_Assets: Implement Rule management UI, apply energetic Material You theme, enable Edge-to-Edge, and create an adaptive app icon.
- **Status:** COMPLETED
- **Updates:** Rule management UI implemented (add, toggle, delete). Material You dynamic colors and vibrant energy theme applied. Full Edge-to-Edge display enabled. Adaptive app icon (shield/filter theme) created. Project builds successfully.
- **Acceptance Criteria:**
  - Users can add, edit, and delete filtering rules (keywords/numbers)
  - Material You dynamic colors and vibrant energetic theme applied
  - Full Edge-to-Edge display implemented
  - Adaptive app icon matching the app's function is present

### Task_5_Verification: Final run and stability check to ensure all features work together and comply with the project brief.
- **Status:** COMPLETED
- **Updates:** Final verification completed on phone emulator. App is stable, no crashes. Features (Roles, Dashboard, Rules, Logs) verified and functional. Material 3 vibrant theme and Edge-to-Edge display confirmed. Adaptive app icon verified. Task 5 completed.
- **Acceptance Criteria:**
  - Build passes and app does not crash during usage
  - All existing tests pass
  - UI aligns with energetic Material Design 3 guidelines
  - System roles are handled gracefully if revoked
- **Duration:** N/A

