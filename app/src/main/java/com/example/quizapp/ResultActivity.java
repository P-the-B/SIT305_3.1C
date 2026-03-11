package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ResultActivity extends AppCompatActivity {

    TextView tvName, tvScore, tvPercent;
    Button btnTakeNewQuiz, btnFinish, btnToggleTheme;

    int score, total;
    String userName;

    static final String PREFS_NAME = "QuizPrefs";
    static final String KEY_DARK   = "darkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvName         = findViewById(R.id.tvName);
        tvScore        = findViewById(R.id.tvScore);
        tvPercent      = findViewById(R.id.tvPercent);
        btnTakeNewQuiz = findViewById(R.id.btnTakeNewQuiz);
        btnFinish      = findViewById(R.id.btnFinish);
        btnToggleTheme = findViewById(R.id.btnToggleTheme);

        userName = getIntent().getStringExtra("USER_NAME");
        score    = getIntent().getIntExtra("SCORE", 0);
        total    = getIntent().getIntExtra("TOTAL", 5);

        int percent = (score * 100) / total;

        // depending on user score, tailor message
        String message;
        if (percent == 100) {
            message = "Perfect score, " + userName + "! Amazing!";
        } else if (percent >= 80) {
            message = "Great work, " + userName + "!";
        } else if (percent >= 60) {
            message = "Good effort, " + userName + "!";
        } else if (percent >= 40) {
            message = "Keep practicing, " + userName + "!";
        } else {
            message = "Better luck next time, " + userName + "!";
        }

        tvName.setText(message);
        tvScore.setText(score + "/" + total);
        //design extra % data
        tvPercent.setText(percent + "%");

        updateToggleLabel();
        //theme listener
        btnToggleTheme.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isDark = prefs.getBoolean(KEY_DARK, false);
            prefs.edit().putBoolean(KEY_DARK, !isDark).apply();
            AppCompatDelegate.setDefaultNightMode(
                    !isDark ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // pass name back to main screen with pre-fill
        btnTakeNewQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_NAME", userName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // removes back button to quiz from stack
            startActivity(intent);
            finish();
        });

        btnFinish.setOnClickListener(v -> finishAffinity());
    }

    // theme must be applied before super.onCreate or you get a flash
    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
    }
    //theme toggle label
    private void updateToggleLabel() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
        btnToggleTheme.setText(isDark ? "Light Mode" : "Dark Mode");
    }
}
