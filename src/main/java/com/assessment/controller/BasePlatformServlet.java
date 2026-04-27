package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class BasePlatformServlet extends HttpServlet {

    protected Integer requireRole(HttpServletRequest request, HttpServletResponse response, String role)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        String actualRole = (String) session.getAttribute("role");
        if (!role.equals(actualRole)) {
            if (PlatformService.ROLE_TEACHER.equals(actualRole)) {
                response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            }
            return null;
        }

        return (Integer) session.getAttribute("userId");
    }

    protected void setFlash(HttpServletRequest request, String type, String message) {
        request.getSession().setAttribute("flashType", type);
        request.getSession().setAttribute("flashMessage", message);
    }

    protected void exposeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        request.setAttribute("flashType", session.getAttribute("flashType"));
        request.setAttribute("flashMessage", session.getAttribute("flashMessage"));
        session.removeAttribute("flashType");
        session.removeAttribute("flashMessage");
    }
}
