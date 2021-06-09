package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordUtils {

    private static Pbkdf2PasswordEncoder passwordEncoder;

    @SuppressWarnings({"java:S3010"})
    private PasswordUtils() {
        PasswordUtils.passwordEncoder = new Pbkdf2PasswordEncoder(AuthConstant.PASSWORD_SECRET);
    }

    public static Pbkdf2PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }


    public static boolean isPasswordStrengthLowLevel(String password) {
        int score = PasswordStrengthChecker.checkPasswordStrength(password);
        var level = PasswordStrengthChecker.getPasswordLevel(password);
        boolean isLowLevel = score < 4;
        log.info("password score:{}, level:{}", score, level);
        return isLowLevel;
    }


}
