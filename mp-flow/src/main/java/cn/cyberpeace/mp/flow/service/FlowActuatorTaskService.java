package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.integration.actuator.FlowActuator;
import cn.cyberpeace.mp.flow.integration.cloud.base.AddVirtualNicParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
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
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import cn.cyberpeace.mp.flow.service.impl.FlowProtocolService;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptParamService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FlowActuatorTaskService extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields, FlowTaskPageQueryParams> {

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
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    @Resource
    private FlowTaskRecordMapper flowTaskRecordMapper;

    @Resource
    private FlowProtocolService flowProtocolService;

    @Resource
    private FlowTaskTaskService flowTaskTaskService;

    @Resource
    private FlowTaskConfService flowTaskConfService;

    @Resource(name = "saiNingCloudClient")
    private ICloudClient cloudClient;

    public FlowActuatorTaskService(FlowTaskTaskMapper flowTaskTaskMapper) {
        super();
        super.setModelMapper(flowTaskTaskMapper);
        this.flowTaskTaskMapper = flowTaskTaskMapper;
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
            flowActuatorModel.getResourceId();
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

    @Transactional
    public void deleteTasks(List<String> ids) {
        flowTaskTaskMapper.bulkDelete(ids.toArray(new String[]{}));
        flowTaskConfMapper.filterDelete(new HumanCondition().filterIn(FlowTaskConfModel::getTaskId, ids.toArray(new String[]{})));
        flowTaskRecordMapper.filterDelete(new HumanCondition().filterIn(FlowTaskRecordModel::getTaskId, ids.toArray(new String[]{})));
    }

    public void startTask(String taskId) {
        asyncPlatFlowTaskService.startTask(taskId);
    }

    public void stopTask(String taskId) {
        asyncPlatFlowTaskService.stopTask(taskId);
    }

    public void startTasks(List<String> taskIds) {
        asyncPlatFlowTaskService.batchStartTasks(taskIds);
    }

    public void stopTasks(List<String> taskIds) {
        asyncPlatFlowTaskService.batchStopTasks(taskIds);
    }

    public void editTaskConf(EditFlowTaskConfRequest request) {
        // 根据 config 字段调整下 replay 和 rate_unlimited
        if (request.getConfig() == null || request.getConfig() == 0) {
            // config 配置关闭时 ：
            //      直接使用pcap重放，所以replay = 1
            //      速率使用默认配置，所以rate_unlimited = 1
            request.setReplay(1);
            request.setRateUnlimited(1);
        } else {
            request.setReplay(0);
            request.setRateUnlimited(0);
        }
        FlowTaskConf flowTaskConf = request.getFlowTaskConf();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(request.getTaskId());
        flowTaskConfModel.setKeepRunning(request.getKeepRunning());
        flowTaskConfModel.setReplay(request.getReplay());
        flowTaskConfModel.setRateUnlimited(request.getRateUnlimited());
        flowTaskConfModel.setConf(JsonCommon.stringify(flowTaskConf));
        flowTaskConfModel.setConfig(request.getConfig());
        flowTaskConfService.update(flowTaskConfModel);
    }

    public QueryFlowTaskConfResponse queryConfByTaskId(String taskId) {
        QueryFlowTaskConfResponse result = new QueryFlowTaskConfResponse();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(taskId);
        BeanUtils.copyProperties(flowTaskConfModel, result);
        FlowTaskConf flowTaskConf = JsonCommon.parse(flowTaskConfModel.getConf(), FlowTaskConf.class);
        result.setFlowTaskConf(flowTaskConf);
        return result;
    }

    public ListObjServiceResult<FlowTaskPageResponse> taskList(FlowTaskPageQueryParams params) {
        int total = flowTaskTaskMapper.countTasks(params);
        if (total == 0) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        } else {
            int offset = (params.getPage() - 1) * params.getPageSize();
            int limit = params.getPageSize();
            var list = flowTaskTaskMapper.listTasks(params, offset, limit);
            return new ListObjServiceResult<>(new ListObjResult<>(list, total));
        }
    }
}
