// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name VirtualMachine.java.
// File size 19310.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.job;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.protocolprobe.ProtocolProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.SSHProbe;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.TopoCloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorTaskService;
import cn.cyberpeace.mp.flow.service.FlowCsceneService;
import cn.cyberpeace.mp.flow.util.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VirtualMachine
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class FlowActuatorStateJob {

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private FlowCsceneService flowCsceneService;

    @Resource
    private FlowActuatorTaskService flowActuatorTaskService;

    @Resource
    private ICloudClient cloudClient;

    @Scheduled(cron = "0/5 * * * * ?")
    public void queryFlowActuator() {
        HumanCondition condition = new HumanCondition().filterEqual(FlowActuatorModel::getStatus, 0);
        var list = flowActuatorMapper.list(condition);
        for (var m : list) {
            log.info("开始处理执行器 {} ", m.getId());
            String actuatorIp = m.getIp();
            if (StringUtils.isEmpty(actuatorIp)) {
                // 没有ip先更新ip
                log.info("更新ip");
                this.updateActuatorIp(m);
            } else {
                // 检测ssh协议是否可用
                log.info("检测ssh是否可用");
                ProtocolProbe protocolProbe = new SSHProbe(actuatorIp, 22);
                if (!protocolProbe.probe()) {
                    log.info("完成处理执行器 {}  ， ssh还不可用", m.getId());
                    continue;
                }
                log.info("ssh 可用 ， 更新发生器代码");
                // 更新 发生器代码、创建任务等
                this.updateActuatorCodesAndServer(actuatorIp);
                log.info("准备创建任务");
                var map = this.getCsceneConfigFlowTaskParams(m);
                var instance = TopoCloudClient.getCscene(m.getCsceneId());
                this.loadNetworkInfo(map, instance);

                log.info("任务内容: {}", JsonCommon.stringify(map));
                for (var e : map.values()) {
                    flowActuatorTaskService.addTasks(e.getTasks(), e.getActuatorId(), e.getNetworkId(), e.getTargetIp());
                }

                m.setCsceneInstanceName(instance.getScene().getTopology().getScene().getName());
                m.setStatus(1);
                flowActuatorMapper.update(m);
            }
            log.info("完成处理执行器 {} ", m.getId());
        }
    }

    private void updateActuatorIp(FlowActuatorModel m) {
        var res = cloudClient.getVirtualMachine(m.getResourceId());
        if (res != null) {
            m.setIp(res.getNetworks().get(0).getFixedIp());
            m.setResourceId(res.getId());
            m.setNetworkId(res.getNetworks().get(0).getNetworkId());
            flowActuatorMapper.update(m);
        }
    }

    private void updateActuatorCodesAndServer(String actuatorIp) {
        // 更新trex代码和服务
        String commands = "./init.sh";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(actuatorIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");

        SshUtil.refreshDir(serverInfo, commands, "traffic_generators.zip", "/opt/project_dirs");
    }

    private void loadNetworkInfo(Map<String, AddFlowTaskParams> map, BusinessCSceneInstance instance) {
        var cloudPorts = instance.getScene().getPlugins().getCpcloud().getElementPorts();
        for (var e : instance.getScene().getTopology().getElements()) {
            if (!map.containsKey(e.getId())) {
                continue;
            }
            for (var p : e.getPorts()) {
                var cloudPort = cloudPorts.get(p.getId());
                String networkId = cloudPort.getAuxiliaryNetworkId();
                String targetIp = p.getIp();
                map.get(e.getId()).setTargetIp(targetIp);
                map.get(e.getId()).setNetworkId(networkId);
                break;
            }
        }
    }

    private Map<String, AddFlowTaskParams> getCsceneConfigFlowTaskParams(FlowActuatorModel m) {
        // 查询模板里的任务
        CsceneConfigFlowTaskRequest request = new CsceneConfigFlowTaskRequest();
        request.setId(m.getCsceneId());
        request.setPage(1);
        request.setPageSize(500);
        var res = flowCsceneService.list(request);
        var models = res.asResult().getData().getList();

        Map<String, AddFlowTaskParams> map = new HashMap<>();
        for (CsceneFlowTaskResponse e : models) {
            if (!map.containsKey(e.getCsceneNodeId())) {
                AddFlowTaskParams params = new AddFlowTaskParams();
                params.setActuatorId(m.getId());
                params.setTasks(new ArrayList<>());
                map.put(e.getCsceneNodeId(), params);
            }
            AddFlowTaskFlowInfo flowTaskFlowInfo = new AddFlowTaskFlowInfo();
            flowTaskFlowInfo.setFlowId(e.getFlowId());

            List<FlowTaskAttackConf> attackConfs = new ArrayList<>();
            if (e.getScriptParamModels() != null) {
                for (var conf : e.getScriptParamModels()) {
                    FlowTaskAttackConf attackConf = new FlowTaskAttackConf();
                    attackConf.setConfDesc(conf.getParamDesc());
                    attackConf.setConfType(Integer.valueOf(conf.getParamType()));
                    attackConf.setConfKey(conf.getParamKey());
                    attackConf.setConfValue(conf.getParamValue());
                    attackConfs.add(attackConf);
                }
            }
            flowTaskFlowInfo.setAttackConfs(attackConfs);
            map.get(e.getCsceneNodeId()).getTasks().add(flowTaskFlowInfo);
        }
        return map;
    }
}
