package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.*;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.service.*;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlowTaskTaskServiceImpl extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields,
        QueryParams> implements FlowTaskTaskService {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowProtocolService flowProtocolService;

    @Autowired
    private FlowScriptParamService flowScriptParamService;

    @Autowired
    private FlowTaskPortService flowTaskPortService;

    @Autowired
    private FlowConfig flowConfig;

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    @Autowired
    private FlowTaskConfService flowTaskConfService;

    public FlowTaskTaskServiceImpl(FlowTaskTaskMapper stuMapper, FlowTaskTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public int updateTaskStatus(String taskId, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getId, taskId);
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    @Override
    public int batchUpdateTaskStatus(List<String> taskIds, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    @Override
    public List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());

        return getModelMapper().list(condition);
    }

    
    @Override
    public List<FlowTaskNode> nodeList(String sceneId) {
        log.info("FlowTaskTaskServiceImpl.nodeList start. sceneId:{}", sceneId);
        List<FlowTaskNode> nodeList = getModelMapper().nodeList(sceneId);
        log.info("FlowTaskTaskServiceImpl.nodeList end. nodeList:{}", nodeList);
        return nodeList;
    }

    
    @Transactional
    public void addTask(List<AddFlowTaskTaskInfo> addFlowTaskTaskInfos, String sceneId, String actuatorId, boolean isAsync, List<FlowTaskTaskModel> flowTaskTaskModels) {
        log.info("FlowTaskTaskServiceImpl.addTask begin. addFlowTaskTaskInfos:{}, sceneId:{}", addFlowTaskTaskInfos, sceneId);
        if (CollectionUtils.isNotEmpty(addFlowTaskTaskInfos)) {
            
            int total = addFlowTaskTaskInfos.stream().mapToInt(detail -> detail.getFlowInfos().size()).sum();

            for (AddFlowTaskTaskInfo detail : addFlowTaskTaskInfos) {
                
                FlowTaskPortModel flowTaskPortModel = flowTaskPortService.findByNetworkIdAndActuatorId(detail.getNetworkId(), actuatorId);
                String macAddress = "";
                if (flowTaskPortModel == null) {
                    flowTaskPortModel = new FlowTaskPortModel();
                    FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
                    if (flowActuatorModel != null && StringUtils.isNotEmpty(flowActuatorModel.getInstanceId())) {
                        JSONObject addPortReq = new JSONObject();
                        addPortReq.put("network_id", detail.getNetworkId());
                        try {
                            String addTrexClientPortRsp = HttpUtils.sendPost(flowConfig.getCpUrl() + "/v1/instances/" +
                                    flowActuatorModel.getInstanceId() + "/interfaces", addPortReq.toJSONString());
                            JSONObject addTrexClientObj = JSONObject.parseObject(addTrexClientPortRsp);
                            log.info("add client port response:{}", addTrexClientObj.toJSONString());
                            if (BaseConstants.SUCCESS_ZERO.equals(addTrexClientObj.getString(BaseConstants.STR_CODE))) {
                                JSONObject clientDataObj = addTrexClientObj.getJSONObject(BaseConstants.STR_DATA);
                                macAddress = clientDataObj.getString("mac_address");
                                flowTaskPortModel.setMac(macAddress);
                                flowTaskPortModel.setActuatorId(actuatorId);
                                flowTaskPortModel.setNetworkId(detail.getNetworkId());
                                flowTaskPortService.create(flowTaskPortModel);
                            }
                        } catch (IOException e) {
                            log.error("【{}】add flow port error for networkId: {}", flowActuatorModel.getTitle(), detail.getNetworkId(), e);
                            throw new MpRuntimeException(FlowErrorCode.ADD_PORT_ERROR);
                        }
                    }
                } else {
                    macAddress = flowTaskPortModel.getMac();
                }
                HumanCondition condition = new HumanCondition();
                List<String> flowIds = detail.getFlowInfos().stream().map(obj -> obj.getFlowId()).collect(Collectors.toList());
                Map<String, AddFlowTaskFlowInfo> addFlowTaskFlowInfoMap = detail.getFlowInfos().stream().collect(Collectors.toMap(addFlowTaskFlowInfo -> addFlowTaskFlowInfo.getFlowId(), addFlowTaskFlowInfo -> addFlowTaskFlowInfo));
                condition.filterIn(FlowInfoModel::getId, flowIds.toArray());
                List<FlowInfoModel> flowInfoModelList = flowInfoMapper.list(condition);
                
                List<FlowTaskTaskModel> flowTaskTaskModelList = new ArrayList<FlowTaskTaskModel>();
                if (isAsync && CollectionUtils.isNotEmpty(flowTaskTaskModels)) {
                    for (FlowInfoModel flowInfoModel : flowInfoModelList) {
                        for (FlowTaskTaskModel modelTemp : flowTaskTaskModels) {
                            if (modelTemp.getFlowId().equals(flowInfoModel.getId()) && modelTemp.getNodeId().equals(detail.getNodeId())) {
                                FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
                                BeanUtils.copyProperties(modelTemp, flowTaskTaskModel);
                                flowTaskTaskModel.setActuatorId(actuatorId);
                                flowTaskTaskModel.setPortId(flowTaskPortModel.getId() == null ? "":flowTaskPortModel.getId());
                                flowTaskTaskModel.setNodeIp(detail.getNodeIp());
                                flowTaskTaskModel.setNodeName(detail.getNodeName());
                                flowTaskTaskModel.setTaskStatus(TaskTaskStatusEnum.INIT.toValue());
                                flowTaskTaskModelList.add(flowTaskTaskModel);
                            }
                        }
                    }
                    this.bulkUpdate(flowTaskTaskModelList);
                } else {
                    for (FlowInfoModel flowInfoModel : flowInfoModelList) {
                        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
                        flowTaskTaskModel.setSceneId(sceneId);
                        flowTaskTaskModel.setActuatorId(actuatorId);
                        flowTaskTaskModel.setResourceType(flowInfoModel.getResourceType());
                        flowTaskTaskModel.setInteractiveType(flowInfoModel.getInteractiveType());
                        flowTaskTaskModel.setFlowId(flowInfoModel.getId());
                        flowTaskTaskModel.setPortId(flowTaskPortModel.getId() == null ? "":flowTaskPortModel.getId());
                        flowTaskTaskModel.setNodeId(detail.getNodeId());
                        flowTaskTaskModel.setNodeIp(detail.getNodeIp());
                        flowTaskTaskModel.setNodeName(detail.getNodeName());
                        flowTaskTaskModelList.add(flowTaskTaskModel);
                    }
                    this.bulkCreate(flowTaskTaskModelList);
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
                        List<FlowTaskAttackConf> attackConfs = addFlowTaskFlowInfoMap.get(flowTaskTaskModel.getFlowId()).getAttackConfs();
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
                flowTaskConfService.bulkCreate(flowTaskConfModelList);
                
                List<String> taskIdList = new ArrayList<>();
                for (FlowTaskTaskModel flowTaskTaskModel : flowTaskTaskModelList) {
                    taskIdList.add(flowTaskTaskModel.getId());
                }
                if (!initNetwork(macAddress, taskIdList, detail.getNodeIp(), actuatorId, total)) {
                    log.error("init network error");
                    throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                }
                
                for (FlowInfoModel flowInfoModel : flowInfoModelList) {
                    if (!uploadPcapToTrex(flowInfoModel, actuatorId)) {
                        log.error("upload flow file info error");
                        throw new MpRuntimeException(FlowErrorCode.ADD_TASK_ERROR);
                    }
                }
            }
        }
        log.info("FlowTaskTaskServiceImpl.addTask end.");
    }


    
    public boolean uploadPcapToTrex(FlowInfoModel flowInfoModel, String actuatorId) {
        log.info("FlowTaskTaskServiceImpl.uploadPcapToTrex begin. flowInfoModel:{}, actuatorId:{}", flowInfoModel, actuatorId);
        if (flowInfoModel == null || StringUtils.isEmpty(actuatorId)) {
            return false;
        }
        boolean isSuccess = true;
        try {
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            String resourceUrl = CommonUtil.getResourcetUrl(flowActuatorModel.getIp());
            byte[] fileByte;

            JSONObject object = new JSONObject();
            object.put("flowId", flowInfoModel.getId());
            
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());
                object.put("fileName", flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getPcapFile().split(BaseConstants.SPOT)[1]);
                List<FlowProtocolModel> flowProtocolModelList = flowProtocolService.selectByFlowId(flowInfoModel.getId());
                if (CollectionUtils.isNotEmpty(flowProtocolModelList)) {
                    JSONObject protocolRate = new JSONObject();
                    for (FlowProtocolModel flowProtocolModel : flowProtocolModelList) {
                        protocolRate.put(flowProtocolModel.getProtocolName(), flowProtocolModel.getRate());
                    }
                    object.put("protocolRate", protocolRate);
                }
            } else {
                fileByte = FsCommon.downloadRawContent(flowInfoModel.getScriptFile());
                object.put("fileName", flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getScriptFile().split(BaseConstants.SPOT)[1]);
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
        log.info("FlowTaskTaskServiceImpl.uploadPcapToTrex end. isSuccess:{}", isSuccess);
        return isSuccess;
    }

    
    public boolean initNetwork(String mac, List<String> taskIdList, String targetIp, String actuatorId, int total) {
        log.info("FlowTaskTaskServiceImpl.initNetwork begin. mac:{}, taskIdList:{}, targetIp:{}, actuatorId:{}", mac, taskIdList, targetIp, actuatorId);
        if (StringUtils.isEmpty(mac) || CollectionUtils.isEmpty(taskIdList) || StringUtils.isEmpty(targetIp) || StringUtils.isEmpty(actuatorId)) {
            return false;
        }
        boolean isSuccess = true;
        try {
            FlowActuatorModel flowActuatorModel = flowActuatorMapper.get(actuatorId);
            String hostUrl = CommonUtil.getHostUrl(flowActuatorModel.getIp());
            JSONObject object = new JSONObject();
            JSONArray networks = new JSONArray();
            for (String taskId : taskIdList) {
                JSONObject network = new JSONObject();
                network.put("mac", mac);
                network.put("task_id", taskId);
                network.put("target_ip", targetIp);
                network.put("total", total);
                networks.add(network);
            }
            object.put("network_infos", networks);
            String pcapRsp = HttpUtils.sendPost(hostUrl + "/network/init", object.toJSONString());
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
        log.info("FlowTaskTaskServiceImpl.initNetwork end. isSuccess:{}", isSuccess);
        return isSuccess;
    }

    @Override
    public List<FlowTaskTaskModel> listByNodeIdAndFlowId(String nodeId, String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getFlowId, flowId);
        condition.orderBy("-createTime");
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    @Override
    public List<FlowTaskTaskModel> listByNodeIdAndSceneId(String nodeId, String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    
    @Override
    public void deleteByNodeIdAndSceneId(String nodeId, String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        getModelMapper().filterDelete(condition);
    }

    @Override
    public List<FlowTaskTaskModel> listBySceneIdAndStatus(String sceneId, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        condition.filterEqual(FlowTaskTaskModel::getTaskStatus, taskStatus);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    @Override
    public List<FlowTaskTaskModel> listBySceneId(String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    @Override
    public List<FlowTaskTaskModel> listByCondition(FlowTaskPageQueryParams request) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, request.getSceneId());
        if (StringUtils.isNotBlank(request.getNodeId())) {
            condition.filterEqual(FlowTaskTaskModel::getNodeId, request.getNodeId());
        }
        if (StringUtils.isNotBlank(request.getInteractiveType())) {
            condition.filterEqual(FlowTaskTaskModel::getInteractiveType, request.getInteractiveType());
        }
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }
}