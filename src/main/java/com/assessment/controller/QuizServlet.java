package com.assessment.controller;

import com.assessment.model.Question;
import com.assessment.model.QuizSession;
import com.assessment.service.QuizService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * QuizServlet - Handles quiz flow: start, question display, and answer
 * submission
 */
@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    private static final String ACTION_START = "start";
    private static final String ACTION_QUESTION = "question";

    private static final String PATH_LOGIN = "/login";
    private static final String PATH_DASHBOARD = "/dashboard";
    private static final String PATH_QUIZ_QUESTION = "/quiz?action=question";
    private static final String PATH_RESULT = "/result?sessionId=";
    private static final String ALL_CATEGORIES = "__ALL__";

    private static final String VIEW_DASHBOARD = "dashboard.jsp";
    private static final String VIEW_QUIZ = "quiz.jsp";
    private static final String VIEW_FEEDBACK = "feedback.jsp";

    private QuizService quizService;

    @Override
    public void init() throws ServletException {
        quizService = new QuizService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (!isLoggedIn(httpSession)) {
            response.sendRedirect(request.getContextPath() + PATH_LOGIN);
            return;
        }

        String action = getParameterOrDefault(request, "action", ACTION_START);

        try {
            switch (action) {
                case ACTION_START:
                    startQuiz(request, response, httpSession);
                    break;
                case ACTION_QUESTION:
                    showQuestion(request, response, httpSession);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Quiz error: " + e.getMessage());
            request.getRequestDispatcher(VIEW_DASHBOARD).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (!isLoggedIn(httpSession)) {
            response.sendRedirect(request.getContextPath() + PATH_LOGIN);
            return;
        }

        try {
            submitAnswer(request, response, httpSession);
        } catch (Exception e) {
            request.setAttribute("error", "Error submitting answer: " + e.getMessage());
            request.getRequestDispatcher(VIEW_DASHBOARD).forward(request, response);
        }
    }

    /**
     * Start a new quiz session
     */
    private void startQuiz(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws Exception {

        int userId = (int) httpSession.getAttribute("userId");
        int totalQuestions = 10;
        String selectedCategory = request.getParameter("category");
        if (selectedCategory != null) {
            selectedCategory = selectedCategory.trim();
            if (selectedCategory.isEmpty() || ALL_CATEGORIES.equals(selectedCategory)) {
                selectedCategory = null;
            }
        }

        String totalParam = request.getParameter("total");
        if (totalParam != null) {
            try {
                totalQuestions = Integer.parseInt(totalParam);
                if (totalQuestions < 5)
                    totalQuestions = 5;
                if (totalQuestions > 20)
                    totalQuestions = 20;
            } catch (NumberFormatException ignored) {
            }
        }

        QuizSession quizSession = quizService.startQuiz(userId, totalQuestions, selectedCategory);
        httpSession.setAttribute("quizSessionId", quizSession.getId());

        response.sendRedirect(request.getContextPath() + PATH_QUIZ_QUESTION);
    }

    /**
     * Display the current question
     */
    private void showQuestion(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws Exception {

        Integer sessionId = (Integer) httpSession.getAttribute("quizSessionId");
        if (sessionId == null) {
            response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
            return;
        }

        QuizSession quizSession = quizService.getSession(sessionId);

        if (quizSession == null || quizSession.isCompleted()) {
            response.sendRedirect(request.getContextPath() + PATH_RESULT + sessionId);
            return;
        }

        Question question = quizService.getNextQuestion(sessionId);

        if (question == null) {
            quizSession.setStatus("COMPLETED");
            response.sendRedirect(request.getContextPath() + PATH_RESULT + sessionId);
            return;
        }

        request.setAttribute("question", question);
        request.setAttribute("session", quizSession);
        request.setAttribute("questionNumber", quizSession.getCurrentQuestionNumber() + 1);
        request.setAttribute("totalQuestions", quizSession.getTotalQuestions());

        request.getRequestDispatcher(VIEW_QUIZ).forward(request, response);
    }

    /**
     * Process submitted answer
     */
    private void submitAnswer(HttpServletRequest request, HttpServletResponse response,
            HttpSession httpSession) throws Exception {

        Integer sessionId = (Integer) httpSession.getAttribute("quizSessionId");
        if (sessionId == null) {
            response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
            return;
        }

        int questionId = Integer.parseInt(request.getParameter("questionId"));
        String selectedOption = request.getParameter("selectedOption");

        if (selectedOption == null || selectedOption.isEmpty()) {
            response.sendRedirect(request.getContextPath() + PATH_QUIZ_QUESTION);
            return;
        }

        boolean isCorrect = quizService.submitAnswer(sessionId, questionId, selectedOption);

        httpSession.setAttribute("lastAnswerCorrect", isCorrect);
        httpSession.setAttribute("lastQuestionId", questionId);

        QuizSession quizSession = quizService.getSession(sessionId);
        if (quizSession.isCompleted()) {
            response.sendRedirect(request.getContextPath() + PATH_RESULT + sessionId);
        } else {
            request.setAttribute("isCorrect", isCorrect);
            request.setAttribute("session", quizSession);

            Question question = quizService.getQuestionById(questionId);
            request.setAttribute("answeredQuestion", question);
            request.setAttribute("selectedOption", selectedOption);

            request.getRequestDispatcher(VIEW_FEEDBACK).forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("userId") != null;
    }

    private String getParameterOrDefault(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        return value == null ? defaultValue : value;
    }
}
