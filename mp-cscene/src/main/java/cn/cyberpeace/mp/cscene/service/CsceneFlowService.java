package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.FsCommon;
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
import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.*;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
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
    private FlowProtocolService flowProtocolService;

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

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

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
        String macAddr = "";
        if (CollectionUtils.isNotEmpty(sceneHandler.getElement(request.getNodeId()).getPorts())) {
            CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            for (var p : sceneHandler.getElement(request.getNodeId()).getPorts()) {
                nodeIp = p.getIp();
                macAddr = p.getMacAddr();
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
        String actuatorId = null;
        if (flowScenePluginHandler == null) {
            actuatorId = flowScenePluginHandler.getScene().getExecutor();
        }
        //流量执行器不存在则创建流量执行器
        if (StringUtils.isEmpty(actuatorId)) {
            String trexName = StringUtils.joinWith("_", FlowPlatformEnum.TREX, sceneId);
            actuatorId = asyncPlatFlowTaskService.syncStartExecutor(trexName);
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
        //保存流量任务信息
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

        //保存流量任务配置信息
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
        //上传pcap包虚拟机
        for (FlowInfoModel flowInfoModel : flowInfoModelList) {
            if (!uploadPcapToTrex(flowInfoModel, actuatorId)) {
                log.error("upload flow file info error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
        }
        //初始化网口
        for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
            if (!initNetwork(macAddr, flowTaskTaskModel.getId(), nodeIp, actuatorId)) {
                log.error("init network error");
                throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
            }
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
        if (StringUtils.isNotEmpty(request.getNodeId())) {
            humanCondition.currentLink(FlowTaskTaskModel.class).filterEqual(FlowTaskTaskModel::getNodeId, request.getNodeId());
        }
        if (StringUtils.isNotEmpty(request.getInteractiveType())) {
            humanCondition.currentLink(FlowTaskTaskModel.class).filterEqual(FlowTaskTaskModel::getInteractiveType, request.getInteractiveType());
        }
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
     * 上传pcap包到trex虚拟机
     * @param flowInfoModel
     * @param actuatorId
     * @return
     */
    public boolean uploadPcapToTrex(FlowInfoModel flowInfoModel, String actuatorId) {
        log.info("CsceneFlowService.uploadPcapToTrex begin. flowInfoModel:{}, actuatorId:{}", flowInfoModel, actuatorId);
        if (flowInfoModel == null || StringUtils.isEmpty(actuatorId)) {
            return false;
        }
        boolean isSuccess = true;
        try {
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());
            byte[] fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());

            JSONObject object = new JSONObject();
            object.put("flowId", flowInfoModel.getId());
            object.put("fileName", flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getPcapFile().split(BaseConstants.SPOT)[1]);
            // 判断是pcap包还是脚本
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                List<FlowProtocolModel> flowProtocolModelList = flowProtocolService.selectByFlowId(flowInfoModel.getId());
                if (CollectionUtils.isNotEmpty(flowProtocolModelList)) {
                    JSONObject protocolRate = new JSONObject();
                    for (FlowProtocolModel flowProtocolModel : flowProtocolModelList) {
                        protocolRate.put(flowProtocolModel.getProtocolName(), flowProtocolModel.getRate());
                    }
                    object.put("protocolRate", protocolRate);
                }
            }
            String pcapRsp = HttpUtils.postFile(resourceUrl + "/pcap/upload", fileByte, "file", object);
            JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
            log.info("upload pcap file response:{}", jsonObject.toJSONString());
            if (!ResponseUtils.checkResponse(jsonObject)) {
                log.error("upload file to flow actuator platform fail!");
                isSuccess = false;
            }
        } catch (Exception e) {
            log.error("upload file to flow actuator platform error!", e);
            isSuccess = false;
        }
        log.info("CsceneFlowService.uploadPcapToTrex end. isSuccess:{}", isSuccess);
        return isSuccess;
    }

    /**
     * 初始化网口
     * @param mac
     * @param taskId
     * @param targetIp
     * @param actuatorId
     * @return
     */
    public boolean initNetwork(String mac, String taskId, String targetIp, String actuatorId) {
        log.info("CsceneFlowService.initNetwork begin. mac:{}, taskId:{}, targetIp:{}, actuatorId:{}", mac, taskId, targetIp, actuatorId);
        if (StringUtils.isEmpty(mac) || StringUtils.isEmpty(taskId) || StringUtils.isEmpty(targetIp) || StringUtils.isEmpty(actuatorId)) {
            return false;
        }
        boolean isSuccess = true;
        try {
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());
            JSONObject object = new JSONObject();
            object.put("mac", mac);
            object.put("taskId", taskId);
            object.put("targetIp", targetIp);
            String pcapRsp = HttpUtils.sendPost(resourceUrl + "/network/init", object.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
            log.info("init network response:{}", jsonObject.toJSONString());
            if (!ResponseUtils.checkResponse(jsonObject)) {
                log.error("init network fail!");
                isSuccess = false;
            }
        } catch (Exception e) {
            log.error("init network error!", e);
            isSuccess = false;
        }
        log.info("CsceneFlowService.initNetwork end. isSuccess:{}", isSuccess);
        return isSuccess;
    }

    /**
     * 流量任务启动
     * @param taskId
     */
    public void startTask(String taskId) {
        log.info("CsceneFlowService.startTask begin. taskId:{}", taskId);
        asyncPlatFlowTaskService.startTask(taskId);
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
