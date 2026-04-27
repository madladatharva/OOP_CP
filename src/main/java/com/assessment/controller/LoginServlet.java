package com.assessment.controller;

import com.assessment.model.User;
import com.assessment.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet - Handles user login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VIEW_LOGIN = "login.jsp";
    private static final String PATH_TEACHER_DASHBOARD = "/teacher/dashboard";
    private static final String PATH_STUDENT_DASHBOARD = "/student/dashboard";
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userService.login(username, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

                if (user.isTeacher()) {
                    response.sendRedirect(request.getContextPath() + PATH_TEACHER_DASHBOARD);
                } else {
                    response.sendRedirect(request.getContextPath() + PATH_STUDENT_DASHBOARD);
                }
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Login failed: " + e.getMessage());
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        }
    }
}
