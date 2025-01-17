 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpContextFilter.java.
 // File size 1068.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpContextFilter extends OncePerRequestFilter {

    private final static String X_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var requestId = request.getHeader(X_REQUEST_ID);
            if (!StringUtils.isEmpty(requestId)) {
                SysContextHolder.setCallChainId(requestId);
            }
            filterChain.doFilter(request, response);
        } finally {
            SysContextHolder.resetSysContext();
        }
    }
}
