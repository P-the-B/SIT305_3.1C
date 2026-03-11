QuizApp — SIT305 Task 3.1C
A simple Android quiz app built in Java for SIT305 Mobile Application Development at Deakin University.

What it does:
The app runs the user through a 5-question general knowledge quiz. 
Each question has four options presented as radio buttons.
After submitting an answer, correct and incorrect choices are highlighted in green and red respectively.
A segmented progress bar tracks results across all questions as you go.
At the end, a results screen shows your score, percentage, and a message that changes based on how well you did.

Features:
 * Name entry screen with input validation
 * 5-question quiz with multiple choice answers
 * Green/red answer feedback on submit
 * Segmented progress bar that builds as you answer
 * Dark/light mode toggle that persists across sessions
 * Quiz state is preserved when toggling the theme mid-quiz
 * Tailored result message based on score percentage
 * Name carries back to the home screen for a second attempt

Built with:
  * Java
  * XML layouts
  * Android SharedPreferences for theme persistence
  * onSaveInstanceState for state management
  * Material Components (Day/Night theme)

How to run

  1. Clone the repo
  2. Open in Android Studio
  3. Change your Questions and answer options [four options] (QuizActivity - line 65 to 75)
  4. Run on an emulator or physical device (API 24+).
