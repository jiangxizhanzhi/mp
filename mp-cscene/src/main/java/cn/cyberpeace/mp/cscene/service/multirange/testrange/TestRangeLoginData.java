package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.StringHttpData;
import cn.cyberpeace.mp.frontadmin.util.EncryptUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class TestRangeLoginData {
    private String userName = "admin";
    private String originPassword = "xctfoj01";

    private LocalDateTime authDataTime;
    private String authToken;

    private String getEncodePassword() {
        return PasswordUtils.desDecrypt(originPassword);
    }

    public String getAuthToken() {
        if (Objects.nonNull(authDataTime) && Objects.nonNull(authToken) && authDataTime.isAfter(LocalDateTime.now().minusMinutes(10))) {
            if (tokenIsValid()) {
                authDataTime = LocalDateTime.now();
                return authToken;
            } else {
                authToken = getAuthTokenFromTestRange();
                authDataTime = LocalDateTime.now();
                return authToken;
            }
        } else {
            authToken = getAuthTokenFromTestRange();
            authDataTime = LocalDateTime.now();
            return authToken;
        }
    }

    private String getAuthTokenFromTestRange() {
        String result = TestRangeClientUtil.post(TestRangeApiUrl.AUTH_TOKEN, new StringHttpData(JsonCommon.snakeStringify(
                        new LoginData(userName, getEncodePassword()))), null, null, 10000)
                .getResult();
        JSONObject jsonObject = JSONObject.parseObject(result);
        try {
            String token = jsonObject.getJSONObject("data").getString("token");
            return token;
        } catch (Exception e) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("获取authToken异常"));
        }
    }

    private boolean tokenIsValid() {
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", "JWT " + authToken);
        try {
            HttpResult httpResult = TestRangeClientUtil.get(TestRangeApiUrl.AUTH_HEALTH_CHECK, null, headers, 10000);
            if (httpResult.isSuccess()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    @Data
    private static class LoginData {
        private String username;
        private String password;
        private String verifyCode;

        public LoginData(String username, String encodePassword) {
            this.username = username;
            this.password = encodePassword;
            this.verifyCode = "";
        }
    }
}
