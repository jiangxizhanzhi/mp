package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.front.admin.config.ActivationConfig;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationModuleItem;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationRequest;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResourceItem;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import cn.cyberpeace.mp.front.admin.service.sys.activmenu.ActivMenuService;
import cn.cyberpeace.mp.front.admin.util.EncryptUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.util.ExecutingCommand;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description: 系统激活相关service
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class SysActivationService {

    private static ActivationResponse ACTIVATION_CONTENT = null;

    public static final String ACTIVATION_FILE = "activation";

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private ActivationConfig activationConfig;

    @Autowired
    private ActivMenuService activMenuService;

    @Autowired
    private SysCtlService sysCtlService;

    /**
     * 激活
     *
     * @param activationRequest
     */
    public void activate(ActivationRequest activationRequest) {
        log.info("activationRequest:{}", activationRequest);
        String sysUUID = querySysUUID();
        if (!Objects.equals(activationRequest.getMachineCode(), sysUUID)) {
            log.error("sysuuid not match! requested uuid is:{}, expected uuid is:{}", activationRequest.getMachineCode(), sysUUID);
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_SYSUUID_NOTMATCH);
        }

        // 拼接json串，调用licenseService验证
        ActivationVerifyInfo verifyInfo = new ActivationVerifyInfo();
        verifyInfo.setDeadline(activationRequest.getDeadline());
        verifyInfo.setMachinecode(activationRequest.getMachineCode());

        // 自定义信息
        Map<String, String> infoMap = convertActivationItems2CustomVerifyInfoMap(activationRequest.getItems());
        log.info("info:{}", infoMap);
        verifyInfo.setInfo(infoMap);

        String verifyStr = JSON.toJSONString(verifyInfo);
        String verifyCode = activationRequest.getVerifyCode();

        // 校验激活码和激活配置项，是否匹配
        boolean success = licenseService.verify(verifyStr, verifyCode);

        log.info("activationDir:{}", activationConfig.getLicenseDir());
        if (success) {
            // 校验通过，生成加密文件存储磁盘
            ACTIVATION_CONTENT = null;
            generateEncryptedFile(activationRequest);
            log.info("encrypted file generated success!");
            ACTIVATION_CONTENT = status();
            log.info("ACTIVATION_CONTENT:{}", ACTIVATION_CONTENT);
            activMenuService.resetMenusVisibility(ACTIVATION_CONTENT);
        } else {
            log.error("license verify failed！");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
        }

    }

    /**
     * 查询系统激活状态
     * @return
     */
    public ActivationResponse status() {

        if (Objects.isNull(ACTIVATION_CONTENT)) {
            ActivationResponse activationResponse = parseContentFromDecrpytedFile();
            log.info("{}", activationResponse);
            ACTIVATION_CONTENT = activationResponse;
        }

        if (Objects.nonNull(ACTIVATION_CONTENT)) {
            DateTime dateTime = new DateTime(ACTIVATION_CONTENT.getExpiredDate());
            if (DateTime.now().isBefore(dateTime.plusDays(1))) {
                ACTIVATION_CONTENT.setIsActived(1);
            } else {
                ACTIVATION_CONTENT.setIsActived(0);
            }
        }

        log.info("{}", ACTIVATION_CONTENT);
        return ACTIVATION_CONTENT;
    }

    public String getValueByKey(String key) {
        log.info(key);
        ActivationResponse activationResponse = status();
        if (!Objects.equals(1, activationResponse.getIsActived())) {
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_ERROR);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expiredDate = LocalDate.parse(activationResponse.getExpiredDate(), dtf);
        LocalDate now = LocalDate.now();

        if (expiredDate.isBefore(now)) {
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_EXPIRED);
        }

        List<ActivationItem> items = activationResponse.getItems();
        Optional<ActivationItem> itemOptional = items.stream().filter(i -> i.getKey().equals(key)).findFirst();
        if (!itemOptional.isPresent()) {
            log.error("config key {} not existed!", key);
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_NOT_EXISTED.format(key));
        }

        return itemOptional.get().getValue();
    }


    /**
     * 查询系统UUID
     *
     * @return
     */
    public String querySysUUID() {
        // If root privileges this will work
        SysActivCtlModel sysActivCtlModel = sysCtlService.getDefaultSysActivCtl();
        Long activeTs = sysActivCtlModel.getActiveTimestamp();
        String activTsStr = activeTs.toString();
        activTsStr = activTsStr.substring(activTsStr.length() - 6, activTsStr.length());

        String uuid = "";
        for (String checkLine : ExecutingCommand.runNative("dmidecode -s system-uuid")) {
            if (StringUtils.isNotBlank(checkLine)) {
                uuid = checkLine;
                log.info("origin uuid is {}", uuid);
            }
        }

        uuid = uuid + "-" + activTsStr;
        return uuid;
    }

    @Deprecated
    public String generateCustomizedVerifyJSONInfo(List<ActivationModuleItem> moduleItems, List<ActivationResourceItem> resourceItems) {
        JSONObject jsonObject = new JSONObject();

        JSONArray moduleArray = new JSONArray();
        moduleItems.forEach(m -> {
            JSONObject module = new JSONObject();
            module.put("identifier", m.getIdentifier());
            module.put("name", m.getName());
            moduleArray.add(module);
        });

        JSONArray resourceArray = new JSONArray();
        resourceItems.forEach(r -> {
            JSONObject resource = new JSONObject();
            resource.put("identifier", r.getIdentifier());
            resource.put("module", r.getModule());
            resource.put("restrictName", r.getRestrictName());
            resource.put("maxValue", r.getMaxValue());
            resourceArray.add(resource);
        });

        jsonObject.put("modules", moduleArray);
        jsonObject.put("resources", resourceArray);

        String jsonStr = jsonObject.toJSONString();
        return jsonStr;
    }

    private void generateEncryptedFile(ActivationRequest activationRequest) {
        log.info("generate encrypted file activationRequest:{}", activationRequest);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        Integer deadline = activationRequest.getDeadline();
        DateTime dateTime = DateTime.now().plusMonths(deadline);
        String expiredDate = dateTime.toString("yyyy-MM-dd");
        activationResponse.setExpiredDate(expiredDate);

        String salt = RandomStringUtils.random(8);
        String jsonStr = JSON.toJSONString(activationResponse);
        String content = salt + jsonStr;
        log.info("salt:{}", salt);

        MD5 md5 = MD5.create();
        md5.setSalt(salt.getBytes());
        String md5Str = md5.digestHex(content);
        log.info("md5Str:{}", md5Str);

        String encryptedContent = EncryptUtils.desEncrypt(content);
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
        String decryptedContent = EncryptUtils.desDecrypt(encryptedContent);
        String jsonStr = StringUtils.substring(decryptedContent, 8, decryptedContent.length());

        ActivationResponse activationResponse = JSON.parseObject(jsonStr, ActivationResponse.class);
        log.info("activationContent:{}", activationResponse);
        return activationResponse;
    }


    private Map<String, String> convertActivationItems2CustomVerifyInfoMap(List<ActivationItem> items) {
        ParamCheck.notEmpty(items, AuthBasei18nCode.REQUEST_PARAMS);
        Map<String, String> map = new HashMap<>();
        items.forEach(i -> {
            map.put(i.getKey(), i.getValue());
        });
        return map;
    }

}
