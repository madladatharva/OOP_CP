<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Dashboard - AdaptIQ</title>
                <meta name="description" content="Your adaptive quiz dashboard. Start a new quiz or view your quiz history.">
                <link rel="stylesheet" href="css/style.css">
                <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            </head>

            <body>
                <div class="page-layout">
                    <!-- Sidebar -->
                    <aside class="sidebar">
                        <div class="sidebar-brand">
                            <div class="brand-logo">A</div>
                            <div class="brand-text">
                                <span class="brand-name">AdaptIQ</span>
                                <span class="brand-subtitle">Smart Assessment</span>
                            </div>
                        </div>

                        <nav class="sidebar-nav">
                            <a href="dashboard" class="active">
                                <span class="material-icons-outlined">dashboard</span>
                                Dashboard
                            </a>
                            <a href="quiz?action=start">
                                <span class="material-icons-outlined">play_circle</span>
                                Start Quiz
                            </a>
                            <c:if test="${sessionScope.role == 'ADMIN'}">
                                <div class="nav-section-title">Administration</div>
                                <a href="admin">
                                    <span class="material-icons-outlined">admin_panel_settings</span>
                                    Admin Panel
                                </a>
                            </c:if>
                        </nav>

                        <div class="sidebar-user">
                            <div class="user-avatar">${sessionScope.username.substring(0,1).toUpperCase()}</div>
                            <div class="user-info">
                                <span class="user-name">${sessionScope.username}</span>
                                <span class="user-role">${sessionScope.role == 'ADMIN' ? 'Administrator' : 'Student'}</span>
                            </div>
                            <a href="logout" style="margin-left:auto;color:var(--text-muted);" title="Logout">
                                <span class="material-icons-outlined" style="font-size:20px;">logout</span>
                            </a>
                        </div>
                    </aside>

                    <!-- Main Content -->
                    <main class="main-content animate-fade-in">
                        <div class="page-header">
                            <h1>Welcome back, ${sessionScope.username}! 👋</h1>
                            <p>Your learning journey continues. Start a quiz or review your history below.</p>
                        </div>

                        <!-- Error Message -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-error">${error}</div>
                        </c:if>

                        <!-- Active Session Warning -->
                        <c:if test="${not empty activeSession}">
                            <div class="alert alert-warning">
                                <span class="material-icons-outlined" style="font-size:18px;">warning</span>
                                You have an active quiz session in
                                <strong>${empty activeSession.selectedCategory ? 'All Topics' : activeSession.selectedCategory}</strong>
                                (Question ${activeSession.currentQuestionNumber}/${activeSession.totalQuestions}).
                                Starting a new quiz will abandon it.
                            </div>
                        </c:if>

                        <!-- Stats Cards -->
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-icon"><span class="material-icons-outlined">quiz</span></div>
                                <div class="stat-number">${history.size() > 0 ? history.size() : "0"}</div>
                                <div class="stat-label">Quizzes Taken</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-icon"><span class="material-icons-outlined">percent</span></div>
                                <div class="stat-number">
                                    <c:set var="totalScore" value="0" />
                                    <c:set var="totalQs" value="0" />
                                    <c:forEach var="s" items="${history}">
                                        <c:set var="totalScore" value="${totalScore + s.score}" />
                                        <c:set var="totalQs" value="${totalQs + s.currentQuestionNumber}" />
                                    </c:forEach>
                                    <c:if test="${totalQs > 0}">
                                        <fmt:formatNumber value="${totalScore * 100.0 / totalQs}" maxFractionDigits="0" />%
                                    </c:if>
                                    <c:if test="${totalQs == 0}">0%</c:if>
                                </div>
                                <div class="stat-label">Avg. Accuracy</div>
                            </div>
                        </div>

                        <!-- Start Quiz Section -->
                        <section class="card">
                            <div class="card-header">
                                <span class="material-icons-outlined icon">play_circle</span>
                                Start a New Quiz
                            </div>
                            <p style="color: var(--text-secondary); margin-bottom: 20px; font-size: 0.9rem;">
                                The quiz adapts to your performance. Answer correctly and difficulty increases. Struggle a bit, and it eases up.
                            </p>

                            <form action="quiz" method="get" class="d-flex items-center flex-wrap gap-2">
                                <input type="hidden" name="action" value="start">
                                <div class="form-group" style="flex: 1; min-width: 220px; margin-bottom: 0;">
                                    <label for="quizCategory">Topic</label>
                                    <select name="category" id="quizCategory" class="form-control">
                                        <option value="__ALL__" selected>All Topics</option>
                                        <c:forEach var="category" items="${categories}">
                                            <option value="${category}">${category}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group" style="flex: 1; min-width: 220px; margin-bottom: 0;">
                                    <label for="totalQuestions">Number of Questions</label>
                                    <select name="total" id="totalQuestions" class="form-control">
                                        <option value="5">5 Questions (Quick)</option>
                                        <option value="10" selected>10 Questions (Standard)</option>
                                        <option value="15">15 Questions (Extended)</option>
                                        <option value="20">20 Questions (Full)</option>
                                    </select>
                                </div>
                                <div style="margin-top: 22px;">
                                    <button type="submit" class="btn btn-primary" id="startQuizBtn">
                                        <span class="material-icons-outlined" style="font-size:18px;">rocket_launch</span>
                                        Start Quiz
                                    </button>
                                </div>
                            </form>

                            <p style="color: var(--text-muted); margin-top: 12px; font-size: 0.8rem;">
                                PDF-generated quizzes appear here as their own topic after an admin uploads the source theory.
                            </p>
                        </section>

                        <!-- Quiz History -->
                        <section class="card">
                            <div class="card-header">
                                <span class="material-icons-outlined icon">history</span>
                                Your Quiz History
                            </div>

                            <c:choose>
                                <c:when test="${not empty history}">
                                    <div class="table-wrapper">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>Date</th>
                                                    <th>Topic</th>
                                                    <th>Score</th>
                                                    <th>Accuracy</th>
                                                    <th>Status</th>
                                                    <th>Action</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="session" items="${history}" varStatus="loop">
                                                    <tr>
                                                        <td>${loop.count}</td>
                                                        <td style="color: var(--primary);">${session.startedAt}</td>
                                                        <td>
                                                            <span class="badge badge-active">
                                                                ${empty session.selectedCategory ? 'All Topics' : session.selectedCategory}
                                                            </span>
                                                        </td>
                                                        <td><strong>${session.score} / ${session.currentQuestionNumber}</strong></td>
                                                        <td>
                                                            <c:set var="accuracy" value="${session.currentQuestionNumber > 0 ? (session.score * 100.0 / session.currentQuestionNumber) : 0}" />
                                                            <span class="badge ${accuracy >= 70 ? 'badge-correct' : accuracy >= 40 ? 'badge-active' : 'badge-wrong'}">
                                                                <fmt:formatNumber value="${accuracy}" maxFractionDigits="0" />%
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <span class="badge ${session.status == 'COMPLETED' ? 'badge-completed' : 'badge-active'}">
                                                                ${session.status}
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <c:if test="${session.status == 'COMPLETED'}">
                                                                <a href="result?sessionId=${session.id}" class="btn btn-secondary btn-sm">
                                                                    View
                                                                </a>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div style="text-align: center; padding: 40px 20px; border: 1px dashed var(--border-color); border-radius: var(--radius); background: var(--bg-input);">
                                        <span class="material-icons-outlined" style="font-size:40px;color:var(--text-muted);margin-bottom:8px;display:block;">inbox</span>
                                        <p style="color: var(--text-secondary); font-size: 0.95rem; margin-bottom: 4px;">No quiz history yet.</p>
                                        <p style="color: var(--text-muted); font-size: 0.85rem;">Start your first quiz above!</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </section>
                    </main>
                </div>
            </body>

            </html>
