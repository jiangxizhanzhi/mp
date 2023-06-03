package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskDetail;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkFlowRateResult;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private FlowTaskConfService flowTaskConfService;

    @Autowired
    private FlowTaskRecordService flowTaskRecordService;

    @Autowired
    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    /**
     * 新增流量任务
     * @param request
     */
    public void addTask(AddFlowTaskTaskRequest request) {
        log.info("CsceneFlowService.addTask begin. request:{}", request);
        var sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        FlowScenePluginHandler flowScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant.PLUGIN_KEY);
        //如果没有flowplugin，则初始化flowplugin
        if (flowScenePluginHandler == null) {
            ScenePluginDataBO scenePluginDataBO = new ScenePluginDataBO();
            scenePluginDataBO.setScene(new HashMap<>());
            scenePluginDataBO.setElementPorts(new HashMap<>());
            scenePluginDataBO.setElements(new HashMap<>());
            FlowPluginSceneElementBO elementBO = new FlowPluginSceneElementBO();
            elementBO.setFlows(new ArrayList<>());
            for (AddFlowTaskTaskDetail detail : request.getDetails()) {
                scenePluginDataBO.getElements().put(detail.getNodeId(), JsonCommon.beanToSnakeMap(elementBO));
            }
            var scene = sceneHandler.toData();
            scene.getPlugins().put(cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant.PLUGIN_KEY, scenePluginDataBO);
            SceneHandler sceneHandler2 = new SceneHandler(scene, null);
            sceneHandler2.save();
            sceneHandler = new SceneHandler(sceneId, null, false, null);
            flowScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant.PLUGIN_KEY);
        }
        String actuatorId = flowScenePluginHandler.getScene().getExecutor();
        //流量执行器不存在则创建流量执行器
        if (StringUtils.isEmpty(actuatorId)) {
            String trexName = StringUtils.joinWith("_", FlowPlatformEnum.TREX, sceneId);
            actuatorId = asyncPlatFlowTaskService.syncStartExecutor(trexName);
            FlowPluginSceneBO scene = flowScenePluginHandler.getScene();
            scene.setExecutor(actuatorId);
            flowScenePluginHandler.saveScene();
        }
        List<AddFlowTaskTaskInfo> addFlowTaskTaskInfoList = new ArrayList<>();
        //遍历任务详情，初始化节点参数
        for (AddFlowTaskTaskDetail detail : request.getDetails()) {
            String nodeName = sceneHandler.getElement(detail.getNodeId()).getName();
            String nodeIp = "";
            String networkId = "";
            if (CollectionUtils.isNotEmpty(sceneHandler.getElement(detail.getNodeId()).getPorts())) {
                CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                for (var p : sceneHandler.getElement(detail.getNodeId()).getPorts()) {
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
            AddFlowTaskTaskInfo addFlowTaskTaskInfo = new AddFlowTaskTaskInfo();
            addFlowTaskTaskInfo.setFlowIds(detail.getFlowIds());
            addFlowTaskTaskInfo.setNodeId(detail.getNodeId());
            addFlowTaskTaskInfo.setNodeIp(nodeIp);
            addFlowTaskTaskInfo.setNodeName(nodeName);
            addFlowTaskTaskInfo.setNetworkId(networkId);
            addFlowTaskTaskInfoList.add(addFlowTaskTaskInfo);
        }
        //新增流量执行任务
        if (CollectionUtils.isNotEmpty(addFlowTaskTaskInfoList)) {
            flowTaskTaskService.addTask(addFlowTaskTaskInfoList, sceneId, actuatorId);
        }
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
    public ListObjServiceResult<FlowTaskPageResponse>  taskList(FlowTaskPageQueryParams request) {
        log.info("CsceneFlowService.taskList begin. request:{}", request);
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        request.setOffset((request.getPage() - 1) * request.getPerPage());
        request.setSceneId(sceneId);
        // 查询符合条件的条数
        int total = flowTaskTaskMapper.countBySceneId(request);
        List<FlowTaskPageResponse> flowTaskPageResponseList = flowTaskTaskMapper.pageBySceneId(request);
        //统计实时速率
        if (CollectionUtils.isNotEmpty(flowTaskPageResponseList)) {
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
     * top5实时速率
     * @param id
     * @return
     */
    public List<FlowNodeTxBps> top5NodeTxBps(String id) {
        log.info("CsceneFlowService.top5NodeTxBps begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        List<FlowNodeTxBps> flowNodeTxBpsList = flowTaskRecordService.queryNodeTxBps(sceneId);
        if (CollectionUtils.isNotEmpty(flowNodeTxBpsList) && flowNodeTxBpsList.size() > 5) {
            flowNodeTxBpsList = flowNodeTxBpsList.subList(0, 5);
        }
        log.info("CsceneFlowService.top5NodeTxBps end. flowNodeTxBpsList:{}", flowNodeTxBpsList);
        return flowNodeTxBpsList;
    }

    /**
     * TOP5流量协议
     * @param id
     * @return
     */
    public JSONArray top5Protocols(String id) {
        log.info("CsceneFlowService.top5Protocols begin. id:{}", id);
        JSONArray protocols = new JSONArray();
        String sceneId = csceneInstanceService.getSceneId(id);
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        FlowScenePluginHandler flowScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant.PLUGIN_KEY);
        if (flowScenePluginHandler == null) {
            return protocols;
        }
        String actuatorId = flowScenePluginHandler.getScene().getExecutor();
        if (StringUtils.isNotEmpty(actuatorId)) {
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            if (flowActuatorModel != null && StringUtils.isNotEmpty(flowActuatorModel.getIp())) {
                try {
                    String rsp = HttpUtils.sendPost(CommonUtil.getHostUrl("flowActuatorModel.getIp()") + "/tasks/protocols/top5", "{}");
                    JSONObject rspObj = JSONObject.parseObject(rsp);
                    log.info("query top5 protocols response:{}", rspObj.toJSONString());
                    if (ResponseUtils.checkResponse(rspObj)) {
                        protocols = rspObj.getJSONObject(BaseConstants.STR_DATA).getJSONArray("protocols");
                    } else {
                        log.error("query top5 protocols failed");
                    }
                } catch (Exception e) {
                    log.error("query top5 protocols error, {}", ExceptionUtils.getStackTrace(e));
                }
            }
        }
        log.info("CsceneFlowService.top5Protocols end. protocols:{}", protocols);
        return protocols;
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
     * 流量任务启动
     * @param taskId
     */
    public void startTask(String taskId) {
        log.info("CsceneFlowService.startTask begin. taskId:{}", taskId);
        asyncPlatFlowTaskService.startTask(taskId, null);
        log.info("CsceneFlowService.startTask end.");
    }

    /**
     * 流量任务启动
     * @param taskIds
     */
    public void batchStartTasks(List<String> taskIds) {
        log.info("CsceneFlowService.batchStartTasks begin. taskIds:{}", taskIds);
        asyncPlatFlowTaskService.batchStartTasks(taskIds);
        log.info("CsceneFlowService.batchStartTasks end.");
    }

    /**
     * 流量任务停止
     * @param taskId
     */
    public void stopTask(String taskId) {
        log.info("CsceneFlowService.stopTask begin. taskId:{}", taskId);
        asyncPlatFlowTaskService.stopTask(taskId);
        log.info("CsceneFlowService.stopTask end.");
    }

    /**
     * 流量任务批量停止
     * @param taskIds
     */
    public void batchStopTasks(List<String> taskIds) {
        log.info("CsceneFlowService.batchStopTasks begin. taskIds:{}", taskIds);
        asyncPlatFlowTaskService.batchStopTasks(taskIds);
        log.info("CsceneFlowService.batchStopTasks end.");
    }

    /**
     * 根据场景id查询带有流量的节点id
     * @param id
     * @return
     */
    public List<FlowTaskNode> nodeOption(String id) {
        log.info("CsceneFlowService.nodeOption begin. id:{}", id);
        var sceneId = csceneInstanceService.getSceneId(id);
        List<FlowTaskNode> nodeList = flowTaskTaskService.nodeList(sceneId);
        log.info("CsceneFlowService.nodeOption end. nodeList:{}", nodeList);
        return nodeList;
    }

    /**
     * 根据场景id查询环境总速率
     * @return
     */
    public List<FlowStatisticsDetail> totalRate(String id) {
        log.info("CsceneFlowService.totalRate begin. id:{}", id);
        var sceneId = csceneInstanceService.getSceneId(id);
        List<FlowStatisticsDetail> result = new ArrayList<>();
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        LocalDateTime createTime = sceneHandler.getScene().getCreateTime();
        Long totalSecond = 0L;
        if (createTime != null) {
            LocalDateTime nowTime = LocalDateTime.now();
            Duration duration = Duration.between(createTime, nowTime);
            totalSecond = duration.toSeconds();
        }
        List<String> portIds = new ArrayList<>();
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, SceneElementPortBO> portMap = sceneHandler.getElementPortMap();
        portMap.forEach((key,value) -> {
            if (cloudPluginHandler.getPort(value.getId()) != null && StringUtils.isNotEmpty(cloudPluginHandler.getPort(value.getId()).getResourceId())) {
                portIds.add(cloudPluginHandler.getPort(value.getId()).getResourceId());
            }
        });
        if (CollectionUtils.isNotEmpty(portIds) && totalSecond > 0) {
            NetworkFlowRateResult networkFlowRateResult = scenePluginCloudSceneService.networkFlowRate(portIds, totalSecond);
            if (networkFlowRateResult != null && networkFlowRateResult.getTimeInt() != null && CollectionUtils.isNotEmpty(networkFlowRateResult.getSumRateList())) {
                Integer offsetValue = 0 - (networkFlowRateResult.getSumRateList().size()-1) * networkFlowRateResult.getTimeInt();
                for (Long value : networkFlowRateResult.getSumRateList()) {
                    FlowStatisticsDetail flowStatisticsDetail = new FlowStatisticsDetail();
                    flowStatisticsDetail.setOffsetValue(offsetValue);
                    flowStatisticsDetail.setValue(value);
                    flowStatisticsDetail.setOffsetUnit("m");
                    result.add(flowStatisticsDetail);
                    offsetValue += networkFlowRateResult.getTimeInt();
                }
            }
        }
        log.info("CsceneFlowService.totalRate end. result:{}", result);
        return result;
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
