package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskRateConf;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.FlowCategory;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.integration.FlowClient;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.TopoCloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowPcapMapper;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
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
@Service(value = "addTaskByRemoteFlowService2")
public class AddTaskByRemoteFlowService2 implements IAddTaskService {

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

    @Resource
    private FlowPcapMapper flowPcapMapper ;

    @Override
    public void addTasksToDb(FlowActuatorModel flowActuator) {
        String actuatorId = flowActuator.getId();
        log.info("开始处理执行器，在数据库中添加流量任务 {} ", flowActuator.getId());

        var instance = this.getInstanceWithNoException(flowActuator.getCsceneInstanceId(), 2);
        Set<String> nodeIds = new HashSet<>();
        Map<String,String> nodeId2NodeName = new HashMap<>();
        if (null != instance && null != instance.getScene() && null != instance.getScene().getTopology()){
            for (var p : instance.getScene().getTopology().getElements()) {
                nodeIds.add(p.getId());
                nodeId2NodeName.put(p.getId(), p.getName());
            }
        }
        log.info("查询对应的场景实例信息: {} ", JsonCommon.snakeStringify(instance));
        // 更新场景名称到执行器表
        if (null != instance){
            this.updateFlowActuatorDbName(actuatorId, instance.getScene().getTopology().getScene().getName());
        }

        // 加载场景中每个节点对应的网络信息
        var mapTmp = this.getCsceneConfigFlowTaskParams(flowActuator);
        Map<String, AddFlowTaskParams> map = new HashMap<>();
        for (String nodeId : nodeIds) {
            if (mapTmp.containsKey(nodeId)) {
                var addFlowTaskParams = mapTmp.get(nodeId);
                String nodeName = nodeId2NodeName.get(nodeId);
                addFlowTaskParams.setNodeName(nodeName);
                map.put(nodeId, addFlowTaskParams);
            }
        }

        if (null != instance){
            this.loadNetworkInfo(map, instance);
        }
        log.info("每个节点对应的网络信息：{}", JsonCommon.snakeStringify(map));

        this.createTaskAndConfig(actuatorId, map);
        log.info("结束处理执行器 {} 在数据库中添加流量任务", flowActuator.getId());
    }

    @Override
    public void addTasksToActuator(FlowActuatorModel flowActuator) {
        String actuatorId = flowActuator.getId();

        // 查询任务 ， 并按照节点id分组
        var condition = new HumanCondition().filterEqual(FlowTaskTaskModel::getActuatorId, actuatorId)
                .filterEqual(FlowTaskTaskModel::getTaskStatus, 5);
        var flowTaskTaskModelList = flowTaskTaskMapper.list(condition);
        if (flowTaskTaskModelList.size() == 0) {
            log.info("没有未处理的任务");
            return;
        }
        List<String> taskIds = new ArrayList<>();
        flowTaskTaskModelList.forEach(f -> taskIds.add(f.getId()));
        log.info("执行器 {} 存在 {} 个未处理的任务 ,{} ", flowActuator.getId(), JsonCommon.stringify(taskIds));
        Map<String, List<FlowTaskTaskModel>> nodeId2TaskTaskModel = new HashMap<>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            var list = nodeId2TaskTaskModel.getOrDefault(flowTaskTaskModel.getNodeId(), new ArrayList<>());
            list.add(flowTaskTaskModel);
            nodeId2TaskTaskModel.put(flowTaskTaskModel.getNodeId(), list);
        }
        for (var e : nodeId2TaskTaskModel.entrySet()) {
            log.info("每个节点对应的流量任务：{} -> {}", e.getKey(), JsonCommon.snakeStringify(e.getValue()));
        }

        // 查流量信息
        Set<String> flowIds = new HashSet<>();
        flowTaskTaskModelList.forEach(t -> flowIds.add(t.getFlowId()));
        var remote = FlowClient.getRemoteFlowInfos(flowIds);
        Map<String, RemoteFlowInfo> flowId2RemoteFlowInfo = new HashMap<>();
        for (RemoteFlowInfo f : remote) {
            flowId2RemoteFlowInfo.put(f.getFlowId(), f);
        }

        // 处理每个节点的任务
        for (var nodeTaskList : nodeId2TaskTaskModel.values()) {
            String nodeId = nodeTaskList.get(0).getNodeId();
            String targetIp = nodeTaskList.get(0).getNodeIp();
            String networkId = nodeTaskList.get(0).getNodeNetworkId();
            log.info("处理节点: {}  , 节点ip:{} , 网络id:{}", nodeId, targetIp, networkId);
            List<RemoteFlowInfo> remoteFlowInfos = new ArrayList<>();
            for (FlowTaskTaskModel task : nodeTaskList) {
                if (flowId2RemoteFlowInfo.containsKey(task.getFlowId())) {
                    remoteFlowInfos.add(flowId2RemoteFlowInfo.get(task.getFlowId()));
                }
            }
            log.info("流量信息: {} ", JsonCommon.snakeStringify(remoteFlowInfos));
            this.handleOneTask(flowActuator, targetIp, networkId, nodeTaskList, remoteFlowInfos);
            log.info("处理节点: {} 处理结束" , nodeId);
        }

        log.info("更新任务状态");
        log.info("完成处理执行器 {} ", flowActuator.getId());
    }

    public void updateFlowActuatorDbStatus(String id, int status) {
        FlowActuatorModel updateModel = new FlowActuatorModel();
        updateModel.setId(id);
        updateModel.setStatus(status);
        flowActuatorMapper.update(updateModel);
    }

    private void updateFlowActuatorDbName(String id, String name) {
        FlowActuatorModel updateModel = new FlowActuatorModel();
        updateModel.setId(id);
        updateModel.setCsceneInstanceName(name);
        flowActuatorMapper.update(updateModel);
    }

    private boolean initNetwork(FlowActuator flowActuatorClient, String portMac, String targetIp, List<String> taskIdList, int tryCount) {
        for (int i = 0; i < tryCount; i++) {
            boolean initNetwork = flowActuatorClient.initNetwork(portMac, targetIp, taskIdList);
            if (initNetwork) {
                return true;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("init network error");
        return false;
    }

    public void handleOneTask(FlowActuatorModel flowActuator, String targetIp, String networkId, List<FlowTaskTaskModel> flowTaskTaskModelList, List<RemoteFlowInfo> remoteFlowInfos) {

        // 获取trex虚拟机上和目标网络相连的端口信息 如果没有则会自动创建一个
        log.info("创建和目标网络相连的网口");
        var portModel = this.getOrCreateActuatorFlowPort(flowActuator.getId(), networkId, flowActuator.getResourceId());
        log.info("网口mac地址: {}", portModel.getMac());

        // 初始化网口
        log.info("初始化网口");
        List<String> taskIdList = new ArrayList<>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            taskIdList.add(flowTaskTaskModel.getId());
        }
        FlowActuator flowActuatorClient = new FlowActuator(flowActuator.getIp(), flowTaskTaskService);
        log.info("mac参数:{} , 目标ip:{} , 任务id列表: {} ", portModel.getMac(), targetIp, taskIdList);
        final boolean b = this.initNetwork(flowActuatorClient, portModel.getMac(), targetIp, taskIdList, 10);
        log.info("初始化网口结束");

        log.info("上传流量资源包(pcap或者脚本)");
        var flowConfig = BeanCommon.getBean(FlowConfig.class);
        for (var flow : remoteFlowInfos) {
            log.info("流量:{} ", flow.getFlowId());
            // 下载文件
            int resourceType = StringUtils.isNotEmpty(flow.getPcapFile()) ? 0 : 1;
            String fileKey = resourceType == 0 ? flow.getPcapFile() : flow.getFile();
            fileKey = fileKey.replace("\\", "/");
            String downloadUrl = flowConfig.getCsceneUrl() + BaseAppConfig.API_PREFIX + "/file/" + fileKey;
            byte[] bytes = this.downloadFileToByteArray(downloadUrl, 2);
            List<FlowActuator.ProtocolRate> protocolRates = new ArrayList<>();
            List<FlowProtocolModel> protocolModels = new ArrayList<>();
            if (flow.getProtocolList() != null) {
                for (var protocol : flow.getProtocolList()) {
                    protocolRates.add(new FlowActuator.ProtocolRate(protocol.getProtocolName(), protocol.getRate()));
                    FlowProtocolModel model = new FlowProtocolModel();
                    model.setFlowId(flow.getFlowId());
                    model.setRate(protocol.getRate());
                    model.setMarked(protocol.getMarked());
                    model.setProtocolName(protocol.getProtocolName());
                    protocolModels.add(model);
                }
            }
            if (CollectionUtils.isNotEmpty(protocolModels)) {
                FlowProtocolMapper flowProtocolMapper = BeanCommon.getBean(FlowProtocolMapper.class);
                flowProtocolMapper.filterDelete(new HumanCondition().filterEqual(FlowProtocolModel::getFlowId, flow.getFlowId()));
                flowProtocolMapper.bulkCreate(protocolModels);
            }


            boolean uploadPcap = false;
            for (int i = 0; i < 2; i++) {
                uploadPcap = flowActuatorClient.uploadPcapV2(bytes, flow.getFlowId(), flow.getTitle(), flow.getFileName(), resourceType, protocolRates);
                if (uploadPcap) {
                    break;
                } else {
                    log.error("upload flow file info error");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        for (var m : flowTaskTaskModelList) {
            m.setTaskStatus(0);
            m.setPortId(portModel.getId());
        }
        flowTaskTaskMapper.bulkUpdate(flowTaskTaskModelList);
    }

    private void createTaskAndConfig(String actuatorId, Map<String, AddFlowTaskParams> map) {
        for (var e : map.entrySet()) {
            var addFlowTaskParams = e.getValue();
            Set<String> flowIds = new HashSet<>();
            for (var task : addFlowTaskParams.getTasks()) {
                flowIds.add(task.getFlowId());
            }
            // 从远程服务上查询流量的信息
            var remoteFlowInfos = FlowClient.getRemoteFlowInfos(flowIds);
            // 创建流量任务
            var flowTaskTaskModelList = this.createFlowTasks(actuatorId, addFlowTaskParams.getCsceneInstanceId(), addFlowTaskParams.getNodeId(), addFlowTaskParams.getNodeName(), "", addFlowTaskParams.getTargetIp(), addFlowTaskParams.getNetworkId(), remoteFlowInfos);
            // 创建任务配置
            this.createTaskConfigs(flowTaskTaskModelList, addFlowTaskParams.getTasks(), remoteFlowInfos);
        }
    }

    private byte[] downloadFileToByteArray(String url, int tryCount) {
        byte[] bytes = null;
        for (int i = 0; i < tryCount; i++) {
            try {
                bytes = HttpUtils.downloadFileToByteArray(url);
                break;
            } catch (Exception e) {
                log.error("failed to download file , url is {}", url, e);
            }
        }
        return bytes;
    }

    private BusinessCSceneInstance getInstanceWithNoException(String instanceId, int tryCount) {
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

    private void loadNetworkInfo(Map<String, AddFlowTaskParams> map, BusinessCSceneInstance instance) {
        var cloudPorts = instance.getScene().getPlugins().getCpcloud().getElementPorts();
        for (var e : instance.getScene().getTopology().getElements()) {
            if (!map.containsKey(e.getId())) {
                continue;
            }
            map.get(e.getId()).setNodeName(e.getName());
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
                params.setNodeId(e.getCsceneNodeId());
                params.setTasks(new ArrayList<>());
                params.setCsceneInstanceId(m.getCsceneInstanceId());
                map.put(e.getCsceneNodeId(), params);
            }
            AddFlowTaskFlowInfo flowTaskFlowInfo = new AddFlowTaskFlowInfo();
            flowTaskFlowInfo.setFlowId(e.getFlowId());
            flowTaskFlowInfo.setBandwidth(e.getBandwidth());
            flowTaskFlowInfo.setRate(e.getRate());
            flowTaskFlowInfo.setResourceType(e.getResourceType());

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
            try {
                params.setAddr(TopoCloudClient.getTrexIp(networkId));
            } catch (Exception e) {
                // 家里环境会报错，捕获异常方便调试
                log.error("failed to get trex ip ", e);
            }
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

    private List<FlowTaskTaskModel> createFlowTasks(String actuatorId,String csceneInstanceId , String nodeId,String nodeName,  String portId, String targetIp, String networkId, List<RemoteFlowInfo> remoteFlowInfos) {
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

    private List<FlowTaskConfModel> createTaskConfigs(List<FlowTaskTaskModel> flowTaskTaskModelList, List<AddFlowTaskFlowInfo> inputTasks, List<RemoteFlowInfo> remoteFlowInfos) {
        Map<String, AddFlowTaskFlowInfo> addFlowTaskMap = new HashMap<>();
        for (var t : inputTasks) {
            addFlowTaskMap.put(t.getFlowId(), t);
        }

        Map<String, RemoteFlowInfo> remoteFlowInfoMap = new HashMap<>();
        for (var flow : remoteFlowInfos) {
            remoteFlowInfoMap.put(flow.getFlowId(), flow);
        }

        List<FlowPcapModel> pcapModels = new ArrayList<>();
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
        var inputAttackConf = addFlowTaskFlowInfo.getAttackConfs();
        FlowTaskConfModel flowTaskConfModel = new FlowTaskConfModel();
        flowTaskConfModel.setTaskId(taskId);
        flowTaskConfModel.setKeepRunning(1);
        flowTaskConfModel.setReplay(1);
        flowTaskConfModel.setRateUnlimited(0);
        FlowTaskConf flowTaskConf = new FlowTaskConf();
        // 记录带宽和流速
        flowTaskConf.setRateConf(new FlowTaskRateConf());
        flowTaskConf.getRateConf().setRate(addFlowTaskFlowInfo.getRate());
        flowTaskConf.getRateConf().setBandwidth(addFlowTaskFlowInfo.getBandwidth());
        flowTaskConf.getRateConf().setCategory(addFlowTaskFlowInfo.getCategory());
        flowTaskConf.getRateConf().setDesc(addFlowTaskFlowInfo.getDesc());

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
}
