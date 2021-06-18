package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.util.PasswordStrengthChecker;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "TEST", tags = "AUTH-TEST")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/test")
public class AuthTestController {

    @PostMapping("/desEncryptPwd")
    public String desEncryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desEncrypt(pwd);
    }

    @PostMapping("/desDecryptPwd")
    public String desDecryptPwd(@RequestParam String pwd) {
        return PasswordUtils.desDecrypt(pwd);
    }


    @PostMapping("/pwdscore")
    public Map<String, Object> pwdScore(@RequestParam String pwd) {
        int score = PasswordStrengthChecker.checkPasswordStrength(pwd);
        var level = PasswordStrengthChecker.getPasswordLevel(pwd);
        boolean isLowLevel = PasswordUtils.isPasswordStrengthLowLevel(pwd);

        Map<String, Object> map = new HashMap();
        map.put("score", score);
        map.put("level", level);
        map.put("isLowLevel", isLowLevel);
        return map;
    }


}
