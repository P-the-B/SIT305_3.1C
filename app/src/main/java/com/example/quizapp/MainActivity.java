package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    Button btnStart, btnCancel, btnToggleTheme;

    static final String PREFS_NAME = "QuizPrefs";
    static final String KEY_DARK   = "darkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName         = findViewById(R.id.etName);
        btnStart       = findViewById(R.id.btnStart);
        btnCancel      = findViewById(R.id.btnCancel);
        btnToggleTheme = findViewById(R.id.btnToggleTheme);

        // pre-fill name if coming back from results
        String returnedName = getIntent().getStringExtra("USER_NAME");
        if (returnedName != null) {
            etName.setText(returnedName);
        }

        //theme
        updateToggleLabel();
        // theme listener / storage
        btnToggleTheme.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isDark = prefs.getBoolean(KEY_DARK, false);
            prefs.edit().putBoolean(KEY_DARK, !isDark).apply();
            AppCompatDelegate.setDefaultNightMode(
                    !isDark ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
        });
        //start button action
        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("USER_NAME", name);
                startActivity(intent);
            }
        });
        //clear name field
        btnCancel.setOnClickListener(v -> etName.setText(""));
    }

    // theme must be applied before super.onCreate or you get a flashing
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
}
