package com.assessment.model;

import java.sql.Timestamp;

/**
 * Question Model Class - Demonstrates ENCAPSULATION and CONSTRUCTOR OVERLOADING
 * 
 * OOP Concepts:
 * - Encapsulation: Private fields with public accessors
 * - Constructor Overloading: Different constructors for different use cases
 * - Method Overriding: Custom toString()
 */
public class Question {

    // ---- Private Fields (Encapsulation) ----
    private int id;
    private Integer subjectId;
    private String subjectName;
    private Integer topicId;
    private String topicName;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // "A", "B", "C", or "D"
    private int difficultyLevel;  // 1 = Easy, 2 = Medium, 3 = Hard
    private String category;
    private int createdBy;
    private Timestamp createdAt;

    // ---- Constructor Overloading ----

    /** Default constructor */
    public Question() {
        this.difficultyLevel = 2; // Default: Medium
        this.category = "General";
    }

    /** Constructor for creating a new question (without id) */
    public Question(String questionText, String optionA, String optionB,
                    String optionC, String optionD, String correctOption,
                    int difficultyLevel, String category) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.difficultyLevel = difficultyLevel;
        this.category = category;
    }

    /** Full constructor (for database reads) */
    public Question(int id, String questionText, String optionA, String optionB,
                    String optionC, String optionD, String correctOption,
                    int difficultyLevel, String category, int createdBy,
                    Timestamp createdAt) {
        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.difficultyLevel = difficultyLevel;
        this.category = category;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // ---- Getters and Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // ---- Convenience Methods ----

    /**
     * Sets all four options from a list.
     * @param options A list of 4 strings for options A, B, C, and D.
     */
    public void setOptions(java.util.List<String> options) {
        if (options != null && options.size() == 4) {
            this.optionA = options.get(0);
            this.optionB = options.get(1);
            this.optionC = options.get(2);
            this.optionD = options.get(3);
        }
    }

    /**
     * Gets all four options as a list.
     * @return A list containing options A, B, C, and D.
     */
    public java.util.List<String> getOptions() {
        return java.util.Arrays.asList(optionA, optionB, optionC, optionD);
    }

    /**
     * Checks if the provided answer is correct.
     * @param answer The user's selected answer (e.g., "A", "B").
     * @return true if the answer is correct, false otherwise.
     */
    public boolean isCorrectAnswer(String answer) {
        return this.correctOption.equalsIgnoreCase(answer);
    }

    /**
     * Returns a user-friendly label for the difficulty level.
     * @return "Easy", "Medium", or "Hard".
     */
    public String getDifficultyLabel() {
        switch (this.difficultyLevel) {
            case 1:
                return "Easy";
            case 3:
                return "Hard";
            case 2:
            default:
                return "Medium";
        }
    }

    // ---- Method Overriding ----

    @Override
    public String toString() {
        return "Question{id=" + id + ", difficulty=" + getDifficultyLabel()
                + ", category='" + category + "'}";
    }
}
