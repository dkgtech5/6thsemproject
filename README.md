# SafeGuard AI - Phishing Detection System

<p align="center">
  <img src="assets/icon.png" width="120" alt="SafeGuard AI Icon">
</p>

**SafeGuard AI** is a comprehensive mobile security application built with Kotlin and Android Studio. It utilizes AI-based logic and real-time scanning to protect users from malicious phishing websites, ensuring a secure browsing experience.

## 🚀 Key Features

- **Dynamic Splash & Onboarding:** Smooth entry experience with real-time loading and feature introductions.
- **Secure Authentication:** Robust local authentication system using SQLite (Signup, Login, and Forgot Password).
- **URL Analysis:** Real-time scanning of website URLs with a detailed security checklist.
- **QR Code Scanner:** Integrated CameraX and Google ML Kit for scanning URLs directly from QR codes.
- **Interactive Dashboard:** Personalized user greeting and overview of security statistics.
- **Scan History:** Filterable log of all previous scans (Safe vs. Phishing).
- **Data Visualization:** Statistical insights using custom-built line and donut charts.
- **Help & Support:** Comprehensive FAQ and contact support system.
- **Legal & Feedback:** Integrated Privacy Policy, Terms of Service, and user feedback reporting.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** XML (ConstraintLayout, Material Design 3)
- **Database:** SQLite (SQLiteOpenHelper)
- **Camera:** CameraX API
- **AI/ML:** Google ML Kit (Barcode Scanning)
- **Architecture:** Simplified Activity-based flow for maximum performance.

## 📸 App Flow

1. **Splash:** Branding and initialization.
2. **Onboarding:** Introduction to phishing protection.
3. **Auth:** User registration and secure login.
4. **Home:** Main hub for scanning and activity overview.
5. **Scanning:** Real-time multi-point security analysis.
6. **Results:** Color-coded threat levels and detailed risk scores.
7. **History/Stats:** Tracking long-term security metrics.

## 📝 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/dkgtech5/6thsemproject.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the app on an emulator or physical device (Min SDK: 26).

---
Developed as a 6th Semester Project.
**SafeGuard AI - Stay Safe. Stay Secure.**
