package com.assessment.model;

import java.sql.Timestamp;

/**
 * QuizSession Model Class - Tracks a user's quiz session
 * 
 * OOP Concepts:
 * - Encapsulation: Private fields with controlled access
 * - Constructor Overloading: Multiple constructors
 * - Method Overriding: toString()
 */
public class QuizSession {

    // ---- Private Fields ----
    private int id;
    private int userId;
    private int currentDifficulty;       // 1, 2, or 3
    private int totalQuestions;           // Fixed number per quiz
    private String selectedCategory;      // Null means all categories
    private int currentQuestionNumber;    // Which question the user is on
    private int score;                    // Number of correct answers
    private int consecutiveCorrect;       // Streak counter for correct
    private int consecutiveWrong;         // Streak counter for wrong
    private String status;                // "ACTIVE", "COMPLETED", "ABANDONED"
    private Timestamp startedAt;
    private Timestamp endedAt;

    // ---- Constructor Overloading ----

    /** Default constructor */
    public QuizSession() {
        this.currentDifficulty = 2;    // Start at Medium
        this.totalQuestions = 10;
        this.currentQuestionNumber = 0;
        this.score = 0;
        this.consecutiveCorrect = 0;
        this.consecutiveWrong = 0;
        this.status = "ACTIVE";
    }

    /** Constructor for starting a new session */
    public QuizSession(int userId, int totalQuestions) {
        this();
        this.userId = userId;
        this.totalQuestions = totalQuestions;
    }

    /** Constructor for starting a new session with a specific question category */
    public QuizSession(int userId, int totalQuestions, String selectedCategory) {
        this(userId, totalQuestions);
        this.selectedCategory = selectedCategory;
    }

    /** Full constructor (for database reads) */
    public QuizSession(int id, int userId, int currentDifficulty,
                       int totalQuestions, String selectedCategory, int currentQuestionNumber,
                       int score, int consecutiveCorrect, int consecutiveWrong,
                       String status, Timestamp startedAt, Timestamp endedAt) {
        this.id = id;
        this.userId = userId;
        this.currentDifficulty = currentDifficulty;
        this.totalQuestions = totalQuestions;
        this.selectedCategory = selectedCategory;
        this.currentQuestionNumber = currentQuestionNumber;
        this.score = score;
        this.consecutiveCorrect = consecutiveCorrect;
        this.consecutiveWrong = consecutiveWrong;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    // ---- Getters and Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void setCurrentQuestionNumber(int currentQuestionNumber) {
        this.currentQuestionNumber = currentQuestionNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
    }

    public void setConsecutiveCorrect(int consecutiveCorrect) {
        this.consecutiveCorrect = consecutiveCorrect;
    }

    public int getConsecutiveWrong() {
        return consecutiveWrong;
    }

    public void setConsecutiveWrong(int consecutiveWrong) {
        this.consecutiveWrong = consecutiveWrong;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }

    // ---- Business Methods ----

    /** Calculate accuracy percentage */
    public double getAccuracyPercentage() {
        if (currentQuestionNumber == 0) return 0.0;
        return ((double) score / currentQuestionNumber) * 100.0;
    }

    /** Check if quiz is complete */
    public boolean isCompleted() {
        return currentQuestionNumber >= totalQuestions
                || "COMPLETED".equals(status);
    }

    /** Get difficulty label for current difficulty */
    public String getDifficultyLabel() {
        switch (currentDifficulty) {
            case 1:  return "Easy";
            case 2:  return "Medium";
            case 3:  return "Hard";
            default: return "Unknown";
        }
    }

    // ---- Method Overriding ----
    @Override
    public String toString() {
        return "QuizSession{id=" + id + ", userId=" + userId
                + ", score=" + score + "/" + currentQuestionNumber
                + ", category=" + (selectedCategory == null ? "ALL" : selectedCategory)
                + ", difficulty=" + getDifficultyLabel()
                + ", status='" + status + "'}";
    }
}
