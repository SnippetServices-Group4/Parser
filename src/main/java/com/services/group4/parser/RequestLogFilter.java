package com.services.group4.parser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class RequestLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String uri = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();
            String prefix = method + " " + uri;

            try {
                chain.doFilter(request, response);
            } finally {
                int statusCode = httpResponse.getStatus();
                logger.info("{} - {}", prefix, statusCode);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}