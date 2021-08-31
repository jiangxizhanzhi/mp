package cn.cyberpeace.mp.auth.service.sys;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.auth.dto.activation.ActivationStatus;
import cn.cyberpeace.mp.auth.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.service.FileService;
import cn.cyberpeace.mp.base.util.IdGenerator;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class SysActivationService {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private AuthConfig authConfig;

    public static final String ACTIVATION_FILE = "activation";

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

        log.info(authConfig.getActivationDir());
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

//        MD5 md5 = MD5.create();
//        md5.setSalt(salt.getBytes());
//        String md5Str = md5.digestHex(content);
//        log.info(md5Str);

        String encryptedContent = PasswordUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        log.info(finalContent);

        FileUtil.writeString(finalContent, new File(authConfig.getActivationDir() + File.separator + ACTIVATION_FILE), CharsetUtil.CHARSET_UTF_8);

    }


    private ActivationRequest parseContentFromDecrpytedFile() {
        File activationFile = new File(authConfig.getActivationDir() + File.separator + ACTIVATION_FILE);
        if (!activationFile.exists()) {
            log.error("activation file not exist!");
            return null;
        }

        List<String> lines = FileUtil.readUtf8Lines(activationFile);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        String content = lines.get(0);
        String salt = StringUtils.substring(content, 0, 8);
        String encryptedContent = StringUtils.substring(content, 8, content.length());
        String decryptedContent = PasswordUtils.desDecrypt(encryptedContent);
        String jsonStr = StringUtils.substring(decryptedContent, 8, decryptedContent.length());

        ActivationRequest activationRequest = JSON.parseObject(jsonStr, ActivationRequest.class);

        log.info("{}", activationRequest);
        return activationRequest;
    }

}
