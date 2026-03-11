package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class QuizActivity extends AppCompatActivity {

    TextView tvProgress, tvQuestion;
    LinearLayout progressSegments;
    RadioGroup radioGroup;
    RadioButton rbOption0, rbOption1, rbOption2, rbOption3;
    Button btnSubmitNext, btnToggleTheme;

    static final String PREFS_NAME = "QuizPrefs";
    static final String KEY_DARK   = "darkMode";

    Question[] questions;
    int currentIndex = 0;
    int score = 0;
    boolean answered = false;
    String userName;

    boolean[] answeredCorrectly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvProgress       = findViewById(R.id.tvProgress);
        tvQuestion       = findViewById(R.id.tvQuestion);
        progressSegments = findViewById(R.id.progressSegments);
        radioGroup       = findViewById(R.id.radioGroup);
        rbOption0        = findViewById(R.id.rbOption0);
        rbOption1        = findViewById(R.id.rbOption1);
        rbOption2        = findViewById(R.id.rbOption2);
        rbOption3        = findViewById(R.id.rbOption3);
        btnSubmitNext    = findViewById(R.id.btnSubmitNext);
        btnToggleTheme   = findViewById(R.id.btnToggleTheme);

        userName = getIntent().getStringExtra("USER_NAME");
        updateToggleLabel();

        btnToggleTheme.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isDark = prefs.getBoolean(KEY_DARK, false);
            prefs.edit().putBoolean(KEY_DARK, !isDark).apply();
            AppCompatDelegate.setDefaultNightMode(
                    !isDark ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
        });

        questions = new Question[]{
                new Question("What is the capital of Australia?",
                        new String[]{"Sydney", "Melbourne", "Canberra", "Brisbane"}, 2),
                new Question("Which planet is closest to the Sun?",
                        new String[]{"Venus", "Mercury", "Earth", "Mars"}, 1),
                new Question("What is 12 x 12?",
                        new String[]{"132", "144", "154", "124"}, 1),
                new Question("What language is primarily used to build Android apps?",
                        new String[]{"Swift", "Python", "Kotlin/Java", "C++"}, 2),
                new Question("What does CPU stand for?",
                        new String[]{"Central Process Unit", "Computer Personal Unit",
                                "Central Processing Unit", "Core Processing Unit"}, 2)
        };

        answeredCorrectly = new boolean[questions.length];

        // recreated after theme toggle — restore saved state
        if (savedInstanceState != null) {
            currentIndex      = savedInstanceState.getInt("CURRENT_INDEX", 0);
            score             = savedInstanceState.getInt("SCORE", 0);
            answered          = savedInstanceState.getBoolean("ANSWERED", false);
            answeredCorrectly = savedInstanceState.getBooleanArray("ANSWERED_CORRECTLY");

            for (int i = 0; i < currentIndex; i++) {
                addProgressSegment(answeredCorrectly[i]);
            }

            // can't call loadQuestion() here — resets the answered state!!!!
            if (answered) {
                restoreAnsweredQuestion(savedInstanceState);
                addProgressSegment(answeredCorrectly[currentIndex]);
            } else {
                loadQuestion();
            }

        } else {
            loadQuestion();
        }

        btnSubmitNext.setOnClickListener(v -> {
            if (!answered) {
                submitAnswer();
            } else {
                moveToNext();
            }
        });
    }
// saves state!
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_INDEX", currentIndex);
        outState.putInt("SCORE", score);
        outState.putBoolean("ANSWERED", answered);
        outState.putBooleanArray("ANSWERED_CORRECTLY", answeredCorrectly);
        outState.putInt("SELECTED_ID", radioGroup.getCheckedRadioButtonId());
    }

    // used when restoring a question already submitted — skips the resets in loadQuestion()
    private void restoreAnsweredQuestion(Bundle savedInstanceState) {
        Question q = questions[currentIndex];
        tvQuestion.setText(q.getQuestionText());
        rbOption0.setText(q.getOptions()[0]);
        rbOption1.setText(q.getOptions()[1]);
        rbOption2.setText(q.getOptions()[2]);
        rbOption3.setText(q.getOptions()[3]);
        tvProgress.setText("Question " + (currentIndex + 1) + " of " + questions.length);

        rbOption0.setEnabled(false);
        rbOption1.setEnabled(false);
        rbOption2.setEnabled(false);
        rbOption3.setEnabled(false);
        btnSubmitNext.setText(currentIndex == questions.length - 1 ? "Finish" : "Next");

        int selectedId = savedInstanceState.getInt("SELECTED_ID", -1);
        if (selectedId != -1) radioGroup.check(selectedId);

        int selectedIndex = -1;
        if (selectedId == R.id.rbOption0) selectedIndex = 0;
        else if (selectedId == R.id.rbOption1) selectedIndex = 1;
        else if (selectedId == R.id.rbOption2) selectedIndex = 2;
        else if (selectedId == R.id.rbOption3) selectedIndex = 3;
        //correct answer highlight
        int correctIndex = questions[currentIndex].getCorrectIndex();
        getOptionButton(correctIndex).setBackgroundResource(R.drawable.option_correct);
        getOptionButton(correctIndex).setTextColor(
                getResources().getColor(android.R.color.white, null));
        // wrong answer highlight
        if (selectedIndex != -1 && selectedIndex != correctIndex) {
            getOptionButton(selectedIndex).setBackgroundResource(R.drawable.option_wrong);
            getOptionButton(selectedIndex).setTextColor(
                    getResources().getColor(android.R.color.white, null));
        }
    }

    // theme must be applied before super.onCreate or you get a flash
    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void updateToggleLabel() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
        btnToggleTheme.setText(isDark ? "Light Mode" : "Dark Mode");
    }

    private void loadQuestion() {
        rbOption0.setEnabled(true);
        rbOption1.setEnabled(true);
        rbOption2.setEnabled(true);
        rbOption3.setEnabled(true);

        // detach before clearCheck so it doesn't trigger the highlight listener
        radioGroup.setOnCheckedChangeListener(null);
        radioGroup.clearCheck();
        resetBackgrounds();

        answered = false;
        btnSubmitNext.setText("Submit");

        Question q = questions[currentIndex];
        tvQuestion.setText(q.getQuestionText());
        rbOption0.setText(q.getOptions()[0]);
        rbOption1.setText(q.getOptions()[1]);
        rbOption2.setText(q.getOptions()[2]);
        rbOption3.setText(q.getOptions()[3]);
        tvProgress.setText("Question " + (currentIndex + 1) + " of " + questions.length);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!answered) {
                resetBackgrounds();
                if (checkedId == R.id.rbOption0) rbOption0.setBackgroundResource(R.drawable.option_selected);
                else if (checkedId == R.id.rbOption1) rbOption1.setBackgroundResource(R.drawable.option_selected);
                else if (checkedId == R.id.rbOption2) rbOption2.setBackgroundResource(R.drawable.option_selected);
                else if (checkedId == R.id.rbOption3) rbOption3.setBackgroundResource(R.drawable.option_selected);
            }
        });
    }

    private void submitAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        // nuot answered check
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        answered = true;
        rbOption0.setEnabled(false);
        rbOption1.setEnabled(false);
        rbOption2.setEnabled(false);
        rbOption3.setEnabled(false);

        btnSubmitNext.setText(currentIndex == questions.length - 1 ? "Finish" : "Next");

        int selectedIndex = -1;
        if (selectedId == R.id.rbOption0) selectedIndex = 0;
        else if (selectedId == R.id.rbOption1) selectedIndex = 1;
        else if (selectedId == R.id.rbOption2) selectedIndex = 2;
        else if (selectedId == R.id.rbOption3) selectedIndex = 3;

        int correctIndex = questions[currentIndex].getCorrectIndex();

        getOptionButton(correctIndex).setBackgroundResource(R.drawable.option_correct);
        getOptionButton(correctIndex).setTextColor(
                getResources().getColor(android.R.color.white, null));

        if (selectedIndex != correctIndex) {
            getOptionButton(selectedIndex).setBackgroundResource(R.drawable.option_wrong);
            getOptionButton(selectedIndex).setTextColor(
                    getResources().getColor(android.R.color.white, null));
        } else {
            score++;
        }

        answeredCorrectly[currentIndex] = (selectedIndex == correctIndex);
        addProgressSegment(selectedIndex == correctIndex);
    }
    //next Q
    private void moveToNext() {
        currentIndex++;
        if (currentIndex < questions.length) {
            loadQuestion();
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", questions.length);
            startActivity(intent);
            finish();
        }
    }

    private RadioButton getOptionButton(int index) {
        switch (index) {
            case 0: return rbOption0;
            case 1: return rbOption1;
            case 2: return rbOption2;
            default: return rbOption3;
        }
    }

    private void resetBackgrounds() {
        rbOption0.setBackgroundResource(R.drawable.option_background);
        rbOption1.setBackgroundResource(R.drawable.option_background);
        rbOption2.setBackgroundResource(R.drawable.option_background);
        rbOption3.setBackgroundResource(R.drawable.option_background);
        rbOption0.setTextAppearance(android.R.style.TextAppearance);
        rbOption1.setTextAppearance(android.R.style.TextAppearance);
        rbOption2.setTextAppearance(android.R.style.TextAppearance);
        rbOption3.setTextAppearance(android.R.style.TextAppearance);
    }

    //progress bar segmented (my favorite design :-) )
    private void addProgressSegment(boolean correct) {
        View segment = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        params.setMargins(4, 0, 4, 0);
        segment.setLayoutParams(params);
        segment.setBackgroundColor(correct ?
                getResources().getColor(R.color.correct_green, null) :
                getResources().getColor(R.color.wrong_red, null));
        progressSegments.addView(segment);
    }
}
