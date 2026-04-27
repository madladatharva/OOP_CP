<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Quiz - Question ${questionNumber} - AdaptIQ</title>
            <meta name="description" content="Answer the quiz question and test your knowledge adaptively.">
            <link rel="stylesheet" href="css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
        </head>

        <body>
            <!-- Navbar -->
            <nav class="navbar">
                <div class="container d-flex justify-between items-center">
                    <a href="dashboard" class="navbar-brand">
                        <span class="brand-icon">A</span> AdaptIQ
                    </a>
                    <ul class="navbar-nav">
                        <li><a href="dashboard"><span class="material-icons-outlined" style="font-size:18px;vertical-align:middle;margin-right:4px;">dashboard</span>Dashboard</a></li>
                        <li><a href="logout" class="btn btn-secondary btn-sm">Logout</a></li>
                    </ul>
                </div>
            </nav>

            <div class="quiz-container animate-fade-in">
                <!-- Progress Header -->
                <div class="quiz-progress">
                    <div class="progress-left">
                        <span class="progress-label">Question</span>
                        <span class="progress-value">${questionNumber} <span style="color: var(--text-muted); font-size: 0.85rem;">/ ${totalQuestions}</span></span>
                    </div>

                    <c:choose>
                        <c:when test="${session.currentDifficulty == 1}">
                            <span class="difficulty-badge difficulty-easy">Easy</span>
                        </c:when>
                        <c:when test="${session.currentDifficulty == 2}">
                            <span class="difficulty-badge difficulty-medium">Medium</span>
                        </c:when>
                        <c:when test="${session.currentDifficulty == 3}">
                            <span class="difficulty-badge difficulty-hard">Hard</span>
                        </c:when>
                    </c:choose>

                    <div class="progress-left" style="text-align: right;">
                        <span class="progress-label">Score</span>
                        <span class="progress-value" style="color: var(--primary);">${session.score}</span>
                    </div>
                </div>

                <div class="progress-bar-wrapper">
                    <c:set var="progressPercent" value="${(questionNumber - 1) * 100 / totalQuestions}" />
                    <div class="progress-bar" style="width: ${progressPercent}%"></div>
                </div>

                <!-- Question Card -->
                <div class="question-card" style="margin-top:20px;">
                    <div style="margin-bottom: 16px;">
                        <span class="badge badge-active">${question.category}</span>
                    </div>

                    <h2 class="question-text">${question.questionText}</h2>

                    <form method="post" action="quiz" id="quizForm">
                        <input type="hidden" name="questionId" value="${question.id}">

                        <div class="options-list">
                            <div class="option-item">
                                <input type="radio" name="selectedOption" value="A" id="optA" required>
                                <label for="optA">
                                    <span class="option-letter">A</span>
                                    ${question.optionA}
                                </label>
                            </div>

                            <div class="option-item">
                                <input type="radio" name="selectedOption" value="B" id="optB">
                                <label for="optB">
                                    <span class="option-letter">B</span>
                                    ${question.optionB}
                                </label>
                            </div>

                            <div class="option-item">
                                <input type="radio" name="selectedOption" value="C" id="optC">
                                <label for="optC">
                                    <span class="option-letter">C</span>
                                    ${question.optionC}
                                </label>
                            </div>

                            <div class="option-item">
                                <input type="radio" name="selectedOption" value="D" id="optD">
                                <label for="optD">
                                    <span class="option-letter">D</span>
                                    ${question.optionD}
                                </label>
                            </div>
                        </div>

                        <div style="margin-top: 28px; padding-top: 20px; border-top: 1px solid var(--border-color); text-align: right;">
                            <button type="submit" class="btn btn-primary" id="submitAnswerBtn">
                                Submit Answer
                                <span class="material-icons-outlined" style="font-size:18px;">arrow_forward</span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </body>

        </html>