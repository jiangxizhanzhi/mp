package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationModuleItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationResourceItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.request.activation.ActivationRequest;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import cn.cyberpeace.mp.frontadmin.util.EncryptUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.util.ExecutingCommand;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private FrontAdminConfig frontAdminConfig;

    @Autowired
    private ActivMenuService activMenuService;

    @Autowired
    private SysCtlService sysCtlService;

    @Autowired
    private SysActivCtlService sysActivCtlService;

    /**
     * 激活
     *
     * @param activationRequest
     */
    public void activate(ActivationRequest activationRequest) {
        log.info("activationRequest:{}", activationRequest);

        ActivationResponse content = status();
        SysActivCtlModel validModel = sysCtlService.getValidCtlModel();
        boolean isSysActivited = false;
        if (Objects.nonNull(content) && Objects.equals(AdminConstant.STATUS_ACTIVITED, content.getIsActived())) {
            log.error("content status is actived!");
            isSysActivited = true;
        }
        if (Objects.nonNull(validModel)) {
            log.error("valid model existed!");
            isSysActivited = true;
        }
        if (isSysActivited) {
            log.error("isSysActivited:{}, system activation status not cleanup! please Check!", isSysActivited);
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_STATUS_ERROR);
        }


        SysActivCtlModel unusedActivCtlModel = sysCtlService.getUnusedActivCtlModel();
        if (Objects.isNull(unusedActivCtlModel)) {
            log.error("unused activctl not existed!");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_ERROR);
        }

        String localSysUUID = querySysUUID();
        if (!Objects.equals(activationRequest.getMachineCode(), localSysUUID)) {
            log.error("sysuuid not match! request machine code is:{}, localSysUUID is:{}", activationRequest.getMachineCode(), localSysUUID);
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_SYSUUID_NOTMATCH);
        }

        // 拼接json串，调用licenseService验证
        ActivationVerifyInfo verifyInfo = new ActivationVerifyInfo();
        verifyInfo.setDeadline(activationRequest.getDeadline());
        verifyInfo.setMachinecode(activationRequest.getMachineCode());

        // 自定义信息
        Map<String, String> infoMap = convertActivationItems2CustomVerifyInfoMap(activationRequest.getItems());
        verifyInfo.setInfo(infoMap);

        String verifyStr = JSON.toJSONString(verifyInfo);
        String verifyCode = activationRequest.getVerifyCode();

        // 校验激活码和激活配置项，是否匹配
        boolean success = licenseService.verify(verifyStr, verifyCode);

        if (success) {
            // 校验通过，生成加密文件存储磁盘
            ACTIVATION_CONTENT = null;
            generateEncryptedFile(activationRequest);

            // 更新数据库激活限制配置
            processActivCtlAfterActivationSuccess(activationRequest);

            ACTIVATION_CONTENT = status();
            log.info("ACTIVATION_CONTENT:{}", ACTIVATION_CONTENT);
            activMenuService.resetMenusVisibility(ACTIVATION_CONTENT);
            activMenuService.resetOptionsVisibility(ACTIVATION_CONTENT);
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
            log.info("activationResponse from file: {}", activationResponse);
            ACTIVATION_CONTENT = activationResponse;
        }

        log.info("before processActived status: {}", ACTIVATION_CONTENT);
        processActivedStatus();

        return ACTIVATION_CONTENT;
    }


    public String getValueByKey(String key) {
        ActivationResponse activationResponse = status();
        if (Objects.isNull(activationResponse) || !Objects.equals(AdminConstant.STATUS_ACTIVITED, activationResponse.getIsActived())) {
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
    /**
     * 查询系统UUID
     *
     * @return
     */
    public String querySysUUID() {
        try {
            status();
            SysActivCtlModel sysActivCtlModel = null;
            if (Objects.isNull(ACTIVATION_CONTENT) || Objects.equals(AdminConstant.STATUS_NOT_ACTIVITED, ACTIVATION_CONTENT.getIsActived())) {
                sysActivCtlModel = sysCtlService.generateOrGetUnusedActivCtlModel();
            } else {
                sysActivCtlModel = sysCtlService.getValidCtlModel();
            }
            String suffix = sysActivCtlModel.getSuffix();

            String uuid = "";
            // If root privileges this will work
            for (String checkLine : ExecutingCommand.runNative("dmidecode -s system-uuid")) {
                if (StringUtils.isNotBlank(checkLine)) {
                    uuid = checkLine;
                    log.info("origin uuid is {}", uuid);
                }
            }

            // todo
            //uuid = "aaaa-bbbb-cccc-dddd";
            uuid = uuid + "-" + suffix;
            return uuid;
        } catch (Exception e) {
            log.error("failed to querySysUUID ", e);
            return "";
        }
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

    /**
     * 判断是否过期有两种方法，1. 过期日期超限  2.系统运行时长超限
     */
    private void processActivedStatus() {
        if (Objects.nonNull(ACTIVATION_CONTENT)) {
            /*
            //默认状态为未激活
            LocalDate expireDate = LocalDate.parse(ACTIVATION_CONTENT.getExpiredDate(), dateFormatter);
            expireDate = expireDate.plusDays(1);

            // 运行时长超限
            boolean isRunSecondsExceeded = false;
            // 过期日期到期
            boolean isExpiredDateExceeded = !LocalDate.now().isBefore(expireDate);

            SysActivCtlModel validActivCtlModel = sysCtlService.getValidCtlModel();
            log.info("validActivCtlModel:{}", validActivCtlModel);
            if (Objects.isNull(validActivCtlModel)) {
                ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_NOT_ACTIVITED);
                return;
            }

            if (!isExpiredDateExceeded) {
                // 过期日期未到，继续判断运行时长
                if (StringUtils.isBlank(validActivCtlModel.getFirstActiveDate())) {
                    log.info("System is not activated right now!");
                    // 未激活，但是CONTENT有值，说明是升级的场景，则还是以CONTENT内容为主
                } else {
                    String sysLifeTimeSecondsStr = validActivCtlModel.getSysLifetime();
                    String currentRunSecondsStr = validActivCtlModel.getSysRunSeconds();
                    if (StringUtils.isNotBlank(sysLifeTimeSecondsStr) && StringUtils.isNotBlank(currentRunSecondsStr)) {
                        Long currentRunSeconds = Long.valueOf(EncryptUtils.desDecrypt(currentRunSecondsStr));
                        Long sysLifeTimeSeconds = Long.valueOf(EncryptUtils.desDecrypt(sysLifeTimeSecondsStr));
                        log.info("currentRunSeconds:{}, sysLifeTimeSeconds:{}", currentRunSeconds, sysLifeTimeSeconds);
                        if (currentRunSeconds > sysLifeTimeSeconds) {
                            isRunSecondsExceeded = true;
                        }
                    }

                }
            }

            if (isExpiredDateExceeded || isRunSecondsExceeded) {
                log.info("isExpiredDateExceeded:{}, isRunSecondsExceeded:{}", isExpiredDateExceeded, isRunSecondsExceeded);
                ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_NOT_ACTIVITED);

                // 过期状态，更新激活限制项配置
                SysActivCtlModel updateActivCtlModel = new SysActivCtlModel();
                updateActivCtlModel.setId(validActivCtlModel.getId());
                updateActivCtlModel.setStatus(SysActivCtlModel.STATUS_INVALID);
                sysActivCtlService.update(updateActivCtlModel);
            }

            ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_ACTIVITED);
            */
            ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_ACTIVITED);
        }
    }

    /**
     * 生成加密文件，存磁盘
     * @param activationRequest
     */
    private void generateEncryptedFile(ActivationRequest activationRequest) {
        String verifyCode = activationRequest.getVerifyCode();
        String encryptedVerifyCode = EncryptUtils.desEncrypt(verifyCode);
        log.info("generate encrypted file activationRequest:{}", activationRequest);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        String expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        activationResponse.setExpiredDate(expiredDateStr);
        activationResponse.setVerifyCode(encryptedVerifyCode);

        String salt = RandomUtil.randomString(8);
        String jsonStr = JSON.toJSONString(activationResponse);
        String content = salt + jsonStr;

        MD5 md5 = MD5.create();
        md5.setSalt(salt.getBytes());
        String md5Str = md5.digestHex(content);

        String encryptedContent = EncryptUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        String filePath = frontAdminConfig.getLicenseDir() + File.separator + ACTIVATION_FILE;
        log.info("finalContent:{}", finalContent);

        FileUtil.writeString(finalContent, new File(filePath), CharsetUtil.CHARSET_UTF_8);
        log.info("activation file has been generated! filePath:{}", filePath);
    }


    private ActivationResponse parseContentFromDecrpytedFile() {
        File activationFile = new File(frontAdminConfig.getLicenseDir() + File.separator + ACTIVATION_FILE);
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

    private String getExpiredDateStr(Integer months) {
        LocalDate localDate = LocalDate.now().plusMonths(months);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }


    /**
     * 激活成功，变更数据库激活限制相关配置信息
     * @param activationRequest
     */
    private void processActivCtlAfterActivationSuccess(ActivationRequest activationRequest) {
        log.info("update activ ctl config info");
        SysActivCtlModel oldSysActivCtlModel = sysCtlService.getUnusedActivCtlModel();
        SysActivCtlModel updateSysActivCtlModel = new SysActivCtlModel();
        updateSysActivCtlModel.setId(oldSysActivCtlModel.getId());

        // 考虑用时间戳替代
        LocalDateTime expiredTime = LocalDateTime.now().plusMonths(activationRequest.getDeadline()).plusDays(1);
        Duration duration = Duration.between(LocalDateTime.now(), expiredTime);
        Long lifeTimeSeconds = duration.toSeconds();
        updateSysActivCtlModel.setSysLifetime(EncryptUtils.desEncrypt(String.valueOf(lifeTimeSeconds)));

        String activeDateStr = LocalDate.now().format(dateFormatter);
        String expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        String encryptedLicenseCode = EncryptUtils.desEncrypt(activationRequest.getVerifyCode());

        if (StringUtils.isNotBlank(oldSysActivCtlModel.getLicenseCode())) {
            // 不是首次激活
            String oldLicenseCode = oldSysActivCtlModel.getLicenseCode();

            // 1. 相同激活码 last_active_date，active_count变动
            if (Objects.equals(oldLicenseCode, encryptedLicenseCode)) {
                updateSysActivCtlModel.setActiveCount(oldSysActivCtlModel.getActiveCount() + 1);
                processActivCtlWhenReActiveWithOldLicense(updateSysActivCtlModel, activeDateStr);
            } else {
                // 2. 不同激活码，当做一次全新的激活，先将其他激活状态都设置成status=0
                processActivCtlWhenReActiveWithNewLicense(activeDateStr, expiredDateStr, encryptedLicenseCode);
            }
        } else {
            // 首次激活，添加first_active_date, last_active_date, expire_date, active_count;
            sysActivCtlService.invalidOtherExceptOne(updateSysActivCtlModel.getId());
            processActivCtlWhenFirstActiveWithNewLicense(updateSysActivCtlModel, activeDateStr, expiredDateStr, encryptedLicenseCode);
        }
    }

    /**
     * 用旧的license再次激活
     * last_active_date，active_count变动
     *
     * @param activeDateStr
     */
    private void processActivCtlWhenReActiveWithOldLicense(SysActivCtlModel updateSysActivCtlModel, String activeDateStr) {
        // 不更新first_active_date
        log.info("activeDateStr:{}", activeDateStr);
        updateSysActivCtlModel.setFirstActiveDate(null);
        updateSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        sysActivCtlService.update(updateSysActivCtlModel);
    }


    /**
     * 用新的license再次激活
     * 当做一次全新的激活，先将其他激活状态都设置成status=0
     * @param activeDateStr
     * @param expiredDateStr
     */
    private void processActivCtlWhenReActiveWithNewLicense(String activeDateStr, String expiredDateStr, String encryptedLicenseCode) {
        log.info("activeDateStr:{}, expiredDateStr:{}", activeDateStr, expiredDateStr);
        SysActivCtlModel newSysActivCtlModel = new SysActivCtlModel();
        Long currTs = System.currentTimeMillis();
        newSysActivCtlModel.setSysTimestamp(String.valueOf(currTs));
        String suffix = RandomUtil.randomString(6);
        newSysActivCtlModel.setSuffix(suffix);
        newSysActivCtlModel.setFirstActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        newSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        newSysActivCtlModel.setLicenseCode(encryptedLicenseCode);
        newSysActivCtlModel.setExpireDate(EncryptUtils.desEncrypt(expiredDateStr));
        newSysActivCtlModel.setActiveCount(1);

        ObjServiceResult<SysActivCtlModel> objResult  = sysActivCtlService.create(newSysActivCtlModel);
        if (Objects.nonNull(objResult.getSource())) {
            log.info("Generate default activCtlModel success! {}", objResult.getSource().getObj());
            newSysActivCtlModel = objResult.getSource().getObj();
            sysActivCtlService.invalidOtherExceptOne(newSysActivCtlModel.getId());
        }
    }

    /**
     * 用新的license第一次激活
     * @param updateSysActivCtlModel
     * @param activeDateStr
     * @param expiredDateStr
     */
    private void processActivCtlWhenFirstActiveWithNewLicense(SysActivCtlModel updateSysActivCtlModel, String activeDateStr, String expiredDateStr, String encryptedLicenseCode) {
        log.info("activeDateStr:{}, expiredDateStr:{}", activeDateStr, expiredDateStr);
        updateSysActivCtlModel.setFirstActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        updateSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        updateSysActivCtlModel.setExpireDate(EncryptUtils.desEncrypt(expiredDateStr));
        updateSysActivCtlModel.setLicenseCode(encryptedLicenseCode);
        updateSysActivCtlModel.setSysTimestamp(String.valueOf(System.currentTimeMillis()));
        updateSysActivCtlModel.setActiveCount(1);
        updateSysActivCtlModel.setStatus(SysActivCtlModel.STATUS_VALID);
        sysActivCtlService.update(updateSysActivCtlModel);
    }




    private ActivationResponse mockActivationContent() {
        ActivationResponse content = new ActivationResponse();
        content.setExpiredDate("2022-05-26");
        content.setIsActived(AdminConstant.STATUS_ACTIVITED);
        content.setEdition(1);


        List<ActivationItem> list = new ArrayList<>();
        ActivationItem item1 = new ActivationItem();
        item1.setKey("LCS-FUNC-CR-SHIXUN");
        item1.setValue("1");
        item1.setType(1);
        list.add(item1);

        ActivationItem item2 = new ActivationItem();
        item2.setKey("LCS-FUNC-CR-JINGSAI");
        item2.setValue("1");
        item2.setType(1);
        list.add(item2);

        ActivationItem item3 = new ActivationItem();
        item3.setKey("LCS-FUNC-CR-HUOTILOUDONG");
        item3.setValue("1");
        item3.setType(1);
        list.add(item3);

        ActivationItem item4 = new ActivationItem();
        item4.setKey("LCS-FUNC-CR-ZHISHIKU");
        item4.setValue("1");
        item4.setType(1);
        list.add(item4);

        ActivationItem item5 = new ActivationItem();
        item5.setKey("LCS-FUNC-CR-ZONGHEXUNLIAN");
        item5.setValue("1");
        item5.setType(1);
        list.add(item5);

        ActivationItem item6 = new ActivationItem();
        item6.setKey("LCS-FUNC-CR-HONGLANDUIKANG");
        item6.setValue("1");
        item6.setType(1);
        list.add(item6);

        ActivationItem item7 = new ActivationItem();
        item7.setKey("LCS-FUNC-CR-SHENTOUSAI");
        item7.setValue("1");
        item7.setType(1);
        list.add(item7);

        ActivationItem item8 = new ActivationItem();
        item8.setKey("LCS-FUNC-CR-YUNWEISAI");
        item8.setValue("1");
        item8.setType(1);
        list.add(item8);

        ActivationItem item9 = new ActivationItem();
        item9.setKey("LCS-FUNC-CR-CEPING");
        item9.setValue("1");
        item9.setType(1);
        list.add(item9);

        ActivationItem item10 = new ActivationItem();
        item10.setKey("LCS-RES-CR-ACTIVENODES");
        item10.setValue("1000");
        item10.setType(2);
        list.add(item10);

        ActivationItem item11 = new ActivationItem();
        item11.setKey("LCS-RES-CR-EDITNODES");
        item11.setValue("100");
        item11.setType(2);
        list.add(item11);

        content.setItems(list);

        return content;

    }


}
