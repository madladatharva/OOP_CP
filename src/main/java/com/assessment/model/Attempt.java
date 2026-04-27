package com.assessment.model;

import java.sql.Timestamp;

/**
 * Attempt Model Class - Records an individual question attempt
 * 
 * OOP Concepts:
 * - Encapsulation: Private fields with public getters/setters
 * - Constructor Overloading: Multiple constructors
 */
public class Attempt {

    // ---- Private Fields ----
    private int id;
    private int sessionId;
    private int questionId;
    private String selectedOption;
    private boolean correct;
    private int difficultyAtTime;
    private Timestamp attemptedAt;

    // ---- Constructor Overloading ----

    /** Default constructor */
    public Attempt() {
    }

    /** Constructor for recording a new attempt */
    public Attempt(int sessionId, int questionId, String selectedOption,
                   boolean correct, int difficultyAtTime) {
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correct = correct;
        this.difficultyAtTime = difficultyAtTime;
    }

    /** Full constructor (for database reads) */
    public Attempt(int id, int sessionId, int questionId, String selectedOption,
                   boolean correct, int difficultyAtTime, Timestamp attemptedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correct = correct;
        this.difficultyAtTime = difficultyAtTime;
        this.attemptedAt = attemptedAt;
    }

    // ---- Getters and Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getDifficultyAtTime() {
        return difficultyAtTime;
    }

    public void setDifficultyAtTime(int difficultyAtTime) {
        this.difficultyAtTime = difficultyAtTime;
    }

    public Timestamp getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Timestamp attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    // ---- Method Overriding ----
    @Override
    public String toString() {
        return "Attempt{id=" + id + ", sessionId=" + sessionId
                + ", questionId=" + questionId
                + ", correct=" + correct + "}";
    }
}
