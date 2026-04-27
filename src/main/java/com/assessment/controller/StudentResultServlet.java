package com.assessment.controller;

import com.assessment.model.AssessmentSession;
import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/student/results")
public class StudentResultServlet extends BasePlatformServlet {

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
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            AssessmentSession session = platformService.getAssessmentSession(studentId, sessionId);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }

            request.setAttribute("session", session);
            request.setAttribute("questions", platformService.getSessionResults(studentId, sessionId));
            request.setAttribute("topicPerformance", platformService.getSessionTopicPerformance(studentId, sessionId));
            request.getRequestDispatcher("/student_result.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load quiz result.", e);
        }
    }
}
