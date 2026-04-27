package com.assessment.controller;

import com.assessment.model.User;
import com.assessment.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * RegisterServlet - Handles user registration
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String VIEW_REGISTER = "register.jsp";
    private static final String VIEW_LOGIN = "login.jsp";

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");

        try {
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            User user = userService.register(username, password, email, fullName, role);

            if (user != null) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("role", role);
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
        }
    }
}
