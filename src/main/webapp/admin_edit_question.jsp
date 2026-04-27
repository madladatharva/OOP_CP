<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Edit Question #${question.id} - AdaptIQ Admin</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
        </head>

        <body>
            <div class="page-layout">
                <!-- Sidebar -->
                <aside class="sidebar">
                    <div class="sidebar-brand">
                        <div class="brand-logo">A</div>
                        <div class="brand-text">
                            <span class="brand-name">AdaptIQ</span>
                            <span class="brand-subtitle">Admin Panel</span>
                        </div>
                    </div>
                    <nav class="sidebar-nav">
                        <a href="${pageContext.request.contextPath}/admin/dashboard">
                            <span class="material-icons-outlined">dashboard</span> Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions" class="active">
                            <span class="material-icons-outlined">quiz</span> Questions
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/materials">
                            <span class="material-icons-outlined">upload_file</span> PDF Quiz Generator
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions/add">
                            <span class="material-icons-outlined">add_circle</span> Add Question
                        </a>
                        <div class="nav-section-title">Navigation</div>
                        <a href="${pageContext.request.contextPath}/dashboard">
                            <span class="material-icons-outlined">home</span> Student Dashboard
                        </a>
                    </nav>
                    <div class="sidebar-user">
                        <div class="user-avatar" style="background:var(--danger-bg);color:var(--danger);">A</div>
                        <div class="user-info">
                            <span class="user-name">${sessionScope.username}</span>
                            <span class="user-role">Administrator</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" style="margin-left:auto;color:var(--text-muted);" title="Logout">
                            <span class="material-icons-outlined" style="font-size:20px;">logout</span>
                        </a>
                    </div>
                </aside>

                <!-- Main -->
                <main class="main-content animate-fade-in">
                    <div class="page-header">
                        <h1>Edit Question #${question.id}</h1>
                        <p>Update the question details below</p>
                    </div>

                    <div class="card" style="max-width:720px;">
                        <form method="post" action="${pageContext.request.contextPath}/admin/questions/edit" id="editQuestionForm">
                            <input type="hidden" name="id" value="${question.id}">

                            <div class="form-group">
                                <label for="questionText">Question Text <span style="color:var(--danger);">*</span></label>
                                <textarea id="questionText" name="questionText" class="form-control"
                                    required style="min-height:100px;resize:vertical;">${question.questionText}</textarea>
                            </div>

                            <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
                                <div class="form-group">
                                    <label for="optionA">Option A <span style="color:var(--danger);">*</span></label>
                                    <input type="text" id="optionA" name="optionA" class="form-control"
                                        value="${question.optionA}" required>
                                </div>
                                <div class="form-group">
                                    <label for="optionB">Option B <span style="color:var(--danger);">*</span></label>
                                    <input type="text" id="optionB" name="optionB" class="form-control"
                                        value="${question.optionB}" required>
                                </div>
                                <div class="form-group">
                                    <label for="optionC">Option C <span style="color:var(--danger);">*</span></label>
                                    <input type="text" id="optionC" name="optionC" class="form-control"
                                        value="${question.optionC}" required>
                                </div>
                                <div class="form-group">
                                    <label for="optionD">Option D <span style="color:var(--danger);">*</span></label>
                                    <input type="text" id="optionD" name="optionD" class="form-control"
                                        value="${question.optionD}" required>
                                </div>
                            </div>

                            <div style="border-top:1px solid var(--border-color);padding-top:18px;margin-top:8px;display:grid;grid-template-columns:1fr 1fr;gap:16px;">
                                <div class="form-group">
                                    <label for="correctOption">Correct Option <span style="color:var(--danger);">*</span></label>
                                    <select id="correctOption" name="correctOption" class="form-control" required>
                                        <option value="A" ${question.correctOption=='A' ? 'selected' : ''}>Option A</option>
                                        <option value="B" ${question.correctOption=='B' ? 'selected' : ''}>Option B</option>
                                        <option value="C" ${question.correctOption=='C' ? 'selected' : ''}>Option C</option>
                                        <option value="D" ${question.correctOption=='D' ? 'selected' : ''}>Option D</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="difficultyLevel">Difficulty Level <span style="color:var(--danger);">*</span></label>
                                    <select id="difficultyLevel" name="difficultyLevel" class="form-control" required>
                                        <option value="1" ${question.difficultyLevel==1 ? 'selected' : ''}>Easy (Level 1)</option>
                                        <option value="2" ${question.difficultyLevel==2 ? 'selected' : ''}>Medium (Level 2)</option>
                                        <option value="3" ${question.difficultyLevel==3 ? 'selected' : ''}>Hard (Level 3)</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="category">Category</label>
                                <input type="text" id="category" name="category" class="form-control"
                                    value="${question.category}">
                            </div>

                            <div style="display:flex;gap:10px;padding-top:16px;border-top:1px solid var(--border-color);">
                                <button type="submit" class="btn btn-primary" id="updateQuestionBtn" style="flex:1;padding:12px;">
                                    <span class="material-icons-outlined" style="font-size:18px;">save</span> Update Question
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/questions" class="btn btn-secondary" style="padding:12px 24px;">
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>
                </main>
            </div>
        </body>

        </html>
