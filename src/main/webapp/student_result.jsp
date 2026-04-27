<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${session.quizTitle} Result - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/student_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Quiz Result</h1>
            <p>${session.quizTitle} · ${session.subjectName}</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">emoji_events</span></div>
                <div class="stat-number">${session.score} / ${session.totalQuestions}</div>
                <div class="stat-label">Correct Answers</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">percent</span></div>
                <div class="stat-number"><fmt:formatNumber value="${(session.score * 100.0) / session.totalQuestions}" maxFractionDigits="1" />%</div>
                <div class="stat-label">Score</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">timer</span></div>
                <div class="stat-number">${session.timeLimitMinutes} min</div>
                <div class="stat-label">Time Budget</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">flag</span></div>
                <div class="stat-number">${session.status}</div>
                <div class="stat-label">Submission</div>
            </div>
        </div>

        <div style="display:grid;grid-template-columns:minmax(300px,420px) 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">insights</span>
                    Topic-wise Performance
                </div>
                <c:choose>
                    <c:when test="${empty topicPerformance}">
                        <div class="empty-state">
                            <strong>No topic details available.</strong>
                            <p>Topic-wise performance appears once the quiz records answer data.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div style="display:flex;flex-direction:column;gap:12px;">
                            <c:forEach items="${topicPerformance}" var="row">
                                <div style="padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--bg-input);">
                                    <div style="display:flex;justify-content:space-between;gap:12px;">
                                        <div>
                                            <div style="font-weight:600;">${row['topic_name']}</div>
                                            <div style="font-size:0.82rem;color:var(--text-secondary);">${row['subject_name']}</div>
                                        </div>
                                        <div style="text-align:right;">
                                            <div style="font-weight:700;"><fmt:formatNumber value="${row['accuracy']}" maxFractionDigits="1" />%</div>
                                            <div style="font-size:0.78rem;color:var(--text-muted);">${row['correct_answers']} / ${row['attempts']} correct</div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">checklist</span>
                    Answer Review
                </div>
                <div style="display:flex;flex-direction:column;gap:14px;">
                    <c:forEach items="${questions}" var="question">
                        <div class="review-card">
                            <div style="display:flex;justify-content:space-between;gap:12px;align-items:flex-start;flex-wrap:wrap;">
                                <div>
                                    <div style="font-size:0.8rem;color:var(--text-secondary);margin-bottom:6px;">Question ${question.questionOrder} · ${question.topicName}</div>
                                    <div style="font-weight:600;line-height:1.6;">${question.questionText}</div>
                                </div>
                                <span class="badge ${question.correct ? 'badge-completed' : 'badge-wrong'}">
                                    ${question.correct ? 'Correct' : 'Incorrect'}
                                </span>
                            </div>
                            <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px;margin-top:14px;">
                                <div>
                                    <div style="font-size:0.76rem;color:var(--text-muted);text-transform:uppercase;">Difficulty</div>
                                    <div style="font-weight:600;">${question.servedDifficulty == 1 ? 'Easy' : question.servedDifficulty == 2 ? 'Medium' : 'Hard'}</div>
                                </div>
                                <div>
                                    <div style="font-size:0.76rem;color:var(--text-muted);text-transform:uppercase;">Your Answer</div>
                                    <div style="font-weight:600;">
                                        <c:choose>
                                            <c:when test="${not empty question.selectedOption}">${question.selectedOption}</c:when>
                                            <c:otherwise>Not answered</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div>
                                    <div style="font-size:0.76rem;color:var(--text-muted);text-transform:uppercase;">Correct Answer</div>
                                    <div style="font-weight:600;">${question.correctOption}</div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
