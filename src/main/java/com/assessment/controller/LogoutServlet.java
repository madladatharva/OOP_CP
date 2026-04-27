package com.assessment.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LogoutServlet - Handles user logout by invalidating session
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final String PATH_LOGIN = "/login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + PATH_LOGIN);
    }
}
