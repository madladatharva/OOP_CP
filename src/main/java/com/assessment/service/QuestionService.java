package com.assessment.service;

import com.assessment.dao.QuestionDAO;
import com.assessment.model.Question;
import java.sql.SQLException;
import java.util.List;

/**
 * QuestionService - Business logic for question management
 */
public class QuestionService {

    private final QuestionDAO questionDAO;

    public QuestionService() {
        this.questionDAO = new QuestionDAO();
    }

    /**
     * Add a new question (admin operation)
     */
    public Question addQuestion(String questionText, String optionA, String optionB,
            String optionC, String optionD, String correctOption,
            int difficultyLevel, String category, int createdBy)
            throws SQLException {

        // Validation
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }
        if (optionA == null || optionB == null || optionC == null || optionD == null) {
            throw new IllegalArgumentException("All four options are required");
        }
        if (correctOption == null || !correctOption.matches("[A-Da-d]")) {
            throw new IllegalArgumentException("Correct option must be A, B, C, or D");
        }
        if (difficultyLevel < 1 || difficultyLevel > 3) {
            throw new IllegalArgumentException("Difficulty must be 1 (Easy), 2 (Medium), or 3 (Hard)");
        }

        Question question = new Question(
                questionText.trim(), optionA.trim(), optionB.trim(),
                optionC.trim(), optionD.trim(), correctOption.toUpperCase(),
                difficultyLevel, category != null ? category.trim() : "General");
        question.setCreatedBy(createdBy);

        int generatedId = questionDAO.insert(question);
        if (generatedId > 0) {
            question.setId(generatedId);
            return question;
        }
        return null;
    }

    /**
     * Update an existing question
     */
    public boolean updateQuestion(Question question) throws SQLException {
        if (question.getId() <= 0) {
            throw new IllegalArgumentException("Question ID is required for update");
        }
        return questionDAO.update(question);
    }

    /**
     * Delete a question by ID
     */
    public boolean deleteQuestion(int questionId) throws SQLException {
        return questionDAO.delete(questionId);
    }

    /**
     * Get question by ID
     */
    public Question getQuestionById(int id) throws SQLException {
        return questionDAO.findById(id);
    }

    /**
     * Get all questions
     */
    public List<Question> getAllQuestions() throws SQLException {
        return questionDAO.findAll();
    }

    /**
     * Get questions by difficulty
     */
    public List<Question> getQuestionsByDifficulty(int difficultyLevel) throws SQLException {
        return questionDAO.findByDifficulty(difficultyLevel);
    }

    /**
     * Get a random question for a given difficulty and session
     */
    public Question getRandomQuestion(int difficultyLevel, int sessionId) throws SQLException {
        return questionDAO.getRandomQuestion(difficultyLevel, sessionId);
    }

    public Question getRandomQuestion(int difficultyLevel, int sessionId, String category)
            throws SQLException {
        return questionDAO.getRandomQuestion(difficultyLevel, sessionId, category);
    }

    /**
     * Get total question count
     */
    public int getTotalQuestionCount() throws SQLException {
        return questionDAO.countAll();
    }

    /**
     * Get question count by difficulty
     */
    public int getQuestionCountByDifficulty(int difficultyLevel) throws SQLException {
        return questionDAO.countByDifficulty(difficultyLevel);
    }

    public int getQuestionCountByCategory(String category) throws SQLException {
        return questionDAO.countByCategory(category);
    }

    public List<String> getAllCategories() throws SQLException {
        return questionDAO.findAllCategories();
    }
}
