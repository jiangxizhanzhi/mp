package cn.cyberpeace.mp.auth.extensions.security.common;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class JwtCommon {

    private JwtCommon() {}

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(AuthConstant.JWT_HEADER_KEY);
        if (token != null) {
            token = token.replace(AuthConstant.JWT_PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

    public static UserModel verifyToken(String token) {
        if (token == null) {
            return null;
        }

        JwtUtils.UserInfo user;
        try {
            user = JwtUtils.validateToken(token);
        } catch (TokenExpiredException e) {
            return null;
        }
        if (user == null) {
            return null;
        }

        return UserUtils.getUser(user.getId());
    }

    public static UserModel verifyToken(HttpServletRequest request) {
        String token = JwtCommon.getToken(request);
        return JwtCommon.verifyToken(token);
    }
}
