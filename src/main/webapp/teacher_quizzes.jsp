<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizzes - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Quiz Management</h1>
            <p>Create adaptive quiz blueprints by subject and topic, then assign them to classes with a deadline.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div style="display:grid;grid-template-columns:1.2fr minmax(320px,420px);gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">add_task</span>
                    Create Quiz Blueprint
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/quizzes">
                    <input type="hidden" name="action" value="create">
                    <div style="display:grid;grid-template-columns:1fr 150px 150px;gap:14px;">
                        <div class="form-group">
                            <label for="quizTitle">Quiz Title</label>
                            <input id="quizTitle" name="title" class="form-control" placeholder="DBMS Normalization Drill" required>
                        </div>
                        <div class="form-group">
                            <label for="questionCount">Questions</label>
                            <input id="questionCount" type="number" min="3" max="30" name="questionCount" class="form-control" value="8" required>
                        </div>
                        <div class="form-group">
                            <label for="timeLimitMinutes">Time Limit</label>
                            <input id="timeLimitMinutes" type="number" min="5" max="120" name="timeLimitMinutes" class="form-control" value="20" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="quizSubjectId">Subject</label>
                        <select id="quizSubjectId" name="subjectId" class="form-control" required>
                            <option value="">Select subject</option>
                            <c:forEach items="${subjects}" var="subject">
                                <option value="${subject.id}">${subject.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="quizDescription">Description</label>
                        <textarea id="quizDescription" name="description" class="form-control" placeholder="Optional teacher note for the adaptive quiz blueprint."></textarea>
                    </div>
                    <div class="form-group">
                        <label>Topics</label>
                        <div id="quizTopicGrid" class="topic-checkbox-grid">
                            <c:forEach items="${topics}" var="topic">
                                <label class="topic-checkbox" data-subject-id="${topic.subjectId}">
                                    <input type="checkbox" name="topicIds" value="${topic.id}">
                                    <span>${topic.name}</span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">Create Quiz</button>
                </form>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">send</span>
                    Assign to Class
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/quizzes">
                    <input type="hidden" name="action" value="assign">
                    <div class="form-group">
                        <label for="quizId">Quiz Blueprint</label>
                        <select id="quizId" name="quizId" class="form-control" required>
                            <option value="">Select quiz</option>
                            <c:forEach items="${quizzes}" var="quiz">
                                <option value="${quiz.id}">${quiz.title} (${quiz.subjectName})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="classroomId">Class</label>
                        <select id="classroomId" name="classroomId" class="form-control" required>
                            <option value="">Select class</option>
                            <c:forEach items="${classrooms}" var="classroom">
                                <option value="${classroom.id}">${classroom.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="deadlineAt">Deadline</label>
                        <input id="deadlineAt" type="datetime-local" name="deadlineAt" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-secondary btn-block">Assign Quiz</button>
                </form>
                <div style="margin-top:18px;padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--bg-input);font-size:0.84rem;color:var(--text-secondary);">
                    Adaptive rule: correct answers push students upward in difficulty, incorrect answers lower difficulty, and topic quotas keep the quiz balanced across the selected syllabus areas.
                </div>
            </section>
        </div>

        <section class="card">
            <div class="card-header">
                <span class="material-icons-outlined icon">folder</span>
                Quiz Blueprints
            </div>
            <c:choose>
                <c:when test="${empty quizzes}">
                    <div class="empty-state">
                        <strong>No quiz blueprints yet.</strong>
                        <p>Create one using tagged subject topics so assignments can pull balanced adaptive questions.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Title</th>
                                <th>Subject</th>
                                <th>Topics</th>
                                <th>Questions</th>
                                <th>Time</th>
                                <th>Assigned</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${quizzes}" var="quiz">
                                <tr>
                                    <td>
                                        <div style="font-weight:600;">${quiz.title}</div>
                                        <div style="font-size:0.8rem;color:var(--text-secondary);">${quiz.description}</div>
                                    </td>
                                    <td>${quiz.subjectName}</td>
                                    <td>${quiz.topicSummary}</td>
                                    <td>${quiz.questionCount}</td>
                                    <td>${quiz.timeLimitMinutes} min</td>
                                    <td>${quiz.assignmentCount}</td>
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
                <span class="material-icons-outlined icon">event_note</span>
                Scheduled Assignments
            </div>
            <c:choose>
                <c:when test="${empty assignments}">
                    <div class="empty-state">
                        <strong>No class assignments yet.</strong>
                        <p>Assignments appear here after you pair a quiz blueprint with a classroom and deadline.</p>
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
                                <th>Responses</th>
                                <th>Average Score</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${assignments}" var="assignment">
                                <tr>
                                    <td>
                                        <div style="font-weight:600;">${assignment.quizTitle}</div>
                                        <div style="font-size:0.8rem;color:var(--text-secondary);">${assignment.subjectName}</div>
                                    </td>
                                    <td>${assignment.classroomName}</td>
                                    <td><fmt:formatDate value="${assignment.deadlineAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                    <td>${assignment.answeredCount}</td>
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
    </main>
</div>

<script>
    (function () {
        const subjectSelect = document.getElementById('quizSubjectId');
        const topicLabels = Array.from(document.querySelectorAll('#quizTopicGrid .topic-checkbox'));

        function filterTopics() {
            const subjectId = subjectSelect.value;
            topicLabels.forEach(function (label) {
                const checkbox = label.querySelector('input');
                const matches = !subjectId || label.dataset.subjectId === subjectId;
                label.style.display = matches ? 'flex' : 'none';
                checkbox.disabled = !matches;
                if (!matches) {
                    checkbox.checked = false;
                }
            });
        }

        subjectSelect.addEventListener('change', filterTopics);
        filterTopics();
    }());
</script>
</body>
</html>
