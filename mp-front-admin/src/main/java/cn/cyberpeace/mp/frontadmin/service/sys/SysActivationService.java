package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SysActivationService {

    private static ActivationResponse ACTIVATION_CONTENT = null;

    public static final String ACTIVATION_FILE = "activation";

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Integer TRIAL_DAYS = 7;

    public static final String TRIAL_USE_SYS_CONFIG = "trial_use_sys_config";

    
    private static List<String> AD_MODULE_KEYS = Arrays.asList(ActivationItemKeyEnums.JINGSAI.getCode(), ActivationItemKeyEnums.SHENTOUSAI.getCode(), ActivationItemKeyEnums.YUNWEISAI.getCode());
    
    private static List<String> OJ_MODULE_KEYS = Arrays.asList(ActivationItemKeyEnums.SHIXUN.getCode());
    
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

            
            ActivationVerifyInfo verifyInfo = new ActivationVerifyInfo();
            verifyInfo.setDeadline(activationRequest.getDeadline());
            verifyInfo.setMachinecode(activationRequest.getMachineCode());

            
            Map<String, String> infoMap = convertActivationItems2CustomVerifyInfoMap(activationRequest.getItems());
            verifyInfo.setInfo(infoMap);

            String verifyStr = JSON.toJSONString(verifyInfo);
            String verifyCode = activationRequest.getVerifyCode();

            
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
                
                ACTIVATION_CONTENT = null;
                generateEncryptedFile(activationRequest);

                
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
        
        for (String checkLine : ExecutingCommand.runNative("dmidecode -s system-uuid")) {
            if (StringUtils.isNotBlank(checkLine)) {
                uuid = checkLine;
                log.info("origin uuid is {}", uuid);
            }
        }

        
        
        uuid = uuid + "-" + suffix;
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

    
    private void processActivedStatus() {
        if (Objects.nonNull(ACTIVATION_CONTENT)) {
            
            LocalDate expireDate = LocalDate.parse(ACTIVATION_CONTENT.getExpiredDate(), dateFormatter);
            expireDate = expireDate.plusDays(1);

            
            boolean isRunSecondsExceeded = false;
            
            boolean isExpiredDateExceeded = !LocalDate.now().isBefore(expireDate);

            SysActivCtlModel validActivCtlModel = sysCtlService.getValidCtlModel();
            log.info("validActivCtlModel:{}", validActivCtlModel);
            if (Objects.isNull(validActivCtlModel)) {
                ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_NOT_ACTIVITED);
                return;
            }

            if (!isExpiredDateExceeded) {
                
                if (StringUtils.isBlank(validActivCtlModel.getFirstActiveDate())) {
                    log.info("System is not activated right now!");
                    
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

                
                SysActivCtlModel updateActivCtlModel = new SysActivCtlModel();
                updateActivCtlModel.setId(validActivCtlModel.getId());
                updateActivCtlModel.setStatus(SysActivCtlModel.STATUS_INVALID);
                sysActivCtlService.update(updateActivCtlModel);
            } else {
                ACTIVATION_CONTENT.setIsActived(AdminConstant.STATUS_ACTIVITED);
            }
        }
    }

    
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

    private String getExpiredDateStrDay(Integer days) {
        LocalDate localDate = LocalDate.now().plusDays(days);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }

    
    private void processActivCtlAfterActivationSuccess(ActivationRequest activationRequest) {
        SysActivCtlModel oldSysActivCtlModel = sysCtlService.getUnusedActivCtlModel();
        SysActivCtlModel updateSysActivCtlModel = new SysActivCtlModel();
        updateSysActivCtlModel.setId(oldSysActivCtlModel.getId());

        
        LocalDateTime expiredTime = null;
        if (activationRequest.getTrialFlag()) {
            expiredTime = LocalDateTime.now().plusDays(TRIAL_DAYS).plusDays(1);
        } else {
            expiredTime = LocalDateTime.now().plusMonths(activationRequest.getDeadline()).plusDays(1);
        }
        Duration duration = Duration.between(LocalDateTime.now(), expiredTime);
        Long lifeTimeSeconds = duration.toSeconds();
        updateSysActivCtlModel.setSysLifetime(EncryptUtils.desEncrypt(String.valueOf(lifeTimeSeconds)));

        String activeDateStr = LocalDate.now().format(dateFormatter);
        String expiredDateStr = "";
        if (activationRequest.getTrialFlag()) {
            expiredDateStr = getExpiredDateStrDay(TRIAL_DAYS);
        } else {
            expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        }
        String encryptedLicenseCode = EncryptUtils.desEncrypt(activationRequest.getVerifyCode());

        if (StringUtils.isNotBlank(oldSysActivCtlModel.getLicenseCode())) {
            
            String oldLicenseCode = oldSysActivCtlModel.getLicenseCode();

            
            if (Objects.equals(oldLicenseCode, encryptedLicenseCode)) {
                updateSysActivCtlModel.setActiveCount(oldSysActivCtlModel.getActiveCount() + 1);
                processActivCtlWhenReActiveWithOldLicense(updateSysActivCtlModel, activeDateStr);
            } else {
                
                processActivCtlWhenReActiveWithNewLicense(activeDateStr, expiredDateStr, encryptedLicenseCode);
            }
        } else {
            
            sysActivCtlService.invalidOtherExceptOne(updateSysActivCtlModel.getId());
            processActivCtlWhenFirstActiveWithNewLicense(updateSysActivCtlModel, activeDateStr, expiredDateStr, encryptedLicenseCode);
        }
    }

    
    private void processActivCtlWhenReActiveWithOldLicense(SysActivCtlModel updateSysActivCtlModel, String activeDateStr) {
        
        updateSysActivCtlModel.setFirstActiveDate(null);
        updateSysActivCtlModel.setLastActiveDate(EncryptUtils.desEncrypt(activeDateStr));
        sysActivCtlService.update(updateSysActivCtlModel);
    }


    
    private void processActivCtlWhenReActiveWithNewLicense(String activeDateStr, String expiredDateStr, String encryptedLicenseCode) {
        SysActivCtlModel newSysActivCtlModel = new SysActivCtlModel();
        Long currTs = System.currentTimeMillis();
        newSysActivCtlModel.setSysTimestamp(currTs);
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


    
    public List<ModuleInfoResponse> getModuleInfoList() {
        var content = status();
        List<ModuleInfoResponse> moduleInfoList = new ArrayList<>();
        if (Objects.isNull(content) || CollectionUtil.isEmpty(content.getItems())) {
            return moduleInfoList;
        }
        var items = content.getItems();
        
        ModuleInfoResponse ad = ModuleInfoResponse.builder().moduleName(ModuleEnums.AD.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        
        ModuleInfoResponse oj = ModuleInfoResponse.builder().moduleName(ModuleEnums.OJ.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        
        ModuleInfoResponse cr = ModuleInfoResponse.builder().moduleName(ModuleEnums.CR.getCode()).isActived(AdminConstant.STATUS_NOT_ACTIVITED).build();
        moduleInfoList.addAll(Arrays.asList(ad, oj, cr));

        Map<String, ActivationItem> key2ItemMap = items.stream().collect(Collectors.toMap(ActivationItem::getKey, Function.identity()));

        
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





}
