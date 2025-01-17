 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysAdminFilter.java.
 // File size 2131.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.filter;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.service.sys.SysCtlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

 /**
  * 系统管理员过滤器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class SysAdminFilter extends OncePerRequestFilter {

     /**
      * 映射器
      */
     private ObjectMapper mapper = null;

     /**
      * sys细胞毒性t淋巴细胞服务
      */
     private SysCtlService sysCtlService;

     /**
      * 转换器
      */
     @Autowired
    private MappingJackson2HttpMessageConverter converter;

     /**
      * 系统管理员过滤器
      *
      * @param sysCtlService sys细胞毒性t淋巴细胞服务
      * @param converter     转换器
      */
     public SysAdminFilter(SysCtlService sysCtlService, MappingJackson2HttpMessageConverter converter) {
        this.sysCtlService = sysCtlService;
        this.converter = converter;
    }

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (sysCtlService.isSystemTimestampRollback()) {
            log.error("systime rollback error！");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            ServiceResult serviceResult = ResultCommon.fail(AdminErrorCode.SYS_TIMESTAMP_ROLLBACK);
            mapper = converter.getObjectMapper();

            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, serviceResult.asResult());
            writer.flush();
            return;
        }

        // todo 拦截license失效？

        filterChain.doFilter(request, response);

    }

}
