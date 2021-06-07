package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Slf4j
public class JwtUtils {
    private static final long JWT_EXPIRATION = 24 * 3600 * 1000L;

    private JwtUtils() {}

    private static String generateJwtSecret() {
        String secret = AuthConstant.JWT_SECRET;
        var algorithm = "HmacSHA256";
        final Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(AuthConstant.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8), algorithm));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Generate Jwt Secret error: {}", e.getMessage());
            return AuthConstant.JWT_SECRET_KEY;
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
        String jwtSecret = generateJwtSecret();
        var expireDate = new Date(System.currentTimeMillis() + JWT_EXPIRATION);
        try {
            var algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.create().withExpiresAt(expireDate).withClaim("id", user.getId())
                    .withClaim("username", user.getUsername()).sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Create jwt token error: {}", e.getMessage());
            return null;
        }
    }

    public static UserInfo validateToken(String token) throws TokenExpiredException {
        var user = getUserInfo(token);
        if (user == null) {
            return null;
        }

        String jwtSecret = generateJwtSecret();
        try {
            var algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw e;
        } catch (JWTVerificationException e) {
            log.error("Verify jwt token error: {}", e.getMessage());
            return null;
        }

        return user;
    }

    public static UserInfo getUserInfo(String token) {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(token);
        } catch (JWTDecodeException e) {
            log.error("Decode jwt token {} error: {}", token, e.getMessage());
            return null;
        }

        var user = new UserInfo();
        user.setId(jwt.getClaim("id").asString());
        user.setUsername(jwt.getClaim("username").asString());
        user.setTokenSecret(jwt.getClaim("key").asString());
        return user;
    }

}
