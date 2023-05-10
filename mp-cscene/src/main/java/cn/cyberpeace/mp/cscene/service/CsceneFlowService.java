package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import cn.cyberpeace.mp.flow.model.*;
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

import java.util.ArrayList;
import java.util.List;

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

        FlowTaskPortModel flowTaskPortModel = flowTaskPortService.findByNetworkIdAndActuatorId(networkId, actuatorId);
        if (flowTaskPortModel == null) {
            //TODO:创建网口
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
            //todo:如果是需要创建网口的情况下，取值从新创建的网口的id
            flowTaskTaskModel.setPortId(flowTaskPortModel.getId());
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
}
