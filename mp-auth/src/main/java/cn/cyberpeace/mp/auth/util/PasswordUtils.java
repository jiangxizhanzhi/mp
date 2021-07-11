package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;


@Component
@Slf4j
public class PasswordUtils {
    private static AuthConfig authConfig;

    private static Pbkdf2PasswordEncoder passwordEncoder;

    private static DES DES = SecureUtil.des(AuthConstant.PASSWORD_DES_KEY.getBytes());

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
                Field field = ReflectUtil.getField(Pbkdf2PasswordEncoder.class, "DEFAULT_ITERATIONS");
                Object value = ReflectUtil.getStaticFieldValue(field);
                iterations = (Integer) value;
            }
            if (Objects.isNull(hashWidth)) {
                Field field = ReflectUtil.getField(Pbkdf2PasswordEncoder.class, "DEFAULT_HASH_WIDTH");
                Object value = ReflectUtil.getStaticFieldValue(field);
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
       return DES.encryptHex(password);
    }

    public static String desDecrypt(String encryptHex) {
        return DES.decryptStr(encryptHex);
    }

}
