<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Classes - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>Class Management</h1>
            <p>Create class groups, share codes with students, and manage rosters for quiz assignment.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div style="display:grid;grid-template-columns:minmax(320px,420px) 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">add_circle</span>
                    Create a Class
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/classes">
                    <input type="hidden" name="action" value="create">
                    <div class="form-group">
                        <label for="name">Class Name</label>
                        <input id="name" name="name" class="form-control" placeholder="SE Sem 3" required>
                    </div>
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" placeholder="Optional teaching notes, section details, or batch context."></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Create Class</button>
                </form>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">groups</span>
                    Your Classes
                </div>
                <c:choose>
                    <c:when test="${empty classrooms}">
                        <div class="empty-state">
                            <strong>No classes yet.</strong>
                            <p>Once you create a class, each one gets a unique join code students can use from their dashboard.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="classroom-grid">
                            <c:forEach items="${classrooms}" var="classroom">
                                <a class="classroom-tile" href="${pageContext.request.contextPath}/teacher/classes?classId=${classroom.id}">
                                    <div style="display:flex;justify-content:space-between;align-items:flex-start;gap:12px;">
                                        <div>
                                            <div style="font-weight:700;color:var(--text-primary);">${classroom.name}</div>
                                            <div style="font-size:0.82rem;color:var(--text-secondary);">Class code: ${classroom.classCode}</div>
                                        </div>
                                        <span class="badge badge-active">${classroom.studentCount} students</span>
                                    </div>
                                    <p style="margin-top:10px;color:var(--text-secondary);font-size:0.88rem;">
                                        <c:out value="${empty classroom.description ? 'Open this class to add students manually and monitor the roster.' : classroom.description}" />
                                    </p>
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
