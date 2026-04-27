<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/student_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Student Dashboard</h1>
            <p>Track assigned adaptive quizzes, join new classes, and review your subject and topic performance over time.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">pending_actions</span></div>
                <div class="stat-number">${fn:length(dashboardData['assignments']['assigned'])}</div>
                <div class="stat-label">Assigned</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">task_alt</span></div>
                <div class="stat-number">${fn:length(dashboardData['assignments']['completed'])}</div>
                <div class="stat-label">Completed</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">event_busy</span></div>
                <div class="stat-number">${fn:length(dashboardData['assignments']['missed'])}</div>
                <div class="stat-label">Missed</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">groups</span></div>
                <div class="stat-number">${fn:length(dashboardData['classes'])}</div>
                <div class="stat-label">Joined Classes</div>
            </div>
        </div>

        <div style="display:grid;grid-template-columns:minmax(300px,360px) 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">group_add</span>
                    Join a Class
                </div>
                <form method="post" action="${pageContext.request.contextPath}/student/dashboard">
                    <div class="form-group">
                        <label for="classCode">Class Code</label>
                        <input id="classCode" name="classCode" class="form-control" placeholder="Enter the code shared by your teacher" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Join Class</button>
                </form>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">school</span>
                    Current Classes
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['classes']}">
                        <div class="empty-state">
                            <strong>You have not joined any classes yet.</strong>
                            <p>Use a teacher's class code to unlock assignments on your dashboard.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="classroom-grid">
                            <c:forEach items="${dashboardData['classes']}" var="classroom">
                                <div class="classroom-tile">
                                    <div style="display:flex;justify-content:space-between;gap:12px;align-items:flex-start;">
                                        <div>
                                            <div style="font-weight:700;color:var(--text-primary);">${classroom.name}</div>
                                            <div style="font-size:0.82rem;color:var(--text-secondary);">Class code: ${classroom.classCode}</div>
                                        </div>
                                        <span class="badge badge-active">${classroom.studentCount} students</span>
                                    </div>
                                    <p style="margin-top:10px;color:var(--text-secondary);font-size:0.88rem;">
                                        <c:out value="${empty classroom.description ? 'Adaptive quizzes assigned to this class will appear below.' : classroom.description}" />
                                    </p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <section class="card" id="assigned">
            <div class="card-header">
                <span class="material-icons-outlined icon">assignment_turned_in</span>
                Assigned Quizzes
            </div>
            <c:choose>
                <c:when test="${empty dashboardData['assignments']['assigned']}">
                    <div class="empty-state">
                        <strong>No active assignments right now.</strong>
                        <p>As soon as a teacher assigns a quiz to one of your classes, it will appear here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Quiz</th>
                                <th>Class</th>
                                <th>Questions</th>
                                <th>Deadline</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${dashboardData['assignments']['assigned']}" var="assignment">
                                <tr>
                                    <td>
                                        <div style="font-weight:600;">${assignment.quizTitle}</div>
                                        <div style="font-size:0.8rem;color:var(--text-secondary);">${assignment.subjectName}</div>
                                    </td>
                                    <td>${assignment.classroomName}</td>
                                    <td>${assignment.questionCount}</td>
                                    <td><fmt:formatDate value="${assignment.deadlineAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                    <td>
                                        <span class="badge badge-active">
                                            ${assignment.sessionStatus == 'IN_PROGRESS' ? 'Resume' : 'Ready'}
                                        </span>
                                    </td>
                                    <td>
                                        <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/student/quiz?assignmentId=${assignment.id}">
                                            ${assignment.sessionStatus == 'IN_PROGRESS' ? 'Resume Quiz' : 'Start Quiz'}
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">history</span>
                    Completed Quizzes
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['assignments']['completed']}">
                        <div class="empty-state">
                            <strong>No completed quizzes yet.</strong>
                            <p>Your finished quiz attempts and scores will appear here.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Quiz</th>
                                    <th>Score</th>
                                    <th>Result</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dashboardData['assignments']['completed']}" var="assignment">
                                    <tr>
                                        <td>
                                            <div style="font-weight:600;">${assignment.quizTitle}</div>
                                            <div style="font-size:0.8rem;color:var(--text-secondary);">${assignment.subjectName}</div>
                                        </td>
                                        <td><fmt:formatNumber value="${assignment.scorePercentage}" maxFractionDigits="1" />%</td>
                                        <td><a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/student/results?sessionId=${assignment.sessionId}">View Result</a></td>
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
                    <span class="material-icons-outlined icon">event_busy</span>
                    Missed Quizzes
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['assignments']['missed']}">
                        <div class="empty-state">
                            <strong>No missed quizzes.</strong>
                            <p>Keep that streak going.</p>
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
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dashboardData['assignments']['missed']}" var="assignment">
                                    <tr>
                                        <td>
                                            <div style="font-weight:600;">${assignment.quizTitle}</div>
                                            <div style="font-size:0.8rem;color:var(--text-secondary);">${assignment.subjectName}</div>
                                        </td>
                                        <td>${assignment.classroomName}</td>
                                        <td><fmt:formatDate value="${assignment.deadlineAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;align-items:start;" id="progress">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">bar_chart</span>
                    Subject Performance
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['subjectPerformance']}">
                        <div class="empty-state">
                            <strong>No subject performance yet.</strong>
                            <p>Finish a few quizzes and your accuracy by subject will appear here.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Subject</th>
                                    <th>Attempts</th>
                                    <th>Accuracy</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dashboardData['subjectPerformance']}" var="row">
                                    <tr>
                                        <td>${row['subject_name']}</td>
                                        <td>${row['attempts']}</td>
                                        <td><fmt:formatNumber value="${row['accuracy']}" maxFractionDigits="1" />%</td>
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
                    <span class="material-icons-outlined icon">analytics</span>
                    Topic Performance
                </div>
                <c:choose>
                    <c:when test="${empty dashboardData['topicPerformance']}">
                        <div class="empty-state">
                            <strong>No topic performance yet.</strong>
                            <p>This panel will surface stronger and weaker topic areas across your quiz history.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Subject</th>
                                    <th>Topic</th>
                                    <th>Attempts</th>
                                    <th>Accuracy</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${dashboardData['topicPerformance']}" var="row">
                                    <tr>
                                        <td>${row['subject_name']}</td>
                                        <td>${row['topic_name']}</td>
                                        <td>${row['attempts']}</td>
                                        <td><fmt:formatNumber value="${row['accuracy']}" maxFractionDigits="1" />%</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <section class="card">
            <div class="card-header">
                <span class="material-icons-outlined icon">query_stats</span>
                Score History
            </div>
            <c:choose>
                <c:when test="${empty dashboardData['recentResults']}">
                    <div class="empty-state">
                        <strong>No score history yet.</strong>
                        <p>Your recent sessions, accuracy, and completion status will appear here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Quiz</th>
                                <th>Status</th>
                                <th>Answered</th>
                                <th>Accuracy</th>
                                <th>Started</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${dashboardData['recentResults']}" var="row">
                                <tr>
                                    <td>
                                        <div style="font-weight:600;">${row['quiz_title']}</div>
                                        <div style="font-size:0.8rem;color:var(--text-secondary);">${row['subject_name']}</div>
                                    </td>
                                    <td>${row['status']}</td>
                                    <td>${row['answered_count']} / ${row['total_questions']}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${row['accuracy'] != null}">
                                                <fmt:formatNumber value="${row['accuracy']}" maxFractionDigits="1" />%
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatDate value="${row['started_at']}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>
