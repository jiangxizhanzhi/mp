 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServletUtils.java.
 // File size 1551.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class ServletUtils {


    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getResponse();
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取请求语言
     * @return
     */
    public static String getAcceptLanguage(){
        HttpServletRequest httpServletRequest = getRequest();
        if (Objects.isNull(httpServletRequest)){
            return "";
        }
        return httpServletRequest.getHeader("Accept-Language");
    }

}
