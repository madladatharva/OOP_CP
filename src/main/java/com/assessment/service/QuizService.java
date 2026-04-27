package com.assessment.service;

import com.assessment.dao.AttemptDAO;
import com.assessment.dao.QuestionDAO;
import com.assessment.dao.QuizSessionDAO;
import com.assessment.model.Attempt;
import com.assessment.model.Question;
import com.assessment.model.QuizSession;
import com.assessment.strategy.AdaptiveStrategy;
import com.assessment.strategy.RuleBasedAdaptive;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * QuizService - Orchestrates quiz logic including adaptive difficulty
 * 
 * OOP Concepts:
 * - Polymorphism: Uses AdaptiveStrategy interface reference (can swap
 * implementations)
 * - Composition: Composes multiple DAOs and strategy
 */
public class QuizService {

    private final QuizSessionDAO sessionDAO;
    private final QuestionDAO questionDAO;
    private final AttemptDAO attemptDAO;
    private final AdaptiveStrategy adaptiveStrategy; // Polymorphism: interface reference

    /** Default constructor - uses RuleBasedAdaptive strategy */
    public QuizService() {
        this.sessionDAO = new QuizSessionDAO();
        this.questionDAO = new QuestionDAO();
        this.attemptDAO = new AttemptDAO();
        this.adaptiveStrategy = new RuleBasedAdaptive(); // Polymorphism in action
    }

    /** Constructor with custom strategy (Polymorphism - Strategy Pattern) */
    public QuizService(AdaptiveStrategy strategy) {
        this.sessionDAO = new QuizSessionDAO();
        this.questionDAO = new QuestionDAO();
        this.attemptDAO = new AttemptDAO();
        this.adaptiveStrategy = strategy;
    }

    /**
     * Start a new quiz session for a user
     * 
     * @param userId         The user starting the quiz
     * @param totalQuestions Number of questions in the quiz
     * @return Created QuizSession
     */
    public QuizSession startQuiz(int userId, int totalQuestions) throws SQLException {
        return startQuiz(userId, totalQuestions, null);
    }

    public QuizSession startQuiz(int userId, int totalQuestions, String selectedCategory)
            throws SQLException {
        // Abandon any existing active session
        QuizSession existing = sessionDAO.findActiveSession(userId);
        if (existing != null) {
            existing.setStatus("ABANDONED");
            existing.setEndedAt(new Timestamp(System.currentTimeMillis()));
            sessionDAO.update(existing);
        }

        int availableQuestions = questionDAO.countByCategory(selectedCategory);
        if (availableQuestions <= 0) {
            throw new IllegalArgumentException("No questions are available for the selected quiz topic.");
        }

        int finalQuestionCount = Math.min(totalQuestions, availableQuestions);

        // Create new session starting at Medium difficulty
        QuizSession session = new QuizSession(userId, finalQuestionCount, selectedCategory);
        int sessionId = sessionDAO.insert(session);
        session.setId(sessionId);

        return session;
    }

    /**
     * Get the next question for the current session
     * 
     * @param sessionId The active session ID
     * @return Next Question based on current difficulty, or null if quiz is over
     */
    public Question getNextQuestion(int sessionId) throws SQLException {
        QuizSession session = sessionDAO.findById(sessionId);

        if (session == null || session.isCompleted()) {
            return null;
        }

        // Get a random question at the current difficulty level
        return questionDAO.getRandomQuestion(
                session.getCurrentDifficulty(),
                sessionId,
                session.getSelectedCategory());
    }

    /**
     * Submit an answer and apply adaptive logic
     * 
     * This is the core method that:
     * 1. Checks if the answer is correct
     * 2. Records the attempt
     * 3. Updates the score
     * 4. Applies adaptive difficulty adjustment
     * 5. Updates the session
     * 
     * @param sessionId      The session ID
     * @param questionId     The question being answered
     * @param selectedOption The user's answer (A, B, C, or D)
     * @return true if answer was correct, false otherwise
     */
    public boolean submitAnswer(int sessionId, int questionId, String selectedOption)
            throws SQLException {

        // 1. Get session and question
        QuizSession session = sessionDAO.findById(sessionId);
        Question question = questionDAO.findById(questionId);

        if (session == null || question == null) {
            throw new IllegalArgumentException("Invalid session or question ID");
        }

        // 2. Check correctness
        boolean isCorrect = question.isCorrectAnswer(selectedOption);

        // 3. Record the attempt in database
        Attempt attempt = new Attempt(
                sessionId, questionId, selectedOption.toUpperCase(),
                isCorrect, session.getCurrentDifficulty());
        attemptDAO.insert(attempt);

        // 4. Update score and question count
        if (isCorrect) {
            session.setScore(session.getScore() + 1);
        }
        session.setCurrentQuestionNumber(session.getCurrentQuestionNumber() + 1);

        // 5. Apply adaptive difficulty adjustment (POLYMORPHISM)
        adaptiveStrategy.adjustDifficulty(session, isCorrect);

        // 6. Check if quiz is complete
        if (session.getCurrentQuestionNumber() >= session.getTotalQuestions()) {
            session.setStatus("COMPLETED");
            session.setEndedAt(new Timestamp(System.currentTimeMillis()));
        }

        // 7. Save updated session to database
        sessionDAO.update(session);

        return isCorrect;
    }

    /**
     * Get the current session state
     */
    public QuizSession getSession(int sessionId) throws SQLException {
        return sessionDAO.findById(sessionId);
    }

    /**
     * Get all attempts for a session (for results page)
     */
    public List<Attempt> getSessionAttempts(int sessionId) throws SQLException {
        return attemptDAO.findBySessionId(sessionId);
    }

    /**
     * Get all completed sessions for a user (history)
     */
    public List<QuizSession> getUserHistory(int userId) throws SQLException {
        return sessionDAO.findByUserId(userId);
    }

    /**
     * Get the active session for a user (if any)
     */
    public QuizSession getActiveSession(int userId) throws SQLException {
        return sessionDAO.findActiveSession(userId);
    }

    /**
     * Get question by ID (for results display)
     */
    public Question getQuestionById(int questionId) throws SQLException {
        return questionDAO.findById(questionId);
    }

    /**
     * Get the name of the currently active adaptive strategy
     */
    public String getStrategyName() {
        return adaptiveStrategy.getStrategyName();
    }
}
