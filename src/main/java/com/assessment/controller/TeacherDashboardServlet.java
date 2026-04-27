package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends BasePlatformServlet {

    private PlatformService platformService;

    @Override
    public void init() throws ServletException {
        platformService = new PlatformService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer teacherId = requireRole(request, response, PlatformService.ROLE_TEACHER);
        if (teacherId == null) {
            return;
        }

        try {
            exposeFlash(request);
            request.setAttribute("dashboardData", platformService.getTeacherDashboardData(teacherId));
            request.getRequestDispatcher("/teacher_dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load teacher dashboard.", e);
        }
    }
}
