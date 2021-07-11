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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class JwtCommon {

    private JwtCommon() {}

    
    public static String getToken() {
        String token = getRequest().getHeader(AuthConstant.JWT_HEADER_KEY);
        return token;
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(AuthConstant.JWT_HEADER_KEY);
        if (token != null) {
            token = token.replace(AuthConstant.JWT_PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

    public static UserModel verifyToken(String token) {
        if (token == null) {
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EMPTY_ERROR);
        }

        JwtUtils.UserInfo user = JwtUtils.validateToken(token);

        return UserUtils.getUser(user.getId());
    }

    public static UserModel verifyToken(HttpServletRequest request) {
        String token = JwtCommon.getToken(request);
        return JwtCommon.verifyToken(token);
    }

    
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    
    public static Map<String,String> getTokenHeader(){
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Authorization", JwtCommon.getToken());
        return headers;
    }
}
