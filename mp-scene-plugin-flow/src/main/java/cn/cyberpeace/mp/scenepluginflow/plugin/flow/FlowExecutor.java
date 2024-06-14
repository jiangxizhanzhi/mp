package cn.cyberpeace.mp.scenepluginflow.plugin.flow;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.BatchStartTaskBO;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.FlowTaskPortService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import cn.cyberpeace.mp.flow.util.SshUtil;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Template;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res.ExternalNetworkInfoRes;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Component
@Slf4j
public class FlowExecutor {
    private static FlowTaskModel DEFAULT_INSTANCE;

    private AsyncPlatFlowTaskService flowService1;

    private FlowActuatorService flowService2;

    private FlowTaskServiceImpl flowService3;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;
    @Autowired
    private FlowTaskPortService flowTaskPortService;

    private static final Network networkClient = new Network();
    private static final VirtualMachine vmClient = new VirtualMachine();
    private static final Template templateClient = new Template();
    private static final String TREX_DEVICE_NAME = "tgnTrex";
    private static final String TGN_PATH = "/root/.mp/resources/flows/traffic_generators.zip";


    @Autowired
    public FlowExecutor(AsyncPlatFlowTaskService flowService1, FlowActuatorService flowService2,
                        FlowTaskServiceImpl flowService3) {
        setFlowService1(flowService1);
        setFlowService2(flowService2);
        setFlowService3(flowService3);
    }

    public String createExecutor(String name, List<FlowNetworkInfo> networkInfos) {
        return flowService1.addExecutorSync(name, networkInfos);
    }

    public String startExecutor(String name) {
        String uuid = TextUtils.uuid();
        this.startExecutor(uuid, name);
        return uuid;
    }

    private void startExecutor(String uuid, String trexName) {
        log.info("addFlowExecutor name:{}", trexName);
        // 获取外部networkId，用于管理端口
        ExternalNetworkInfoRes externalNetworkInfoRes = networkClient.getExternalNetworkInfo();
        String trexTemplateId = templateClient.getTemplateIdByDeviceName(TREX_DEVICE_NAME);
        FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);


        //创建trex虚机
        VirtualMachineCreateData createData = new VirtualMachineCreateData();
        createData.setName(trexName);
        createData.setTemplateId(trexTemplateId);
        createData.setCpu(flowConfig.getTrexCpu());
        createData.setRam(flowConfig.getTrexRam());
        createData.setDisk(flowConfig.getTrexDisk());
        createData.setMainCloud(true);
        createData.setOsType(SystemTypeCommandUtils.OS_TYPE_LINUX);

        List<VirtualNic> networks = new ArrayList<>();

        VirtualNic externalNetwork = new VirtualNic();
        externalNetwork.setNetworkId(externalNetworkInfoRes.getId());
        externalNetwork.setGateway(externalNetworkInfoRes.getGatewayIp());
        externalNetwork.setCidr(externalNetworkInfoRes.getCidr());
        //todo 排除已经分配的
        externalNetwork.setFixedIp(IpUtils.newRandomIps(externalNetwork.getCidr(), 1, Collections.emptyList(), Collections.emptyList(), false).get(0));
        networks.add(externalNetwork);
        createData.setNetwork(networks);
        VirtualMachineCreateData trex = vmClient.create(createData);

        // 读取虚机的端口ip和mac地址
        VirtualNic virtualNic = trex.getNetwork().get(0);
        String ip = virtualNic.getFixedIp();
        String mac = virtualNic.getMacAddress();

        FlowActuatorModel flowActuatorModel = new FlowActuatorModel();
        // 新增流量执行器信息
        flowActuatorModel.setId(uuid);
        flowActuatorModel.setTitle(trexName);
        flowActuatorModel.setInstanceId(trex.getId());
        flowActuatorModel.setIp(ip);
        flowActuatorModel.setNetworkId(externalNetworkInfoRes.getId());
        flowActuatorMapper.create(flowActuatorModel);

        // 添加流量任务网口信息
        FlowTaskPortModel flowTaskPortModel = new FlowTaskPortModel();
        flowTaskPortModel.setActuatorId(uuid);
        flowTaskPortModel.setNetworkId(externalNetworkInfoRes.getId());
        flowTaskPortModel.setMac(mac);
        flowTaskPortService.create(flowTaskPortModel);

        // 轮询流量发生器端口，直到端口可访问才返回（轮询10分钟）
        String finalFixedIp = ip;
        boolean flag = NetworkUtils.probePort(ip, 22, 1000, 1000,
                1000 * 60 * 2, null, 0);
        if (!flag) {
            log.error("Trex vm start error:{}", trexName);
        }
        // 检查trex虚机状态
        checkStartTrex(finalFixedIp);

    }

    private void checkStartTrex(String fixedIp) {
        String commands = "./init.sh";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(fixedIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");
        SshUtil.refreshDir(serverInfo, commands, TGN_PATH, "/opt/project_dirs");

        boolean flag = NetworkUtils.probePort(fixedIp, 5001, 1000, 1000, 1000 * 60 * 2,
                null, 0);
        if (!flag) {
            log.error("Trex vm start error:{}", fixedIp);
        }
    }

    public void deleteExecutor(String executorId) {
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(executorId);
        String trexInstanceId = flowActuatorModel.getInstanceId();
        vmClient.deleteInstanceById(trexInstanceId);
    }


    public void startFlow(String taskId, String sourceIp) {
        ServiceResult<Boolean> result = flowService1.startTask(taskId, sourceIp);
//        var result = flowService3.actuator(taskId, TaskActionEnum.START.toValue(), stopCallback, errorCallback);
        if (!result.judgeSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void stopFlow(String taskId) {
//        var result = flowService3.actuator(taskId, TaskActionEnum.STOP.toValue(), null, null);
        ServiceResult<Boolean> result = flowService1.stopTask(taskId);
        if (!result.judgeSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void startFlows(BatchStartTaskBO batchStartTaskBO) {
        ServiceResult<Boolean> result = flowService1.batchStartTasks(null, batchStartTaskBO);
        if (!result.judgeSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void stopFlows(List<String> taskIds) {
//        BatchActuatorTaskRequest request = new BatchActuatorTaskRequest();
//        request.setTaskAction(TaskActionEnum.STOP.toValue());
//        request.setTaskIds(taskIds);
        ServiceResult<Boolean> result = flowService1.batchStopTasks(taskIds);
        if (!result.judgeSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void deleteFlow(String taskId) {
        ServiceResult<Boolean> result = flowService3.deleteFlowTask(taskId);
        if (!result.judgeSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public int updateFlowTaskInfo(FlowTaskModel flowTaskModel) {
        return this.flowService3.updateFlowTaskInfo(flowTaskModel);
    }

    public ServiceResult<FlowRecordsResponse> queryFlowRecords(String taskId) {
        return this.flowService3.queryFlowRecordsByTaskId(taskId);
    }

    public ServiceResult<FlowRecordsResponse> queryBatchFlowRecords(String sceneId) {
        return this.flowService3.queryBatchFlowRecords(sceneId);
    }

    private synchronized void initDefaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new FlowTaskModel();
            DEFAULT_INSTANCE.initDefault();
        }
    }

    public void addFlowTask(List<AddFlowTaskTaskInfo> details, String sceneId, String actuatorId) {
        flowTaskTaskService.addTask(details, sceneId, actuatorId, false, null);
    }

    public void deleteByNodeIdAndSceneId(String nodeId, String sceneId) {
        flowTaskTaskService.deleteByNodeIdAndSceneId(nodeId, sceneId);
    }

    public String getTaskIdByNodeId() {

        return null;
    }

}
