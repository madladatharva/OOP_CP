<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Performance Analytics</h1>
            <p>Track class performance, identify weak topics, and inspect question-level accuracy from completed adaptive sessions.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div style="display:grid;grid-template-columns:1.1fr 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">leaderboard</span>
                    Class-wise Performance
                </div>
                <c:choose>
                    <c:when test="${empty analyticsData['classPerformance']}">
                        <div class="empty-state">
                            <strong>No class analytics yet.</strong>
                            <p>Class-level averages will appear after students complete assigned quizzes.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Class</th>
                                    <th>Code</th>
                                    <th>Students</th>
                                    <th>Assigned</th>
                                    <th>Average Score</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${analyticsData['classPerformance']}" var="row">
                                    <tr>
                                        <td>${row['class_name']}</td>
                                        <td>${row['class_code']}</td>
                                        <td>${row['students']}</td>
                                        <td>${row['assigned_quizzes']}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${row['average_score'] != null}">
                                                    <fmt:formatNumber value="${row['average_score']}" maxFractionDigits="1" />%
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
                    <span class="material-icons-outlined icon">report</span>
                    Topic-wise Weak Areas
                </div>
                <c:choose>
                    <c:when test="${empty analyticsData['weakTopics']}">
                        <div class="empty-state">
                            <strong>No weak-topic trends yet.</strong>
                            <p>Once attempts exist, this panel highlights the topics students struggle with most.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="display:flex;flex-direction:column;gap:12px;">
                            <c:forEach items="${analyticsData['weakTopics']}" var="row">
                                <div style="padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--bg-input);">
                                    <div style="display:flex;justify-content:space-between;gap:12px;">
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
                <span class="material-icons-outlined icon">fact_check</span>
                Question-wise Accuracy
            </div>
            <c:choose>
                <c:when test="${empty analyticsData['questionAccuracy']}">
                    <div class="empty-state">
                        <strong>No question-level analytics yet.</strong>
                        <p>Question-level accuracy will show which prompts are easy, hard, or potentially unclear.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Question</th>
                                <th>Subject</th>
                                <th>Topic</th>
                                <th>Attempts</th>
                                <th>Accuracy</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${analyticsData['questionAccuracy']}" var="row">
                                <tr>
                                    <td>${row['question_text']}</td>
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
    </main>
</div>
</body>
</html>
