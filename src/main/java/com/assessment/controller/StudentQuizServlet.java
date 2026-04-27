package com.assessment.controller;

import com.assessment.model.AssessmentSession;
import com.assessment.model.SessionQuestion;
import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/student/quiz")
public class StudentQuizServlet extends BasePlatformServlet {

    private PlatformService platformService;

    @Override
    public void init() throws ServletException {
        platformService = new PlatformService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer studentId = requireRole(request, response, PlatformService.ROLE_STUDENT);
        if (studentId == null) {
            return;
        }

        try {
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            AssessmentSession session = platformService.ensureStudentSession(studentId, assignmentId);
            if (!"IN_PROGRESS".equals(session.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/student/results?sessionId=" + session.getId());
                return;
            }

            SessionQuestion question = platformService.getCurrentQuestion(studentId, assignmentId);
            session = platformService.ensureStudentSession(studentId, assignmentId);
            if (question == null || !"IN_PROGRESS".equals(session.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/student/results?sessionId=" + session.getId());
                return;
            }

            request.setAttribute("session", session);
            request.setAttribute("question", question);
            request.setAttribute("remainingSeconds", platformService.getRemainingSeconds(session));
            request.getRequestDispatcher("/student_quiz.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } catch (Exception e) {
            throw new ServletException("Unable to load quiz session.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer studentId = requireRole(request, response, PlatformService.ROLE_STUDENT);
        if (studentId == null) {
            return;
        }

        try {
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            String action = request.getParameter("action");
            AssessmentSession session;
            if ("timeout".equals(action)) {
                session = platformService.submitSessionOnTimeout(studentId, sessionId);
            } else {
                session = platformService.submitAnswer(
                        studentId,
                        sessionId,
                        Integer.parseInt(request.getParameter("sessionQuestionId")),
                        request.getParameter("selectedOption"));
            }

            if ("IN_PROGRESS".equals(session.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/student/quiz?assignmentId=" + session.getAssignmentId());
            } else {
                response.sendRedirect(request.getContextPath() + "/student/results?sessionId=" + session.getId());
            }
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } catch (Exception e) {
            throw new ServletException("Unable to submit quiz answer.", e);
        }
    }
}
