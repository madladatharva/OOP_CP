<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Answer Feedback - AdaptIQ</title>
            <link rel="stylesheet" href="css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
            <style>
                .feedback-container {
                    max-width: 640px;
                    margin: 0 auto;
                    padding: 40px 24px;
                }
                .feedback-card {
                    background: var(--bg-card);
                    border: 1px solid var(--border-color);
                    border-radius: var(--radius-xl);
                    padding: 40px;
                    text-align: center;
                    box-shadow: var(--shadow-md);
                }
                .feedback-icon {
                    width: 72px; height: 72px;
                    border-radius: 50%;
                    display: flex; align-items: center; justify-content: center;
                    margin: 0 auto 20px;
                    font-size: 36px;
                }
                .feedback-icon.correct {
                    background: var(--success-bg); color: var(--success);
                }
                .feedback-icon.wrong {
                    background: var(--danger-bg); color: var(--danger);
                }
                .stats-row {
                    display: grid; grid-template-columns: repeat(3, 1fr);
                    gap: 12px; margin: 28px 0;
                }
                .stat-mini {
                    background: var(--bg-input); border: 1px solid var(--border-color);
                    border-radius: var(--radius); padding: 14px 8px; text-align: center;
                }
                .stat-mini .value { font-size: 1.3rem; font-weight: 700; color: var(--text-primary); }
                .stat-mini .label { font-size: 0.7rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; margin-top: 2px; }
                .correct-answer-box {
                    background: var(--success-bg); border: 1px solid #bbf7d0;
                    border-radius: var(--radius); padding: 16px; margin-top: 20px;
                    text-align: left;
                }
            </style>
        </head>

        <body>
            <nav class="navbar">
                <div class="container d-flex justify-between items-center">
                    <a href="dashboard" class="navbar-brand">
                        <span class="brand-icon">A</span> AdaptIQ
                    </a>
                </div>
            </nav>

            <div class="feedback-container animate-fade-in">
                <div class="feedback-card">
                    <c:choose>
                        <c:when test="${isCorrect}">
                            <div class="feedback-icon correct">
                                <span class="material-icons-outlined">check</span>
                            </div>
                            <h2 style="font-size:1.6rem;font-weight:700;color:var(--success);margin-bottom:6px;">Correct!</h2>
                            <p style="color:var(--text-secondary);">Great job! Keep the momentum going.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="feedback-icon wrong">
                                <span class="material-icons-outlined">close</span>
                            </div>
                            <h2 style="font-size:1.6rem;font-weight:700;color:var(--danger);margin-bottom:6px;">Incorrect</h2>
                            <p style="color:var(--text-secondary);margin-bottom:0;">Don't worry, learning from mistakes is key!</p>

                            <div class="correct-answer-box">
                                <p style="font-size:0.75rem;text-transform:uppercase;letter-spacing:0.05em;color:var(--success);font-weight:600;margin-bottom:8px;">
                                    The correct answer was
                                </p>
                                <div style="display:flex;align-items:center;gap:10px;">
                                    <span style="width:32px;height:32px;border-radius:50%;background:var(--success);color:white;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:0.85rem;flex-shrink:0;">
                                        ${answeredQuestion.correctOption}
                                    </span>
                                    <span style="font-weight:500;color:var(--text-primary);">
                                        <c:choose>
                                            <c:when test="${answeredQuestion.correctOption == 'A'}">${answeredQuestion.optionA}</c:when>
                                            <c:when test="${answeredQuestion.correctOption == 'B'}">${answeredQuestion.optionB}</c:when>
                                            <c:when test="${answeredQuestion.correctOption == 'C'}">${answeredQuestion.optionC}</c:when>
                                            <c:when test="${answeredQuestion.correctOption == 'D'}">${answeredQuestion.optionD}</c:when>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Current Stats -->
                    <div class="stats-row">
                        <div class="stat-mini">
                            <div class="value" style="color:var(--primary);">${session.score}</div>
                            <div class="label">Score</div>
                        </div>
                        <div class="stat-mini">
                            <div class="value">${session.currentQuestionNumber}<span style="color:var(--text-muted);font-size:0.85rem;">/${session.totalQuestions}</span></div>
                            <div class="label">Question</div>
                        </div>
                        <div class="stat-mini">
                            <c:choose>
                                <c:when test="${session.currentDifficulty == 1}">
                                    <div><span class="difficulty-badge difficulty-easy">Easy</span></div>
                                </c:when>
                                <c:when test="${session.currentDifficulty == 2}">
                                    <div><span class="difficulty-badge difficulty-medium">Medium</span></div>
                                </c:when>
                                <c:when test="${session.currentDifficulty == 3}">
                                    <div><span class="difficulty-badge difficulty-hard">Hard</span></div>
                                </c:when>
                            </c:choose>
                            <div class="label" style="margin-top:6px;">Next Diff.</div>
                        </div>
                    </div>

                    <a href="quiz?action=question" class="btn btn-primary btn-block" id="nextQuestionBtn" style="padding:12px;">
                        Next Question
                        <span class="material-icons-outlined" style="font-size:18px;">arrow_forward</span>
                    </a>
                </div>
            </div>
        </body>

        </html>