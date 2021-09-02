package cn.cyberpeace.mp.auth.service.sys;

import cn.cyberpeace.mp.auth.config.ActivationConfig;
import cn.cyberpeace.mp.auth.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.auth.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.auth.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.IdGenerator;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private ActivationConfig activationConfig;

    private static ActivationResponse ACTIVATION_CONTENT = null;

    public static final String ACTIVATION_FILE = "activation";


    public void activate(ActivationRequest activationRequest) {
        log.info("activationRequest:{}", activationRequest);
        // 拼接json串，调用licenseService验证
        ActivationVerifyInfo verifyInfo = new ActivationVerifyInfo();
        verifyInfo.setDeadline(activationRequest.getDeadline());
        verifyInfo.setMachinecode(activationRequest.getMachineCode());

        // TODO setInfo
        Map<String, String> info = new HashMap<>();
        info.put("thj", "2");
        verifyInfo.setInfo(info);

        String verifyStr = JSON.toJSONString(verifyInfo);
        String verifyCode = activationRequest.getVerifyCode();

        boolean success = licenseService.verify(verifyStr, verifyCode);

        log.info("activationDir:{}", activationConfig.getLicenseDir());
//        File activationFile = new File();
        // 验证通过，加载内存激活配置项目，生成加密文件存储磁盘
        if (success) {
            generateEncryptedFile(activationRequest);
        } else {
            log.error("验证失败！");
        }

    }


    public ActivationResponse status() {

        if (Objects.isNull(ACTIVATION_CONTENT)) {
            ActivationResponse activationResponse = parseContentFromDecrpytedFile();
            log.info("{}", activationResponse);
            ACTIVATION_CONTENT = activationResponse;
        }

        return ACTIVATION_CONTENT;
    }


    private void generateEncryptedFile(ActivationRequest activationRequest) {
        log.info("generate encrypted file activationRequest:{}", activationRequest);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        Integer deadline = activationRequest.getDeadline();
        DateTime dateTime = DateTime.now().plusMonths(deadline);
        String expiredDate = dateTime.toString("yyyy-MM-dd");
        activationResponse.setExpiredDate(expiredDate);

        String salt = IdGenerator.generateRandomStrNum(8);
        String jsonStr = JSON.toJSONString(activationResponse);
        String content = salt + jsonStr;
        log.info("salt:{}", salt);

        MD5 md5 = MD5.create();
        md5.setSalt(salt.getBytes());
        String md5Str = md5.digestHex(content);
        log.info("md5Str:{}", md5Str);

        String encryptedContent = PasswordUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        String filePath = activationConfig.getLicenseDir() + File.separator + ACTIVATION_FILE;
        log.info("finalContent:{}", finalContent);

        FileUtil.writeString(finalContent, new File(filePath), CharsetUtil.CHARSET_UTF_8);
        log.info("activation file has been generated! filePath:{}", filePath);
    }


    private ActivationResponse parseContentFromDecrpytedFile() {
        File activationFile = new File(activationConfig.getLicenseDir() + File.separator + ACTIVATION_FILE);
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

        ActivationResponse activationResponse = JSON.parseObject(jsonStr, ActivationResponse.class);
        log.info("salt:{}", salt);
        log.info("activationContent:{}", activationResponse);
        return activationResponse;
    }

}
