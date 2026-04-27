<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Login - AdaptIQ Smart Assessment</title>
            <meta name="description" content="Log in to AdaptIQ, your personalized adaptive assessment platform.">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        </head>

        <body>
            <div class="auth-page">
                <!-- Left: Login Form -->
                <div class="auth-left">
                    <div class="auth-card animate-slide-up">
                        <div class="logo">
                            <div style="display:flex;align-items:center;justify-content:center;gap:10px;margin-bottom:8px;">
                                <div style="width:40px;height:40px;background:var(--primary);border-radius:var(--radius);display:flex;align-items:center;justify-content:center;color:white;font-weight:800;font-size:1.1rem;">A</div>
                                <h2>AdaptIQ</h2>
                            </div>
                            <p>Access your personalized learning dashboard</p>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/login" id="loginForm">
                            <div class="form-group">
                                <label for="username">Username</label>
                                <input type="text" id="username" name="username" class="form-control"
                                    placeholder="Enter your username" required autofocus>
                            </div>

                            <div class="form-group">
                                <label for="password">Password</label>
                                <input type="password" id="password" name="password" class="form-control"
                                    placeholder="Enter your password" required>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block mt-2" id="loginBtn">
                                Sign In
                            </button>
                        </form>

                        <p class="text-center mt-3" style="font-size: 0.875rem; color: var(--text-secondary);">
                            New to the platform?
                            <a href="${pageContext.request.contextPath}/register" class="link">Create Account</a>
                        </p>

                        <div class="mt-3" style="background: var(--primary-50); border-radius: var(--radius); padding: 14px; border: 1px solid var(--border-color);">
                            <p style="font-size: 0.8rem; color: var(--primary); margin-bottom: 6px; font-weight: 600;">
                                <span class="material-icons-outlined" style="font-size:14px;vertical-align:middle;margin-right:4px;">info</span>
                                Demo Accounts
                            </p>
                            <p style="font-size: 0.8rem; color: var(--text-secondary);">Teacher: <strong style="color: var(--text-primary);">teacher_demo</strong> / teacher123</p>
                            <p style="font-size: 0.8rem; color: var(--text-secondary);">Student: <strong style="color: var(--text-primary);">student_demo</strong> / student123</p>
                            <p style="font-size: 0.8rem; color: var(--text-secondary);">Legacy teacher: <strong style="color: var(--text-primary);">admin</strong> / admin123</p>
                            <p style="font-size: 0.8rem; color: var(--text-secondary);">Legacy student: <strong style="color: var(--text-primary);">john_doe</strong> / user123</p>
                        </div>
                    </div>
                </div>

                <!-- Right: Branding Panel -->
                <div class="auth-right">
                    <div class="auth-right-content">
                        <h2>Welcome Back to AdaptIQ</h2>
                        <p>Teachers manage classes and assessments here, while students receive adaptive quizzes that respond to their performance in real time.</p>
                        <div style="margin-top:32px;display:flex;gap:16px;">
                            <div style="display:flex;align-items:center;gap:8px;background:rgba(255,255,255,0.12);padding:8px 14px;border-radius:var(--radius);font-size:0.85rem;">
                                <span class="material-icons-outlined" style="font-size:18px;">auto_awesome</span>
                                Adaptive AI
                            </div>
                            <div style="display:flex;align-items:center;gap:8px;background:rgba(255,255,255,0.12);padding:8px 14px;border-radius:var(--radius);font-size:0.85rem;">
                                <span class="material-icons-outlined" style="font-size:18px;">insights</span>
                                Analytics
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const errorMsg = "${error}";
                    const successMsg = "${success}";

                    if (errorMsg && errorMsg.trim() !== "") {
                        Swal.fire({
                            icon: 'error',
                            title: 'Authentication Failed',
                            text: errorMsg
                        });
                    }
                    if (successMsg && successMsg.trim() !== "") {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success!',
                            text: successMsg
                        });
                    }
                });
            </script>
        </body>

        </html>
