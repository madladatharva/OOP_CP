<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Dashboard - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Teacher Dashboard</h1>
            <p>Manage classes, assignments, adaptive quizzes, and topic-level performance from one place.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">groups</span></div>
                <div class="stat-number">${dashboardData['classroomCount']}</div>
                <div class="stat-label">Classes</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">quiz</span></div>
                <div class="stat-number">${dashboardData['questionCount']}</div>
                <div class="stat-label">Tagged Questions</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">assignment</span></div>
                <div class="stat-number">${dashboardData['quizCount']}</div>
                <div class="stat-label">Quiz Blueprints</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">schedule</span></div>
                <div class="stat-number">${dashboardData['dueSoonCount']}</div>
                <div class="stat-label">Due Soon</div>
            </div>
        </div>

        <div style="display:grid;grid-template-columns:1.4fr 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">calendar_month</span>
                    Recent Assignments
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['assignments']}">
                        <div class="empty-state">
                            <strong>No assignments yet.</strong>
                            <p>Create a quiz blueprint and assign it to one of your classes to start collecting adaptive assessment data.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Quiz</th>
                                    <th>Class</th>
                                    <th>Deadline</th>
                                    <th>Avg Score</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dashboardData['assignments']}" var="assignment">
                                    <tr>
                                        <td>
                                            <div style="font-weight:600;">${assignment.quizTitle}</div>
                                            <div style="color:var(--text-muted);font-size:0.8rem;">${assignment.subjectName}</div>
                                        </td>
                                        <td>${assignment.classroomName}</td>
                                        <td><fmt:formatDate value="${assignment.deadlineAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${assignment.scorePercentage != null}">
                                                    <fmt:formatNumber value="${assignment.scorePercentage}" maxFractionDigits="1" />%
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">warning_amber</span>
                    Weak Topics
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['weakTopics']}">
                        <div class="empty-state">
                            <strong>No weak-topic data yet.</strong>
                            <p>Weak areas appear here once students start submitting quizzes.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="display:flex;flex-direction:column;gap:12px;">
                            <c:forEach items="${dashboardData['weakTopics']}" var="row">
                                <div style="padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--bg-input);">
                                    <div style="display:flex;justify-content:space-between;gap:12px;align-items:center;">
                                        <div>
                                            <div style="font-weight:600;">${row['topic_name']}</div>
                                            <div style="font-size:0.82rem;color:var(--text-secondary);">${row['subject_name']}</div>
                                        </div>
                                        <div style="text-align:right;">
                                            <div style="font-weight:700;color:var(--danger);">
                                                <fmt:formatNumber value="${row['accuracy']}" maxFractionDigits="1" />%
                                            </div>
                                            <div style="font-size:0.78rem;color:var(--text-muted);">${row['attempts']} attempts</div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <section class="card">
            <div class="card-header">
                <span class="material-icons-outlined icon">school</span>
                Classes at a Glance
            </div>
            <c:choose>
                <c:when test="${empty dashboardData['classrooms']}">
                    <div class="empty-state">
                        <strong>No classes created yet.</strong>
                        <p>Start with a class like SE Sem 3 or DBMS Batch A, then invite students by code or manual roster entry.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="classroom-grid">
                        <c:forEach items="${dashboardData['classrooms']}" var="classroom">
                            <a class="classroom-tile" href="${pageContext.request.contextPath}/teacher/classes?classId=${classroom.id}">
                                <div style="display:flex;justify-content:space-between;gap:12px;align-items:flex-start;">
                                    <div>
                                        <div style="font-weight:700;color:var(--text-primary);">${classroom.name}</div>
                                        <div style="font-size:0.82rem;color:var(--text-secondary);">${classroom.classCode}</div>
                                    </div>
                                    <span class="badge badge-active">${classroom.studentCount} students</span>
                                </div>
                                <p style="margin-top:10px;color:var(--text-secondary);font-size:0.88rem;">
                                    <c:out value="${empty classroom.description ? 'Ready for quiz assignment and roster management.' : classroom.description}" />
                                </p>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>
