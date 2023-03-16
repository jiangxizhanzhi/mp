/*
 * Copyright (c) 2018. 云帐房网络科技有限公司 版权所有
 */

package cn.cyberpeace.mp.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
public class CookieUtils {

    /**
     * 设置 Cookie
     *
     * @param response
     * @param name
     * @param value
     * @param domain
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String domain, int maxAge) {
        setCookie(response, name, value, "/", domain, maxAge);
    }

    /**
     * 设置 Cookie
     *
     * @param response
     * @param name
     * @param value
     * @param path
     * @param domain
     * @param maxAge
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path, String domain, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        try {
            cookie.setValue(URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addCookie(cookie);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return getCookie(request, null, name, false);
    }

    /**
     * 获得指定Cookie的值，并删除。
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        return getCookie(request, response, name, true);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param name 名字
     * @param isRemove 是否移除
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name, boolean isRemove) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (isRemove) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
        log.info("name:{}  value:{}", name, value);
        return value;
    }
}
