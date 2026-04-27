<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Error - AdaptIQ</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet">
    </head>

    <body>
        <div style="display:flex;align-items:center;justify-content:center;min-height:100vh;padding:24px;">
            <div class="card animate-fade-in" style="max-width:420px;width:100%;text-align:center;padding:48px 36px;">
                <div style="width:72px;height:72px;border-radius:50%;background:var(--danger-bg);color:var(--danger);display:flex;align-items:center;justify-content:center;margin:0 auto 20px;font-size:36px;">
                    <span class="material-icons-outlined">error_outline</span>
                </div>

                <h2 style="font-size:1.5rem;font-weight:700;color:var(--text-primary);margin-bottom:8px;">
                    Oops! Something went wrong
                </h2>

                <p style="color:var(--text-secondary);font-size:0.95rem;margin-bottom:32px;line-height:1.6;">
                    The page you are looking for might not exist or an unexpected error occurred.
                </p>

                <div style="display:flex;flex-direction:column;gap:10px;">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary btn-block" id="goHomeBtn">
                        <span class="material-icons-outlined" style="font-size:18px;">arrow_back</span>
                        Go to Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="link" style="font-size:0.9rem;">
                        Go to Login
                    </a>
                </div>
            </div>
        </div>
    </body>

    </html>