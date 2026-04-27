<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${session.quizTitle} - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/student_sidebar.jspf" %>
    <main class="main-content">
        <div class="quiz-container">
            <div class="quiz-progress">
                <div class="progress-left">
                    <span class="progress-label">${session.subjectName}</span>
                    <span class="progress-value">${session.quizTitle}</span>
                    <div class="progress-bar-wrapper">
                        <div class="progress-bar" style="width:${(question.questionOrder * 100.0) / session.totalQuestions}%;"></div>
                    </div>
                </div>
                <div style="display:flex;gap:16px;align-items:center;flex-wrap:wrap;">
                    <div>
                        <div class="progress-label">Question</div>
                        <div class="progress-value">${question.questionOrder} / ${session.totalQuestions}</div>
                    </div>
                    <div>
                        <div class="progress-label">Time Left</div>
                        <div class="progress-value" id="countdown"></div>
                    </div>
                </div>
            </div>

            <div class="card mb-2" style="display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap;">
                <div>
                    <div style="font-size:0.82rem;color:var(--text-secondary);margin-bottom:4px;">Topic</div>
                    <div style="font-weight:700;">${question.topicName}</div>
                </div>
                <span class="difficulty-badge ${question.servedDifficulty == 1 ? 'difficulty-easy' : question.servedDifficulty == 2 ? 'difficulty-medium' : 'difficulty-hard'}">
                    ${question.servedDifficulty == 1 ? 'Easy' : question.servedDifficulty == 2 ? 'Medium' : 'Hard'}
                </span>
            </div>

            <div class="question-card">
                <div class="question-text">${question.questionText}</div>
                <form method="post" action="${pageContext.request.contextPath}/student/quiz" id="answerForm">
                    <input type="hidden" name="sessionId" value="${session.id}">
                    <input type="hidden" name="sessionQuestionId" value="${question.id}">
                    <div class="options-list">
                        <div class="option-item">
                            <input type="radio" id="optionA" name="selectedOption" value="A" required>
                            <label for="optionA"><span class="option-letter">A</span><span>${question.optionA}</span></label>
                        </div>
                        <div class="option-item">
                            <input type="radio" id="optionB" name="selectedOption" value="B">
                            <label for="optionB"><span class="option-letter">B</span><span>${question.optionB}</span></label>
                        </div>
                        <div class="option-item">
                            <input type="radio" id="optionC" name="selectedOption" value="C">
                            <label for="optionC"><span class="option-letter">C</span><span>${question.optionC}</span></label>
                        </div>
                        <div class="option-item">
                            <input type="radio" id="optionD" name="selectedOption" value="D">
                            <label for="optionD"><span class="option-letter">D</span><span>${question.optionD}</span></label>
                        </div>
                    </div>
                    <div style="display:flex;justify-content:flex-end;margin-top:24px;">
                        <button type="submit" class="btn btn-primary">Submit Answer</button>
                    </div>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/student/quiz" id="timeoutForm" style="display:none;">
                    <input type="hidden" name="action" value="timeout">
                    <input type="hidden" name="sessionId" value="${session.id}">
                </form>
            </div>
        </div>
    </main>
</div>

<script>
    (function () {
        const countdownEl = document.getElementById('countdown');
        const timeoutForm = document.getElementById('timeoutForm');
        const answerForm = document.getElementById('answerForm');
        let remaining = ${remainingSeconds};
        let submitted = false;

        function formatTime(totalSeconds) {
            const minutes = Math.floor(totalSeconds / 60);
            const seconds = totalSeconds % 60;
            return String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
        }

        function tick() {
            countdownEl.textContent = formatTime(Math.max(0, remaining));
            if (remaining <= 0 && !submitted) {
                submitted = true;
                timeoutForm.submit();
                return;
            }
            remaining -= 1;
        }

        answerForm.addEventListener('submit', function () {
            submitted = true;
        });

        tick();
        window.setInterval(tick, 1000);
    }());
</script>
</body>
</html>
