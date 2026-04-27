package com.assessment.controller;

import com.assessment.model.Question;
import com.assessment.model.StudyMaterial;
import com.assessment.service.QuestionService;
import com.assessment.service.StudyMaterialService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AdminServlet - Handles admin dashboard, question management, and study-material upload.
 */
@WebServlet("/admin/*")
@MultipartConfig
public class AdminServlet extends HttpServlet {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String LOGIN_PATH = "/login";
    private static final String ADMIN_DASHBOARD_PATH = "/admin/dashboard";
    private static final String ADMIN_QUESTIONS_PATH = "/admin/questions";

    private static final String VIEW_ADMIN_DASHBOARD = "/admin_dashboard.jsp";
    private static final String VIEW_ADMIN_QUESTIONS = "/admin_questions.jsp";
    private static final String VIEW_ADD_QUESTION = "/admin_add_question.jsp";
    private static final String VIEW_EDIT_QUESTION = "/admin_edit_question.jsp";
    private static final String VIEW_ADMIN_MATERIALS = "/admin_materials.jsp";

    private static final String ROUTE_DASHBOARD = "/dashboard";
    private static final String ROUTE_QUESTIONS = "/questions";
    private static final String ROUTE_QUESTIONS_ADD = "/questions/add";
    private static final String ROUTE_QUESTIONS_EDIT = "/questions/edit";
    private static final String ROUTE_QUESTIONS_DELETE = "/questions/delete";
    private static final String ROUTE_MATERIALS = "/materials";
    private static final String ROUTE_MATERIALS_UPLOAD = "/materials/upload";

    private QuestionService questionService;
    private StudyMaterialService studyMaterialService;

    @Override
    public void init() throws ServletException {
        questionService = new QuestionService();
        studyMaterialService = new StudyMaterialService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this page.");
            return;
        }

        String action = getPathInfoOrDefault(request, ROUTE_DASHBOARD);

        try {
            switch (action) {
                case ROUTE_DASHBOARD:
                    showAdminDashboard(request, response);
                    break;
                case ROUTE_QUESTIONS:
                    listQuestions(request, response);
                    break;
                case ROUTE_QUESTIONS_ADD:
                    showAddQuestionForm(request, response);
                    break;
                case ROUTE_QUESTIONS_EDIT:
                    showEditQuestionForm(request, response);
                    break;
                case ROUTE_QUESTIONS_DELETE:
                    deleteQuestion(request, response);
                    break;
                case ROUTE_MATERIALS:
                    showMaterials(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        String action = getPathInfoOrDefault(request, "");

        try {
            switch (action) {
                case ROUTE_QUESTIONS_ADD:
                    addQuestion(request, response, session);
                    break;
                case ROUTE_QUESTIONS_EDIT:
                    updateQuestion(request, response);
                    break;
                case ROUTE_MATERIALS_UPLOAD:
                    uploadMaterial(request, response, session);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + ADMIN_DASHBOARD_PATH);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            if (ROUTE_MATERIALS_UPLOAD.equals(action)) {
                try {
                    showMaterials(request, response);
                } catch (Exception materialViewError) {
                    throw new ServletException("Failed to reload the materials page.", materialViewError);
                }
            } else {
                try {
                    showAdminDashboard(request, response);
                } catch (Exception dashboardError) {
                    throw new ServletException("Failed to reload the admin dashboard.", dashboardError);
                }
            }
        }
    }

    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("totalQuestions", questionService.getTotalQuestionCount());
        request.setAttribute("easyCount", questionService.getQuestionCountByDifficulty(1));
        request.setAttribute("mediumCount", questionService.getQuestionCountByDifficulty(2));
        request.setAttribute("hardCount", questionService.getQuestionCountByDifficulty(3));
        request.setAttribute("materialCount", studyMaterialService.getMaterialCount());

        request.getRequestDispatcher(VIEW_ADMIN_DASHBOARD).forward(request, response);
    }

    private void listQuestions(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Question> questions = questionService.getAllQuestions();
        request.setAttribute("questions", questions);
        request.getRequestDispatcher(VIEW_ADMIN_QUESTIONS).forward(request, response);
    }

    private void showAddQuestionForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_ADD_QUESTION).forward(request, response);
    }

    private void showEditQuestionForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        Question question = questionService.getQuestionById(id);

        if (question == null) {
            response.sendRedirect(request.getContextPath() + ADMIN_QUESTIONS_PATH);
            return;
        }

        request.setAttribute("question", question);
        request.getRequestDispatcher(VIEW_EDIT_QUESTION).forward(request, response);
    }

    private void showMaterials(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<StudyMaterial> materials = studyMaterialService.getAllMaterials();
        request.setAttribute("materials", materials);

        String generatedCategory = request.getParameter("generatedCategory");
        String generatedCount = request.getParameter("generatedCount");
        if (generatedCategory != null && generatedCount != null) {
            request.setAttribute(
                    "success",
                    "Generated " + generatedCount + " quiz questions for topic '" + generatedCategory + "'.");
        }

        request.getRequestDispatcher(VIEW_ADMIN_MATERIALS).forward(request, response);
    }

    private void addQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        String questionText = request.getParameter("questionText");
        String optionA = request.getParameter("optionA");
        String optionB = request.getParameter("optionB");
        String optionC = request.getParameter("optionC");
        String optionD = request.getParameter("optionD");
        String correctOption = request.getParameter("correctOption");
        int difficultyLevel = Integer.parseInt(request.getParameter("difficultyLevel"));
        String category = request.getParameter("category");
        int createdBy = (int) session.getAttribute("userId");

        questionService.addQuestion(
                questionText, optionA, optionB, optionC, optionD,
                correctOption, difficultyLevel, category, createdBy);

        response.sendRedirect(request.getContextPath() + ADMIN_QUESTIONS_PATH);
    }

    private void updateQuestion(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Question question = new Question();
        question.setId(Integer.parseInt(request.getParameter("id")));
        question.setQuestionText(request.getParameter("questionText"));
        question.setOptionA(request.getParameter("optionA"));
        question.setOptionB(request.getParameter("optionB"));
        question.setOptionC(request.getParameter("optionC"));
        question.setOptionD(request.getParameter("optionD"));
        question.setCorrectOption(request.getParameter("correctOption"));
        question.setDifficultyLevel(Integer.parseInt(request.getParameter("difficultyLevel")));
        question.setCategory(request.getParameter("category"));

        questionService.updateQuestion(question);
        response.sendRedirect(request.getContextPath() + ADMIN_QUESTIONS_PATH);
    }

    private void deleteQuestion(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        questionService.deleteQuestion(id);
        response.sendRedirect(request.getContextPath() + ADMIN_QUESTIONS_PATH);
    }

    private void uploadMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {
        String title = request.getParameter("title");
        String quizCategory = request.getParameter("quizCategory");
        int questionCount = Integer.parseInt(request.getParameter("questionCount"));
        Part pdfFile = request.getPart("pdfFile");
        int uploadedBy = (int) session.getAttribute("userId");

        StudyMaterial material = studyMaterialService.uploadMaterialAndGenerateQuiz(
                title, quizCategory, pdfFile, questionCount, uploadedBy);

        String encodedCategory = URLEncoder.encode(
                material.getQuizCategory(), StandardCharsets.UTF_8.toString());
        response.sendRedirect(request.getContextPath() + ROUTE_MATERIALS
                + "?generatedCategory=" + encodedCategory
                + "&generatedCount=" + material.getGeneratedQuestionCount());
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && ROLE_ADMIN.equals(session.getAttribute("role"));
    }

    private String getPathInfoOrDefault(HttpServletRequest request, String defaultPath) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? defaultPath : pathInfo;
    }
}
