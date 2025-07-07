package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskRateConf;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.constant.FlowCategory;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.integration.FlowClient;
import cn.cyberpeace.mp.flow.integration.addtask.AddTaskByRemoteFlowService2;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.TopoCloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowPcapMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.CsceneInstanceAddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.CsceneInstanceFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneInstanceFlowTaskResponse;
import cn.cyberpeace.mp.flow.response.RemoteFlowInfo;
import cn.cyberpeace.mp.flow.response.ScriptParamModel;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class FlowCsceneInstanceService {

    @Resource
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private FlowTaskConfMapper flowTaskConfMapper;

    @Resource
    private FlowPcapMapper flowPcapMapper ;

    public ListObjServiceResult<CsceneInstanceFlowTaskResponse> list(CsceneInstanceFlowTaskRequest request) {
        var condition = new HumanCondition().filterEqual(FlowTaskTaskModel::getSceneId, request.getId());
        if (StringUtils.isNotEmpty(request.getNodeId())) {
            condition.filterEqual(FlowTaskTaskModel::getNodeId, request.getNodeId());
        }
        condition.pagination(request.getPage(), request.getPageSize());
        var total = flowTaskTaskMapper.total(condition);
        var list = flowTaskTaskMapper.list(condition);
        List<CsceneInstanceFlowTaskResponse> result = new ArrayList<>();
        List<String> taskIds = new ArrayList<>();
        for (var m : list) {
            CsceneInstanceFlowTaskResponse response = new CsceneInstanceFlowTaskResponse();
            taskIds.add(m.getId());
            response.setTaskId(m.getId());
            response.setCsceneInstanceId(m.getSceneId());
            response.setCsceneNodeId(m.getNodeId());
            response.setName(m.getFlowName());
            response.setFlowId(m.getFlowId());
            response.setInteractiveType(m.getInteractiveType());
            response.setResourceType(m.getResourceType());
            result.add(response);
        }
        if (CollectionUtils.isNotEmpty(taskIds)) {
            var taskConfigs = flowTaskConfMapper.list(new HumanCondition().filterIn(FlowTaskConfModel::getTaskId, taskIds.toArray(new String[]{})));
            Map<String, FlowTaskConfModel> taskId2Conf = new HashMap<>();
            for (FlowTaskConfModel t : taskConfigs) {
                taskId2Conf.put(t.getTaskId(), t);
            }
            for (var r : result) {
                if (taskId2Conf.containsKey(r.getTaskId())) {
                    var confModel = taskId2Conf.get(r.getTaskId());
                    if (StringUtils.isNotEmpty(confModel.getConf())) {
                        var conf = JsonCommon.parse(confModel.getConf(), FlowTaskConf.class);
                        if (conf.getRateConf() != null) {
                            r.setBandwidth(conf.getRateConf().getBandwidth());
                            r.setRate(conf.getRateConf().getRate());
                            r.setDesc(conf.getRateConf().getDesc());
                            r.setCategory(conf.getRateConf().getCategory());
                        }
                    }
                }
            }
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total)).asResult();
    }

    public ListObjServiceResult<CsceneInstanceFlowTaskResponse> add(CsceneInstanceAddFlowTaskRequest request) {
        var flowActuator = flowActuatorMapper.first(new HumanCondition().filterEqual(FlowActuatorModel::getCsceneInstanceId, request.getId()));
        log.info("查询到的执行器信息: {} ", JsonCommon.stringify(flowActuator));
        if (flowActuator == null) {
            log.info("执行器不存在,需要创建,先在数据库添加执行器信息，等待异步启动执行器");
            FlowActuatorModel actuatorModel = new FlowActuatorModel();
            actuatorModel.setStatus(0);
//        actuatorModel.setTitle(params.getActuatorName());
            var topo = TopoCloudClient.getCscene(request.getId());
            actuatorModel.setCsceneId(topo.getCscene());
            actuatorModel.setCsceneInstanceId(request.getId());
            actuatorModel.setResourceId("");
            actuatorModel.setNetworkId("");
            actuatorModel.setIp("");
            flowActuatorMapper.save(actuatorModel);
            flowActuator = flowActuatorMapper.first(new HumanCondition().filterEqual(FlowActuatorModel::getCsceneInstanceId, request.getId()));
            log.info("新建的执行器信息: {} ", JsonCommon.stringify(flowActuator));
        }
        log.info("先向数据库添加任务信息");
        String actuatorId = flowActuator.getId();
        log.info("开始处理执行器，在数据库中添加流量任务 {} ", flowActuator.getId());

        var instance = this.getInstanceWithNoException(flowActuator.getCsceneInstanceId(), 2);
        String nodeName = "";
        Set<String> nodeIds = new HashSet<>();
        if (null != instance && null != instance.getScene() && null != instance.getScene().getTopology()) {
            for (var p : instance.getScene().getTopology().getElements()) {
                nodeIds.add(p.getId());
                if (StringUtils.equals(p.getId(), request.getNodeId())) {
                    nodeName = p.getName();
                }
            }
        }

        AddFlowTaskParams addFlowTaskParams = new AddFlowTaskParams();
        addFlowTaskParams.setActuatorId(actuatorId);
        addFlowTaskParams.setNodeId(request.getNodeId());
        addFlowTaskParams.setCsceneInstanceId(request.getId());
        addFlowTaskParams.setNodeName(nodeName);
        addFlowTaskParams.setTasks(new ArrayList<>());
        for (var task : request.getTasks()) {
            AddFlowTaskFlowInfo flowTaskFlowInfo = new AddFlowTaskFlowInfo();
            flowTaskFlowInfo.setTaskId(task.getTaskId());
            flowTaskFlowInfo.setFlowId(task.getFlowId());
            flowTaskFlowInfo.setRate(task.getRate());
            flowTaskFlowInfo.setBandwidth(task.getBandwidth());
            flowTaskFlowInfo.setResourceType(task.getResourceType());

            List<FlowTaskAttackConf> attackConfs = new ArrayList<>();
            if (task.getScriptParamModels() != null) {
                for (var conf : task.getScriptParamModels()) {
                    FlowTaskAttackConf attackConf = new FlowTaskAttackConf();
                    attackConf.setConfDesc(conf.getParamDesc());
                    attackConf.setConfType(Integer.valueOf(conf.getParamType()));
                    attackConf.setConfKey(conf.getParamKey());
                    attackConf.setConfValue(conf.getParamValue());
                    attackConfs.add(attackConf);
                }
            }
            flowTaskFlowInfo.setAttackConfs(attackConfs);
            addFlowTaskParams.getTasks().add(flowTaskFlowInfo);
        }

        // 加载场节点对应的网络信息
        if (null != instance) {
            this.loadNetworkInfo(addFlowTaskParams, instance);
        }
        log.info("节点对应的网络信息：{}", JsonCommon.snakeStringify(addFlowTaskParams));

        var result = this.handleTaskAndGetNewTasks(actuatorId, addFlowTaskParams);
        log.info("结束处理执行器 {} 在数据库中添加流量任务", flowActuator.getId());

        List<CsceneInstanceFlowTaskResponse> responses = new ArrayList<>();
        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList();
        flowTaskTaskModelList.addAll(result.getAddResult());
        flowTaskTaskModelList.addAll(result.getUpdateResult());
        for (var m : flowTaskTaskModelList) {
            CsceneInstanceFlowTaskResponse response = new CsceneInstanceFlowTaskResponse();
            response.setTaskId(m.getId());
            response.setCsceneInstanceId(m.getSceneId());
            response.setCsceneNodeId(m.getNodeId());
            response.setName(m.getNodeName());
            response.setFlowId(m.getFlowId());
            response.setInteractiveType(m.getInteractiveType());
            responses.add(response);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(responses, responses.size())).asResult();
    }

    public void updateFlowActuatorDbStatus(String id, int status) {
        FlowActuatorModel updateModel = new FlowActuatorModel();
        updateModel.setId(id);
        updateModel.setStatus(status);
        flowActuatorMapper.update(updateModel);
    }

    private List<RemoteFlowInfo> getRemoteFlowInfos(List<String> flowIds) {
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

    public void loadNetworkInfo(AddFlowTaskParams map, BusinessCSceneInstance instance) {
        var cloudPorts = instance.getScene().getPlugins().getCpcloud().getElementPorts();
        for (var e : instance.getScene().getTopology().getElements()) {
            if (StringUtils.equals(map.getNodeId(), e.getId())) {
                for (var p : e.getPorts()) {
                    var cloudPort = cloudPorts.get(p.getId());
                    String networkId = cloudPort.getAuxiliaryNetworkId();
                    String targetIp = p.getIp();
                    map.setTargetIp(targetIp);
                    map.setNetworkId(networkId);
                }
            }
        }
    }

    public BusinessCSceneInstance getInstanceWithNoException(String instanceId, int tryCount) {
        BusinessCSceneInstance instance = null;
        for (int i = 0; i < tryCount; i++) {
            try {
                instance = TopoCloudClient.getCscene(instanceId);
                break;
            } catch (Exception e) {
                log.error("failed to query cscene instance , id is {}", instanceId, e);
            }
        }
        return instance;
    }

    private List<FlowTaskTaskModel> createFlowTasks(String actuatorId, String csceneInstanceId, String nodeId, String nodeName, String portId, String targetIp, String networkId, List<RemoteFlowInfo> remoteFlowInfos) {
        // 生成流量任务
        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (var flow : remoteFlowInfos) {
            FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
            flowTaskTaskModel.setSceneId("");
            flowTaskTaskModel.setActuatorId(actuatorId);
            flowTaskTaskModel.setFlowId(flow.getFlowId());
            flowTaskTaskModel.setPortId(portId);
            flowTaskTaskModel.setNodeId(nodeId);
            flowTaskTaskModel.setNodeIp(targetIp);
            flowTaskTaskModel.setSceneId(csceneInstanceId);
            flowTaskTaskModel.setNodeName("");
            flowTaskTaskModel.setNodeName(nodeName);
            flowTaskTaskModel.setNodeNetworkId(networkId);
            flowTaskTaskModel.setInteractiveType(flow.getInteractiveType());
            flowTaskTaskModel.setResourceType(StringUtils.isNotEmpty(flow.getPcapFile()) ? 0 : 1);
            // 设置成初始化中的状态
            flowTaskTaskModel.setTaskStatus(5);
            flowTaskTaskModelList.add(flowTaskTaskModel);
        }
        flowTaskTaskMapper.bulkCreate(flowTaskTaskModelList);
        return flowTaskTaskModelList;
    }

    public FlowTaskTaskModelResult handleTaskAndGetNewTasks(String actuatorId, AddFlowTaskParams addFlowTaskParams) {
        // 先得到数据库原有的任务
        var cond = new HumanCondition().filterEqual(FlowTaskTaskModel::getSceneId, addFlowTaskParams.getCsceneInstanceId())
                .filterEqual(FlowTaskTaskModel::getNodeId, addFlowTaskParams.getNodeId());
        var dbTasks = flowTaskTaskMapper.list(cond);

        // 区分新增、更新的流量任务
        AddFlowTaskParams addTaskParams = new AddFlowTaskParams();
        AddFlowTaskParams updateTaskParams = new AddFlowTaskParams();
        BeanUtils.copyProperties(addFlowTaskParams, addTaskParams);
        addTaskParams.setTasks(new ArrayList<>());
        BeanUtils.copyProperties(addFlowTaskParams, updateTaskParams);
        updateTaskParams.setTasks(new ArrayList<>());
        for (var task : addFlowTaskParams.getTasks()) {
            if (StringUtils.isNotEmpty(task.getTaskId())) {
                updateTaskParams.getTasks().add(task);
            } else {
                addTaskParams.getTasks().add(task);
            }
        }

        // 区分删除的任务
        Set<String> updateTaskIds = new HashSet<>();
        for (AddFlowTaskFlowInfo d : updateTaskParams.getTasks()) {
            updateTaskIds.add(d.getTaskId());
        }
        Set<String> toDeleteTaskIds = new HashSet<>();
        for (FlowTaskTaskModel d : dbTasks) {
            toDeleteTaskIds.add(d.getId());
        }
        toDeleteTaskIds.removeAll(updateTaskIds);

        // 删除
        if (CollectionUtils.isNotEmpty(toDeleteTaskIds)) {
            flowTaskTaskMapper.bulkDelete(toDeleteTaskIds.toArray(new String[]{}));
            flowTaskConfMapper.filterDelete(new HumanCondition().filterIn(FlowTaskConfModel::getTaskId, toDeleteTaskIds.toArray(new String[]{})));
        }
        FlowTaskTaskModelResult result = new FlowTaskTaskModelResult();
        // 更新
        if (CollectionUtils.isNotEmpty(updateTaskParams.getTasks())) {
            List<FlowTaskTaskModel> updateModels = new ArrayList<>();
            for (var m : dbTasks) {
                if (updateTaskIds.contains(m.getId())) {
                    updateModels.add(m);
                }
            }
            var updateResult = this.updateTaskAndConfig(updateModels, updateTaskParams);
            result.setUpdateResult(updateResult);
        }
        // 新增
        if (CollectionUtils.isNotEmpty(addTaskParams.getTasks())) {
            List<FlowTaskTaskModel> addResult = this.addTaskAndConfig(actuatorId, addTaskParams);
            result.setAddResult(addResult);
        }
        return result;
    }

    private List<FlowTaskTaskModel> addTaskAndConfig(String actuatorId, AddFlowTaskParams addFlowTaskParams) {
        List<String> flowIds = new ArrayList<>();
        for (var task : addFlowTaskParams.getTasks()) {
            flowIds.add(task.getFlowId());
        }
        // 从远程服务上查询流量的信息
        var remoteFlowInfos = this.getRemoteFlowInfos(flowIds);
        // 创建流量任务
        var flowTaskTaskModelList = this.createFlowTasks(actuatorId, addFlowTaskParams.getCsceneInstanceId(), addFlowTaskParams.getNodeId(), addFlowTaskParams.getNodeName(), "", addFlowTaskParams.getTargetIp(), addFlowTaskParams.getNetworkId(), remoteFlowInfos);
        // 创建任务配置
        this.createTaskConfigs(flowTaskTaskModelList, addFlowTaskParams.getTasks(), remoteFlowInfos);
        return flowTaskTaskModelList;
    }

    private List<FlowTaskTaskModel> updateTaskAndConfig(List<FlowTaskTaskModel> updateModels, AddFlowTaskParams addFlowTaskParams) {
        List<String> flowIds = new ArrayList<>();
        for (var task : addFlowTaskParams.getTasks()) {
            flowIds.add(task.getFlowId());
        }
        List<String> taskIds = new ArrayList<>();
        for (AddFlowTaskFlowInfo t : addFlowTaskParams.getTasks()) {
            taskIds.add(t.getTaskId());
        }
        // 从远程服务上查询流量的信息
        var remoteFlowInfos = this.getRemoteFlowInfos(flowIds);
        // 删除旧配置
        flowTaskConfMapper.filterDelete(new HumanCondition().filterIn(FlowTaskConfModel::getTaskId, taskIds.toArray(new String[]{})));
        // 加新配置
        this.createTaskConfigs(updateModels, addFlowTaskParams.getTasks(), remoteFlowInfos);
        return updateModels;
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
            addFlowTaskFlowInfo.setDesc(remoteFlowInfo.getFlowDesc());
            addFlowTaskFlowInfo.setCategory(remoteFlowInfo.getCategory());
            FlowTaskConfModel confModel = createTaskConfig(taskId, resourceType, addFlowTaskFlowInfo, remoteFlowInfo.getScriptParamModels());
            confModels.add(confModel);

            // 更新或创建流量pcap信息
            if (resourceType == ResourceTypeEnum.PACP.toValue()) {
                FlowPcapModel pcapModel = new FlowPcapModel();
                pcapModel.setFlowId(taskModel.getFlowId());
                pcapModel.setDuration(remoteFlowInfo.getDuration());
                pcapModel.setPcapSize(remoteFlowInfo.getPcapSize());
                pcapModel.setPktNum(remoteFlowInfo.getPktNum());
                List<String> protocols = new ArrayList<>();
                if (remoteFlowInfo.getProtocolList() != null) {
                    for (ProtocolBO p : remoteFlowInfo.getProtocolList()) {
                        protocols.add(p.getProtocolName());
                    }
                }
                pcapModel.setProtocols(JsonCommon.stringify(protocols));
                pcapModel.setPcapFile(remoteFlowInfo.getPcapFile());
                FlowPcapModel existedFlowPcap = getFlowPcapModelByFlowId(taskModel.getFlowId());
                if (Objects.isNull(existedFlowPcap)) {
                    flowPcapMapper.create(pcapModel);
                } else {
                    pcapModel.setId(existedFlowPcap.getId());
                    flowPcapMapper.update(pcapModel);
                }
            }
        }
        flowTaskConfMapper.bulkCreate(confModels);
        return confModels;
    }

    private FlowPcapModel getFlowPcapModelByFlowId(String flowId) {
        if (StringUtils.isEmpty(flowId)) {
            return null;
        } else {
            var flowPcapModels = flowPcapMapper.list(new HumanCondition().filterEqual(FlowPcapModel::getFlowId, flowId));
            if (CollectionUtils.isEmpty(flowPcapModels)) {
                return null;
            } else {
                return flowPcapModels.get(0);
            }
        }
    }

    private FlowTaskConfModel createTaskConfig(String taskId, int resourceType, AddFlowTaskFlowInfo addFlowTaskFlowInfo, List<ScriptParamModel> defaultScriptModels) {
        FlowTaskConfModel flowTaskConfModel = new FlowTaskConfModel();
        flowTaskConfModel.setTaskId(taskId);
        flowTaskConfModel.setKeepRunning(1);
        flowTaskConfModel.setReplay(1);
        flowTaskConfModel.setRateUnlimited(0);
        FlowTaskConf flowTaskConf = new FlowTaskConf();
        if (ResourceTypeEnum.SCRIPT.toValue() == resourceType) {
            // 优先使用场景页面输入的参数
            var inputAttackConf = addFlowTaskFlowInfo.getAttackConfs();
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
        FlowTaskRateConf rateConf = new FlowTaskRateConf();
        rateConf.setBandwidth(addFlowTaskFlowInfo.getBandwidth());
        rateConf.setRate(addFlowTaskFlowInfo.getRate());
        rateConf.setDesc(addFlowTaskFlowInfo.getDesc());
        rateConf.setCategory(addFlowTaskFlowInfo.getCategory());
        flowTaskConf.setRateConf(rateConf);
        flowTaskConfModel.setConf(JSONObject.toJSONString(flowTaskConf));
        return flowTaskConfModel;
    }

    @Getter
    @Setter
    public static class FlowTaskTaskModelResult{
        public List<FlowTaskTaskModel> addResult = new ArrayList<>();
        public List<FlowTaskTaskModel> updateResult = new ArrayList<>();
    }
}
