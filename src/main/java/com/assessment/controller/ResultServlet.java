package com.assessment.controller;

import com.assessment.model.Attempt;
import com.assessment.model.Question;
import com.assessment.model.QuizSession;
import com.assessment.service.QuizService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ResultServlet - Displays quiz results and detailed breakdown
 */
@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String PATH_LOGIN = "/login";
    private static final String PATH_DASHBOARD = "/dashboard";
    private static final String VIEW_RESULT = "result.jsp";
    private static final String VIEW_DASHBOARD = "dashboard.jsp";

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

        try {
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            QuizSession quizSession = quizService.getSession(sessionId);

            if (quizSession == null) {
                response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
                return;
            }

            int userId = (int) httpSession.getAttribute("userId");
            String role = (String) httpSession.getAttribute("role");
            if (quizSession.getUserId() != userId && !ROLE_ADMIN.equals(role)) {
                response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
                return;
            }

            List<Attempt> attempts = quizService.getSessionAttempts(sessionId);
            List<Question> questions = new ArrayList<>();
            for (Attempt attempt : attempts) {
                Question q = quizService.getQuestionById(attempt.getQuestionId());
                questions.add(q);
            }

            request.setAttribute("quizSession", quizSession);
            request.setAttribute("attempts", attempts);
            request.setAttribute("questions", questions);
            request.setAttribute("accuracy", String.format("%.1f", quizSession.getAccuracyPercentage()));

            httpSession.removeAttribute("quizSessionId");

            request.getRequestDispatcher(VIEW_RESULT).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + PATH_DASHBOARD);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to load results: " + e.getMessage());
            request.getRequestDispatcher(VIEW_DASHBOARD).forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("userId") != null;
    }
}
