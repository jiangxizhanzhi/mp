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
package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.protocolprobe.ProtocolProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.SSHProbe;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.service.FlowScriptParamService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskServiceImpl;
import cn.cyberpeace.mp.flow.util.SshUtil;
import cn.cyberpeace.mp.scenepluginflow.util.RemoteServerUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class TopoService {

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private FlowTaskPortMapper flowTaskPortMapper;

    @Resource
    private FlowInfoMapper flowInfoMapper;

    @Resource
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Resource
    private FlowScriptParamService flowScriptParamService;

    @Resource
    private FlowTaskConfMapper flowTaskConfMapper;

    @Resource
    private FlowTaskTaskServiceImpl flowTaskTaskService;

    @Resource
    private FlowTaskRecordMapper flowTaskRecordMapper;

    public List<FlowActuatorResponse> getFlowActuator() {
        var list = flowActuatorMapper.list(new HumanCondition());
        List<FlowActuatorResponse> responses = new ArrayList<>();
        for (var m : list) {
            FlowActuatorResponse response = new FlowActuatorResponse();
            response.setStatus(m.getStatus());
            response.setIp(m.getIp());
            response.setCreateTime(TimeUtils.localDateTimeToString(m.getCreateTime()));
            response.setTargetSubnet(m.getNetworkId());
            response.setTitle(m.getTitle());
            response.setId(m.getId());
            responses.add(response);
        }
        return responses;
    }

    public FlowActuatorModel createFlowTrex() {
        // 提交创建请求
        var instance = RemoteServerUtil.createFlowTopo();
        String instanceId = instance.getId();

        FlowActuatorModel actuatorModel = new FlowActuatorModel();
        actuatorModel.setId(instanceId);
        actuatorModel.setStatus(0);
        actuatorModel.setTitle("");
        flowActuatorMapper.save(actuatorModel);
        return actuatorModel;
    }

    public void addTasks(List<AddFlowTaskFlowInfo> tasks, String actuatorId, String networkId, String targetIp) {
        // 判断网络是否已经接入
        HumanCondition portCondition = new HumanCondition().filterEqual(FlowTaskPortModel::getActuatorId, actuatorId)
                .filterEqual(FlowTaskPortModel::getNetworkId, networkId);
        var portModel = flowTaskPortMapper.first(portCondition);
        String mac = "" ;
        if (portModel == null) {
            // 无数据 说明未接入网络 ， 需要先接入网络
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            flowActuatorModel.getInstanceId();
            mac = RemoteServerUtil.addPort(flowActuatorModel.getInstanceId(), networkId);
            portModel = new FlowTaskPortModel();
            portModel.setActuatorId(actuatorId);
            portModel.setNetworkId(networkId);
            portModel.setMac(mac);
            flowTaskPortMapper.save(portModel);
        }

        List<String> flowIds = new ArrayList<>();
        for (AddFlowTaskFlowInfo t : tasks) {
            flowIds.add(t.getFlowId());
        }
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowInfoModel::getId, flowIds.toArray());
        List<FlowInfoModel> flowInfoModelList = flowInfoMapper.list(condition);

        // 存任务
        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (FlowInfoModel flowInfoModel : flowInfoModelList) {
            FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
            flowTaskTaskModel.setSceneId("");
            flowTaskTaskModel.setActuatorId(actuatorId);
            flowTaskTaskModel.setResourceType(flowInfoModel.getResourceType());
            flowTaskTaskModel.setInteractiveType(flowInfoModel.getInteractiveType());
            flowTaskTaskModel.setFlowId(flowInfoModel.getId());
            flowTaskTaskModel.setPortId(portModel.getId());
            flowTaskTaskModel.setNodeId("");
            flowTaskTaskModel.setNodeIp(targetIp);
            flowTaskTaskModel.setNodeName("");
            flowTaskTaskModelList.add(flowTaskTaskModel);
        }
        flowTaskTaskMapper.bulkCreate(flowTaskTaskModelList);
        // 存任务配置
        Map<String, AddFlowTaskFlowInfo> addFlowTaskFlowInfoMap = new HashMap<>();
        for (AddFlowTaskFlowInfo f : tasks) {
            addFlowTaskFlowInfoMap.put(f.getFlowId(), f);
        }
        List<FlowTaskConfModel> flowTaskConfModelList = new ArrayList<FlowTaskConfModel>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            FlowTaskConfModel flowTaskConfModel = new FlowTaskConfModel();
            flowTaskConfModel.setTaskId(flowTaskTaskModel.getId());
            flowTaskConfModel.setKeepRunning(0);
            flowTaskConfModel.setReplay(1);
            flowTaskConfModel.setRateUnlimited(1);
            FlowTaskConf flowTaskConf = new FlowTaskConf();
            if (ResourceTypeEnum.SCRIPT.toValue() == flowTaskTaskModel.getResourceType()) {
                List<FlowTaskAttackConf> attackConfs = addFlowTaskFlowInfoMap.get(flowTaskTaskModel.getFlowId())
                        .getAttackConfs();
                if (CollectionUtils.isNotEmpty(attackConfs)) {
                    flowTaskConf.setAttackConfs(attackConfs);
                } else {
                    List<FlowScriptParamModel> flowScriptParamModelList = flowScriptParamService.selectByFlowId(flowTaskTaskModel.getFlowId());
                    if (CollectionUtils.isNotEmpty(flowScriptParamModelList)) {
                        List<FlowTaskAttackConf> flowTaskAttackConfList = new ArrayList<FlowTaskAttackConf>();
                        for (FlowScriptParamModel flowScriptParamModel : flowScriptParamModelList) {
                            FlowTaskAttackConf flowTaskAttackConf = new FlowTaskAttackConf();
                            flowTaskAttackConf.setConfDesc(flowScriptParamModel.getParamDesc());
                            flowTaskAttackConf.setConfType(flowScriptParamModel.getParamType());
                            flowTaskAttackConf.setConfKey(flowScriptParamModel.getParamKey());
                            flowTaskAttackConf.setConfValue(flowScriptParamModel.getParamValue());
                            flowTaskAttackConfList.add(flowTaskAttackConf);
                        }
                        flowTaskConf.setAttackConfs(flowTaskAttackConfList);
                    }
                }
            }
            flowTaskConfModel.setConf(JSONObject.toJSONString(flowTaskConf));
            flowTaskConfModelList.add(flowTaskConfModel);
        }
        flowTaskConfMapper.bulkCreate(flowTaskConfModelList);
        // 初始化网口
        List<String> taskIdList = new ArrayList<>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            taskIdList.add(flowTaskTaskModel.getId());
        }

        if (!flowTaskTaskService.initNetwork(portModel.getMac(), taskIdList, targetIp, actuatorId, taskIdList.size())) {
            log.error("init network error");
            throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
        }
        //上传pcap包虚拟机
        for (FlowInfoModel flowInfoModel : flowInfoModelList) {
            if (!flowTaskTaskService.uploadPcapToTrex(flowInfoModel, actuatorId)) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
        }
    }

    @Transactional
    public void deleteTasks(List<String> ids) {
        flowTaskTaskMapper.bulkDelete(ids.toArray(new String[]{}));
        flowTaskConfMapper.filterDelete(new HumanCondition().filterIn(FlowTaskConfModel::getTaskId, ids.toArray(new String[]{})));
        flowTaskRecordMapper.filterDelete(new HumanCondition().filterIn(FlowTaskRecordModel::getTaskId, ids.toArray(new String[]{})));
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void queryFlowActuator() {
        HumanCondition condition = new HumanCondition().filterEqual(FlowActuatorModel::getStatus, 0);
        var list = flowActuatorMapper.list(condition);
        for (var m : list) {
            if (StringUtils.isEmpty(m.getIp())) {
                var instance = RemoteServerUtil.getTopo(m.getCsceneId());
                String flowNodeResourceId = RemoteServerUtil.getFlowNodeResourceId(instance);
                String ip = RemoteServerUtil.getFlowNodeIp(instance);
                String netNodeResourceId = RemoteServerUtil.getFlowNodeNetResourceId(instance);
                if (StringUtils.isNotEmpty(flowNodeResourceId)) {
                    m.setIp(ip);
                    m.setInstanceId(flowNodeResourceId);
                    m.setNetworkId(netNodeResourceId);
                    flowActuatorMapper.update(m);
                }
            } else {
                String ip = m.getIp();
                ProtocolProbe protocolProbe = new SSHProbe(ip, 22);
                if (protocolProbe.probe()) {
                    // 更新trex代码
                    String commands = "./init.sh";
                    SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
                    serverInfo.setHost(ip);
                    serverInfo.setUser("root");
                    serverInfo.setPassword("111111");
                    String TGN_PATH = "D:\\MJ\\code2024\\30s\\mp\\mp-scene-plugin-flow\\src\\main\\resources\\traffic_generators.zip";
                    SshUtil.refreshDir(serverInfo, commands, TGN_PATH, "/opt/project_dirs");
                    m.setStatus(1);
                    flowActuatorMapper.update(m);
                }
            }
        }
    }
}
