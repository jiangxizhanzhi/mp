package cn.cyberpeace.mp.auth.extensions.security.common;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName JwtCommon
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public class JwtCommon {

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
     * getToken
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(AuthConstant.JWT_HEADER_KEY);
        if (token != null) {
            token = token.replace(AuthConstant.JWT_PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

    /**
     * verifyToken
     *
     * @param token
     * @return
     */
    public static UserModel verifyToken(String token) {
        if (token == null) {
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EMPTY_ERROR);
        }

        JwtUtils.UserInfo user = JwtUtils.validateToken(token);

        return UserUtils.getUser(user.getId());
    }

    /**
     * verifyToken
     *
     * @param request
     * @return
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
}
