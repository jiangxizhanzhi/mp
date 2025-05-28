// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskTaskServiceImpl.java.
// File size 23287.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FlowTaskTaskServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowTaskTaskService extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields, QueryParams> {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowProtocolService flowProtocolService;

    public FlowTaskTaskService(FlowTaskTaskMapper stuMapper, FlowTaskTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    public int updateTaskStatus(String taskId, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getId, taskId);
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    public int batchUpdateTaskStatus(List<String> taskIds, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    public List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());

        return getModelMapper().list(condition);
    }

    /**
     * 根据场景id查询节点列表
     * @param sceneId
     * @return
     */
    public List<FlowTaskNode> nodeList(String sceneId) {
        log.info("FlowTaskTaskServiceImpl.nodeList start. sceneId:{}", sceneId);
        List<FlowTaskNode> nodeList = getModelMapper().nodeList(sceneId);
        log.info("FlowTaskTaskServiceImpl.nodeList end. nodeList:{}", nodeList);
        return nodeList;
    }


    /**
     * 上传pcap包到trex虚拟机
     * @param flowInfoModel
     * @param actuatorId
     * @return
     */
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
            // 判断是pcap包还是脚本
            if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
                fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());
                object.put("fileName", flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getPcapFile()
                        .split(BaseConstants.SPOT)[1]);
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
                object.put("fileName", flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getScriptFile()
                        .split(BaseConstants.SPOT)[1]);
            }
            String pcapRsp = HttpUtils.postFileV2(resourceUrl + "/pcap/upload", fileByte, "file", object);
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

    /**
     * 初始化网口
     * @param mac
     * @param taskIdList
     * @param targetIp
     * @param actuatorId
     * @param total
     * @return
     */
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
            for (int i = 0; i < 10; i++) {
                try {
                    String pcapRsp = HttpUtils.sendPost(hostUrl + "/network/init", object.toJSONString());
                    JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
                    log.info("init network response:{}", jsonObject.toJSONString());
                    if (!ResponseUtils.checkResponse(jsonObject)) {
                        log.error("init network fail!");
                        isSuccess = false;
                        Thread.sleep(2000);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("init network error at {}", i, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("init network error!", e);
            isSuccess = false;
        }
        log.info("FlowTaskTaskServiceImpl.initNetwork end. isSuccess:{}", isSuccess);
        return isSuccess;
    }

    public List<FlowTaskTaskModel> listByNodeIdAndFlowId(String nodeId, String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getFlowId, flowId);
        condition.orderBy("-createTime");
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    public List<FlowTaskTaskModel> listByNodeIdAndSceneId(String nodeId, String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    /**
     * 根据节点id、scendId删除任务
     * @param nodeId
     * @param sceneId
     * @return
     */

    public void deleteByNodeIdAndSceneId(String nodeId, String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getNodeId, nodeId);
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        getModelMapper().filterDelete(condition);
    }

    public List<FlowTaskTaskModel> listBySceneIdAndStatus(String sceneId, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        condition.filterEqual(FlowTaskTaskModel::getTaskStatus, taskStatus);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    public List<FlowTaskTaskModel> listBySceneId(String sceneId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getSceneId, sceneId);
        List<FlowTaskTaskModel> flowTaskTaskModels = getModelMapper().list(condition);
        return flowTaskTaskModels;
    }

    public List<FlowTaskTaskModel> listByActuatorIds(List<String> sceneId) {
        HumanCondition condition = new HumanCondition();
        if (CollectionUtils.isNotEmpty(sceneId)) {
            condition.filterIn(FlowTaskTaskModel::getActuatorId, sceneId.toArray());
            condition.distinct();
        }
        return getModelMapper().list(condition);
    }

    public List<FlowTaskTaskModel> listByCondition(FlowTaskPageQueryParams request) {
        return null;
    }
}