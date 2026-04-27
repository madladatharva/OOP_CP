package com.assessment.model;

import java.sql.Timestamp;

/**
 * Stores uploaded theory material used to generate quiz questions.
 */
public class StudyMaterial {

    private int id;
    private String title;
    private String quizCategory;
    private String originalFilename;
    private String storedPath;
    private String contentType;
    private long fileSizeBytes;
    private String extractedText;
    private int generatedQuestionCount;
    private int uploadedBy;
    private Timestamp uploadedAt;

    public StudyMaterial() {
    }

    public StudyMaterial(String title, String quizCategory, String originalFilename,
            String storedPath, String contentType, long fileSizeBytes,
            String extractedText, int generatedQuestionCount, int uploadedBy) {
        this.title = title;
        this.quizCategory = quizCategory;
        this.originalFilename = originalFilename;
        this.storedPath = storedPath;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
        this.extractedText = extractedText;
        this.generatedQuestionCount = generatedQuestionCount;
        this.uploadedBy = uploadedBy;
    }

    public StudyMaterial(int id, String title, String quizCategory, String originalFilename,
            String storedPath, String contentType, long fileSizeBytes,
            String extractedText, int generatedQuestionCount, int uploadedBy,
            Timestamp uploadedAt) {
        this.id = id;
        this.title = title;
        this.quizCategory = quizCategory;
        this.originalFilename = originalFilename;
        this.storedPath = storedPath;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
        this.extractedText = extractedText;
        this.generatedQuestionCount = generatedQuestionCount;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(String quizCategory) {
        this.quizCategory = quizCategory;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredPath() {
        return storedPath;
    }

    public void setStoredPath(String storedPath) {
        this.storedPath = storedPath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public int getGeneratedQuestionCount() {
        return generatedQuestionCount;
    }

    public void setGeneratedQuestionCount(int generatedQuestionCount) {
        this.generatedQuestionCount = generatedQuestionCount;
    }

    public int getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getExcerpt() {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            return "No text preview available.";
        }
        String normalized = extractedText.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 180) {
            return normalized;
        }
        return normalized.substring(0, 177) + "...";
    }

    @Override
    public String toString() {
        return "StudyMaterial{id=" + id + ", title='" + title + "', quizCategory='"
                + quizCategory + "', generatedQuestionCount=" + generatedQuestionCount + "}";
    }
}
