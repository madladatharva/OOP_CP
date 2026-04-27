<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Question Bank - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Question Bank</h1>
            <p>Maintain a tagged academic question bank by subject, topic, and difficulty for adaptive quiz generation.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div style="display:grid;grid-template-columns:minmax(330px,420px) minmax(280px,340px) 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">tune</span>
                    Filter Questions
                </div>
                <form method="get" action="${pageContext.request.contextPath}/teacher/questions">
                    <div class="form-group">
                        <label for="filterSubjectId">Subject</label>
                        <select id="filterSubjectId" name="subjectId" class="form-control">
                            <option value="">All subjects</option>
                            <c:forEach items="${subjects}" var="subject">
                                <option value="${subject.id}" ${selectedSubjectId == subject.id ? 'selected' : ''}>${subject.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="filterTopicId">Topic</label>
                        <select id="filterTopicId" name="topicId" class="form-control">
                            <option value="">All topics</option>
                            <c:forEach items="${topics}" var="topic">
                                <option value="${topic.id}" ${selectedTopicId == topic.id ? 'selected' : ''}>${topic.subjectName} - ${topic.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="difficulty">Difficulty</label>
                        <select id="difficulty" name="difficulty" class="form-control">
                            <option value="">All levels</option>
                            <option value="1" ${selectedDifficulty == 1 ? 'selected' : ''}>Easy</option>
                            <option value="2" ${selectedDifficulty == 2 ? 'selected' : ''}>Medium</option>
                            <option value="3" ${selectedDifficulty == 3 ? 'selected' : ''}>Hard</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Apply Filters</button>
                </form>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">upload_file</span>
                    Bulk CSV Upload
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/questions" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="import">
                    <div class="form-group">
                        <label for="csvFile">CSV File</label>
                        <input id="csvFile" type="file" name="csvFile" class="form-control" accept=".csv" required>
                    </div>
                    <button type="submit" class="btn btn-secondary btn-block">Import Questions</button>
                </form>
                <div style="margin-top:16px;padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--bg-input);font-size:0.84rem;color:var(--text-secondary);">
                    Required columns: <strong>subject</strong>, <strong>topic</strong>, <strong>difficulty</strong>,
                    <strong>question_text</strong>, <strong>option_a</strong>, <strong>option_b</strong>,
                    <strong>option_c</strong>, <strong>option_d</strong>, <strong>correct_option</strong>.
                </div>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">edit_note</span>
                    ${editingQuestion != null ? 'Edit Question' : 'Add Question'}
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/questions">
                    <input type="hidden" name="action" value="save">
                    <c:if test="${editingQuestion != null}">
                        <input type="hidden" name="questionId" value="${editingQuestion.id}">
                    </c:if>
                    <div style="display:grid;grid-template-columns:1fr 1fr 150px;gap:14px;">
                        <div class="form-group">
                            <label for="questionSubjectId">Subject</label>
                            <select id="questionSubjectId" name="subjectId" class="form-control" required>
                                <option value="">Select subject</option>
                                <c:forEach items="${subjects}" var="subject">
                                    <option value="${subject.id}" ${editingQuestion.subjectId == subject.id ? 'selected' : ''}>${subject.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="questionTopicId">Topic</label>
                            <select id="questionTopicId" name="topicId" class="form-control" required>
                                <option value="">Select topic</option>
                                <c:forEach items="${topics}" var="topic">
                                    <option value="${topic.id}" data-subject-id="${topic.subjectId}" ${editingQuestion.topicId == topic.id ? 'selected' : ''}>${topic.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="difficultyLevel">Difficulty</label>
                            <select id="difficultyLevel" name="difficultyLevel" class="form-control" required>
                                <option value="1" ${editingQuestion.difficultyLevel == 1 ? 'selected' : ''}>Easy</option>
                                <option value="2" ${(editingQuestion == null || editingQuestion.difficultyLevel == 2) ? 'selected' : ''}>Medium</option>
                                <option value="3" ${editingQuestion.difficultyLevel == 3 ? 'selected' : ''}>Hard</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="questionText">Question</label>
                        <textarea id="questionText" name="questionText" class="form-control" placeholder="Enter the full question statement." required>${editingQuestion.questionText}</textarea>
                    </div>
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                        <div class="form-group">
                            <label for="optionA">Option A</label>
                            <input id="optionA" name="optionA" class="form-control" value="${editingQuestion.optionA}" required>
                        </div>
                        <div class="form-group">
                            <label for="optionB">Option B</label>
                            <input id="optionB" name="optionB" class="form-control" value="${editingQuestion.optionB}" required>
                        </div>
                        <div class="form-group">
                            <label for="optionC">Option C</label>
                            <input id="optionC" name="optionC" class="form-control" value="${editingQuestion.optionC}" required>
                        </div>
                        <div class="form-group">
                            <label for="optionD">Option D</label>
                            <input id="optionD" name="optionD" class="form-control" value="${editingQuestion.optionD}" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="correctOption">Correct Answer</label>
                        <select id="correctOption" name="correctOption" class="form-control" required>
                            <option value="A" ${(editingQuestion == null || editingQuestion.correctOption == 'A') ? 'selected' : ''}>Option A</option>
                            <option value="B" ${editingQuestion.correctOption == 'B' ? 'selected' : ''}>Option B</option>
                            <option value="C" ${editingQuestion.correctOption == 'C' ? 'selected' : ''}>Option C</option>
                            <option value="D" ${editingQuestion.correctOption == 'D' ? 'selected' : ''}>Option D</option>
                        </select>
                    </div>
                    <div style="display:flex;gap:12px;flex-wrap:wrap;">
                        <button type="submit" class="btn btn-primary">${editingQuestion != null ? 'Update Question' : 'Save Question'}</button>
                        <c:if test="${editingQuestion != null}">
                            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/teacher/questions">Cancel Edit</a>
                        </c:if>
                    </div>
                </form>
            </section>
        </div>

        <section class="card">
            <div class="card-header">
                <span class="material-icons-outlined icon">library_books</span>
                Active Question Bank
            </div>
            <c:choose>
                <c:when test="${empty questions}">
                    <div class="empty-state">
                        <strong>No questions match the current filters.</strong>
                        <p>Try a broader filter, add a manual question, or import a CSV batch.</p>
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
                                <th>Difficulty</th>
                                <th style="width:170px;">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${questions}" var="question">
                                <tr>
                                    <td>${question.questionText}</td>
                                    <td>${question.subjectName}</td>
                                    <td>${question.topicName}</td>
                                    <td>
                                        <span class="difficulty-badge ${question.difficultyLevel == 1 ? 'difficulty-easy' : question.difficultyLevel == 2 ? 'difficulty-medium' : 'difficulty-hard'}">
                                            ${question.difficultyLabel}
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display:flex;gap:8px;flex-wrap:wrap;">
                                            <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/teacher/questions?editId=${question.id}">Edit</a>
                                            <form method="post" action="${pageContext.request.contextPath}/teacher/questions" onsubmit="return confirm('Archive this question?');" style="margin:0;">
                                                <input type="hidden" name="action" value="archive">
                                                <input type="hidden" name="questionId" value="${question.id}">
                                                <button type="submit" class="btn btn-danger btn-sm">Archive</button>
                                            </form>
                                        </div>
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
        const subjectSelect = document.getElementById('questionSubjectId');
        const topicSelect = document.getElementById('questionTopicId');
        if (!subjectSelect || !topicSelect) {
            return;
        }

        function filterTopicOptions() {
            const subjectId = subjectSelect.value;
            Array.from(topicSelect.options).forEach(function (option, index) {
                if (index === 0) {
                    option.hidden = false;
                    option.disabled = false;
                    return;
                }
                const matches = !subjectId || option.dataset.subjectId === subjectId;
                option.hidden = !matches;
                option.disabled = !matches;
                if (!matches && option.selected) {
                    topicSelect.value = '';
                }
            });
        }

        subjectSelect.addEventListener('change', filterTopicOptions);
        filterTopicOptions();
    }());
</script>
</body>
</html>
