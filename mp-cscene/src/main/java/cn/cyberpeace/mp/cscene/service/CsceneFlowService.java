package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskDetail;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowNodeTxBps;
import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticalCondition;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsAll;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsDetail;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkFlowRateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.util.*;
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

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    /**
     * 新增流量任务
     *
     * @param request
     */
    public void addTask(AddFlowTaskTaskRequest request) {
        log.debug("CsceneFlowService.addTask begin. request:{}", request);
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }

        // 过滤未添加流量的节点
        List<AddFlowTaskTaskDetail> list = new ArrayList<>();
        for (AddFlowTaskTaskDetail addFlowTaskTaskDetail : request.getDetails()) {
            if (CollectionUtils.isNotEmpty(addFlowTaskTaskDetail.getFlowInfos())) {
                list.add(addFlowTaskTaskDetail);
            }
        }
        request.setDetails(list);

        List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
        for (AddFlowTaskTaskDetail detail : request.getDetails()) {
            HumanCondition condition = new HumanCondition();
            List<String> flowIds = new ArrayList<>();
            for (AddFlowTaskFlowInfo obj : detail.getFlowInfos()) {
                String flowId = obj.getFlowId();
                flowIds.add(flowId);
            }
            Map<String, AddFlowTaskFlowInfo> addFlowTaskFlowInfoMap = new HashMap<>();
            for (AddFlowTaskFlowInfo addFlowTaskFlowInfo : detail.getFlowInfos()) {
                if (addFlowTaskFlowInfoMap.put(addFlowTaskFlowInfo.getFlowId(), addFlowTaskFlowInfo) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            condition.filterIn(FlowInfoModel::getId, flowIds.toArray());
            List<FlowInfoModel> flowInfoModelList = flowInfoMapper.list(condition);
            //保存流量任务信息
            for (FlowInfoModel flowInfoModel : flowInfoModelList) {
                FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
                flowTaskTaskModel.setSceneId(sceneId);
                flowTaskTaskModel.setResourceType(flowInfoModel.getResourceType());
                flowTaskTaskModel.setInteractiveType(flowInfoModel.getInteractiveType());
                flowTaskTaskModel.setFlowId(flowInfoModel.getId());
                flowTaskTaskModel.setNodeId(detail.getNodeId());
                flowTaskTaskModel.setTaskStatus(TaskTaskStatusEnum.INITING.toValue());
                flowTaskTaskModelList.add(flowTaskTaskModel);
            }
        }
        flowTaskTaskService.bulkCreate(flowTaskTaskModelList);

        TaskCommon.executeTask(() -> asyncAddTask(sceneId, request.getDetails(), flowTaskTaskModelList));

        log.debug("CsceneFlowService.addTask end.");
    }

    public void asyncAddTask(String sceneId, List<AddFlowTaskTaskDetail> addFlowTaskTaskDetails, List<FlowTaskTaskModel> flowTaskTaskModelList) {
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
            for (AddFlowTaskTaskDetail detail : addFlowTaskTaskDetails) {
                scenePluginDataBO.getElements().put(detail.getNodeId(), JsonCommon.beanToSnakeMap(elementBO));
            }
            SceneDataBO scene = sceneHandler.toData();
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
        for (AddFlowTaskTaskDetail detail : addFlowTaskTaskDetails) {
            if (CollectionUtils.isEmpty(detail.getFlowInfos())) {
                continue;
            }

            for (var f : detail.getFlowInfos()) {
                Flow flow = new Flow();
                flow.setId(f.getFlowId());
                var element = flowScenePluginHandler.getElement(detail.getNodeId());
                if (element.getFlows() == null) {
                    element.setFlows(new ArrayList<>());
                }
                element.getFlows().add(flow);
            }
            String nodeName = sceneHandler.getElement(detail.getNodeId()).getName();
            String nodeIp = "";
            String networkId = "";
            if (CollectionUtils.isNotEmpty(sceneHandler.getElement(detail.getNodeId()).getPorts())) {
                CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                for (SceneElementPortBO p : sceneHandler.getElement(detail.getNodeId()).getPorts()) {
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
            addFlowTaskTaskInfo.setFlowInfos(detail.getFlowInfos());
            addFlowTaskTaskInfo.setNodeId(detail.getNodeId());
            addFlowTaskTaskInfo.setNodeIp(nodeIp);
            addFlowTaskTaskInfo.setNodeName(nodeName);
            addFlowTaskTaskInfo.setNetworkId(networkId);
            addFlowTaskTaskInfoList.add(addFlowTaskTaskInfo);
        }
        sceneHandler.save();
        //新增流量执行任务
        if (CollectionUtils.isNotEmpty(addFlowTaskTaskInfoList)) {
            flowTaskTaskService.addTask(addFlowTaskTaskInfoList, sceneId, actuatorId, true, flowTaskTaskModelList);
        }
    }

    /**
     * 删除流量任务
     *
     * @param id
     */
    @Transactional
    public void deleteTask(String id) {
        log.debug("CsceneFlowService.deleteTask begin. id:{}", id);
        flowTaskTaskService.delete(id);
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(id);
        if (flowTaskConfModel != null) {
            flowTaskConfService.delete(flowTaskConfModel.getId());
        }
        flowTaskRecordService.deleteByTaskId(id);
        log.debug("CsceneFlowService.deleteTask end.");
    }

    /**
     * 编辑流量任务配置
     *
     * @param request
     */
    public void editTaskConf(EditFlowTaskConfRequest request) {
        log.debug("CsceneFlowService.editTaskConf begin. request:{}", request);
        FlowTaskConf flowTaskConf = request.getFlowTaskConf();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(request.getTaskId());
        flowTaskConfModel.setKeepRunning(request.getKeepRunning());
        flowTaskConfModel.setReplay(request.getReplay());
        flowTaskConfModel.setRateUnlimited(request.getRateUnlimited());
        flowTaskConfModel.setConf(JsonCommon.stringify(flowTaskConf));
        flowTaskConfService.update(flowTaskConfModel);
        log.debug("CsceneFlowService.editTaskConf end.");
    }

    /**
     * 流量任务列表查询
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<FlowTaskPageResponse> taskList(FlowTaskPageQueryParams request) {
        log.debug("CsceneFlowService.taskList begin. request:{}", request);
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        request.setOffset((request.getPage() - 1) * request.getPerPage());
        request.setSceneId(sceneId);
        // 查询符合条件的条数
        int total = flowTaskTaskMapper.countBySceneId(request);
        List<FlowTaskPageResponse> flowTaskPageResponseList = flowTaskTaskMapper.pageBySceneId(request);
        //统计实时速率
        if (CollectionUtils.isNotEmpty(flowTaskPageResponseList)) {
            List<String> ids = new ArrayList<>();
            for (FlowTaskPageResponse flowTaskPageResponse : flowTaskPageResponseList) {
                String id = flowTaskPageResponse.getId();
                ids.add(id);
            }
            List<FlowStatisticalCondition> conditions = this.getRealTimeCondition();
            Map<String, List<FlowRealTimeRate>> tealTimeRateMap = flowTaskRecordService.realTimeRate(ids, conditions);
            for (FlowTaskPageResponse temp : flowTaskPageResponseList) {
                temp.setRealTimeRateList(tealTimeRateMap.get(temp.getId()));
            }
        }

        if (CollectionUtils.isNotEmpty(flowTaskPageResponseList)) {
            List<String> list = new ArrayList<>();
            Set<String> uniqueValues = new HashSet<>();
            for (FlowTaskPageResponse flowTaskPageResponse : flowTaskPageResponseList) {
                String nodeId = flowTaskPageResponse.getNodeId();
                if (uniqueValues.add(nodeId)) {
                    list.add(nodeId);
                }
            }
            String[] nodeIds = list.toArray(new String[0]);
            List<SceneElementModel> sceneElementModelList = csceneInstanceService.listCsceneInstanceNodes(sceneId, nodeIds, SceneElementModel::getNodeId, SceneElementModel::getName);
            if (CollectionUtils.isNotEmpty(sceneElementModelList)) {
                Map<String, String> nodeIdAndNameMap = new HashMap<>();
                for (SceneElementModel sceneElementModel : sceneElementModelList) {
                    nodeIdAndNameMap.putIfAbsent(sceneElementModel.getNodeId(), sceneElementModel.getName());
                }
                for (FlowTaskPageResponse node : flowTaskPageResponseList) {
                    String realNodeName = nodeIdAndNameMap.get(node.getNodeId());
                    if (StringUtils.isNotBlank(realNodeName)) {
                        node.setNodeName(realNodeName);
                    }
                }
            }
        }

        ListObjServiceResult<FlowTaskPageResponse> result = new ListObjServiceResult(new ListObjResult<>(flowTaskPageResponseList, total).asResult()).asResult();
        log.debug("CsceneFlowService.taskList end. result:{}", result);
        return result;
    }

    /**
     * 条件查询任务id集合
     *
     * @param request
     * @return
     */
    public ServiceResult<List<String>> getTaskIds(FlowTaskPageQueryParams request) {
        String sceneId = csceneInstanceService.getSceneId(request.getId());
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        request.setSceneId(sceneId);
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByCondition(request);
        List<String> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowTaskTaskModels)) {
            List<String> list = new ArrayList<>();
            for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModels) {
                String id = flowTaskTaskModel.getId();
                list.add(id);
            }
            taskIds = list;
        }

        return ResultCommon.success(taskIds);
    }

    /**
     * 流量任务配置查询
     *
     * @param taskId
     * @return
     */
    public QueryFlowTaskConfResponse queryConfByTaskId(String taskId) {
        log.debug("CsceneFlowService.queryConfByTaskId begin. taskId:{}", taskId);
        QueryFlowTaskConfResponse result = new QueryFlowTaskConfResponse();
        FlowTaskConfModel flowTaskConfModel = flowTaskConfService.findByTaskId(taskId);
        BeanUtils.copyProperties(flowTaskConfModel, result);
        FlowTaskConf flowTaskConf = JsonCommon.parse(flowTaskConfModel.getConf(), FlowTaskConf.class);
        result.setFlowTaskConf(flowTaskConf);
        log.debug("CsceneFlowService.queryConfByTaskId end. result:{}", result);
        return result;
    }

    /**
     * 流量仿真概况
     *
     * @param id
     * @return
     */
    public FlowSummary queryFlowSummary(String id) {
        log.debug("CsceneFlowService.queryFlowSummary begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        FlowSummary flowSummary = flowTaskRecordService.queryFlowSummary(sceneId);
        log.debug("CsceneFlowService.queryFlowSummary end. flowSummary:{}", flowSummary);
        return flowSummary;
    }

    /**
     * top5实时速率
     *
     * @param id
     * @return
     */
    public List<FlowNodeTxBps> top5NodeTxBps(String id) {
        log.debug("CsceneFlowService.top5NodeTxBps begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        List<FlowNodeTxBps> flowNodeTxBpsList = flowTaskRecordService.queryNodeTxBps(sceneId);
        if (CollectionUtils.isNotEmpty(flowNodeTxBpsList) && flowNodeTxBpsList.size() > 5) {
            flowNodeTxBpsList = flowNodeTxBpsList.subList(0, 5);
        }
        log.debug("CsceneFlowService.top5NodeTxBps end. flowNodeTxBpsList:{}", flowNodeTxBpsList);
        return flowNodeTxBpsList;
    }

    /**
     * TOP5流量协议
     *
     * @param id
     * @return
     */
    public ArrayNode top5Protocols(String id) {
        log.debug("CsceneFlowService.top5Protocols begin. id:{}", id);
        ArrayNode protocols = JsonCommon.createArrayNode();
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
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
                    log.info("top5Protocols url is:{}", CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/protocols/top5");
                    String rsp = HttpUtils.sendPost(CommonUtil.getHostUrl(flowActuatorModel.getIp()) + "/tasks/protocols/top5", "{}");
                    log.info("top5Protocols rsp is:{}", rsp);
                    JsonNode rspObj = JsonCommon.parseTree(rsp);
                    log.info("query top5 protocols response:{}", rspObj.toPrettyString());
                    if (ResponseUtils.checkResponse(rspObj)) {
                        protocols.addAll((ArrayNode) rspObj.get(BaseConstants.STR_DATA).get("protocols"));
                    } else {
                        log.error("query top5 protocols failed");
                    }
                } catch (Exception e) {
                    log.error("query top5 protocols error, {}", ExceptionUtils.getStackTrace(e));
                }
            }
        }
        log.debug("CsceneFlowService.top5Protocols end. protocols:{}", protocols);
        return protocols;
    }

    /**
     * 流量仿真统计
     *
     * @param id
     * @return
     */
    public FlowStatisticsInfo flowStatistics(String id) {
        log.debug("CsceneFlowService.flowStatistics begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        FlowStatisticsInfo flowStatisticsInfo = flowTaskRecordService.flowStatistics(sceneId);
        log.debug("CsceneFlowService.flowStatistics end. flowStatisticsInfo:{}", flowStatisticsInfo);
        return flowStatisticsInfo;
    }

    /**
     * 流量仿真统计(所有)
     *
     * @param id
     * @return
     */
    public FlowStatisticsAll flowStatisticsAll(String id) {
        log.debug("CsceneFlowService.flowStatisticsAll begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
        FlowStatisticsAll flowStatisticsAll = new FlowStatisticsAll();
        flowStatisticsAll.setFlowSummary(flowTaskRecordService.queryFlowSummary(sceneId));
        flowStatisticsAll.setFlowStatisticsInfo(flowTaskRecordService.flowStatistics(sceneId));
        flowStatisticsAll.setTop5NodeTxBpsList(this.top5NodeTxBps(id));
        flowStatisticsAll.setTop5Protocols(this.top5Protocols(id));
        flowStatisticsAll.setTotalRateList(this.totalRate(id));
        log.debug("CsceneFlowService.flowStatisticsAll end. flowStatisticsAll:{}", flowStatisticsAll);
        return flowStatisticsAll;
    }

    /**
     * 流量任务启动
     *
     * @param taskId
     */
    public void startTask(String taskId) {
        log.debug("CsceneFlowService.startTask begin. taskId:{}", taskId);
        asyncPlatFlowTaskService.startTask(taskId, null);
        log.debug("CsceneFlowService.startTask end.");
    }

    /**
     * 流量任务启动
     *
     * @param taskIds
     */
    public void batchStartTasks(List<String> taskIds) {
        log.debug("CsceneFlowService.batchStartTasks begin. taskIds:{}", taskIds);
        asyncPlatFlowTaskService.batchStartTasks(taskIds, null);
        log.debug("CsceneFlowService.batchStartTasks end.");
    }

    /**
     * 流量任务停止
     *
     * @param taskId
     */
    public void stopTask(String taskId) {
        log.debug("CsceneFlowService.stopTask begin. taskId:{}", taskId);
        asyncPlatFlowTaskService.stopTask(taskId);
        log.debug("CsceneFlowService.stopTask end.");
    }

    /**
     * 流量任务批量停止
     *
     * @param taskIds
     */
    public void batchStopTasks(List<String> taskIds) {
        log.debug("CsceneFlowService.batchStopTasks begin. taskIds:{}", taskIds);
        asyncPlatFlowTaskService.batchStopTasks(taskIds);
        log.debug("CsceneFlowService.batchStopTasks end.");
    }

    /**
     * 根据场景id查询带有流量的节点id
     *
     * @param id
     * @return
     */
    public List<FlowTaskNode> nodeOption(String id) {
        log.debug("CsceneFlowService.nodeOption begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        List<FlowTaskNode> nodeList = flowTaskTaskService.nodeList(sceneId);

        if (CollectionUtils.isNotEmpty(nodeList)) {
            List<String> list = new ArrayList<>();
            Set<String> uniqueValues = new HashSet<>();
            for (FlowTaskNode flowTaskNode : nodeList) {
                String nodeId = flowTaskNode.getNodeId();
                if (uniqueValues.add(nodeId)) {
                    list.add(nodeId);
                }
            }
            String[] nodeIds = list.toArray(new String[0]);
            List<SceneElementModel> sceneElementModelList = csceneInstanceService.listCsceneInstanceNodes(sceneId, nodeIds, SceneElementModel::getNodeId, SceneElementModel::getName);
            if (CollectionUtils.isNotEmpty(sceneElementModelList)) {
                Map<String, String> nodeIdAndNameMap = new HashMap<>();
                for (SceneElementModel sceneElementModel : sceneElementModelList) {
                    nodeIdAndNameMap.putIfAbsent(sceneElementModel.getNodeId(), sceneElementModel.getName());
                }
                for (FlowTaskNode node : nodeList) {
                    String realNodeName = nodeIdAndNameMap.get(node.getNodeId());
                    if (StringUtils.isNotBlank(realNodeName)) {
                        node.setNodeName(realNodeName);
                    }
                }
            }
        }

        List<FlowTaskNode> list = new ArrayList<>();
        Set<FlowTaskNode> uniqueValues = new HashSet<>();
        for (FlowTaskNode flowTaskNode : nodeList) {
            if (uniqueValues.add(flowTaskNode)) {
                list.add(flowTaskNode);
            }
        }
        nodeList = list;

        log.debug("CsceneFlowService.nodeOption end. nodeList:{}", nodeList);
        return nodeList;
    }

    /**
     * 根据场景id查询环境总速率
     *
     * @return
     */
    public List<FlowStatisticsDetail> totalRate(String id) {
        log.debug("CsceneFlowService.totalRate begin. id:{}", id);
        String sceneId = csceneInstanceService.getSceneId(id);
        if (StringUtils.isEmpty(sceneId)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_STARTED);
        }
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
        for (Map.Entry<String, SceneElementPortBO> entry : portMap.entrySet()) {
            String key = entry.getKey();
            SceneElementPortBO v = entry.getValue();
            if (cloudPluginHandler.getPort(v.getId()) != null && StringUtils.isNotEmpty(cloudPluginHandler.getPort(v.getId()).getResourceId())) {
                portIds.add(cloudPluginHandler.getPort(v.getId()).getResourceId());
            }
        }
        if (CollectionUtils.isNotEmpty(portIds) && totalSecond > 0) {
            List<String> newPortIds = new ArrayList<>();
            Set<String> uniqueValues = new HashSet<>();
            for (String portId : portIds) {
                if (uniqueValues.add(portId)) {
                    newPortIds.add(portId);
                }
            }
            NetworkFlowRateResult networkFlowRateResult = scenePluginCloudSceneService.networkFlowRate(newPortIds, totalSecond);
            if (networkFlowRateResult != null && networkFlowRateResult.getTimeInt() != null && CollectionUtils.isNotEmpty(networkFlowRateResult.getSumRateList())) {
                Integer offsetValue = 0 - (networkFlowRateResult.getSumRateList().size() - 1) * networkFlowRateResult.getTimeInt();
                for (Long value : networkFlowRateResult.getSumRateList()) {
                    FlowStatisticsDetail flowStatisticsDetail = new FlowStatisticsDetail();
                    flowStatisticsDetail.setOffsetValue(offsetValue);
                    flowStatisticsDetail.setValue(value);
                    flowStatisticsDetail.setOffsetUnit(networkFlowRateResult.getUnit());
                    result.add(flowStatisticsDetail);
                    offsetValue += networkFlowRateResult.getTimeInt();
                }
            }
        }
        log.debug("CsceneFlowService.totalRate end. result:{}", result);
        return result;
    }

    /**
     * 获取实时速率统计条件
     *
     * @return
     */
    private List<FlowStatisticalCondition> getRealTimeCondition() {
        log.debug("CsceneFlowService.getRealTimeCondition begin.");
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
            offSet -= 10;
        }
        log.debug("CsceneFlowService.getRealTimeCondition end. queryList:{}", queryList);
        return queryList;
    }


    private String getUsableIp(String networkId, SceneHandler sceneHandler) {
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        ArrayList<CloudPluginSceneElementBO> elements = new ArrayList<>(pluginHandler.getElementMap().values());
        ConnectedNetworkBO findNet = null;
        for (CloudPluginSceneElementBO e : elements) {
            List<ConnectedNetworkBO> nets = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(e.getDeriveNetworks())) {
                nets.addAll(e.getDeriveNetworks());
            }
            if (CollectionUtils.isNotEmpty(e.getAccessNetworks())) {
                nets.addAll(e.getAccessNetworks());
            }
            boolean finded = false;
            for (ConnectedNetworkBO n : nets) {
                if (networkId.equals(n.getNetwork().getResourceId())) {
                    finded = true;
                    findNet = n;
                }
            }
            if (finded) {
                break;
            }
        }
        if (findNet == null) {
            return null;
        } else {
            List<SceneElementPortBO> ports = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(findNet.getFromPorts())) {
                ports.addAll(findNet.getFromPorts());
            }
            if (CollectionUtils.isNotEmpty(findNet.getToPorts())) {
                ports.addAll(findNet.getToPorts());
            }
            List<String> ips = new ArrayList<>();
            for (SceneElementPortBO p : ports) {
                if (StringUtils.isNotEmpty(p.getIp())) {
                    String ip = p.getIp();
                    ips.add(ip);
                }
            }
            List<String> allocateIp = IpUtils.newRandomIps(findNet.getNetwork().getCidr(), 1, ips, new ArrayList<>(), false);
            if (CollectionUtils.isNotEmpty(allocateIp)) {
                return allocateIp.get(0);
            } else {
                return null;
            }
        }
    }
}
