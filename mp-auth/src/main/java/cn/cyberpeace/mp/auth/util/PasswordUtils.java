package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class PasswordUtils {
    private static AuthConfig authConfig;

    private static Pbkdf2PasswordEncoder passwordEncoder;

    @SuppressWarnings({"java:S3010"})
    private PasswordUtils(AuthConfig authConfig) {
        PasswordUtils.authConfig = authConfig;

        Integer iterations = null;
        Integer hashWidth = null;
        if (Objects.nonNull(authConfig.getAuthenticateEncoderHashWidth()) && Objects.nonNull(authConfig.getAuthenticateEncoderIterations())) {
            iterations = authConfig.getAuthenticateEncoderIterations();
            hashWidth = authConfig.getAuthenticateEncoderHashWidth();
        }
        if (Objects.isNull(iterations) && Objects.isNull(hashWidth)) {
            PasswordUtils.passwordEncoder = new Pbkdf2PasswordEncoder(AuthConstant.PASSWORD_SECRET);
        } else {
            if (Objects.isNull(iterations)) {
                Object value = ClassUtils.getStaticFieldValue(Pbkdf2PasswordEncoder.class, "DEFAULT_ITERATIONS");
                iterations = (Integer) value;
            }
            if (Objects.isNull(hashWidth)) {
                Object value = ClassUtils.getStaticFieldValue(Pbkdf2PasswordEncoder.class, "DEFAULT_HASH_WIDTH");
                hashWidth = (Integer) value;
            }
            PasswordUtils.passwordEncoder = new Pbkdf2PasswordEncoder(AuthConstant.PASSWORD_SECRET, iterations, hashWidth);
        }
    }

    public static Pbkdf2PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }


    public static boolean isPasswordInvalid(String password) {
        return !PasswordStrengthChecker.isValidPassword(password);
    }

    public static String desEncrypt(String password) {
       return EncryptUtils.desEncrypt(password);
    }

    public static String desDecrypt(String encryptHex) {
        return EncryptUtils.desDecrypt(encryptHex);
    }

}
