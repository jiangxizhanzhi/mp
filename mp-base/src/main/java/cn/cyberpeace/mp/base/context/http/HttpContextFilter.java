 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpContextFilter.java.
 // File size 1345.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.context.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName HttpContextFilter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see OncePerRequestFilter
 */
public class HttpContextFilter extends OncePerRequestFilter {

    private final static String X_REQUEST_ID = "X-Request-Id";

    /**
     * doFilterInternal
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 开启跨域
            //response.setHeader("Access-Control-Allow-Origin", "*");
            //response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            //response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

            String requestId = request.getHeader(X_REQUEST_ID);
            if (!StringUtils.isEmpty(requestId)) {
                SysContextHolder.setCallChainId(requestId);
            }
            filterChain.doFilter(request, response);
        } finally {
            SysContextHolder.resetSysContext();
        }
    }
}
