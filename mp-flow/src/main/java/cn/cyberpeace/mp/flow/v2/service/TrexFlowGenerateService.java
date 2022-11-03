package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.cloud.VirtualMachine;
import cn.cyberpeace.mp.flow.v2.client.trex.FlowResourceData;
import cn.cyberpeace.mp.flow.v2.client.trex.NetworkInfos;
import cn.cyberpeace.mp.flow.v2.client.trex.StartTaskData;
import cn.cyberpeace.mp.flow.v2.client.trex.TaskActionData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
public class TrexFlowGenerateService implements IGenerateService{
    private static final Integer DURATION = 50;
    @Override
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        // 上传flow配置
        List<FlowResourceData> flowResources = new ArrayList<>();
        List<TrexClient.ProtocolInfo> protocolInfos = new ArrayList<>();

        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                FlowResourceData resource = new FlowResourceData();
                byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
                resource.setFile(fileByte);
                resource.setFlowId(flow.getFlowId());
                resource.setFileName(flow.getName());
                flowResources.add(resource);
            } else {
                TrexClient.ProtocolInfo protocolInfo = new TrexClient.ProtocolInfo();
                protocolInfos.add(protocolInfo);
            }
        }
        // 创建网卡
        VirtualMachine virtualMachine = new VirtualMachine();

        var addPortResult = virtualMachine.addPort(generatorBO.getResourceId(), flowTaskRecordBO.getTargetNetworkId());

        // 初始化trex服务
        NetworkInfos networkInfos = new NetworkInfos();
        NetworkInfos.InitInfoData initInfoData = new NetworkInfos.InitInfoData();
        initInfoData.setTaskId(flowTaskRecordBO.getId());
        initInfoData.setMac(addPortResult.getMacAddress());//addPortResult.getMacAddress()
        networkInfos.getNetworkInfos().add(initInfoData);
        trexClient.initTrexServer(networkInfos);

        // 上传 pcap
        trexClient.uploadResource(flowResources);

        // 计算负载
        List<TrexClient.LoadInfo> loadInfos = trexClient.calculateLoad(DURATION, flowTaskRecordBO.getBandwidth(), flowTaskRecordBO.getLoadMode());

        // 启动流量
        StartTaskData startInfoData = new StartTaskData();
        startInfoData.setId(flowTaskRecordBO.getId());
        startInfoData.setAction(TaskActionData.Action.START);
        startInfoData.setTime(LocalDateTime.now());
//        startInfoData.setDuration(DURATION);
        startInfoData.setBandwidth(flowTaskRecordBO.getBandwidth());
        startInfoData.setTargetIp(flowTaskRecordBO.getTargetIp());

        var flows = flowTaskRecordBO.getFlows().stream().map(f -> {
            StartTaskData.Flow taskFlow = new StartTaskData.Flow();
            taskFlow.setFlowId(f.getFlowId());
            taskFlow.setWeight(f.getWeight());
            taskFlow.setResourceType(f.getResourceType());;
            taskFlow.setDelay(f.getDelay());
            return taskFlow;
        }).collect(Collectors.toList());
        startInfoData.getFlows().addAll(flows);

        startInfoData.getLoadLists().addAll(loadInfos);

        trexClient.startTask(startInfoData);

        return new TrexFlowCallBackTask(flowTaskRecordBO, generatorBO);
    }

    @Override
    public FlowCallBackTask stopFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        TaskActionData taskActionData = new TaskActionData();
        taskActionData.setAction(TaskActionData.Action.STOP);
        taskActionData.setId(flowTaskRecordBO.getId());
        trexClient.doTaskAction(taskActionData);
        return null;
    }

    @Override
    public FlowCallBackTask pauseFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        TaskActionData taskActionData = new TaskActionData();
        taskActionData.setAction(TaskActionData.Action.PAUSE);
        taskActionData.setId(flowTaskRecordBO.getId());
        trexClient.doTaskAction(taskActionData);
        return null;
    }

    @Override
    public FlowCallBackTask unpauseFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        TaskActionData taskActionData = new TaskActionData();
        taskActionData.setAction(TaskActionData.Action.UNPAUSE);
        taskActionData.setId(flowTaskRecordBO.getId());
        trexClient.doTaskAction(taskActionData);

        return new TrexFlowCallBackTask(flowTaskRecordBO, generatorBO);
    }

    @Override
    public FlowCallBackTask restartFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        stopFlowTask(flowTaskRecordBO, generatorBO);
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        // 执行同一个流量任务,直接传递流量信息执行即可，不用初始化trex/上传pcap文件
        List<TrexClient.LoadInfo> loadInfos = trexClient.calculateLoad(DURATION, flowTaskRecordBO.getBandwidth(), flowTaskRecordBO.getLoadMode());
        // 启动流量
        StartTaskData startInfoData = new StartTaskData();
        startInfoData.setId(flowTaskRecordBO.getId());
        startInfoData.setAction(1);
        startInfoData.setTime(LocalDateTime.now());
//        startInfoData.setDuration(DURATION);
        startInfoData.setBandwidth(flowTaskRecordBO.getBandwidth());
        startInfoData.setTargetIp(flowTaskRecordBO.getTargetIp());

        var flows = flowTaskRecordBO.getFlows().stream().map(f -> {
            StartTaskData.Flow taskFlow = new StartTaskData.Flow();
            taskFlow.setFlowId(f.getFlowId());
            taskFlow.setWeight(f.getWeight());
            taskFlow.setResourceType(f.getResourceType());;
            taskFlow.setDelay(f.getDelay());
            return taskFlow;
        }).collect(Collectors.toList());
        startInfoData.getFlows().addAll(flows);

        startInfoData.getLoadLists().addAll(loadInfos);

        trexClient.startTask(startInfoData);

        return new TrexFlowCallBackTask(flowTaskRecordBO, generatorBO);
    }

}
