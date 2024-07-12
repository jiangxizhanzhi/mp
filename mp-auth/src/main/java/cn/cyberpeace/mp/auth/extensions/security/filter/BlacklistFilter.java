 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BlacklistFilter.java.
 // File size 3218.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName BlacklistFilter
 * @Description
 * @Author litian
 * @Date 2023年09月25 11:18
 * @Version 1.0.0
 * @see OncePerRequestFilter
 */
@Slf4j
@Component
public class BlacklistFilter extends OncePerRequestFilter {
    @Resource
    private BlacklistModel blacklistModel;

    @Resource
    private BaseAppConfig baseAppConfig;

    private ObjectMapper mapper = null;

    @Resource
    private MappingJackson2HttpMessageConverter converter;

    /**
     * doFilterInternal
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (blacklistModel.isEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();

            if (CollectionUtils.isNotEmpty(blacklistModel.getPatterns())) {
                // 快速失败模式，不使用流过滤
                for (BlacklistModel.AccessItem item : blacklistModel.getPatterns()) {
                    if (StringUtils.isEmpty(item.getMethod()) || method.equals(item.getMethod())) {
                        if (item.getPattern().matches(PathContainer.parsePath(uri))) {
                            denyAccess(response, uri);
                            return;
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * denyAccess
     *
     * @param response
     * @param uri
     */
    private void denyAccess(HttpServletResponse response, String uri) throws IOException {
        log.error("access to {} was forbidden！", uri);
        response.setStatus(HttpServletResponse.SC_OK);
        ServiceResult serviceResult = ResultCommon.fail(AuthErrorCode.DEMO_ACCESS_ERROR);
        mapper = converter.getObjectMapper();

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, serviceResult.asResult());
        writer.flush();
    }
}
