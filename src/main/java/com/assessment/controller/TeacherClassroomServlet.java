package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher/classes")
public class TeacherClassroomServlet extends BasePlatformServlet {

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
            String classIdValue = request.getParameter("classId");
            if (classIdValue != null && !classIdValue.trim().isEmpty()) {
                int classroomId = Integer.parseInt(classIdValue);
                if (platformService.getTeacherClassroom(teacherId, classroomId) == null) {
                    setFlash(request, "error", "Classroom not found.");
                    response.sendRedirect(request.getContextPath() + "/teacher/classes");
                    return;
                }
                request.setAttribute("classroom", platformService.getTeacherClassroom(teacherId, classroomId));
                request.setAttribute("students", platformService.getClassroomStudents(classroomId));
                request.getRequestDispatcher("/teacher_class_detail.jsp").forward(request, response);
                return;
            }

            request.setAttribute("classrooms", platformService.getTeacherClassrooms(teacherId));
            request.getRequestDispatcher("/teacher_classes.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load classrooms.", e);
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
                platformService.createClassroom(
                        teacherId,
                        request.getParameter("name"),
                        request.getParameter("description"));
                setFlash(request, "success", "Class created successfully.");
                response.sendRedirect(request.getContextPath() + "/teacher/classes");
                return;
            }

            if ("add-student".equals(action)) {
                int classroomId = Integer.parseInt(request.getParameter("classId"));
                platformService.addStudentToClassroom(teacherId, classroomId, request.getParameter("studentIdentifier"));
                setFlash(request, "success", "Student added to class.");
                response.sendRedirect(request.getContextPath() + "/teacher/classes?classId=" + classroomId);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/teacher/classes");
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            String classroomId = request.getParameter("classId");
            if ("add-student".equals(action) && classroomId != null && !classroomId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/teacher/classes?classId=" + classroomId);
            } else {
                response.sendRedirect(request.getContextPath() + "/teacher/classes");
            }
        } catch (Exception e) {
            throw new ServletException("Unable to update classroom data.", e);
        }
    }
}
