package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.EncryptUtils;
import cn.cyberpeace.mp.auth.util.FileUtils;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.frontadmin.config.FrontAdminConfig;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationModuleItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationResourceItem;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationVerifyInfo;
import cn.cyberpeace.mp.frontadmin.enums.ActivationItemKeyEnums;
import cn.cyberpeace.mp.frontadmin.enums.ModuleEnums;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import cn.cyberpeace.mp.frontadmin.request.activation.ActivationRequest;
import cn.cyberpeace.mp.frontadmin.response.ModuleInfoResponse;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import cn.cyberpeace.mp.frontadmin.util.RandomUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.util.ExecutingCommand;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final Integer TRIAL_DAYS = 7;

    public static final String TRIAL_USE_SYS_CONFIG = "trial_use_sys_config";

    // 竞赛相关激活项目
    private static List<String> AD_MODULE_KEYS = Arrays.asList(ActivationItemKeyEnums.JINGSAI.getCode(), ActivationItemKeyEnums.SHENTOUSAI.getCode(), ActivationItemKeyEnums.YUNWEISAI.getCode());
    // 实训相关激活项
    private static List<String> OJ_MODULE_KEYS = Arrays.asList(ActivationItemKeyEnums.SHIXUN.getCode());
    // 靶场相关激活项
    private static List<String> CR_MODULE_KEYS = Arrays.asList(ActivationItemKeyEnums.HUOTILOUDONG.getCode(),ActivationItemKeyEnums.ZHISHIKU.getCode(),ActivationItemKeyEnums.ZONGHEXUNLIAN.getCode(),ActivationItemKeyEnums.HONGLANDUIKANG.getCode(),ActivationItemKeyEnums.CEPING.getCode());

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

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 激活
     *
     * @param activationRequest
     */
    public void activate(ActivationRequest activationRequest) {
        ActivationResponse content = status();
        boolean isSysActivited = false;
        if (Objects.nonNull(content) && Objects.equals(AdminConstant.STATUS_ACTIVITED, content.getIsActived())) {
            log.error("content status is actived!");
            isSysActivited = true;
        }

        synchronized (this) {
            if (!isSysActivited) {
                SysActivCtlModel validModel = sysCtlService.getValidCtlModel();
                if (Objects.nonNull(validModel)) {
                    log.error("valid model existed!");
                    isSysActivited = true;
                }
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
            boolean success = false;
            if (activationRequest.getTrialFlag()) {
                HumanCondition condition = new HumanCondition().filterEqual(SysConfigModel::getKeyName, TRIAL_USE_SYS_CONFIG);
                SysConfigModel sysConfigModel = sysConfigMapper.first(condition);
                if (sysConfigModel == null) {
                    success = true;
                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.setTenant("default");
                    sysConfigModel.setKeyName(TRIAL_USE_SYS_CONFIG);
                    sysConfigModel.setInitValue("1");
                    sysConfigModel.setValue("1");
                    sysConfigMapper.create(sysConfigModel);
                } else {
                    log.error("Trial use out of date!");
                    throw new MpRuntimeException(AdminErrorCode.TRIAL_USE_OUT_OF_DATE);
                }
            } else {
                success = licenseService.verify(verifyStr, verifyCode);
            }

            if (success) {
                // 校验通过，生成加密文件存储磁盘
                ACTIVATION_CONTENT = null;
                updateMachineCode(activationRequest);
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

    }

    public void resetMenuVisibility() {
        ACTIVATION_CONTENT = status();
        activMenuService.resetMenusVisibility(ACTIVATION_CONTENT);
    }

    /**
     * 授权延期
     * @param activationRequest
     */
    public void delayActivate(ActivationRequest activationRequest) {
        ActivationResponse content = status();
        if (Objects.isNull(content) || Objects.equals(AdminConstant.STATUS_NOT_ACTIVITED, content.getIsActived())) {
            log.error("System activation status error! Can not delay activate ");
            throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_STATUS_ERROR);
        }

        synchronized (this) {
            SysActivCtlModel validActivCtlModel = sysCtlService.getValidCtlModel();
            if (Objects.isNull(validActivCtlModel)) {
                log.error("valid activctl not existed!");
                throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_ERROR);
            }

            String localSysUUID = querySysUUID();
            if (!Objects.equals(activationRequest.getMachineCode(), localSysUUID)) {
                log.error("sysuuid not match! request machine code is:{}, localSysUUID is:{}", activationRequest.getMachineCode(), localSysUUID);
                throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_SYSUUID_NOTMATCH);
            }

            String encryptedLicenseCode = EncryptUtils.desEncrypt(activationRequest.getVerifyCode());
            String oldLicenseCode = "";
            if (StringUtils.isNotBlank(validActivCtlModel.getLicenseCode())) {
                oldLicenseCode = validActivCtlModel.getLicenseCode();
                if (Objects.equals(oldLicenseCode, encryptedLicenseCode)) {
                    log.error("License code is already used!");
                    throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_CODE_USED);
                }
            }

            List<String> delayVerifyCodeList = content.getDelayVerifyCodeList();
            if (CollectionUtils.isNotEmpty(delayVerifyCodeList) && delayVerifyCodeList.contains(encryptedLicenseCode)) {
                log.error("Delay license code is already used!");
                throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_CODE_USED);
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
                String oldExpireDate = content.getExpiredDate();
                ACTIVATION_CONTENT = null;

                updateMachineCode(activationRequest);
                // 校验通过，更新加密文件存储磁盘
                updateEncryptedFile(activationRequest, oldExpireDate, delayVerifyCodeList, oldLicenseCode);

                // 更新数据库激活限制配置
                updateActivCtlAfterActivationSuccess(activationRequest, oldExpireDate);

                ACTIVATION_CONTENT = status();
                log.info("DELAY ACTIVATION_CONTENT:{}", ACTIVATION_CONTENT);
                activMenuService.resetMenusVisibility(ACTIVATION_CONTENT);
                activMenuService.resetOptionsVisibility(ACTIVATION_CONTENT);
            } else {
                log.error("license verify failed！");
                throw new MpRuntimeException(AdminErrorCode.SYS_ACTIVATION_FAILED);
            }
        }
    }

    /**
     *  由于续期是使用machine code 和功能配置+续期时间按照固定算法加密生成的激活码，而且激活码不能重复使用，
     *  因此，如果用户续期时，选择了同样的功能配置和续期时间时，需要machine code发生变化。machine code构成是
     *  dmidecode -s system-uuid 得到的硬件唯一序列信息，加上-suffix(6位随机字符)。
     *  因此在延期保存延期信息时，添加变更suffix和matchine code
     * @author zhanzhi
     * @date 15:17 2023/9/26
     **/
    private void updateMachineCode(ActivationRequest activationRequest) {
        String hardwareUuid = StringUtils.substringBeforeLast(activationRequest.getMachineCode(), "-");
        String suffix = RandomUtils.randomString(6);
        activationRequest.setMachineCode(String.join("-", hardwareUuid, suffix));
    }

    private String getSuffixFromMachineCode(String machineCode) {
        return StringUtils.substringAfterLast(machineCode, "-");
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
        Optional<ActivationItem> itemOptional = Optional.empty();
        for (ActivationItem i : items) {
            if (i.getKey().equals(key)) {
                itemOptional = Optional.of(i);
                break;
            }
        }
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
        status();
        SysActivCtlModel sysActivCtlModel = null;
        if (Objects.isNull(ACTIVATION_CONTENT) || Objects.equals(AdminConstant.STATUS_NOT_ACTIVITED, ACTIVATION_CONTENT.getIsActived())) {
            sysActivCtlModel = sysCtlService.generateOrGetUnusedActivCtlModel();
        } else {
            sysActivCtlModel = sysCtlService.getValidCtlModel();
        }
        if (Objects.isNull(sysActivCtlModel)) {
            log.error("sysActivCtlModel is null!");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
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
    }

    @Deprecated
    public String generateCustomizedVerifyJSONInfo(List<ActivationModuleItem> moduleItems, List<ActivationResourceItem> resourceItems) {
        JSONObject jsonObject = new JSONObject();

        JSONArray moduleArray = new JSONArray();
        for (ActivationModuleItem m : moduleItems) {
            JSONObject module = new JSONObject();
            module.put("identifier", m.getIdentifier());
            module.put("name", m.getName());
            moduleArray.add(module);
        }

        JSONArray resourceArray = new JSONArray();
        for (ActivationResourceItem r : resourceItems) {
            JSONObject resource = new JSONObject();
            resource.put("identifier", r.getIdentifier());
            resource.put("module", r.getModule());
            resource.put("restrictName", r.getRestrictName());
            resource.put("maxValue", r.getMaxValue());
            resourceArray.add(resource);
        }

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
            } else {
                ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_ACTIVITED);
            }
        }
    }

    /**
     * 生成加密文件，存磁盘
     * @param activationRequest
     */
    private void generateEncryptedFile(ActivationRequest activationRequest) {
        String verifyCode = activationRequest.getVerifyCode();
        String encryptedVerifyCode = EncryptUtils.desEncrypt(verifyCode);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        String expiredDateStr = "";
        if (activationRequest.getTrialFlag()) {
            expiredDateStr = getExpiredDateStrDay(TRIAL_DAYS);
        } else {
            expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        }
        activationResponse.setExpiredDate(expiredDateStr);
        activationResponse.setVerifyCode(encryptedVerifyCode);
        //记录第一次激活时使用的verifycode，防止用户再次通过第一次激活的verifycode白嫖
        activationResponse.setDelayVerifyCodeList(Collections.singletonList(encryptedVerifyCode));

        String salt = RandomUtils.randomString(8);
        String jsonStr = JSON.toJSONString(activationResponse);
        String content = salt + jsonStr;

        String encryptedContent = EncryptUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        String filePath = frontAdminConfig.getLicenseDir() + File.separator + ACTIVATION_FILE;
        log.info("finalContent:{}", finalContent);

        FileUtils.writeString(finalContent, new File(filePath), StandardCharsets.UTF_8);
        log.info("activation file has been generated! filePath:{}", filePath);
    }

    /**
     * 更新磁盘上加密文件
     * @param activationRequest
     * @param expireDate
     * @param delayVerifyCodeList
     * @param oldLicenseCode
     */
    private void updateEncryptedFile(ActivationRequest activationRequest, String expireDate, List<String> delayVerifyCodeList, String oldLicenseCode) {
        String verifyCode = activationRequest.getVerifyCode();
        String encryptedVerifyCode = EncryptUtils.desEncrypt(verifyCode);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        String expiredDateStr = getDelayExpireDateStr(activationRequest.getDeadline(), expireDate);
        activationResponse.setExpiredDate(expiredDateStr);
        activationResponse.setVerifyCode(oldLicenseCode);
        if (CollectionUtils.isNotEmpty(delayVerifyCodeList)) {
            delayVerifyCodeList.add(encryptedVerifyCode);
        } else {
            delayVerifyCodeList = Collections.singletonList(encryptedVerifyCode);
        }
        activationResponse.setDelayVerifyCodeList(delayVerifyCodeList);
        activationResponse.setVerifyCode(encryptedVerifyCode);

        String salt = RandomUtils.randomString(8);
        String jsonStr = JSON.toJSONString(activationResponse);
        String content = salt + jsonStr;

        String encryptedContent = EncryptUtils.desEncrypt(content);
        String finalContent = salt + encryptedContent;
        String filePath = frontAdminConfig.getLicenseDir() + File.separator + ACTIVATION_FILE;
        log.info("update finalContent:{}", finalContent);

        FileUtils.writeString(finalContent, new File(filePath), StandardCharsets.UTF_8);
        log.info("update activation file has been generated! filePath:{}", filePath);
    }


    private ActivationResponse parseContentFromDecrpytedFile() {
        File activationFile = new File(frontAdminConfig.getLicenseDir() + File.separator + ACTIVATION_FILE);
        if (!activationFile.exists()) {
            log.error("activation file not exist!");
            return null;
        }

        List<String> lines = FileUtils.readUtf8Lines(activationFile);
        if (CollectionUtils.isEmpty(lines)) {
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
        for (ActivationItem i : items) {
            map.put(i.getKey(), i.getValue());
        }
        return map;
    }

    private String getExpiredDateStr(Integer months) {
        LocalDate localDate = LocalDate.now().plusMonths(months);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }

    private String getExpiredDateStrDay(Integer days) {
        LocalDate localDate = LocalDate.now().plusDays(days);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }

    private String getDelayExpireDateStr(Integer months, String strExpireDate) {
        LocalDate oldExpireDate = TimeUtils.stringToLocalDate(strExpireDate);
        LocalDate localDate = oldExpireDate.plusMonths(months);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }


    /**
     * 激活成功，变更数据库激活限制相关配置信息
     * @param activationRequest
     */
    private void processActivCtlAfterActivationSuccess(ActivationRequest activationRequest) {
        SysActivCtlModel oldSysActivCtlModel = sysCtlService.getUnusedActivCtlModel();
        SysActivCtlModel updateSysActivCtlModel = new SysActivCtlModel();
        updateSysActivCtlModel.setId(oldSysActivCtlModel.getId());

        // 考虑用时间戳替代
        LocalDateTime expiredTime = null;
        if (activationRequest.getTrialFlag()) {
            expiredTime = LocalDateTime.now().plusDays(TRIAL_DAYS).plusDays(1);
        } else {
            expiredTime = LocalDateTime.now().plusMonths(activationRequest.getDeadline()).plusDays(1);
        }
        Duration duration = Duration.between(LocalDateTime.now(), expiredTime);
        Long lifeTimeSeconds = duration.toSeconds();
        updateSysActivCtlModel.setSysLifetime(EncryptUtils.desEncrypt(String.valueOf(lifeTimeSeconds)));

        //第一次激活也需要更新suffix
        String newSuffix = getSuffixFromMachineCode(activationRequest.getMachineCode());
        updateSysActivCtlModel.setSuffix(newSuffix);

        String activeDateStr = LocalDate.now().format(dateFormatter);
        String expiredDateStr = "";
        if (activationRequest.getTrialFlag()) {
            expiredDateStr = getExpiredDateStrDay(TRIAL_DAYS);
        } else {
            expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        }
        String encryptedLicenseCode = EncryptUtils.desEncrypt(activationRequest.getVerifyCode());

        //这里正常应该是空的，不会走这个分支。
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
     * 延期激活成功，变更数据库激活限制相关配置信息
     * @param activationRequest
     * @param oldExpireDate
     */
    private void updateActivCtlAfterActivationSuccess(ActivationRequest activationRequest, String oldExpireDate) {
        SysActivCtlModel oldSysActivCtlModel = sysCtlService.getValidCtlModel();
        SysActivCtlModel updateSysActivCtlModel = new SysActivCtlModel();
        updateSysActivCtlModel.setId(oldSysActivCtlModel.getId());

        String newSuffix = getSuffixFromMachineCode(activationRequest.getMachineCode());
        updateSysActivCtlModel.setSuffix(newSuffix);


        long delayLifeTime = Duration.of(activationRequest.getDeadline(), ChronoUnit.MONTHS).toSeconds();
        long oldLifeTime = Long.parseLong(Optional.ofNullable(EncryptUtils.desDecrypt(oldSysActivCtlModel.getSysLifetime())).orElse("0"));
        long updateLifeTime = delayLifeTime + oldLifeTime;
        updateSysActivCtlModel.setSysLifetime(EncryptUtils.desEncrypt(String.valueOf(updateLifeTime)));

        String expiredDateStr = getDelayExpireDateStr(activationRequest.getDeadline(), oldExpireDate);
        updateSysActivCtlModel.setExpireDate(EncryptUtils.desEncrypt(expiredDateStr));
        String activeDateStr = LocalDate.now().format(dateFormatter);
        updateSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        sysActivCtlService.update(updateSysActivCtlModel);
    }

    /**
     * 用旧的license再次激活
     * last_active_date，active_count变动
     *
     * @param activeDateStr
     */
    private void processActivCtlWhenReActiveWithOldLicense(SysActivCtlModel updateSysActivCtlModel, String activeDateStr) {
        // 不更新first_active_date
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
        SysActivCtlModel newSysActivCtlModel = new SysActivCtlModel();
        Long currTs = System.currentTimeMillis();
        newSysActivCtlModel.setSysTimestamp(currTs);
        String suffix = RandomUtils.randomString(6);
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
        updateSysActivCtlModel.setFirstActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        updateSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        updateSysActivCtlModel.setExpireDate(EncryptUtils.desEncrypt(expiredDateStr));
        updateSysActivCtlModel.setLicenseCode(encryptedLicenseCode);
        updateSysActivCtlModel.setSysTimestamp(System.currentTimeMillis());
        updateSysActivCtlModel.setActiveCount(1);
        updateSysActivCtlModel.setStatus(SysActivCtlModel.STATUS_VALID);
        sysActivCtlService.update(updateSysActivCtlModel);
    }


    /**
     * 查询模块激活信息
     * @return
     */
    public List<ModuleInfoResponse> getModuleInfoList() {
        ActivationResponse content = status();
        List<ModuleInfoResponse> moduleInfoList = new ArrayList<>();
        if (Objects.isNull(content) || CollectionUtils.isEmpty(content.getItems())) {
            return moduleInfoList;
        }
        List<ActivationItem> items = content.getItems();
        // 竞赛
        ModuleInfoResponse ad = ModuleInfoResponse.builder().moduleName(ModuleEnums.AD.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        // 实训
        ModuleInfoResponse oj = ModuleInfoResponse.builder().moduleName(ModuleEnums.OJ.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        // 靶场
        ModuleInfoResponse cr = ModuleInfoResponse.builder().moduleName(ModuleEnums.CR.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        moduleInfoList.addAll(Arrays.asList(ad, oj, cr));

        Map<String, ActivationItem> key2ItemMap = new HashMap<>();
        for (ActivationItem item : items) {
            if (key2ItemMap.put(item.getKey(), item) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        // 任意一项激活项勾选，则该模块被激活
        for (String key : AD_MODULE_KEYS) {
            if (Objects.equals(key2ItemMap.get(key).getValue(), String.valueOf(AdminConstant.STATUS_ACTIVITED))) {
                ad.setIsActived(1);
            }
        }

        for (String key : OJ_MODULE_KEYS) {
            if (Objects.equals(key2ItemMap.get(key).getValue(), String.valueOf(AdminConstant.STATUS_ACTIVITED))) {
                oj.setIsActived(1);
            }
        }

        for (String key : CR_MODULE_KEYS) {
            if (Objects.equals(key2ItemMap.get(key).getValue(), String.valueOf(AdminConstant.STATUS_ACTIVITED))) {
                cr.setIsActived(1);
            }
        }

        return moduleInfoList;
    }



    @Deprecated
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
        item2.setValue("0");
        item2.setType(1);
        list.add(item2);

        ActivationItem item3 = new ActivationItem();
        item3.setKey("LCS-FUNC-CR-HUOTILOUDONG");
        item3.setValue("0");
        item3.setType(1);
        list.add(item3);

        ActivationItem item4 = new ActivationItem();
        item4.setKey("LCS-FUNC-CR-ZHISHIKU");
        item4.setValue("0");
        item4.setType(1);
        list.add(item4);

        ActivationItem item5 = new ActivationItem();
        item5.setKey("LCS-FUNC-CR-ZONGHEXUNLIAN");
        item5.setValue("0");
        item5.setType(1);
        list.add(item5);

        ActivationItem item6 = new ActivationItem();
        item6.setKey("LCS-FUNC-CR-HONGLANDUIKANG");
        item6.setValue("0");
        item6.setType(1);
        list.add(item6);

        ActivationItem item7 = new ActivationItem();
        item7.setKey("LCS-FUNC-CR-SHENTOUSAI");
        item7.setValue("0");
        item7.setType(1);
        list.add(item7);

        ActivationItem item8 = new ActivationItem();
        item8.setKey("LCS-FUNC-CR-YUNWEISAI");
        item8.setValue("0");
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
