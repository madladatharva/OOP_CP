<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Register - AdaptIQ Smart Assessment</title>
            <meta name="description" content="Create an account on AdaptIQ adaptive assessment platform.">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        </head>

        <body>
            <div class="auth-page">
                <div class="auth-left">
                    <div class="auth-card animate-slide-up" style="max-width:480px;">
                        <div class="logo">
                            <div style="display:flex;align-items:center;justify-content:center;gap:10px;margin-bottom:8px;">
                                <div style="width:40px;height:40px;background:var(--primary);border-radius:var(--radius);display:flex;align-items:center;justify-content:center;color:white;font-weight:800;font-size:1.1rem;">A</div>
                                <h2>Create Account</h2>
                            </div>
                            <p>Join the AdaptIQ Assessment Platform</p>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm">
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" name="fullName" class="form-control"
                                    placeholder="Enter your full name" value="${fullName}" required>
                            </div>

                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" name="email" class="form-control"
                                    placeholder="Enter your email" value="${email}" required>
                            </div>

                            <div class="form-group">
                                <label for="username">Username</label>
                                <input type="text" id="username" name="username" class="form-control"
                                    placeholder="Choose a username" value="${username}" required>
                            </div>

                            <div class="form-group">
                                <label for="role">Role</label>
                                <select id="role" name="role" class="form-control" required>
                                    <option value="STUDENT" ${(empty role || role == 'STUDENT') ? 'selected' : ''}>Student</option>
                                    <option value="TEACHER" ${role == 'TEACHER' ? 'selected' : ''}>Teacher</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="password">Password</label>
                                <input type="password" id="password" name="password" class="form-control"
                                    placeholder="Create a password (min 4 characters)" required minlength="4">
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword">Confirm Password</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                                    placeholder="Confirm your password" required>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block mt-2" id="registerBtn">
                                Create Account
                            </button>
                        </form>

                        <p class="text-center mt-3" style="font-size: 0.875rem; color: var(--text-secondary);">
                            Already have an account?
                            <a href="${pageContext.request.contextPath}/login" class="link">Sign in here</a>
                        </p>
                    </div>
                </div>

                <div class="auth-right">
                    <div class="auth-right-content">
                        <h2>Start Your Learning Journey</h2>
                        <p>Create a teacher or student account for a classroom-based adaptive assessment workflow with subject-tagged quizzes and performance analytics.</p>
                        <div style="margin-top:32px;display:flex;flex-direction:column;gap:12px;">
                            <div style="display:flex;align-items:center;gap:10px;font-size:0.9rem;opacity:0.9;">
                                <span class="material-icons-outlined" style="font-size:20px;">check_circle</span>
                                Personalized difficulty adaptation
                            </div>
                            <div style="display:flex;align-items:center;gap:10px;font-size:0.9rem;opacity:0.9;">
                                <span class="material-icons-outlined" style="font-size:20px;">check_circle</span>
                                Detailed performance analytics
                            </div>
                            <div style="display:flex;align-items:center;gap:10px;font-size:0.9rem;opacity:0.9;">
                                <span class="material-icons-outlined" style="font-size:20px;">check_circle</span>
                                Track your progress over time
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const errorMsg = "${error}";
                    if (errorMsg && errorMsg.trim() !== "") {
                        Swal.fire({ icon: 'error', title: 'Registration Failed', text: errorMsg });
                    }
                });

                document.getElementById('registerForm').addEventListener('submit', function (e) {
                    const pwd = document.getElementById('password').value;
                    const confirmPwd = document.getElementById('confirmPassword').value;
                    if (pwd !== confirmPwd) {
                        e.preventDefault();
                        Swal.fire({ icon: 'warning', title: 'Passwords do not match', text: 'Please make sure your passwords match before submitting.' });
                    }
                });
            </script>
        </body>

        </html>
