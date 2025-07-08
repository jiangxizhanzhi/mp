package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.TopoCloudClient;
import cn.cyberpeace.mp.flow.integration.cloud.topocloud.request.BusinessCSceneInstance;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import cn.cyberpeace.mp.flow.service.FlowCsceneService;
import cn.cyberpeace.mp.flow.service.impl.FlowProtocolService;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptParamService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value = "addTaskByLocalFlowService")
public class AddTaskByLocalFlowService implements IAddTaskService {
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
    private FlowProtocolService flowProtocolService;

    @Resource
    private FlowTaskTaskService flowTaskTaskService;

    @Resource
    private ICloudClient cloudClient;

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
        // 判断网络是否已经接入
        HumanCondition portCondition = new HumanCondition().filterEqual(FlowTaskPortModel::getActuatorId, actuatorId)
                .filterEqual(FlowTaskPortModel::getNetworkId, networkId);
        var portModel = flowTaskPortMapper.first(portCondition);
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
        String mac = "";
        if (portModel == null) {
            // 无数据 说明未接入网络 ， 需要先接入网络
            AddVirtualNicParams params = new AddVirtualNicParams();
            params.setInstanceId(flowActuatorModel.getResourceId());
            params.setNetworkId(networkId);
            var res = cloudClient.addVirtualNic(params);
            mac = res.getMacAddress();
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
            flowTaskTaskModel.setFlowName(flowInfoModel.getTitle());
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
            flowTaskConfModel.setKeepRunning(1);
            flowTaskConfModel.setReplay(1);
            flowTaskConfModel.setRateUnlimited(0);
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

        FlowActuator flowActuator = new FlowActuator(flowActuatorModel.getIp(), flowTaskTaskService);
        boolean initNetwork = flowActuator.initNetwork(portModel.getMac(), targetIp, taskIdList);
        if (!initNetwork) {
            log.error("init network error");
            throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
        }
        //上传pcap包虚拟机
        for (FlowInfoModel flowInfoModel : flowInfoModelList) {
            List<FlowProtocolModel> flowProtocolModelList = flowProtocolService.selectByFlowId(flowInfoModel.getId());
            boolean uploadPcap = flowActuator.uploadPcap(flowInfoModel, flowProtocolModelList);
            if (!uploadPcap) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
        }
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
