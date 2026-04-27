package com.assessment.controller;

import com.assessment.service.PlatformService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

@WebServlet("/teacher/questions")
@MultipartConfig
public class TeacherQuestionBankServlet extends BasePlatformServlet {

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
            Integer subjectId = parseInteger(request.getParameter("subjectId"));
            Integer topicId = parseInteger(request.getParameter("topicId"));
            Integer difficulty = parseInteger(request.getParameter("difficulty"));
            Integer editId = parseInteger(request.getParameter("editId"));

            request.setAttribute("subjects", platformService.getSubjects());
            request.setAttribute("topics", platformService.getTopics(null));
            request.setAttribute("questions", platformService.searchQuestionBank(subjectId, topicId, difficulty));
            request.setAttribute("selectedSubjectId", subjectId);
            request.setAttribute("selectedTopicId", topicId);
            request.setAttribute("selectedDifficulty", difficulty);
            if (editId != null) {
                request.setAttribute("editingQuestion", platformService.getQuestion(editId));
            }

            request.getRequestDispatcher("/teacher_questions.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Unable to load question bank.", e);
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
            if ("save".equals(action)) {
                platformService.saveQuestion(
                        teacherId,
                        parseInteger(request.getParameter("questionId")),
                        Integer.parseInt(request.getParameter("subjectId")),
                        Integer.parseInt(request.getParameter("topicId")),
                        request.getParameter("questionText"),
                        request.getParameter("optionA"),
                        request.getParameter("optionB"),
                        request.getParameter("optionC"),
                        request.getParameter("optionD"),
                        request.getParameter("correctOption"),
                        Integer.parseInt(request.getParameter("difficultyLevel")));
                setFlash(request, "success", "Question saved successfully.");
            } else if ("import".equals(action)) {
                Part csvPart = request.getPart("csvFile");
                int inserted = platformService.importQuestionsFromCsv(teacherId, csvPart);
                setFlash(request, "success", "Imported " + inserted + " questions from CSV.");
            } else if ("archive".equals(action)) {
                platformService.archiveQuestion(Integer.parseInt(request.getParameter("questionId")));
                setFlash(request, "success", "Question archived.");
            }

            response.sendRedirect(request.getContextPath() + "/teacher/questions");
        } catch (IllegalArgumentException e) {
            setFlash(request, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/questions");
        } catch (Exception e) {
            throw new ServletException("Unable to update question bank.", e);
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
