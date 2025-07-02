 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JwtUtils.java.
 // File size 8686.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

 /**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
@Component
public class JwtUtils {

    public static JwtUtils jwtUtils;

    @Autowired
    private AuthConfig authConfig;

    //private static final long JWT_EXPIRATION = 24 * 3600 * 1000L * 7;

    private JwtUtils() {}

    @PostConstruct
    public void init() {
        jwtUtils = this;
        jwtUtils.authConfig = authConfig;
    }

    private static String getSecret(UserInfo user) {
        String secret = "";
        if (jwtUtils.authConfig.getAuthenticateUseIndividualTokenSecret()) {
            secret = user.getLatestTokenSecret();
        }
        if (StringUtils.isBlank(secret)) {
            //secret = AuthConstant.JWT_SECRET;
            secret = jwtUtils.authConfig.getAuthenticateJwtSecret();
        }
        return secret;
    }

    private static String generateJwtSecret(UserInfo user) {
        String secret = getSecret(user);
        final Mac mac;
        try {
            mac = Mac.getInstance(AuthConstant.JWT_ALGORITHM);
            //mac.init(new SecretKeySpec(AuthConstant.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8), algorithm));
            mac.init(new SecretKeySpec(jwtUtils.authConfig.getAuthenticateJwtSecret().getBytes(StandardCharsets.UTF_8), AuthConstant.JWT_ALGORITHM));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Generate Jwt Secret error: {}", ExceptionUtils.getStackTrace(e));
            return jwtUtils.authConfig.getAuthenticateJwtSecret();
        }

        mac.update(secret.getBytes(StandardCharsets.UTF_8));
        return new String(mac.doFinal());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String tokenSecret;

        public String getLatestTokenSecret() {
            String latestTokenSecret;
            UserModel user = UserUtils.getUser(id);
            if (user != null) {
                latestTokenSecret = user.getTokenSecret();
            } else {
                latestTokenSecret = getTokenSecret();
            }

            setTokenSecret(latestTokenSecret);

            return latestTokenSecret;
        }
    }

    public static String generateToken(UserInfo user) {
        String jwtSecret = generateJwtSecret(user);
        Long expireSeconds = jwtUtils.authConfig.getAuthenticateJwtExpires();
        if (Objects.isNull(expireSeconds)) {
            expireSeconds = AuthConstant.TOKEN_EXPIRE_TIME;
        }
        var expireDate = new Date(System.currentTimeMillis() + expireSeconds * AuthConstant.MILLI_TO_SECOND);
        String secret = getSecret(user);
        try {
            var algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.create().withExpiresAt(expireDate).withClaim(AuthConstant.JWT_CLAIM_ID, user.getId())
                    .withClaim(AuthConstant.JWT_CLAIM_USERNAME, user.getUsername()).withClaim(AuthConstant.JWT_CLAIM_KEY, secret).sign(algorithm);
        } catch (JWTCreationException e) {
            log.error(AuthConstant.FAIL_CREATE_JWT_TOKEN, e.getMessage());
            return null;
        }
    }


    @Deprecated
    public static boolean addResponseHeaderWithJWT(UserModel user) {
        if (user == null) {
            return false;
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletResponse response = servletRequestAttributes.getResponse();

        String jwt = JwtUtils.generateToken(new JwtUtils.UserInfo(user.getId(), user.getUsername(), user.getTokenSecret()));

        if (StringUtils.isBlank(jwt)) {
            return false;
        }

        response.setHeader(AuthConstant.JWT_HEADER_KEY, AuthConstant.JWT_PREFIX + jwt);
        return true;
    }

    public static UserInfo validateToken(String token) {
        var user = getUserInfo(token);
        String jwtSecret = generateJwtSecret(user);
        try {
            var algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (Exception e){
            log.error("{}", e);
        }
        // 调用第三方接口验证 token
        TokenMapper mapper = BeanCommon.getBean(TokenMapper.class);
        var x = mapper.first(new HumanCondition().filterEqual(TokenModel::getJwt, token)
                                     .orderBy(TokenModel::getPk, true));
        if(x == null){
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EXPIRED);
        }
        String loginToken = x.getLogonToken();
        ThirdSsoService thirdSsoService = BeanCommon.getBean(ThirdSsoService.class);
        if (!thirdSsoService.tokenCheck(loginToken)) {
            throw new MpRuntimeException(AuthErrorCode.TOKEN_EXPIRED);
        }
        return user;
    }

    public static Date getTokenExpiredTime(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        DecodedJWT jwt = JWT.decode(token);
        Date date = jwt.getExpiresAt();
        return date;
    }

    public static UserInfo getUserInfo(String token) {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(token);
        } catch (JWTDecodeException e) {
            log.info(AuthConstant.FAIL_DECODE_JWT_TOKEN, token, e.getMessage());
            throw new MpRuntimeException(AuthErrorCode.JWT_DECODE);
        }

        var user = new UserInfo();
        user.setId(jwt.getClaim(AuthConstant.JWT_CLAIM_ID).asString());
        user.setUsername(jwt.getClaim(AuthConstant.JWT_CLAIM_USERNAME).asString());
        user.setTokenSecret(jwt.getClaim(AuthConstant.JWT_CLAIM_KEY).asString());
        return user;
    }

    public static String shortenToken(String token) {
        String[] tokenParts = StringUtils.split(token, AuthConstant.SEPARATOR_CHAR_DOT);
        if (tokenParts.length > 0) {
            return tokenParts[tokenParts.length - 1];
        }
        return token;
    }


}
