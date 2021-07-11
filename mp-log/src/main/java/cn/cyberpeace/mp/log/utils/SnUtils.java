package cn.cyberpeace.mp.log.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


@Slf4j
public class SnUtils {

    
    public static String getRealRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        log.debug("X-Real-IP is {}", ip);

        if (!hasText(ip)) {
            ip = request.getHeader("REMOTE-HOST");
            log.debug("REMOTE-HOST is {}", ip);
        }

        if (!hasText(ip)) {
            ip = request.getHeader("x-forwarded-for");
            log.debug("x-forwarded-for is {}", ip);
        }

        if (!hasText(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.debug("Proxy-Client-IP is {}", ip);
        }

        if (!hasText(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.debug("WL-Proxy-Client-IP is {}", ip);
        }

        if (!hasText(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.debug("HTTP_CLIENT_IP is {}", ip);
        }

        if (!hasText(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.debug("HTTP_X_FORWARDED_FOR is {}", ip);
        }

        if (hasText(ip)) {
            StringTokenizer st = new StringTokenizer(ip, ",");

            
            if (st.countTokens() > 1) {
                ip = st.nextToken();
            }
        } else {
            
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    
    public static Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;

    }

    
    public static boolean hasText(String str) {
        return StringUtils.isNotBlank(str);
    }

    
    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    
    public static String getExceptionMessage(Throwable throwable) {
        String msg = throwable.getMessage();
        if (hasText(msg)) {
            return msg;
        }
        if (throwable.getCause() != null) { 
            msg = throwable.getCause().getMessage();
            if (hasText(msg)) {
                return msg;
            }
        }
        return getStackTrace(throwable);
    }
}
