package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devops.cluster.constant.MonitorConstant;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.CpNodeMonitorScriptConfigModel;
import cn.cyberpeace.mp.devops.cluster.repository.CpNodeMonitorScriptConfigMapper;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhLauncher;
import cn.cyberpeace.mp.situationaware.config.MonitorConfig;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/13 16:16
 */
@Slf4j
@Service
public class CpNodeMonitorScriptConfigService {

    @Autowired
    private CpNodeMonitorScriptConfigMapper monitorScriptConfigMapper;

    @Autowired
    private HypervisorConverter hypervisorConverter;

    @Autowired
    private FastDfsConfig fastDfsConfig;

    @Autowired
    private MonitorConfig monitorConfig;

    public void init(List<LocalFileMappingModel> mappings) {
        scriptInstallationCheck(mappings);
    }

    // 定时调度，查询脚本是否安装成功
    public void schedule() {

    }

    public List<CpNodeMonitorScriptConfigModel> listAll() {
        return monitorScriptConfigMapper.listAll();
    }


    public List<CpNodeMonitorScriptConfigModel> listByAgentName(String agentName) {
        return monitorScriptConfigMapper.listByAgentName(agentName);
    }


    public void restart(String hostname, List<String> serviceList) {
        List<MonitorConfig.MonitorProbe> cpSrvs = monitorConfig.getCpSrvMonitors();
        List<MonitorConfig.MonitorProbe> bizSrvs = monitorConfig.getBizSrvMonitors();

        if (CollectionUtil.isEmpty(cpSrvs)) {
            return;
        }

        String cpLaunchId = cpSrvs.get(0).getLaunchId();
        String bizLaunchId = bizSrvs.get(0).getLaunchId();
        String srvs = StringUtils.join(serviceList, ",");
        List<String> launchIds = Arrays.asList(cpLaunchId, bizLaunchId);

        for (String launchId : launchIds) {
            String cmd = MonitorConstant.BIN_TOOLS_DIR + "/python27/python " + MonitorConstant.BIN_TOOLS_DIR + File.separator + launchId + ".py restart " + srvs;
            log.info("hostname:{}, cmd:{}" + hostname, cmd);
            var runCommandItem = new WazuhLauncher.RunCommandItem();
            runCommandItem.setAgentName(hostname);
            runCommandItem.setOsType(MonitorConstant.OS_TYPE_LINUX);
            runCommandItem.setCommand(cmd);
            WazuhControlCommon.wazuhControl.getWazuhLauncher().runCommand(List.of(runCommandItem));
        }
    }


    private void scriptInstallationCheck(List<LocalFileMappingModel> mappings) {
        BasePageBO<HypervisorBO> model = hypervisorConverter.page(new BasePageDTO(100, 1));

        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

        if (CollectionUtils.isNotEmpty(model.getList())) {

            List<CpNodeMonitorScriptConfigModel> configModelList = listAll();
            // 检查每个节点，是否安装了脚本
            List<String> hostNames = model.getList().stream().map(HypervisorBO::getName).collect(Collectors.toList());
            List<String> notInstalledHosts = new ArrayList<>();

            if (CollectionUtils.isEmpty(configModelList)) {
                notInstalledHosts = hostNames;
            } else {
                for (String nodeName : hostNames) {
                    for (CpNodeMonitorScriptConfigModel config : configModelList) {
                        if (config.getHostname().equals(nodeName)) {
                            if (!config.getStatus().equals(CpNodeMonitorScriptConfigModel.STATUS_INSTALLED)) {
                                notInstalledHosts.add(nodeName);
                            }
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(notInstalledHosts)) {
                // 发送安装指令
                notInstalledHosts.forEach(hostName -> {
                    sendScriptInstallCmd(hostName, nameFileMap);
                    updateScriptConfigByHostName(hostName);
                });
            }

        }
    }

    private void sendScriptInstallCmd(String hostname, Map<String, String> nameFileMap) {
        WazuhControl.ControlConfigParam configParam = new WazuhControl.ControlConfigParam();


        List<MonitorConfig.MonitorProbe> cpSrvs = monitorConfig.getCpSrvMonitors();
        List<MonitorConfig.MonitorProbe> bizSrvs = monitorConfig.getBizSrvMonitors();

        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>();
        for (MonitorConfig.MonitorProbe cpSrvProbe : cpSrvs) {
            controlProbeParams.add(generateControlProbeParam(cpSrvProbe, nameFileMap));
        }

        for (MonitorConfig.MonitorProbe bizSrvProbe : bizSrvs) {
            controlProbeParams.add(generateControlProbeParam(bizSrvProbe, nameFileMap));
        }

        configParam.setProbeParams(controlProbeParams);
        // 控制节点和计算节点，hostname = agentName
        WazuhControlCommon.wazuhControl.controlConfig(hostname, MonitorConstant.OS_TYPE_LINUX, configParam);
    }


    private WazuhControl.ControlProbeParam generateControlProbeParam(MonitorConfig.MonitorProbe monitorProbe, Map<String, String> nameFileMap) {

        WazuhControl.ControlProbeParam probeParam = new WazuhControl.ControlProbeParam();

        probeParam.setId(monitorProbe.getLaunchId());
        probeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        probeParam.setGroup("server");
        FastDfsConfig.Group group = fastDfsConfig.getGroupByName(FdfsGroupEnum.DEFAULT);
        String url = group.getInnerFileServerAddr() + File.separator + nameFileMap.get(monitorProbe.getProbeName());
        probeParam.setScriptUrl(url);
        probeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        probeParam.setOutputFormat(WazuhLauncher.CMD_OUTPUT_FORMAT_JSON);
        probeParam.setInterval(monitorProbe.getInterval());

        String[] params = {MonitorConstant.OP_STATUS, monitorProbe.getServiceList()};
        probeParam.setScriptParam(StringUtils.joinWith(StringUtils.SPACE, params));
        return probeParam;
    }

    private void updateScriptConfigByHostName(String hostName) {
        log.info("hotName:{}", hostName);
        List<CpNodeMonitorScriptConfigModel> scriptConfigModelList = listByAgentName(hostName);
        if (CollectionUtil.isEmpty(scriptConfigModelList)) {
            CpNodeMonitorScriptConfigModel configModel = new CpNodeMonitorScriptConfigModel();
            configModel.setAgentName(hostName);
            configModel.setHostname(hostName);
            // todo status installed
            configModel.setStatus(CpNodeMonitorScriptConfigModel.STATUS_INSTALLED);
            monitorScriptConfigMapper.save(configModel);
        }
    }

}
