 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpAuthenticationEntryPoint.java.
 // File size 1679.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

 /**
  * HTTP身份验证入口点
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {
     /**
      * 映射器
      */
     private final ObjectMapper mapper;

     /**
      * HTTP身份验证入口点
      *
      * @param messageConverter 消息转换器
      */
     @Autowired
    public HttpAuthenticationEntryPoint(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

     /**
      * 开始
      *
      * @param request       请求
      * @param response      响应
      * @param authException 身份验证异常
      * @throws IOException ioexception
      */
     @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ServiceResult result = ServiceResult.builder().serviceCode(AuthErrorCode.NOT_CERTIFIED).build().asResult();

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result);
        writer.flush();
    }
}
