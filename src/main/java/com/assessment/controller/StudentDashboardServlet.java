package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends BasePlatformServlet {

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
            exposeFlash(request);
            request.setAttribute("dashboardData", platformService.getStudentDashboardData(studentId));
            request.getRequestDispatcher("/student_dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load student dashboard.", e);
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
            platformService.joinClassroom(studentId, request.getParameter("classCode"));
            setFlash(request, "success", "Class joined successfully.");
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } catch (Exception e) {
            throw new ServletException("Unable to join classroom.", e);
        }
    }
}
