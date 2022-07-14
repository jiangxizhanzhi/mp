package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.CpNodeMonitorScriptConfigModel;
import cn.cyberpeace.mp.devops.cluster.repository.CpNodeMonitorScriptConfigMapper;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControlCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon.WAZUH_AGENT_GROUP;

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

    public void init() {
        scriptInstallationCheck();
    }

    public void scriptInstallationCheck() {
        BasePageBO<HypervisorBO> model = hypervisorConverter.page(new BasePageDTO(100, 0));
        if (CollectionUtils.isNotEmpty(model.getList())) {

            List<CpNodeMonitorScriptConfigModel> scriptConfigModelList = listAll();
            // 检查每个节点，是否安装了脚本
            List<String> hostNames = model.getList().stream().map(HypervisorBO::getName).collect(Collectors.toList());
            List<String> notInstalledHosts = new ArrayList<>();

            if (CollectionUtils.isEmpty(scriptConfigModelList)) {
                notInstalledHosts = hostNames;
            } else {
                for (String nodeName : hostNames) {
                    for (CpNodeMonitorScriptConfigModel config : scriptConfigModelList) {
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
                    sendScriptInstallCmd(hostName);
                    updateScriptConfigByHostName(hostName);
                });
            }

        }
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


    private void sendScriptInstallCmd(String hostname) {
        WazuhControl.ControlConfigParam configParam = new WazuhControl.ControlConfigParam();
        String probeName = "monitor_cp_service.py";
        LocalFileMappingModel fileMappingModel = LocalFileMappingCommon.getMapping(WAZUH_AGENT_GROUP, probeName);
        WazuhControl.ControlProbeParam probeParam = generateControlProbeParam(hostname, fileMappingModel);
        configParam.setProbeParams(Arrays.asList(probeParam));
        WazuhControlCommon.wazuhControl.controlConfig(hostname, "linux", configParam);
    }


    private WazuhControl.ControlProbeParam generateControlProbeParam(String agentName, LocalFileMappingModel fileMappingModel) {

        String launchId = "AAAABBBBCCCC";

        WazuhControl.ControlProbeParam controlConfigParam = new WazuhControl.ControlProbeParam();

        controlConfigParam.setId(launchId);
        controlConfigParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        controlConfigParam.setGroup(agentName);
        controlConfigParam.setScriptUrl(fileMappingModel.getRemoteFile());
        //if (scriptType.equals(WazuhControl.SCRIPT_SUFFIX_BAT)) {
        //    controlConfigParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        //} else if (scriptType.equals(WazuhControl.SCRIPT_SUFFIX_PY)) {
        //    controlConfigParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        //} else if (scriptType.equals(WazuhControl.SCRIPT_SUFFIX_SH)) {
        //    controlConfigParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_SH);
        //}
        controlConfigParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        controlConfigParam.setOutputFormat("json");
        controlConfigParam.setInterval("180");

        controlConfigParam.setScriptParam("TestParam1");

        return controlConfigParam;
    }

    private void updateScriptConfigByHostName(String hostName) {
        log.info("hotName:{}", hostName);
    }

}
