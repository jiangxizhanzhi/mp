package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.CollectionApp;
import cn.cyberpeace.mp.cscene.dto.common.CollectionType;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.CollectionAppRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.utils.MessageCommonUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CollectionTypeService {


    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    public List<CollectionType> typeTrees(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        List<CollectionType> types = new ArrayList<>();
        types.add(changeEvent("00", null));
        types.add(sourceLogFile("01", CollectionType.TYPE_LOG_FILE));
        types.add(logEvent("02", CollectionType.TYPE_LOG_EVENT));
        types.add(customCollect("03", CollectionType.TYPE_CUSTOM_COLLECT));

        types.forEach(type -> processCollectionType(type));
        return types;
    }

    public List<CollectionApp> getAppNames(CollectionAppRequest appRequest) {
        log.info("baseRequest:{}", appRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(appRequest);

        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(appRequest.getSceneId(), appRequest.getElementIds());

        
        List<String> ruleIdsByCatagories = new ArrayList<>();
        Map<String, String> ruleId2File = scenePluginCollectionSceneService.getRuleId2FileByCodes(appRequest.getCatagories()).getSource();
        if (CollectionUtils.isNotEmpty(ruleId2File.keySet())) {
            List<String> ruleIds = ruleId2File.keySet().stream().distinct().collect(Collectors.toList());
            ruleIdsByCatagories.addAll(ruleIds);
        }

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        if (Objects.nonNull(appRequest.getStartTime())) {
            wazuhEventRequest.setStartTime(appRequest.getStartTime());
        } else {
            String translateSceneId = csceneCommonService.translateSceneId(appRequest.getSceneId());
            var sceneHandler = new SceneHandler(translateSceneId, null, false, null);
            wazuhEventRequest.setStartTime(sceneHandler.getScene().getCreateTime());
        }
        wazuhEventRequest.setEndTime(Optional.ofNullable(appRequest.getEndTime()).orElse(LocalDateTime.now()));
        
        Map<String, Long> ruleIdCntMap = dataCollectionService.aggsRuleIdsByAgentName(wazuhEventRequest);
        List<String> ruleIdsByAgentName = new ArrayList<>(ruleIdCntMap.keySet());
        if (CollectionUtil.isEmpty(ruleIdsByAgentName)) {
            return Collections.emptyList();
        }

        
        List<String> filteredRuleIds = null;
        if (CollectionUtil.isNotEmpty(ruleIdsByCatagories)) {
            filteredRuleIds = ruleIdsByAgentName.stream().filter(rid -> ruleIdsByCatagories.contains(rid)).collect(Collectors.toList());
        } else {
            filteredRuleIds = ruleIdsByAgentName;
        }

        ServiceResult<Map<String, String>> result = scenePluginCollectionSceneService.getRuleId2FileByIds(filteredRuleIds);
        Map<String, String> ruleId2FileMap = result.getSource();
        Set<String> ruleFiles = new HashSet(ruleId2FileMap.values());
        log.info("ruleFiles:{}", ruleFiles);
        List<CollectionApp> apps = ruleFiles.stream().sorted().map(r -> {
            ServiceResult<String> appNameResult = scenePluginCollectionSceneService.ruleFile2AppName(r);
            String appName = appNameResult.getSource();
            CollectionApp app = null;
            if (StringUtils.isNotBlank(appName)) {
                app = new CollectionApp();
                app.setName(appName);
            }
            return app;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        log.info("apps:{}", apps);
        return apps;
    }

    
    private CollectionType changeEvent(String code, Integer type) {
        CollectionType changeEvent = CollectionType.builder().name("变更事件").code(code).type(-1).build();

        CollectionType fileChangeEvent = CollectionType.builder().name("文件变更事件").code(code + "01").type(CollectionType.TYPE_CHANGE_EVENT_FILE).build();
        CollectionType registryChangeEvent = CollectionType.builder().name("注册表变更事件").code(code + "02").type(CollectionType.TYPE_CHANGE_EVENT_REGISTRY).build();
        

        changeEvent.setChildren(Arrays.asList(fileChangeEvent, registryChangeEvent));
        return changeEvent;
    }

    
    private CollectionType sourceLogFile(String code, Integer type) {
        CollectionType sourceLogEvent = CollectionType.builder().name("原始日志").code("01").type(CollectionType.TYPE_LOG_FILE).build();

        return sourceLogEvent;
    }

    
    private CollectionType customCollect(String code, Integer type) {
        CollectionType customCollect = CollectionType.builder().name("自定义采集").code(code).type(type).build();

        return customCollect;
    }


    
    private CollectionType logEvent(String code, Integer type) {
        CollectionType logEvent = CollectionType.builder().name("日志事件").code(code).type(-1).build();
        CollectionType osType = logEventOSType(code + "01", type);
        CollectionType app = logEventApp(code + "02", type);
        CollectionType securitySystem = logEventSecuritySystem(code + "03", type);
        logEvent.setChildren(Arrays.asList(osType, app, securitySystem));
        return logEvent;
    }

    
    private CollectionType logEventOSType(String code, Integer type) {
        CollectionType osType = CollectionType.builder().name("操作系统日志").code(code).type(-1).build();

        CollectionType windows = CollectionType.builder().name("Windows").code(code + "01").type(type).build();
        CollectionType linux = CollectionType.builder().name("Linux").code(code + "02").type(type).build();
        CollectionType cisco = CollectionType.builder().name("Cisco-ios").code(code + "03").type(type).build();
        CollectionType hp = CollectionType.builder().name("HP").code(code + "04").type(type).build();
        CollectionType freebsd = CollectionType.builder().name("BSD").code(code + "05").type(type).build();
        CollectionType junos = CollectionType.builder().name("Junos").code(code + "06").type(type).build();
        CollectionType solaris = CollectionType.builder().name("solaris").code(code + "07").type(type).build();

        List<CollectionType> osSubTypes = Arrays.asList(windows, linux, cisco, hp, freebsd, junos, solaris);
        osType.setChildren(osSubTypes);

        return osType;
    }


    
    private CollectionType logEventApp(String code, Integer type) {
        CollectionType appType = CollectionType.builder().name("应用系统日志").code(code).type(-1).build();

        CollectionType mailServer = CollectionType.builder().name("邮件服务器").code(code + "01").type(type).build();
        CollectionType webserver = CollectionType.builder().name("WEB服务器").code(code + "02").type(type).build();
        CollectionType database = CollectionType.builder().name("数据库").code(code + "03").type(type).build();
        CollectionType networkServer = CollectionType.builder().name("网络服务").code(code + "04").type(type).build();
        CollectionType app = CollectionType.builder().name("应用软件").code(code + "05").type(type).build();
        CollectionType cloud = CollectionType.builder().name("云").code(code + "06").type(type).build();
        CollectionType loadbalancer = CollectionType.builder().name("负载均衡").code(code + "07").type(type).build();
        CollectionType appFramework = CollectionType.builder().name("应用框架").code(code + "08").type(type).build();


        CollectionType ftp = CollectionType.builder().name("文件服务器").code(code + "10").type(type).build();


        List<CollectionType> appSubTypes = Arrays.asList(mailServer, webserver, database, networkServer,
                app, cloud, loadbalancer, appFramework, ftp);
        appType.setChildren(appSubTypes);

        return appType;
    }


    
    private CollectionType logEventSecuritySystem(String code, Integer type) {
        CollectionType secSysType = CollectionType.builder().name("安全系统日志").code(code).type(-1).build();

        CollectionType av = CollectionType.builder().name("AV").code(code + "01").type(type).build();
        CollectionType fw = CollectionType.builder().name("FW").code(code + "02").type(type).build();
        CollectionType idsIps = CollectionType.builder().name("IDS/IPS").code(code + "03").type(type).build();
        CollectionType vpn = CollectionType.builder().name("VPN").code(code + "04").type(type).build();








        CollectionType waf = CollectionType.builder().name("WAF").code(code + "13").type(type).build();

        CollectionType other = CollectionType.builder().name("其他").code(code + "05").type(type).build();
        List<CollectionType> secSubTypes = Arrays.asList(av, fw, idsIps, vpn, waf, other);
        secSysType.setChildren(secSubTypes);

        return secSysType;
    }

    private void processCollectionType(CollectionType type) {
        
        String key = String.format(SituationBase18nCode.COLLECTION_TYPE_PARENT_I18N_CODE_PREFIX, type.getCode());
        String name = MessageCommonUtil.convertMessage(key);
        type.setName(name);
        processCollectionTypeChildren(type);
    }

    private void processCollectionTypeChildren(CollectionType type) {
        if (CollectionUtils.isEmpty(type.getChildren())) {
            return;
        }
        type.getChildren().forEach(t -> {
            String key = String.format(SituationBase18nCode.COLLECTION_TYPE_PARENT_I18N_CODE_PREFIX, t.getCode());
            String childrenName = MessageCommonUtil.convertMessage(key);
            t.setName(childrenName);
            processCollectionTypeChildren(t);
        });
    }
}
