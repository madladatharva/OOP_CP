package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/teacher/quizzes")
public class TeacherQuizServlet extends BasePlatformServlet {

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
            request.setAttribute("subjects", platformService.getSubjects());
            request.setAttribute("topics", platformService.getTopics(null));
            request.setAttribute("classrooms", platformService.getTeacherClassrooms(teacherId));
            request.setAttribute("quizzes", platformService.getTeacherQuizzes(teacherId));
            request.setAttribute("assignments", platformService.getTeacherAssignments(teacherId));
            request.getRequestDispatcher("/teacher_quizzes.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load quiz management page.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer teacherId = requireRole(request, response, PlatformService.ROLE_TEACHER);
        if (teacherId == null) {
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                platformService.createQuiz(
                        teacherId,
                        Integer.parseInt(request.getParameter("subjectId")),
                        request.getParameter("title"),
                        request.getParameter("description"),
                        parseTopicIds(request.getParameterValues("topicIds")),
                        Integer.parseInt(request.getParameter("questionCount")),
                        Integer.parseInt(request.getParameter("timeLimitMinutes")));
                setFlash(request, "success", "Quiz created successfully.");
            } else if ("assign".equals(action)) {
                Timestamp deadlineAt = Timestamp.valueOf(LocalDateTime.parse(request.getParameter("deadlineAt")));
                platformService.assignQuiz(
                        teacherId,
                        Integer.parseInt(request.getParameter("quizId")),
                        Integer.parseInt(request.getParameter("classroomId")),
                        deadlineAt);
                setFlash(request, "success", "Quiz assigned successfully.");
            }

            response.sendRedirect(request.getContextPath() + "/teacher/quizzes");
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/quizzes");
        } catch (Exception e) {
            throw new ServletException("Unable to update quiz configuration.", e);
        }
    }

    private List<Integer> parseTopicIds(String[] values) {
        List<Integer> ids = new ArrayList<>();
        if (values == null) {
            return ids;
        }
        for (String value : values) {
            ids.add(Integer.parseInt(value));
        }
        return ids;
    }
}
