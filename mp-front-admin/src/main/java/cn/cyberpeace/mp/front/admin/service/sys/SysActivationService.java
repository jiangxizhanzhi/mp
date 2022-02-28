package cn.cyberpeace.mp.front.admin.service.sys;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
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

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private ActivationConfig activationConfig;

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

            // 更新数据库激活限制配置
            processActivCtl(activationRequest);

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

        processActivedStatus();

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
        SysActivCtlModel sysActivCtlModel = sysCtlService.getDefaultSysActivCtl();
        String suffix = sysActivCtlModel.getSuffix();

        String uuid = "";
        // If root privileges this will work
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

    /**
     * 判断是否过期有两种方法，1. 过期日期超限  2.系统运行时长超限
     */
    private void processActivedStatus() {
        if (Objects.nonNull(ACTIVATION_CONTENT)) {
            //默认状态为未激活
            ACTIVATION_CONTENT.setIsActived(0);

            LocalDate expireDate = LocalDate.parse(ACTIVATION_CONTENT.getExpiredDate(), dateFormatter);
            expireDate = expireDate.plusDays(1);

            // 运行时长超限
            boolean isRunSecondsExceeded = false;
            // 过期日期到期
            boolean isExpired = !LocalDate.now().isBefore(expireDate);

            SysActivCtlModel sysActivCtlModel = sysCtlService.generateOrGetDefaultActivCtlModel();

            if (!isExpired) {
                // 过期日期未到，继续判断运行时长
                if (StringUtils.isBlank(sysActivCtlModel.getFirstActiveDate())) {
                    log.info("System is not activated right now!");
                    // 未激活，但是CONTENT有值，说明是升级的场景，则还是以CONTENT内容为主
                } else {
                    String firstActiveDateStr = EncryptUtils.desDecrypt(sysActivCtlModel.getFirstActiveDate());
                    LocalDate firstActiveDate = LocalDate.parse(firstActiveDateStr, dateFormatter);
                    Duration duration = Duration.between(firstActiveDate.atTime(0, 0, 0), expireDate.atTime(0, 0, 0));
                    Long calculatedRunSeconds = duration.toSeconds();

                    if (sysActivCtlModel.getSysRunSeconds() > calculatedRunSeconds) {
                        isRunSecondsExceeded = true;
                    }
                }
            }

            if (isExpired || isRunSecondsExceeded) {
                ACTIVATION_CONTENT.setIsActived(0);

                // 过期状态，更新激活限制项配置
                SysActivCtlModel updateActivCtlModel = new SysActivCtlModel();
                updateActivCtlModel.setId(sysActivCtlModel.getId());
                updateActivCtlModel.setStatus(0);
                sysActivCtlService.update(updateActivCtlModel);
            } else {
                ACTIVATION_CONTENT.setIsActived(1);
            }
        }
    }

    /**
     * 生成加密文件，存磁盘
     * @param activationRequest
     */
    private void generateEncryptedFile(ActivationRequest activationRequest) {
        log.info("generate encrypted file activationRequest:{}", activationRequest);
        ActivationResponse activationResponse = new ActivationResponse();
        BeanUtils.copyProperties(activationRequest, activationResponse);
        String expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        activationResponse.setExpiredDate(expiredDateStr);

        String salt = RandomUtil.randomString(8);
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

    private String getExpiredDateStr(Integer months) {
        LocalDate localDate = LocalDate.now().plusMonths(months);
        String expiredDate = localDate.format(dateFormatter);
        return expiredDate;
    }


    /**
     * 激活成功，变更数据库激活限制相关配置信息
     * @param activationRequest
     */
    private void processActivCtl(ActivationRequest activationRequest) {
        log.info("update activ ctl config info");
        SysActivCtlModel oldSysActivCtlModel = sysCtlService.getDefaultSysActivCtl();
        SysActivCtlModel updateSysActivCtlModel = new SysActivCtlModel();
        updateSysActivCtlModel.setId(oldSysActivCtlModel.getId());

        String activeDateStr = LocalDate.now().format(dateFormatter);
        String expiredDateStr = getExpiredDateStr(activationRequest.getDeadline());
        String encryptedLicenseCode = EncryptUtils.desEncrypt(activationRequest.getVerifyCode());

        if (StringUtils.isNotBlank(oldSysActivCtlModel.getLicenseCode())) {
            // 不是首次激活
            String oldLicenseCode = EncryptUtils.desDecrypt(oldSysActivCtlModel.getLicenseCode());

            // todo 考虑相同激活码，如果原来已经过期了怎么处理？
            // 1. 相同激活码 last_active_date，active_count变动
            if (Objects.equals(oldLicenseCode, activationRequest.getVerifyCode())) {
                updateSysActivCtlModel.setActiveCount(oldSysActivCtlModel.getActiveCount() + 1);
                processActivCtlWhenReActiveWithOldLicense(updateSysActivCtlModel, activeDateStr);
            } else {
                // 2. 不同激活码，当做一次全新的激活，先将其他激活状态都设置成status=0
                processActivCtlWhenReActiveWithNewLicense(activeDateStr, expiredDateStr, encryptedLicenseCode);
            }
        } else {
            // 首次激活，添加first_active_date, last_active_date, expire_date, active_count;
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
    // todo 考虑用事务
    private void processActivCtlWhenReActiveWithNewLicense(String activeDateStr, String expiredDateStr, String encryptedLicenseCode) {
        log.info("activeDateStr:{}, expiredDateStr:{}", activeDateStr, expiredDateStr);
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
        updateSysActivCtlModel.setSysTimestamp(System.currentTimeMillis());
        updateSysActivCtlModel.setActiveCount(1);
        sysActivCtlService.update(updateSysActivCtlModel);
    }


}
