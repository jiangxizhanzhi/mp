package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.constant.MonitorConstant;
import cn.cyberpeace.mp.devopscluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.ServiceRestartStatusDto;
import cn.cyberpeace.mp.devopscluster.enums.MonitorStateEnum;
import cn.cyberpeace.mp.devopscluster.model.CpNodeMonitorScriptConfigModel;
import cn.cyberpeace.mp.devopscluster.repository.CpNodeMonitorScriptConfigMapper;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhLauncher;
import cn.cyberpeace.mp.situationaware.config.ServerMonitorConfig;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MonitorScriptControlService {

    @Autowired
    private CpNodeMonitorScriptConfigMapper monitorScriptConfigMapper;

    @Autowired
    private HypervisorConverter hypervisorConverter;

    @Autowired
    private AgentService agentService;

    
    private TimedCache<String, ServiceRestartStatusDto> SRV_STATUS_CACHE = CacheUtil.newTimedCache(20 * 60 * 1000);


    public void init(List<LocalFileMappingModel> mappings) {
        serviceMonitorScriptInit(mappings);
    }

    
    public void schedule() {

    }

    public List<CpNodeMonitorScriptConfigModel> listAll() {
        return monitorScriptConfigMapper.listAll();
    }


    public CpNodeMonitorScriptConfigModel getByAgentName(String agentName) {
        return monitorScriptConfigMapper.getByAgentName(agentName);
    }


    public void restart(String hostname, List<String> serviceList) {
        
        TimedCache<String, ServiceRestartStatusDto> timedCache = getStatusCache();
        log.info("restart serviceList:{}, timedCache keySet:{}", serviceList, timedCache.keySet());
        Iterator<String> iterator = serviceList.iterator();
        while (iterator.hasNext()) {
            String cacheKey = hostname + "_" + iterator.next();
            ServiceRestartStatusDto statusDto = timedCache.get(cacheKey);
            if (Objects.nonNull(statusDto)) {
                iterator.remove();
            }
        }

        if (CollectionUtil.isEmpty(serviceList)) {
            return;
        }

        List<ServerMonitorConfig.MonitorProbe> cpSrvs = ServerMonitorConfig.cpCloudSrvMonitors;
        List<ServerMonitorConfig.MonitorProbe> bizSrvs = ServerMonitorConfig.businessSrvMonitors;

        if (CollectionUtil.isEmpty(cpSrvs)) {
            return;
        }

        String cpLaunchId = cpSrvs.get(0).getLaunchId();
        String bizLaunchId = bizSrvs.get(0).getLaunchId();
        String servers = StringUtils.join(serviceList, ",");
        List<String> launchIds = Arrays.asList(cpLaunchId, bizLaunchId);

        for (String launchId : launchIds) {
            String cmd = String.format(MonitorConstant.MONITOR_CMD, launchId, MonitorConstant.OP_RESTART, servers);
            log.info("restart hostname:{}, cmd:{}", hostname, cmd);
            var runCommandItem = new WazuhLauncher.RunCommandItem();
            runCommandItem.setAgentName(hostname);
            runCommandItem.setOsType(MonitorConstant.OS_TYPE_LINUX);
            runCommandItem.setCommand(cmd);
            WazuhControlCommon.wazuhControl.getWazuhLauncher().runCommand(List.of(runCommandItem));
        }

        
        for (String srv : serviceList) {
            String cacheKey = hostname + "_" + srv;
            ServiceRestartStatusDto statusDto = ServiceRestartStatusDto.builder().restartTime(LocalDateTime.now()).status(MonitorStateEnum.RESTARTING.getDesc()).build();
            timedCache.put(cacheKey, statusDto);
        }

    }



    public void statusManually(String hostname) {
        List<ServerMonitorConfig.MonitorProbe> allSrvMonitors = new ArrayList<>();
        List<ServerMonitorConfig.MonitorProbe> cpSrvs = ServerMonitorConfig.cpCloudSrvMonitors;
        List<ServerMonitorConfig.MonitorProbe> bizSrvs = ServerMonitorConfig.businessSrvMonitors;

        if (CollectionUtil.isNotEmpty(cpSrvs)) {
            allSrvMonitors.addAll(cpSrvs);
        }

        if (CollectionUtil.isNotEmpty(bizSrvs)) {
            allSrvMonitors.addAll(bizSrvs);
        }

        if (CollectionUtil.isEmpty(allSrvMonitors)) {
            return;
        }

        for (ServerMonitorConfig.MonitorProbe monitor : allSrvMonitors) {
            String servers = StringUtils.join(monitor.getServiceList(), ",");
            String cmd = String.format(MonitorConstant.MONITOR_CMD, monitor.getLaunchId(), MonitorConstant.OP_STATUS, servers);
            log.info("status manually, hostname:{}, cmd:{}", hostname, cmd);
            var runCommandItem = new WazuhLauncher.RunCommandItem();
            runCommandItem.setRunId(monitor.getLaunchId());
            runCommandItem.setAgentName(hostname);
            runCommandItem.setOsType(MonitorConstant.OS_TYPE_LINUX);
            runCommandItem.setCommand(cmd);
            WazuhControlCommon.wazuhControl.getWazuhLauncher().runCommand(List.of(runCommandItem));
        }

    }


    private void serviceMonitorScriptInit(List<LocalFileMappingModel> mappings) {
        BasePageBO<HypervisorBO> hypervisorBOBasePageBO = hypervisorConverter.page(new BasePageDTO());
        List<HypervisorBO> hypervisorBOs = hypervisorBOBasePageBO.getList();

        if (CollectionUtils.isEmpty(hypervisorBOs)) {
            return;
        }

        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));
        List<CpNodeMonitorScriptConfigModel> configedNodeList = listAll();
        List<String> hostNameList = hypervisorBOs.stream().map(HypervisorBO::getName).collect(Collectors.toList());

        List<String> toInstallList = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(configedNodeList)) {
            toInstallList = hostNameList;
        } else {
            
            List<String> configedNodeNames = configedNodeList.stream().map(CpNodeMonitorScriptConfigModel::getAgentName).collect(Collectors.toList());
            List<String> newAddedNodes = hostNameList.stream().filter(host -> !configedNodeNames.contains(host)).collect(Collectors.toList());
            log.info("newAddedNodes:{}", newAddedNodes);
            toInstallList.addAll(newAddedNodes);

            
            for (String nodeName : hostNameList) {
                for (CpNodeMonitorScriptConfigModel configedNodeModel : configedNodeList) {
                    if (configedNodeModel.getAgentName().equals(nodeName)) {
                        if (!Objects.equals(configedNodeModel.getStatus(), CpNodeMonitorScriptConfigModel.STATUS_INSTALLED) || isConfigChanged(configedNodeModel.getConfig(), nameFileMap)) {
                            toInstallList.add(nodeName);
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(toInstallList)) {
            log.info("toInstallList:{}", toInstallList);
            
            toInstallList.forEach(hostName -> {
                
                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setName(hostName);
                agentRequest.setStatus("active");
                List<Agent> agents = agentService.listAgents(agentRequest);
                if (!CollectionUtil.isEmpty(agents)) {
                    sendScriptInstallCmd(hostName, nameFileMap);
                    addOrUpdateNodeConfigByHostName(hostName, nameFileMap);
                } else {
                    log.info("install host:{} not found active wazuh agent!", hostName);
                }
            });
        }

        
        for (String hostName : hostNameList) {
            if (!toInstallList.contains(hostName)) {
                statusManually(hostName);
            }
        }

    }

    private void sendScriptInstallCmd(String hostname, Map<String, String> nameFileMap) {
        WazuhControl.ControlConfigParam configParam = new WazuhControl.ControlConfigParam();

        List<ServerMonitorConfig.MonitorProbe> cpSrvs = ServerMonitorConfig.cpCloudSrvMonitors;
        List<ServerMonitorConfig.MonitorProbe> bizSrvs = ServerMonitorConfig.businessSrvMonitors;

        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>();
        for (ServerMonitorConfig.MonitorProbe cpSrvProbe : cpSrvs) {
            controlProbeParams.add(generateControlProbeParam(cpSrvProbe, nameFileMap));
        }

        for (ServerMonitorConfig.MonitorProbe bizSrvProbe : bizSrvs) {
            controlProbeParams.add(generateControlProbeParam(bizSrvProbe, nameFileMap));
        }

        configParam.setProbeParams(controlProbeParams);
        
        WazuhControlCommon.wazuhControl.controlConfigOld(hostname, MonitorConstant.OS_TYPE_LINUX, configParam);
    }


    private WazuhControl.ControlProbeParam generateControlProbeParam(ServerMonitorConfig.MonitorProbe monitorProbe, Map<String, String> nameFileMap) {

        WazuhControl.ControlProbeParam probeParam = new WazuhControl.ControlProbeParam();

        probeParam.setId(monitorProbe.getLaunchId());
        probeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        probeParam.setGroup("server");
        String url = FsCommon.genInnerHttpUrl(nameFileMap.get(monitorProbe.getProbeName()));
        probeParam.setScriptUrl(url);
        probeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        probeParam.setOutputFormat(WazuhLauncher.CMD_OUTPUT_FORMAT_NONE);
        probeParam.setInterval(monitorProbe.getInterval());

        String[] params = {MonitorConstant.OP_STATUS, StringUtils.join(monitorProbe.getServiceList(), ",")};
        probeParam.setScriptParam(StringUtils.joinWith(StringUtils.SPACE, params));
        return probeParam;
    }

    private void addOrUpdateNodeConfigByHostName(String hostName, Map<String, String> nameFileMap) {
        List<CpNodeMonitorScriptConfigModel.ParamConfig> paramConfigs = getConfigFromYaml(nameFileMap);
        String configStr = JsonCommon.stringify(paramConfigs);

        CpNodeMonitorScriptConfigModel scriptConfigModel = getByAgentName(hostName);
        if (Objects.isNull(scriptConfigModel)) {
            
            CpNodeMonitorScriptConfigModel toAdd = new CpNodeMonitorScriptConfigModel();
            toAdd.setAgentName(hostName);
            toAdd.setHostname(hostName);
            toAdd.setStatus(CpNodeMonitorScriptConfigModel.STATUS_INSTALLED);
            toAdd.setConfig(configStr);
            monitorScriptConfigMapper.save(toAdd);
        } else {
            
            CpNodeMonitorScriptConfigModel toUpdate = new CpNodeMonitorScriptConfigModel();
            toUpdate.setId(scriptConfigModel.getId());
            toUpdate.setAgentName(hostName);
            toUpdate.setHostname(hostName);
            toUpdate.setStatus(CpNodeMonitorScriptConfigModel.STATUS_INSTALLED);
            toUpdate.setConfig(configStr);
            toUpdate.setUpdateTime(LocalDateTime.now());
            monitorScriptConfigMapper.update(toUpdate);
        }
    }


    public TimedCache<String, ServiceRestartStatusDto> getStatusCache() {
        return SRV_STATUS_CACHE;
    }

    
    private boolean isConfigChanged(String configFromDB, Map<String, String> nameFileMap) {
        if (StringUtils.isBlank(configFromDB)) {
            return true;
        }
        List<CpNodeMonitorScriptConfigModel.ParamConfig> configsFromYaml = getConfigFromYaml(nameFileMap);
        List<CpNodeMonitorScriptConfigModel.ParamConfig> configsFromDB = JsonCommon.parse(configFromDB, new TypeReference<List<CpNodeMonitorScriptConfigModel.ParamConfig>>() {});

        if (CollectionUtil.isEmpty(configsFromYaml) || CollectionUtil.isEmpty(configsFromDB)) {
            return true;
        }

        configsFromYaml.sort(Comparator.comparing(CpNodeMonitorScriptConfigModel.ParamConfig::getId));
        configsFromDB.sort(Comparator.comparing(CpNodeMonitorScriptConfigModel.ParamConfig::getId));

        return !CollectionUtil.isEqualList(configsFromYaml, configsFromDB);
    }

    private List<CpNodeMonitorScriptConfigModel.ParamConfig> getConfigFromYaml(Map<String, String> nameFileMap) {
        List<ServerMonitorConfig.MonitorProbe> allSrvs = new ArrayList<>();
        allSrvs.addAll(ServerMonitorConfig.cpCloudSrvMonitors);
        allSrvs.addAll(ServerMonitorConfig.businessSrvMonitors);

        if (CollectionUtil.isNotEmpty(allSrvs)) {
            allSrvs.sort(new Comparator<ServerMonitorConfig.MonitorProbe>() {
                @Override
                public int compare(ServerMonitorConfig.MonitorProbe o1, ServerMonitorConfig.MonitorProbe o2) {
                    return o1.getProbeName().compareTo(o2.getProbeName());
                }
            });
        }

        List<CpNodeMonitorScriptConfigModel.ParamConfig> configs = new ArrayList<>();
        for (ServerMonitorConfig.MonitorProbe monitorProbe : allSrvs) {
            String url = FsCommon.genInnerHttpUrl(nameFileMap.get(monitorProbe.getProbeName()));

            CpNodeMonitorScriptConfigModel.ParamConfig config = new CpNodeMonitorScriptConfigModel.ParamConfig();
            config.setId(monitorProbe.getLaunchId());
            config.setScriptUrl(url);
            config.setInterval(monitorProbe.getInterval());
            configs.add(config);
        }
        
        return configs;
    }

}
