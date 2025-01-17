 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BlacklistFilter.java.
 // File size 2920.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.AuthenticationServiceException;
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
  * 黑名单过滤器
  *
  * @author litian
  * @date 2023年09月25 11:18
  */
 @Slf4j
@Component
public class BlacklistFilter extends OncePerRequestFilter {
     /**
      * 黑名单模式
      */
     @Resource
    private BlacklistModel blacklistModel;

     /**
      * base应用程序配置
      */
     @Resource
    private BaseAppConfig baseAppConfig;

     /**
      *
      */
     private ObjectMapper mapper = null;

     /**
      *
      */
     @Resource
    private MappingJackson2HttpMessageConverter converter;

     /**
      * 做内部过滤
      *
      * @param request     请求
      * @param response    响应
      * @param filterChain 过滤器链
      * @throws ServletException servlet异常
      * @throws IOException      ioexception
      */
     @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (blacklistModel.isEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();

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

        filterChain.doFilter(request, response);
    }

     /**
      * 拒绝访问
      *
      * @param response 响应
      * @param uri
      * @throws IOException ioexception
      */
     private void denyAccess(HttpServletResponse response, String uri) throws IOException {
        log.error("access to {} was forbidden！", uri);
        response.setStatus(HttpServletResponse.SC_OK);
        ServiceResult serviceResult = ResultCommon.fail(AuthErrorCode.DEMO_ACCESS_ERROR);
        mapper = converter.getObjectMapper();

        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, serviceResult.asResult());
        writer.flush();
    }
}
