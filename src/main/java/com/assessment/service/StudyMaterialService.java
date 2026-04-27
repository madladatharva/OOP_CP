package com.assessment.service;

import com.assessment.dao.QuestionDAO;
import com.assessment.dao.StudyMaterialDAO;
import com.assessment.model.Question;
import com.assessment.model.StudyMaterial;
import com.assessment.util.DBConnection;
import com.assessment.util.PdfQuestionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Handles PDF upload, storage, text extraction, and quiz generation.
 */
public class StudyMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialService.class);
    private static final int MIN_GENERATED_QUESTIONS = 3;
    private static final int MAX_GENERATED_QUESTIONS = 25;

    private final StudyMaterialDAO studyMaterialDAO;
    private final QuestionDAO questionDAO;

    public StudyMaterialService() {
        this.studyMaterialDAO = new StudyMaterialDAO();
        this.questionDAO = new QuestionDAO();
    }

    public StudyMaterial uploadMaterialAndGenerateQuiz(String title, String quizCategory,
            Part pdfPart, int requestedQuestionCount, int uploadedBy)
            throws SQLException, IOException {

        String normalizedTitle = normalizeRequiredField(title, "Material title");
        if (normalizedTitle.length() > 150) {
            normalizedTitle = normalizedTitle.substring(0, 150);
        }
        String normalizedCategory = buildQuizCategory(quizCategory, normalizedTitle);
        int questionCount = normalizeQuestionCount(requestedQuestionCount);
        String originalFilename = sanitizeFilename(pdfPart != null ? pdfPart.getSubmittedFileName() : null);

        if (pdfPart == null || originalFilename == null || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("Please upload a valid PDF file.");
        }

        Path uploadDir = resolveUploadDirectory();
        Files.createDirectories(uploadDir);

        Path storedFile = uploadDir.resolve(UUID.randomUUID() + "-" + originalFilename);
        try (InputStream inputStream = pdfPart.getInputStream()) {
            Files.copy(inputStream, storedFile, StandardCopyOption.REPLACE_EXISTING);
        }

        boolean committed = false;
        try {
            String extractedText = PdfQuestionGenerator.extractText(storedFile);
            List<Question> generatedQuestions = PdfQuestionGenerator.generateQuestions(
                    extractedText, normalizedCategory, questionCount);

            for (Question question : generatedQuestions) {
                question.setCreatedBy(uploadedBy);
            }

            StudyMaterial material = new StudyMaterial(
                    normalizedTitle,
                    normalizedCategory,
                    originalFilename,
                    storedFile.toAbsolutePath().toString(),
                    pdfPart.getContentType(),
                    pdfPart.getSize(),
                    extractedText,
                    generatedQuestions.size(),
                    uploadedBy);

            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                int materialId = studyMaterialDAO.insert(material, conn);
                material.setId(materialId);
                questionDAO.insertBatch(generatedQuestions, conn);

                conn.commit();
                committed = true;
                logger.info("Generated {} questions from uploaded PDF '{}' into category '{}'",
                        generatedQuestions.size(), originalFilename, normalizedCategory);
                return material;
            } catch (Exception e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
        } catch (IllegalArgumentException | IOException | SQLException e) {
            if (!committed) {
                Files.deleteIfExists(storedFile);
            }
            throw e;
        } catch (Exception e) {
            if (!committed) {
                Files.deleteIfExists(storedFile);
            }
            throw new IllegalStateException("Failed to generate quiz from the uploaded PDF.", e);
        }
    }

    public List<StudyMaterial> getAllMaterials() throws SQLException {
        return studyMaterialDAO.findAll();
    }

    public int getMaterialCount() throws SQLException {
        return studyMaterialDAO.countAll();
    }

    private String normalizeRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String buildQuizCategory(String quizCategory, String fallbackTitle) {
        String candidate = quizCategory == null || quizCategory.trim().isEmpty()
                ? fallbackTitle : quizCategory.trim();
        if (candidate.length() > 100) {
            candidate = candidate.substring(0, 100);
        }
        return candidate;
    }

    private int normalizeQuestionCount(int requestedQuestionCount) {
        if (requestedQuestionCount < MIN_GENERATED_QUESTIONS) {
            return MIN_GENERATED_QUESTIONS;
        }
        if (requestedQuestionCount > MAX_GENERATED_QUESTIONS) {
            return MAX_GENERATED_QUESTIONS;
        }
        return requestedQuestionCount;
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }
        String justName = Paths.get(filename).getFileName().toString();
        return justName.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private Path resolveUploadDirectory() {
        String configuredDir = System.getProperty("smart.assessment.uploadDir");
        if (configuredDir != null && !configuredDir.trim().isEmpty()) {
            return Paths.get(configuredDir);
        }

        String baseDir = System.getProperty("catalina.base", System.getProperty("java.io.tmpdir"));
        return Paths.get(baseDir, "smart-assessment-uploads");
    }
}
