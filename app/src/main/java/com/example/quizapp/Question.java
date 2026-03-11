package com.example.quizapp;

public class Question {
    private String questionText;
    private String[] options;
    private int correctIndex; // 0, 1, 2, or 3 as the answer

    public Question(String questionText, String[] options, int correctIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
}
