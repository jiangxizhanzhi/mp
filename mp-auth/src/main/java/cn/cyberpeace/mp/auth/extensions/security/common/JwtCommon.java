 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JwtCommon.java.
 // File size 2765.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.common;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

 /**
  * jwt常见
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class JwtCommon {

     /**
      * jwt常见
      */
     private JwtCommon() {}

     /**
      * 获取登录token
      *
      * @return
      */
     public static String getToken() {
        String token = getRequest().getHeader(AuthConstant.JWT_HEADER_KEY);
        return token;
    }

     /**
      * 获得令牌
      *
      * @param request 请求
      * @return {@link String }
      */
     public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(AuthConstant.JWT_HEADER_KEY);
        if (token != null) {
            token = token.replace(AuthConstant.JWT_PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

     /**
      * 获得第三个代币
      *
      * @param request 请求
      * @return {@link String }
      */
     public static String getThirdToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                //查找名为"third_token"的Cookie
                if ("third_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

     /**
      * 验证令牌
      *
      * @param token 令牌
      * @return {@link UserModel }
      */
     public static UserModel verifyToken(String token) {
        if (token == null) {
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EMPTY_ERROR);
        }

        JwtUtils.UserInfo user = JwtUtils.validateToken(token);

        return UserUtils.getUser(user.getId());
    }

     /**
      * 验证令牌
      *
      * @param request 请求
      * @return {@link UserModel }
      */
     public static UserModel verifyToken(HttpServletRequest request) {
        String token = JwtCommon.getToken(request);
        return JwtCommon.verifyToken(token);
    }

     /**
      * 获取当前HTTP请求对象
      *
      * @return
      */
     public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

     /**
      * 获取token header
      *
      * @return
      */
     public static Map<String,String> getTokenHeader(){
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization", JwtCommon.getToken());
        return headers;
    }
}
