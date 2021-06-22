package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharSet;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class PasswordUtils {

    private static Pbkdf2PasswordEncoder passwordEncoder;

    private static DES DES = SecureUtil.des(AuthConstant.PASSWORD_DES_KEY.getBytes());

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


    public static String desEncrypt(String password) {
       return DES.encryptHex(password);
    }

    public static String desDecrypt(String encryptHex) {
        return DES.decryptStr(encryptHex);
    }


    public static void main(String[] args) {
        String context = "Ab123#";
        String strB64 = DES.encryptBase64(context);
        String encryptHex = DES.encryptHex(context);
        String encryptHexUtf8 = DES.encryptHex(context, "UTF-8");
        String decrypt = DES.decryptStr(encryptHex);
        System.out.println(strB64);
        System.out.println(encryptHex);
        System.out.println(encryptHexUtf8);
        System.out.println(decrypt);
    }


}
