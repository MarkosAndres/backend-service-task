package com.marcos.incode_home_task.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VerificationIdMdcFilter extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {
        String verificationId = valueFromHeaderOrParameter(
                request, VerificationContext.HEADER_NAME, VerificationContext.MDC_KEY);
        String query = valueFromHeaderOrParameter(
                request, VerificationContext.QUERY_HEADER_NAME, VerificationContext.QUERY_MDC_KEY);

        if (StringUtils.hasText(verificationId))
        {
            MDC.put(VerificationContext.MDC_KEY, verificationId);
        }
        if (StringUtils.hasText(query))
        {
            MDC.put(VerificationContext.QUERY_MDC_KEY, query);
        }

        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            MDC.remove(VerificationContext.MDC_KEY);
            MDC.remove(VerificationContext.QUERY_MDC_KEY);
        }
    }

    private String valueFromHeaderOrParameter(HttpServletRequest request, String headerName, String parameterName)
    {
        String value = request.getHeader(headerName);
        return StringUtils.hasText(value) ? value : request.getParameter(parameterName);
    }
}
