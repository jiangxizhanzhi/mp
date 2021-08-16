package cn.cyberpeace.mp.auth.service.sys;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.auth.dto.activation.ActivationStatus;
import cn.cyberpeace.mp.auth.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.util.IdGenerator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharSetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.Charset;

@Service
@Slf4j
public class SysActivationService {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private AuthConfig authConfig;


    private void init() {
        // 系统启动，初始化加密配置文件，并加载到内存中
    }


    public void activate(ActivationRequest activationRequest) {
        // 拼接json串，调用licenseService验证
        ActivationVerifyInfo verifyInfo = new ActivationVerifyInfo();
        verifyInfo.setDeadline(activationRequest.getDeadline());
        verifyInfo.setMachinecode(activationRequest.getMachinecode());

        String verifyStr = JSON.toJSONString(verifyInfo);
        String verifyCode = activationRequest.getVerifyCode();

        generateEncryptedFile(activationRequest);

        parseContentFromDecrpytedFile();

        boolean success = licenseService.verify(verifyStr, verifyCode);

        log.info(authConfig.getActivationFile());
//        File activationFile = new File();
        // 验证通过，加载内存激活配置项目，生成加密文件存储磁盘
        if (success) {

            // todo
        }
    }


    public ActivationStatus status() {


        return null;
    }


    private void generateEncryptedFile(ActivationRequest activationRequest) {
        String salt = IdGenerator.generateRandomStrNum(8);
        String jsonStr = JSON.toJSONString(activationRequest);
        String content = salt + jsonStr;
        log.info(salt);

        MD5 md5 = MD5.create();
        md5.setSalt(salt.getBytes());
        String md5Str = md5.digestHex(content);
        log.info(md5Str);

        String encryptedContent = PasswordUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        log.info(finalContent);

        FileUtil.writeString(finalContent, new File("/Users/sternberg/tmp/0811/activation"), CharsetUtil.CHARSET_UTF_8);
        FileUtil.writeString(md5Str, new File("/Users/sternberg/tmp/0811/md5"), CharsetUtil.CHARSET_UTF_8);

    }


    private void parseContentFromDecrpytedFile() {
        String content = "9X89X5u62ed2f8bcc98f38162eaff527c9fac34916af8b1897216d41419880e2d33f65bba6344cf44a4d0d834612cb0e1577f5613173984b38dc8ce267a38055178b7087";
        String salt = StringUtils.substring(content, 0, 8);
        String encryptedContent = StringUtils.substring(content, 8, content.length());
        String decryptedContent = PasswordUtils.desDecrypt(encryptedContent);
        String jsonStr = StringUtils.substring(decryptedContent, 8, decryptedContent.length());

        ActivationRequest activationRequest = JSON.parseObject(jsonStr, ActivationRequest.class);

        log.info("{}", activationRequest);
    }

}
