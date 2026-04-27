<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Admin Dashboard - AdaptIQ</title>
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
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">
                            <span class="material-icons-outlined">dashboard</span>
                            Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions">
                            <span class="material-icons-outlined">quiz</span>
                            Questions
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/materials">
                            <span class="material-icons-outlined">upload_file</span>
                            PDF Quiz Generator
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions/add">
                            <span class="material-icons-outlined">add_circle</span>
                            Add Question
                        </a>
                        <div class="nav-section-title">Navigation</div>
                        <a href="${pageContext.request.contextPath}/dashboard">
                            <span class="material-icons-outlined">home</span>
                            Student Dashboard
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

                <!-- Main Content -->
                <main class="main-content animate-fade-in">
                    <div class="page-header">
                        <h1>Admin Dashboard</h1>
                        <p>Manage questions and monitor the assessment platform</p>
                    </div>

                    <!-- Error -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">${error}</div>
                    </c:if>

                    <!-- Stats -->
                    <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:16px;margin-bottom:28px;">
                        <div class="stat-card">
                            <div class="stat-icon"><span class="material-icons-outlined">description</span></div>
                            <div class="stat-number">${totalQuestions}</div>
                            <div class="stat-label">Total Questions</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon" style="color:var(--success);"><span class="material-icons-outlined">speed</span></div>
                            <div class="stat-number" style="color:var(--success);">${easyCount}</div>
                            <div class="stat-label">Easy</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon" style="color:var(--warning);"><span class="material-icons-outlined">speed</span></div>
                            <div class="stat-number" style="color:var(--warning);">${mediumCount}</div>
                            <div class="stat-label">Medium</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon" style="color:var(--danger);"><span class="material-icons-outlined">speed</span></div>
                            <div class="stat-number" style="color:var(--danger);">${hardCount}</div>
                            <div class="stat-label">Hard</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon" style="color:var(--primary);"><span class="material-icons-outlined">library_books</span></div>
                            <div class="stat-number" style="color:var(--primary);">${materialCount}</div>
                            <div class="stat-label">PDF Sources</div>
                        </div>
                    </div>

                    <div style="display:grid;grid-template-columns:repeat(2,1fr);gap:20px;">
                        <!-- Quick Actions -->
                        <div class="card">
                            <div class="card-header">
                                <span class="material-icons-outlined icon">bolt</span>
                                Quick Actions
                            </div>
                            <div style="display:flex;flex-direction:column;gap:10px;">
                                <a href="${pageContext.request.contextPath}/admin/questions/add"
                                    class="btn btn-success btn-block" id="addQuestionBtn" style="padding:14px;">
                                    <span class="material-icons-outlined" style="font-size:18px;">add</span>
                                    Add New Question
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/questions"
                                    class="btn btn-primary btn-block" id="manageQuestionsBtn" style="padding:14px;">
                                    <span class="material-icons-outlined" style="font-size:18px;">list</span>
                                    Manage Questions
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/materials"
                                    class="btn btn-secondary btn-block" id="uploadPdfBtn" style="padding:14px;">
                                    <span class="material-icons-outlined" style="font-size:18px;">upload_file</span>
                                    Upload Theory PDF
                                </a>
                            </div>
                        </div>

                        <!-- Info Card -->
                        <div class="card">
                            <div class="card-header">
                                <span class="material-icons-outlined icon">info</span>
                                Adaptive System Info
                            </div>
                            <div style="color:var(--text-secondary);font-size:0.9rem;line-height:1.8;">
                                <p style="font-weight:600;color:var(--text-primary);margin-bottom:8px;">How Adaptive Difficulty Works:</p>
                                <ul style="padding-left:20px;">
                                    <li>Quiz starts at <strong style="color:var(--success);">Medium (Level 2)</strong></li>
                                    <li><strong style="color:var(--success);">3 consecutive correct</strong> → Difficulty increases</li>
                                    <li><strong style="color:var(--danger);">2 consecutive wrong</strong> → Difficulty decreases</li>
                                    <li>Each quiz: 5-20 customizable questions</li>
                                    <li>Questions randomly selected to avoid repetition</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </body>

        </html>
