package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTaskPageRequest;
import cn.cyberpeace.mp.cscene.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.*;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 场景流量服务
 * @Author: guquan
 * @Date：2023/5/8 15:25
 */
@Slf4j
@Service
@Data
public class CsceneFlowService {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private FlowTaskPortService flowTaskPortService;

    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private FlowTaskConfService flowTaskConfService;

    @Autowired
    private FlowScriptParamService flowScriptParamService;

    @Autowired
    private FlowTaskRecordService flowTaskRecordService;

    @Autowired
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowConfig flowConfig;

    /**
     * 新增流量任务
     * @param request
     */
    @Transactional
    public void addTask(AddFlowTaskTaskRequest request) {
        log.info("CsceneFlowService.addTask begin. request:{}", request);
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        //根据节点id获取节点ip，节点名称，网络id
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        String nodeName = sceneHandler.getElement(request.getNodeId()).getName();
        String nodeIp = "";
        String networkId = "";
        if (CollectionUtils.isNotEmpty(sceneHandler.getElement(request.getNodeId()).getPorts())) {
            CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            for (var p : sceneHandler.getElement(request.getNodeId()).getPorts()) {
                nodeIp = p.getIp();
                if (StringUtils.isNotEmpty(nodeIp)) {
                    CloudPluginSceneElementNetworkBO cloudPluginSceneElementNetworkBO = cloudPluginHandler.getHandlerUtils().getNetworkByPort(p);
                    networkId = cloudPluginSceneElementNetworkBO.getResourceId();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(nodeIp)) {
            throw new MpRuntimeException(CsceneErrorCode.NODEIP_NOT_EXIST);
        }
        if (StringUtils.isEmpty(networkId)) {
            throw new MpRuntimeException(CsceneErrorCode.NETWORKID_NOT_EXIST);
        }
        FlowScenePluginHandler flowScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant.PLUGIN_KEY);
        String actuatorId = flowScenePluginHandler.getScene().getExecutor();
        if (StringUtils.isEmpty(actuatorId)) {
            //TODO:如果没有流量执行器id，则需要创建流量执行器
        }
        //网口信息，如果不存在则创建网口
        FlowTaskPortModel flowTaskPortModel = flowTaskPortService.findByNetworkIdAndActuatorId(networkId, actuatorId);
        if (flowTaskPortModel == null) {
            flowTaskPortModel = new FlowTaskPortModel();
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            if (flowActuatorModel != null && StringUtils.isNotEmpty(flowActuatorModel.getInstanceId())) {
                JSONObject addPortReq = new JSONObject();
                addPortReq.put("network_id", networkId);
                try {
                    String addTrexClientPortRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/instances/" +
                            flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
                    JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                    log.info("add client port response:{}", addTrexClientObj.toJSONString());
                    if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                        JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                        String clientMac = clientDataObj.getString("mac_address");
                        flowTaskPortModel.setMac(clientMac);
                        flowTaskPortModel.setActuatorId(actuatorId);
                        flowTaskPortModel.setNetworkId(networkId);
                        flowTaskPortService.create(flowTaskPortModel);
                    }
                } catch (IOException e) {
                    log.error("【{}】add flow port error for networkId: {}", flowActuatorModel.getTitle(), networkId, e);
                    throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                }
            }
        }
        List<FlowInfoModel> flowInfoModelList = flowServiceImpl.findByIds(request.getFlowIds());
        if (CollectionUtils.isEmpty(flowInfoModelList)) {
            throw new MpRuntimeException(CsceneErrorCode.FLOW_NOT_EXIST);
        }
        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (FlowInfoModel flowInfoModel : flowInfoModelList) {
            FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
            flowTaskTaskModel.setSceneId(sceneId);
            flowTaskTaskModel.setActuatorId(actuatorId);
            flowTaskTaskModel.setResourceType(flowInfoModel.getResourceType());
            flowTaskTaskModel.setInteractiveType(flowInfoModel.getInteractiveType());
            flowTaskTaskModel.setFlowId(flowInfoModel.getId());
            flowTaskTaskModel.setPortId(flowTaskPortModel.getId() == null ? "":flowTaskPortModel.getId());
            flowTaskTaskModel.setNodeId(request.getNodeId());
            flowTaskTaskModel.setNodeIp(nodeIp);
            flowTaskTaskModel.setNodeName(nodeName);
            flowTaskTaskModelList.add(flowTaskTaskModel);
        }
        flowTaskTaskService.bulkCreate(flowTaskTaskModelList);

        List<FlowTaskConfModel> flowTaskConfModelList = new ArrayList<FlowTaskConfModel>();
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            FlowTaskConfModel flowTaskConfModel = new FlowTaskConfModel();
            flowTaskConfModel.setTaskId(flowTaskTaskModel.getId());
            flowTaskConfModel.setKeepRunning(1);
            flowTaskConfModel.setReplay(1);
            flowTaskConfModel.setRateUnlimited(1);
            FlowTaskConf flowTaskConf = new FlowTaskConf();
            if (InteractiveTypeEnum.ATTACK.toValue() == flowTaskTaskModel.getInteractiveType()) {
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
            flowTaskConfModel.setConf(JSONObject.toJSONString(flowTaskConf));
            flowTaskConfModelList.add(flowTaskConfModel);
        }
        flowTaskConfService.bulkCreate(flowTaskConfModelList);
        log.info("CsceneFlowService.addTask end.");
    }

    /**
     * 删除流量任务
     * @param id
     */
    @Transactional
    public void deleteTask(String id) {
        log.info("CsceneFlowService.deleteTask begin. id:{}", id);
        flowTaskTaskService.delete(id);
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(id);
        if (flowTaskConfModel != null) {
            flowTaskConfService.delete(flowTaskConfModel.getId());
        }
        flowTaskRecordService.deleteByTaskId(id);
        log.info("CsceneFlowService.deleteTask end.");
    }

    /**
     * 编辑流量任务配置
     * @param request
     */
    public void editTaskConf(EditFlowTaskConfRequest request) {
        log.info("CsceneFlowService.editTaskConf begin. request:{}", request);
        FlowTaskConf flowTaskConf = request.getFlowTaskConf();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(request.getTaskId());
        flowTaskConfModel.setKeepRunning(request.getKeepRunning());
        flowTaskConfModel.setReplay(request.getReplay());
        flowTaskConfModel.setRateUnlimited(request.getRateUnlimited());
        flowTaskConfModel.setConf(JSONObject.toJSONString(flowTaskConf));
        flowTaskConfService.update(flowTaskConfModel);
        log.info("CsceneFlowService.editTaskConf end.");
    }

    /**
     * 流量任务列表查询
     * @param request
     * @return
     */
    public ListObjServiceResult<FlowTaskPageResponse>  taskList(FlowTaskPageRequest request) {
        log.info("CsceneFlowService.taskList begin. request:{}", request);
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.link(FlowTaskTaskModel.class, FlowInfoModel.class, FlowPcapModel.class)
                .filterLink(FlowTaskTaskModel.class, FlowTaskTaskModel::getFlowId, FlowInfoModel.class, FlowInfoModel::getId)
                .filterLink(FlowTaskTaskModel.class, FlowTaskTaskModel::getFlowId, FlowPcapModel.class, FlowPcapModel::getFlowId)
                .currentLink(FlowTaskTaskModel.class)
                .filterEqual(FlowTaskTaskModel::getSceneId, sceneId)
                .selectAs(FlowTaskTaskModel::getId, FlowTaskPageResponse::getId)
                .selectAs(FlowTaskTaskModel::getInteractiveType, FlowTaskPageResponse::getInteractiveType)
                .selectAs(FlowTaskTaskModel::getNodeName, FlowTaskPageResponse::getNodeName)
                .selectAs(FlowTaskTaskModel::getNodeIp, FlowTaskPageResponse::getNodeIp)
                .selectAs(FlowTaskTaskModel::getSuccessCount, FlowTaskPageResponse::getSuccessCount)
                .selectAs(FlowTaskTaskModel::getErrorCount, FlowTaskPageResponse::getErrorCount)
                .selectAs(FlowTaskTaskModel::getErrorCount, FlowTaskPageResponse::getErrorCount)
                .selectAs(FlowTaskTaskModel::getTaskStatus, FlowTaskPageResponse::getTaskStatus)
                .currentLink(FlowInfoModel.class)
                .selectAs(FlowInfoModel::getTitle, FlowTaskPageResponse::getTitle)
                .currentLink(FlowPcapModel.class)
                .selectAs(FlowPcapModel::getDuration, FlowTaskPageResponse::getDuration)
                .selectAs(FlowPcapModel::getPcapSize, FlowTaskPageResponse::getPcapSize)
                .selectAs(FlowPcapModel::getPktNum, FlowTaskPageResponse::getPktNum)
                .selectAs(FlowPcapModel::getProtocols, FlowTaskPageResponse::getProtocols);
        humanCondition.setLimit(request.getPerPage());
        humanCondition.setOffset((request.getPage() - 1) * request.getPerPage());
        // 查询符合条件的条数
        int total = flowTaskTaskMapper.total(humanCondition);
        List<FlowTaskPageResponse> flowTaskPageResponseList = flowTaskTaskMapper.list(humanCondition, FlowTaskPageResponse.class);
        //统计实时速率
        if (CollectionUtils.isNotEmpty(flowTaskPageResponseList)) {
            flowTaskPageResponseList.stream().forEach(rsp -> rsp.setTotalCount(rsp.getSuccessCount()+rsp.getErrorCount()));
            List<String> ids = flowTaskPageResponseList.stream().map(FlowTaskPageResponse::getId).collect(Collectors.toList());
            List<FlowStatisticalCondition> conditions = this.getRealTimeCondition();
            Map<String, List<FlowRealTimeRate>> tealTimeRateMap = flowTaskRecordService.realTimeRate(ids, conditions);
            for (FlowTaskPageResponse temp : flowTaskPageResponseList) {
                temp.setRealTimeRateList(tealTimeRateMap.get(temp.getId()));
            }
        }
        ListObjServiceResult<FlowTaskPageResponse> result = new ListObjServiceResult(new ListObjResult<>(flowTaskPageResponseList, total).asResult()).asResult();
        log.info("CsceneFlowService.taskList end. result:{}", result);
        return result;
    }

    /**
     * 流量任务配置查询
     * @param taskId
     * @return
     */
    public QueryFlowTaskConfResponse queryConfByTaskId(String taskId) {
        log.info("CsceneFlowService.queryConfByTaskId begin. taskId:{}", taskId);
        QueryFlowTaskConfResponse result = new QueryFlowTaskConfResponse();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(taskId);
        BeanUtils.copyProperties(flowTaskConfModel, result);
        FlowTaskConf flowTaskConf = JSONObject.parseObject(flowTaskConfModel.getConf(), FlowTaskConf.class);
        result.setFlowTaskConf(flowTaskConf);
        log.info("CsceneFlowService.queryConfByTaskId end. result:{}", result);
        return result;
    }

    /**
     * 流量仿真概况
     * @param id
     * @return
     */
    public FlowSummary queryFlowSummary(String id) {
        log.info("CsceneFlowService.queryFlowSummary begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        FlowSummary flowSummary = flowTaskRecordService.queryFlowSummary(sceneId);
        log.info("CsceneFlowService.queryFlowSummary end. flowSummary:{}", flowSummary);
        return flowSummary;
    }

    /**
     * 流量仿真统计
     * @param id
     * @return
     */
    public FlowStatisticsInfo flowStatistics(String id) {
        log.info("CsceneFlowService.flowStatistics begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        FlowStatisticsInfo flowStatisticsInfo = flowTaskRecordService.flowStatistics(sceneId);
        log.info("CsceneFlowService.flowStatistics end. flowStatisticsInfo:{}", flowStatisticsInfo);
        return flowStatisticsInfo;
    }

    /**
     * 获取实时速率统计条件
     * @return
     */
    private List<FlowStatisticalCondition> getRealTimeCondition() {
        log.info("CsceneFlowService.getRealTimeCondition begin.");
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.plusSeconds(-300);
        List<FlowStatisticalCondition> queryList = new ArrayList<FlowStatisticalCondition>();
        Integer offSet = 0;
        while (endTime.isAfter(startTime)) {
            FlowStatisticalCondition flowStatisticalCondition = new FlowStatisticalCondition();
            flowStatisticalCondition.setEndTime(endTime);
            flowStatisticalCondition.setStartTime(endTime.minusSeconds(5));
            flowStatisticalCondition.setOffsetUnit("s");
            flowStatisticalCondition.setOffsetValue(offSet);
            queryList.add(flowStatisticalCondition);
            endTime = endTime.minusSeconds(5);
            offSet -= 5;
        }
        log.info("CsceneFlowService.getRealTimeCondition end. queryList:{}", queryList);
        return queryList;
    }
}
