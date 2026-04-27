package com.assessment.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Global Exception Handler Filter.
 * Catches all unhandled exceptions across the application, logs them,
 * and routes the user to a generic, friendly error page.
 */
@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ExceptionHandlerFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (Throwable e) {
            logger.error("Unhandled Exception captured by Filter at URI: " + req.getRequestURI(), e);

            if (!res.isCommitted()) {
                request.setAttribute("errorDetail", "A server error occurred. Please try again later.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        logger.info("ExceptionHandlerFilter destroyed.");
    }
}
