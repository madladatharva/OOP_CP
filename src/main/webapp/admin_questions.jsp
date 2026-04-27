<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Manage Questions - AdaptIQ Admin</title>
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
                    <div style="display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:24px;flex-wrap:wrap;gap:16px;">
                        <div class="page-header" style="padding:0;">
                            <h1>Manage Questions</h1>
                            <p>View, edit, and delete quiz questions</p>
                        </div>
                        <div style="display:flex;align-items:center;gap:16px;">
                            <span style="color:var(--text-secondary);font-size:0.9rem;">
                                Total: <strong style="color:var(--text-primary);">${questions.size()}</strong> questions
                            </span>
                            <a href="${pageContext.request.contextPath}/admin/questions/add" class="btn btn-success" id="addNewBtn">
                                <span class="material-icons-outlined" style="font-size:18px;">add</span> Add Question
                            </a>
                        </div>
                    </div>

                    <!-- Questions Table -->
                    <div class="card" style="padding:0;overflow:hidden;">
                        <div style="overflow-x:auto;">
                            <table>
                                <thead>
                                    <tr>
                                        <th style="text-align:center;width:60px;">ID</th>
                                        <th>Question</th>
                                        <th style="text-align:center;width:80px;">Correct</th>
                                        <th style="text-align:center;width:100px;">Difficulty</th>
                                        <th style="width:120px;">Category</th>
                                        <th style="text-align:center;width:140px;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="q" items="${questions}">
                                        <tr>
                                            <td style="text-align:center;color:var(--text-muted);font-weight:500;">${q.id}</td>
                                            <td>
                                                <p style="max-width:320px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${q.questionText}">
                                                    ${q.questionText}
                                                </p>
                                            </td>
                                            <td style="text-align:center;">
                                                <span style="display:inline-flex;align-items:center;justify-content:center;width:28px;height:28px;border-radius:50%;background:var(--success-bg);color:var(--success);font-weight:700;font-size:0.8rem;">
                                                    ${q.correctOption}
                                                </span>
                                            </td>
                                            <td style="text-align:center;">
                                                <c:choose>
                                                    <c:when test="${q.difficultyLevel == 1}">
                                                        <span class="badge badge-correct">Easy</span>
                                                    </c:when>
                                                    <c:when test="${q.difficultyLevel == 2}">
                                                        <span class="badge" style="background:var(--warning-bg);color:var(--warning);">Medium</span>
                                                    </c:when>
                                                    <c:when test="${q.difficultyLevel == 3}">
                                                        <span class="badge badge-wrong">Hard</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="badge badge-active">${q.category}</span>
                                            </td>
                                            <td style="text-align:center;">
                                                <div style="display:flex;justify-content:center;gap:6px;">
                                                    <a href="${pageContext.request.contextPath}/admin/questions/edit?id=${q.id}"
                                                        class="btn btn-secondary btn-sm">Edit</a>
                                                    <a href="${pageContext.request.contextPath}/admin/questions/delete?id=${q.id}"
                                                        class="btn btn-danger btn-sm"
                                                        onclick="return confirm('Are you sure you want to delete this question?')">Delete</a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty questions}">
                                        <tr>
                                            <td colspan="6" style="text-align:center;padding:40px;color:var(--text-muted);">
                                                No questions found. Start by adding one!
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </main>
            </div>
        </body>

        </html>
