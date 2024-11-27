package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.mptool.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ThirdSsoUtil {
    public static String createToken(String AppID, String AppSecret) {
        String token = JWT.create().withIssuer(AppID).withIssuedAt(new Date()).withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(AppSecret));
        return token;
    }

    public static void verify(String token, String secretKey) {
        StrUtil.removePrefix(token, "Bearer").trim();
        DecodedJWT decode = JWT.decode(token);
        int jwtTimeout = 60000;
        try {
            Date date = decode.getIssuedAt();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey))
                    .acceptIssuedAt((long) (jwtTimeout / 1000)).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException var6) {
            log.error("Exception occurred:", var6);
        }
    }

    public static void syncRole(SyncRole syncRole) {
        ThirdSsoConfig config = BeanCommon.getBean(ThirdSsoConfig.class);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "Bearer " + createToken("appId", "appSecret"));
        try {
            var res = HttpUtils.sendHttpPost(config.getSyncroleurl(), JsonCommon.snakeStringify(syncRole), header);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    public static class SyncRole {
        private String appAppCode;
        private String appRoleStatus;
        private String appRoleCode;
        private String appRoleName;
        private String appRoleRemarks;
    }
}
