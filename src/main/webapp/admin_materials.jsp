<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>PDF Quiz Generator - AdaptIQ Admin</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
        </head>

        <body>
            <div class="page-layout">
                <aside class="sidebar">
                    <div class="sidebar-brand">
                        <div class="brand-logo">A</div>
                        <div class="brand-text">
                            <span class="brand-name">AdaptIQ</span>
                            <span class="brand-subtitle">Admin Panel</span>
                        </div>
                    </div>
                    <nav class="sidebar-nav">
                        <a href="${pageContext.request.contextPath}/admin/dashboard">
                            <span class="material-icons-outlined">dashboard</span> Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions">
                            <span class="material-icons-outlined">quiz</span> Questions
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/materials" class="active">
                            <span class="material-icons-outlined">upload_file</span> PDF Quiz Generator
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions/add">
                            <span class="material-icons-outlined">add_circle</span> Add Question
                        </a>
                        <div class="nav-section-title">Navigation</div>
                        <a href="${pageContext.request.contextPath}/dashboard">
                            <span class="material-icons-outlined">home</span> Student Dashboard
                        </a>
                    </nav>
                    <div class="sidebar-user">
                        <div class="user-avatar" style="background:var(--danger-bg);color:var(--danger);">A</div>
                        <div class="user-info">
                            <span class="user-name">${sessionScope.username}</span>
                            <span class="user-role">Administrator</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" style="margin-left:auto;color:var(--text-muted);" title="Logout">
                            <span class="material-icons-outlined" style="font-size:20px;">logout</span>
                        </a>
                    </div>
                </aside>

                <main class="main-content animate-fade-in">
                    <div class="page-header">
                        <h1>PDF Quiz Generator</h1>
                        <p>Upload theory PDFs and turn them into topic-based quizzes for your learners.</p>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-error">${error}</div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success">${success}</div>
                    </c:if>

                    <section class="card" style="max-width:900px;margin-bottom:24px;">
                        <div class="card-header">
                            <span class="material-icons-outlined icon">picture_as_pdf</span>
                            Upload Study Material
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/admin/materials/upload"
                            enctype="multipart/form-data">
                            <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:16px;">
                                <div class="form-group">
                                    <label for="title">Material Title</label>
                                    <input type="text" id="title" name="title" class="form-control"
                                        placeholder="e.g., JVM Fundamentals Notes" required>
                                </div>
                                <div class="form-group">
                                    <label for="quizCategory">Quiz Topic Name</label>
                                    <input type="text" id="quizCategory" name="quizCategory" class="form-control"
                                        placeholder="Leave blank to reuse the title">
                                </div>
                                <div class="form-group">
                                    <label for="questionCount">Questions to Generate</label>
                                    <select id="questionCount" name="questionCount" class="form-control">
                                        <option value="5">5 Questions</option>
                                        <option value="10" selected>10 Questions</option>
                                        <option value="15">15 Questions</option>
                                        <option value="20">20 Questions</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="pdfFile">Theory PDF</label>
                                <input type="file" id="pdfFile" name="pdfFile" class="form-control"
                                    accept="application/pdf,.pdf" required>
                            </div>

                            <p style="color:var(--text-muted);font-size:0.85rem;margin-bottom:16px;">
                                Generated questions are added to the question bank under the selected topic, and students can choose that topic when starting a quiz.
                            </p>

                            <button type="submit" class="btn btn-primary" id="generatePdfQuizBtn">
                                <span class="material-icons-outlined" style="font-size:18px;">auto_awesome</span>
                                Generate Quiz from PDF
                            </button>
                        </form>
                    </section>

                    <section class="card" style="padding:0;overflow:hidden;">
                        <div style="padding:20px 24px;border-bottom:1px solid var(--border-color);display:flex;align-items:center;justify-content:space-between;gap:12px;flex-wrap:wrap;">
                            <div style="display:flex;align-items:center;gap:8px;">
                                <span class="material-icons-outlined" style="color:var(--primary);">library_books</span>
                                <h2 style="font-size:1.1rem;font-weight:600;">Uploaded Materials</h2>
                            </div>
                            <span style="color:var(--text-secondary);font-size:0.9rem;">
                                ${materials.size()} source PDFs
                            </span>
                        </div>

                        <c:choose>
                            <c:when test="${not empty materials}">
                                <div style="overflow-x:auto;">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Title</th>
                                                <th>Topic</th>
                                                <th>Generated</th>
                                                <th>Uploaded</th>
                                                <th>Preview</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="material" items="${materials}">
                                                <tr>
                                                    <td>
                                                        <div style="display:flex;flex-direction:column;gap:4px;">
                                                            <strong>${material.title}</strong>
                                                            <span style="color:var(--text-muted);font-size:0.8rem;">
                                                                ${material.originalFilename}
                                                            </span>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span class="badge badge-active">${material.quizCategory}</span>
                                                    </td>
                                                    <td>
                                                        <span class="badge badge-correct">${material.generatedQuestionCount} questions</span>
                                                    </td>
                                                    <td style="color:var(--text-secondary);">${material.uploadedAt}</td>
                                                    <td style="max-width:320px;color:var(--text-secondary);">
                                                        ${material.excerpt}
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="padding:40px 24px;text-align:center;color:var(--text-muted);">
                                    <span class="material-icons-outlined" style="font-size:40px;display:block;margin-bottom:8px;">note_add</span>
                                    No study materials uploaded yet.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </main>
            </div>
        </body>

        </html>
