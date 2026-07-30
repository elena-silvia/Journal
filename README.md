# Journal APP

A modern personal journal mobile application natively developed for Android, integrating artificial intelligence for emotional state analysis and an interactive calendar to track daily moods

## Implemented features

To date, the application includes the following modules and features:
- **Secure Authentication & Profile:**
      - User authentication and registration via **Firebase Authentication**
      - Automatic saving of user profile data in **Cloud Firestore**
-**Notes Management:**
      - Creation of new journal notes
      - Viewing the list of notes on the main screen
      - Secure deletion of notes, protected by database permission rules
-**AI-Powered Emotional Analysis(Gemini):**
  - Integration with the **Gemini API**
  - Automatic analysis of entered text to detect emotional state (*Mood*), generation of a 1 to 5 score (*Score*),
        and provision of personalized advice (*AI Advice*)
-**Detailed View (Insights Screen):**
      - Dedicated page for each individual note where users can read the full content and the AI-generated analysis results.
-**Interactive Calendar:**
      - Calendar-based view of journal entries
      - Calendar days are automatically color-coded based on the AI-generated emotional score, offering a visual perspective of
        mood changes over time.

## Technologies & Tools Used 
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** Model-View-ViewModel with Kotlin Coroutines
- **Backend & Database:** Firebase
- **Artificial Inteligence:** Google Gemini API  
