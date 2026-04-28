package com.me.learning.framework.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Shared HTTP request/response logging interceptor provided by sakila-framework.
 *            Automatically registered for all /api/** paths via {@link SakilaWebMvcConfigurer}.
 *            Child services do NOT need to create their own LoggingInterceptor.
 * Since    : Version 1.0
 */
@Slf4j
public class SakilaLoggingInterceptor implements HandlerInterceptor {

    private static final int HTTP_STATUS_CODE_500 = 500;
    private static final int HTTP_STATUS_CODE_400 = 400;

    @Override
    public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis ();
        request.setAttribute ("startTime", startTime);

        if (log.isDebugEnabled ()) {
            log.debug ("%n====== Request Begin ======\nURI    : {}%nMethod : {}%nAddr   : {}",
                    request.getRequestURI (), request.getMethod (), request.getRemoteAddr ());
        }

        return true;
    }

    @Override
    public void afterCompletion (HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        Object startAttr = request.getAttribute ("startTime");
        long executionTime = startAttr instanceof Long startLong
                ? System.currentTimeMillis () - startLong
                : 0L;

        String logMessage = String.format (
                "%n====== Response End ======%nURI    : %s%nMethod : %s%nStatus : %d%nTime   : %d ms",
                request.getRequestURI (), request.getMethod (), response.getStatus (), executionTime);

        if (response.getStatus () >= HTTP_STATUS_CODE_500) {
            log.error (logMessage);
        } else if (response.getStatus () >= HTTP_STATUS_CODE_400) {
            log.warn (logMessage);
        } else {
            log.info (logMessage);
        }

        if (ex != null) {
            log.error ("Request completed with exception: {}", ex.getMessage (), ex);
        }
    }
}

