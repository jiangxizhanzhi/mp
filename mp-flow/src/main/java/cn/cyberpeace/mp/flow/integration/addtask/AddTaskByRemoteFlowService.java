package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.FlowCategory;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.integration.FlowClient;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.TopoCloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import cn.cyberpeace.mp.flow.response.RemoteFlowInfo;
import cn.cyberpeace.mp.flow.response.ScriptParamModel;
import cn.cyberpeace.mp.flow.service.FlowCsceneService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service(value = "addTaskByRemoteFlowService")
public class AddTaskByRemoteFlowService implements IAddTaskService {

    @Resource
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Resource
    private FlowTaskPortMapper flowTaskPortMapper;

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private FlowTaskConfMapper flowTaskConfMapper;

    @Resource
    private ICloudClient cloudClient;

    @Resource
    private FlowTaskTaskService flowTaskTaskService;

    @Resource
    private FlowCsceneService flowCsceneService;

    @Override
    public void addTasksToDb(FlowActuatorModel flowActuatorModel) {
        // 空方法 该service添加流量任务时，逻辑都在 addTasksToActuator
    }

    @Override
    public void addTasksToActuator(FlowActuatorModel flowActuatorModel) {
        log.info("准备创建任务");
        var map = this.getCsceneConfigFlowTaskParams(flowActuatorModel);
        var instance = TopoCloudClient.getCscene(flowActuatorModel.getCsceneInstanceId());
        this.loadNetworkInfo(map, instance);
        log.info("任务内容: {}", JsonCommon.stringify(map));
        for (var e : map.values()) {
            this.addTasks(e.getTasks(), e.getActuatorId(), e.getNetworkId(), e.getTargetIp());
        }
        flowActuatorModel.setCsceneInstanceName(instance.getScene().getTopology().getScene().getName());
        flowActuatorModel.setStatus(1);
        flowActuatorMapper.update(flowActuatorModel);
    }

    public void addTasks(List<AddFlowTaskFlowInfo> tasks, String actuatorId, String networkId, String targetIp) {
        Set<String> flowIds = new HashSet<>();
        for (AddFlowTaskFlowInfo task : tasks) {
            flowIds.add(task.getFlowId());
        }

        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
        // 获取trex虚拟机上和目标网络相连的端口信息 如果没有则会自动创建一个
        var portModel = this.getOrCreateActuatorFlowPort(actuatorId, networkId, flowActuatorModel.getResourceId());

        // 从远程服务上查询流量的信息
        var remoteFlowInfos = this.getRemoteFlowInfos(flowIds);

        // 创建流量任务
        var flowTaskTaskModelList = this.createFlowTasks(actuatorId, portModel.getId(), targetIp, remoteFlowInfos);

        // 创建任务配置
        var flowTaskConfs = this.createTaskConfigs(flowTaskTaskModelList, tasks, remoteFlowInfos);

        // 初始化网口
        List<String> taskIdList = new ArrayList<>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            taskIdList.add(flowTaskTaskModel.getId());
        }
        FlowActuator flowActuator = new FlowActuator(flowActuatorModel.getIp(), flowTaskTaskService);
        boolean initNetwork = flowActuator.initNetwork(portModel.getMac(), targetIp, taskIdList);
        if (!initNetwork) {
            log.error("init network error");
            throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
        }

        //上传pcap包或脚本到trex虚拟机
        var flowConfig = BeanCommon.getBean(FlowConfig.class);
        for (var flow : remoteFlowInfos) {
            // 下载文件
            int resourceType = StringUtils.isNotEmpty(flow.getPcapFile()) ? 0 : 1;
            String fileKey = resourceType == 0 ? flow.getPcapFile() : flow.getFile();
            fileKey = fileKey.replace("\\", "/");
            var bytes = HttpUtils.downloadFileToByteArray(flowConfig.getCsceneUrl() + BaseAppConfig.API_PREFIX + "/file/" + fileKey);
            List<FlowActuator.ProtocolRate> protocolRates = new ArrayList<>();
            if (flow.getProtocolList() != null) {
                for (var protocol : flow.getProtocolList()) {
                    protocolRates.add(new FlowActuator.ProtocolRate(protocol.getProtocolName(), protocol.getRate()));
                }
            }
            boolean uploadPcap = flowActuator.uploadPcapV2(bytes, flow.getFlowId(), flow.getTitle(), flow.getFileName(), resourceType, protocolRates);
            if (!uploadPcap) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
        }
    }

    private FlowTaskPortModel getOrCreateActuatorFlowPort(String actuatorId, String networkId, String actuatorResourceId) {
        // 判断网络是否已经接入
        HumanCondition portCondition = new HumanCondition().filterEqual(FlowTaskPortModel::getActuatorId, actuatorId)
                .filterEqual(FlowTaskPortModel::getNetworkId, networkId);
        var portModel = flowTaskPortMapper.first(portCondition);
        String mac = "";
        if (portModel == null) {
            AddVirtualNicParams params = new AddVirtualNicParams();
            params.setInstanceId(actuatorResourceId);
            params.setNetworkId(networkId);
            var res = cloudClient.addVirtualNic(params);
            mac = res.getMacAddress();
            portModel = new FlowTaskPortModel();
            portModel.setActuatorId(actuatorId);
            portModel.setNetworkId(networkId);
            portModel.setMac(mac);
            flowTaskPortMapper.save(portModel);
        }
        return portModel;
    }

    private List<RemoteFlowInfo> getRemoteFlowInfos(Set<String> flowIds) {
        List<RemoteFlowInfo> remoteFlowInfos = new ArrayList<>();
        for (var flowId : flowIds) {
            var flow = FlowClient.getRemoteFlowInfo(flowId);
            if (flow == null) {
                log.info("未查询到流量,flow id : {}", flowId);
            } else {
                remoteFlowInfos.add(flow);
            }
        }
        return remoteFlowInfos;
    }

    private List<FlowTaskTaskModel> createFlowTasks(String actuatorId, String portId, String targetIp, List<RemoteFlowInfo> remoteFlowInfos) {
        // 生成流量任务
        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (var flow : remoteFlowInfos) {
            FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
            flowTaskTaskModel.setSceneId("");
            flowTaskTaskModel.setActuatorId(actuatorId);
            flowTaskTaskModel.setFlowId(flow.getFlowId());
            flowTaskTaskModel.setPortId(portId);
            flowTaskTaskModel.setNodeId("");
            flowTaskTaskModel.setNodeIp(targetIp);
            flowTaskTaskModel.setNodeName("");
            flowTaskTaskModel.setInteractiveType(flow.getInteractiveType());
            flowTaskTaskModel.setResourceType(StringUtils.isNotEmpty(flow.getPcapFile()) ? 0 : 1);
            flowTaskTaskModelList.add(flowTaskTaskModel);
        }
        flowTaskTaskMapper.bulkCreate(flowTaskTaskModelList);
        return flowTaskTaskModelList;
    }

    private FlowTaskConfModel createTaskConfig(String taskId, int resourceType, List<FlowTaskAttackConf> inputAttackConf, List<ScriptParamModel> defaultScriptModels) {
        FlowTaskConfModel flowTaskConfModel = new FlowTaskConfModel();
        flowTaskConfModel.setTaskId(taskId);
        flowTaskConfModel.setKeepRunning(0);
        flowTaskConfModel.setReplay(1);
        flowTaskConfModel.setRateUnlimited(1);
        FlowTaskConf flowTaskConf = new FlowTaskConf();
        if (ResourceTypeEnum.SCRIPT.toValue() == resourceType) {
            // 优先使用场景页面输入的参数
            flowTaskConf.setAttackConfs(inputAttackConf);
            // 如果没有输入参数就用流量默认的参数
            if (CollectionUtils.isEmpty(flowTaskConf.getAttackConfs()) && defaultScriptModels != null) {
                List<FlowTaskAttackConf> flowTaskAttackConfList = new ArrayList<FlowTaskAttackConf>();
                for (ScriptParamModel model : defaultScriptModels) {
                    FlowTaskAttackConf conf = new FlowTaskAttackConf();
                    conf.setConfDesc(model.getParamDesc());
                    conf.setConfType(FlowCategory.paramTypeMap.get(model.getParamType()));
                    conf.setConfKey(model.getParamKey());
                    conf.setConfValue(model.getParamValue());
                    flowTaskAttackConfList.add(conf);
                }
                flowTaskConf.setAttackConfs(flowTaskAttackConfList);
            }
        }
        flowTaskConfModel.setConf(JSONObject.toJSONString(flowTaskConf));
        return flowTaskConfModel;
    }

    private List<FlowTaskConfModel> createTaskConfigs(List<FlowTaskTaskModel> flowTaskTaskModelList, List<AddFlowTaskFlowInfo> inputTasks, List<RemoteFlowInfo> remoteFlowInfos) {
        Map<String, AddFlowTaskFlowInfo> addFlowTaskMap = new HashMap<>();
        for (var t : inputTasks) {
            addFlowTaskMap.put(t.getFlowId(), t);
        }

        Map<String, RemoteFlowInfo> remoteFlowInfoMap = new HashMap<>();
        for (var flow : remoteFlowInfos) {
            remoteFlowInfoMap.put(flow.getFlowId(), flow);
        }

        List<FlowTaskConfModel> confModels = new ArrayList<>();
        for (var taskModel : flowTaskTaskModelList) {
            String taskId = taskModel.getId();
            int resourceType = taskModel.getResourceType();
            AddFlowTaskFlowInfo addFlowTaskFlowInfo = addFlowTaskMap.get(taskModel.getFlowId());
            RemoteFlowInfo remoteFlowInfo = remoteFlowInfoMap.get(taskModel.getFlowId());
            FlowTaskConfModel confModel = createTaskConfig(taskId, resourceType, addFlowTaskFlowInfo.getAttackConfs(), remoteFlowInfo.getScriptParamModels());
            confModels.add(confModel);
        }
        flowTaskConfMapper.bulkCreate(confModels);
        return confModels;
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
