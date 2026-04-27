<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${classroom.name} - AdaptIQ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="page-layout">
    <%@ include file="/WEB-INF/fragments/teacher_sidebar.jspf" %>
    <main class="main-content">
        <div class="page-header">
            <h1>${classroom.name}</h1>
            <p>Class code <strong>${classroom.classCode}</strong> lets students self-enroll, and you can still add them manually by username or email.</p>
        </div>

        <%@ include file="/WEB-INF/fragments/flash.jspf" %>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">key</span></div>
                <div class="stat-number">${classroom.classCode}</div>
                <div class="stat-label">Join Code</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><span class="material-icons-outlined">group</span></div>
                <div class="stat-number">${classroom.studentCount}</div>
                <div class="stat-label">Students</div>
            </div>
        </div>

        <div style="display:grid;grid-template-columns:minmax(320px,420px) 1fr;gap:20px;align-items:start;">
            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">person_add</span>
                    Add Student Manually
                </div>
                <form method="post" action="${pageContext.request.contextPath}/teacher/classes">
                    <input type="hidden" name="action" value="add-student">
                    <input type="hidden" name="classId" value="${classroom.id}">
                    <div class="form-group">
                        <label for="studentIdentifier">Username or Email</label>
                        <input id="studentIdentifier" name="studentIdentifier" class="form-control" placeholder="student_demo or student@example.com" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Add Student</button>
                </form>
                <div style="margin-top:18px;padding:14px;border:1px solid var(--border-color);border-radius:var(--radius);background:var(--primary-50);">
                    <div style="font-weight:600;color:var(--primary);margin-bottom:6px;">Share this code with students</div>
                    <div style="font-size:1.2rem;font-weight:800;letter-spacing:0.08em;">${classroom.classCode}</div>
                </div>
            </section>

            <section class="card">
                <div class="card-header">
                    <span class="material-icons-outlined icon">badge</span>
                    Enrolled Students
                </div>
                <c:choose>
                    <c:when test="${empty students}">
                        <div class="empty-state">
                            <strong>No students enrolled yet.</strong>
                            <p>Students can join from their dashboard using the class code, or you can add them manually here.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-wrapper">
                            <table>
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Username</th>
                                    <th>Email</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${students}" var="student">
                                    <tr>
                                        <td>${student.fullName}</td>
                                        <td>${student.username}</td>
                                        <td>${student.email}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
