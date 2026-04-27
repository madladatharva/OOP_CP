<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Quiz Results - AdaptIQ</title>
            <meta name="description" content="View your quiz results and detailed performance breakdown.">
            <link rel="stylesheet" href="css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
            <style>
                .results-container { max-width: 960px; margin: 0 auto; padding: 32px 24px; }
                .result-hero {
                    background: var(--bg-card); border: 1px solid var(--border-color);
                    border-radius: var(--radius-xl); padding: 40px;
                    display: flex; align-items: center; justify-content: space-between;
                    gap: 32px; margin-bottom: 24px; box-shadow: var(--shadow-card);
                    flex-wrap: wrap;
                }
                .result-score-section { text-align: center; }
                .result-score-section .big-score {
                    font-size: 3.5rem; font-weight: 800; color: var(--primary); line-height: 1;
                }
                .result-score-section .big-score span { color: var(--text-muted); font-size: 1.8rem; }
                .result-score-section .accuracy-text {
                    color: var(--primary-lighter); font-weight: 600; font-size: 1rem; margin-top: 4px;
                }
                .grade-badge {
                    padding: 10px 24px; border-radius: var(--radius); font-weight: 700;
                    font-size: 1.1rem; display: inline-flex; align-items: center; gap: 8px;
                }
                .grade-excellent { background: var(--success-bg); color: var(--success); }
                .grade-good { background: var(--info-bg); color: var(--info); }
                .grade-average { background: var(--warning-bg); color: var(--warning); }
                .grade-poor { background: var(--danger-bg); color: var(--danger); }
                .stats-4grid {
                    display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 24px;
                }
                .stat-box {
                    background: var(--bg-card); border: 1px solid var(--border-color);
                    border-radius: var(--radius-lg); padding: 20px; text-align: center;
                    box-shadow: var(--shadow-card); transition: var(--transition);
                }
                .stat-box:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
                .stat-box .icon { font-size: 1.5rem; margin-bottom: 8px; }
                .stat-box .num { font-size: 1.8rem; font-weight: 700; color: var(--text-primary); }
                .stat-box .lbl { font-size: 0.7rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; margin-top: 2px; }
                .diff-badge-sm {
                    display: inline-flex; padding: 2px 8px; border-radius: var(--radius-full);
                    font-size: 0.65rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.03em;
                }
                .result-icon {
                    width: 28px; height: 28px; border-radius: 50%;
                    display: inline-flex; align-items: center; justify-content: center;
                    font-size: 14px;
                }
                @media (max-width: 768px) {
                    .result-hero { flex-direction: column; text-align: center; }
                    .stats-4grid { grid-template-columns: repeat(2, 1fr); }
                }
            </style>
        </head>

        <body>
            <nav class="navbar">
                <div class="container d-flex justify-between items-center">
                    <a href="dashboard" class="navbar-brand">
                        <span class="brand-icon">A</span> AdaptIQ
                    </a>
                    <ul class="navbar-nav">
                        <li><a href="dashboard">Dashboard</a></li>
                        <li><a href="logout" class="btn btn-secondary btn-sm">Logout</a></li>
                    </ul>
                </div>
            </nav>

            <div class="results-container animate-fade-in">
                <div style="text-align:center;margin-bottom:28px;">
                    <h1 style="font-size:2rem;font-weight:700;color:var(--text-primary);margin-bottom:4px;">Quiz Complete!</h1>
                    <p style="color:var(--text-secondary);">Here's how you performed</p>
                </div>

                <!-- Error -->
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <!-- Result Summary -->
                <div class="result-hero">
                    <div class="result-score-section">
                        <div class="big-score">${quizSession.score} <span>/ ${quizSession.currentQuestionNumber}</span></div>
                        <div class="accuracy-text">Accuracy: ${accuracy}%</div>
                        <div class="accuracy-text" style="font-size:0.9rem;color:var(--text-secondary);">
                            Topic: ${empty quizSession.selectedCategory ? 'All Topics' : quizSession.selectedCategory}
                        </div>
                    </div>

                    <c:set var="accNum" value="${quizSession.accuracyPercentage}" />
                    <c:choose>
                        <c:when test="${accNum >= 80}">
                            <div class="grade-badge grade-excellent">
                                <span class="material-icons-outlined">emoji_events</span> Excellent
                            </div>
                        </c:when>
                        <c:when test="${accNum >= 60}">
                            <div class="grade-badge grade-good">
                                <span class="material-icons-outlined">thumb_up</span> Good
                            </div>
                        </c:when>
                        <c:when test="${accNum >= 40}">
                            <div class="grade-badge grade-average">
                                <span class="material-icons-outlined">trending_flat</span> Average
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="grade-badge grade-poor">
                                <span class="material-icons-outlined">fitness_center</span> Needs Practice
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Stats -->
                <div class="stats-4grid">
                    <div class="stat-box">
                        <div class="icon" style="color:var(--success);"><span class="material-icons-outlined">check_circle</span></div>
                        <div class="num">${quizSession.score}</div>
                        <div class="lbl">Correct</div>
                    </div>
                    <div class="stat-box">
                        <div class="icon" style="color:var(--danger);"><span class="material-icons-outlined">cancel</span></div>
                        <div class="num">${quizSession.currentQuestionNumber - quizSession.score}</div>
                        <div class="lbl">Wrong</div>
                    </div>
                    <div class="stat-box">
                        <div class="icon" style="color:var(--primary);"><span class="material-icons-outlined">percent</span></div>
                        <div class="num">${accuracy}%</div>
                        <div class="lbl">Accuracy</div>
                    </div>
                    <div class="stat-box">
                        <div class="icon" style="color:var(--secondary);"><span class="material-icons-outlined">format_list_numbered</span></div>
                        <div class="num">${quizSession.currentQuestionNumber}</div>
                        <div class="lbl">Attempted</div>
                    </div>
                </div>

                <!-- Detailed Breakdown -->
                <div class="card" style="padding:0;overflow:hidden;">
                    <div style="padding:20px 24px;border-bottom:1px solid var(--border-color);display:flex;align-items:center;gap:8px;">
                        <span class="material-icons-outlined" style="color:var(--primary);">analytics</span>
                        <h2 style="font-size:1.1rem;font-weight:600;">Detailed Breakdown</h2>
                    </div>

                    <div style="overflow-x:auto;">
                        <table>
                            <thead>
                                <tr>
                                    <th style="text-align:center;width:50px;">#</th>
                                    <th>Question</th>
                                    <th>Your Answer</th>
                                    <th>Correct</th>
                                    <th style="text-align:center;">Difficulty</th>
                                    <th style="text-align:center;">Result</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="attempt" items="${attempts}" varStatus="loop">
                                    <tr>
                                        <td style="text-align:center;color:var(--text-muted);font-weight:500;">${loop.count}</td>
                                        <td>
                                            <p style="max-width:280px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--text-primary);"
                                                title="${questions[loop.index].questionText}">
                                                ${questions[loop.index].questionText}
                                            </p>
                                        </td>
                                        <td style="font-weight:500;">Option ${attempt.selectedOption}</td>
                                        <td style="font-weight:500;color:var(--success);">Option ${questions[loop.index].correctOption}</td>
                                        <td style="text-align:center;">
                                            <c:choose>
                                                <c:when test="${attempt.difficultyAtTime == 1}">
                                                    <span class="diff-badge-sm" style="background:var(--success-bg);color:var(--success);">Easy</span>
                                                </c:when>
                                                <c:when test="${attempt.difficultyAtTime == 2}">
                                                    <span class="diff-badge-sm" style="background:var(--warning-bg);color:var(--warning);">Medium</span>
                                                </c:when>
                                                <c:when test="${attempt.difficultyAtTime == 3}">
                                                    <span class="diff-badge-sm" style="background:var(--danger-bg);color:var(--danger);">Hard</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td style="text-align:center;">
                                            <c:choose>
                                                <c:when test="${attempt.correct}">
                                                    <div class="result-icon" style="background:var(--success-bg);color:var(--success);" title="Correct">
                                                        <span class="material-icons-outlined" style="font-size:16px;">check</span>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="result-icon" style="background:var(--danger-bg);color:var(--danger);" title="Wrong">
                                                        <span class="material-icons-outlined" style="font-size:16px;">close</span>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap;margin:32px 0;">
                    <a href="quiz?action=start" class="btn btn-primary btn-lg" id="retakeQuizBtn">
                        <span class="material-icons-outlined" style="font-size:18px;">replay</span>
                        Take Another Quiz
                    </a>
                    <a href="dashboard" class="btn btn-secondary btn-lg" id="backDashboardBtn">
                        <span class="material-icons-outlined" style="font-size:18px;">arrow_back</span>
                        Back to Dashboard
                    </a>
                </div>
            </div>

            <footer class="footer">
                <div class="container">
                    <p>AdaptIQ Smart Assessment Platform &copy; 2026</p>
                </div>
            </footer>
        </body>

        </html>
