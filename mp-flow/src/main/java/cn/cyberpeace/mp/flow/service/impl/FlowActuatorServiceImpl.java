package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowActuatorQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.response.SceneInstanceResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.field.FlowActuatorFields;
import cn.cyberpeace.mp.flow.service.field.FlowInfoFields;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: 流量执行器service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowActuatorServiceImpl extends BaseModelServiceImpl<FlowActuatorModel, FlowActuatorMapper, FlowActuatorFields, FlowActuatorQueryParams> implements FlowActuatorService {

    @Autowired
    private FlowConfig flowConfig;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private FlowTaskTaskService flowTaskTaskService;

    @Autowired
    private AsyncPlatFlowTaskService asyncPlatFlowTaskService;

    public FlowActuatorServiceImpl(FlowActuatorMapper stuMapper, FlowActuatorFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 删除虚拟机实例
     */
    @Override
    public void delExecutor(String actuatorId) {
        FlowActuatorModel flowActuatorModel = this.getModelMapper().get(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flowActuatorModel not exist for [{}]", actuatorId);
            return;
        }

        try {
            String delVmInstanceRsp = HttpUtils.sendDelete(flowConfig.getCpUrl() + "/v1/instances/" + flowActuatorModel.getInstanceId());
            JSONObject delVmInstanceObj = JSONObject.parseObject(delVmInstanceRsp);
            log.info("delete vm instance response:{}", delVmInstanceObj.toJSONString());
            if (!BaseConstants.SUCCESS_ZERO.equals(delVmInstanceObj.getString(BaseConstants.STR_CODE))) {
                log.error("delete vm instance error!");
            } else {
                log.info("delete vm instance success!");
            }
        } catch (Exception e) {
            log.error("delete vm instance error, {}", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public ListObjServiceResult<FlowActuatorResponse> page(FlowActuatorQueryParams queryParams) {

        // 获取流量发生器列表
        ListObjServiceResult<FlowActuatorModel> listObjServiceResult = list(queryParams);

        // 结果转换
        List<FlowActuatorResponse> responseList = new ArrayList<>();
        List<FlowActuatorModel> flowActuatorModels = listObjServiceResult.getSource().getObjs();
        if (CollectionUtils.isNotEmpty(flowActuatorModels)) {
            List<String> sceneIds = new ArrayList<>();
            for (FlowActuatorModel flowActuatorModel : flowActuatorModels) {
                String id = flowActuatorModel.getInstanceId();
                sceneIds.add(id);
            }
            // 获取关联场景实例数据
            HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                    .filterIn(SceneModel::getId, sceneIds.toArray());
            List<SceneModel> sceneModels = sceneMapper.list(humanCondition);
            Map<String, String> sceneMap = new HashMap<>();
            for (SceneModel sceneModel : sceneModels) {
                sceneMap.put(sceneModel.getId(), sceneModel.getName());
            }
            // 拼装返回信息
            for (FlowActuatorModel model : flowActuatorModels) {
                FlowActuatorResponse response = FlowActuatorResponse.builder()
                        .id(model.getId()).title(model.getTitle()).sceneName(sceneMap.get(model.getInstanceId()))
                        .ip(model.getIp()).targetSubnet(model.getNetworkId()).status(model.getStatus()).build();
                if (Objects.nonNull(model.getCreateTime())) {
                    response.setCreateTime(TimeUtils.localDateTimeToString(model.getCreateTime()));
                }
                responseList.add(response);
            }
        }

        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listObjServiceResult.getSource().getTotal())).asResult();
    }

    @Override
    public void batchStart(List<String> actuatorIds) {

        // 根据流量发生器id列表获取对应的任务id列表
        List<String> taskIds = getTaskIdsByActuatorIds(actuatorIds);

        // 启动对应任务
        if (CollectionUtils.isNotEmpty(taskIds)) {
            log.debug("FlowActuatorService.batchStart begin. taskIds:{}", taskIds);
            asyncPlatFlowTaskService.batchStartTasks(taskIds, null);
            log.debug("FlowActuatorService.batchStart end.");
        }

    }

    @Override
    public void batchPause(List<String> actuatorIds) {
        // 根据流量发生器id列表获取对应的任务id列表
        List<String> taskIds = getTaskIdsByActuatorIds(actuatorIds);

        // 停止对应任务
        if (CollectionUtils.isNotEmpty(taskIds)) {
            log.debug("FlowActuatorService.batchPause begin. taskIds:{}", taskIds);
            asyncPlatFlowTaskService.batchStopTasks(taskIds);
            log.debug("FlowActuatorService.batchPause end.");
        }

    }

    @Override
    public void batchReboot(List<String> actuatorIds) {
        // 根据流量发生器id列表获取对应的任务id列表
        List<String> taskIds = getTaskIdsByActuatorIds(actuatorIds);

        // 重启对应任务
        if (CollectionUtils.isNotEmpty(taskIds)) {
            log.debug("FlowActuatorService.batchReboot begin. taskIds:{}", taskIds);
            // 先停止
            asyncPlatFlowTaskService.batchStartTasks(taskIds, null);
            // 再启动
            asyncPlatFlowTaskService.batchStopTasks(taskIds);
            log.debug("FlowActuatorService.batchReboot end.");
        }
    }

    @Override
    public ListObjServiceResult<SceneInstanceResponse> listSceneByFuzzy(String name, int limit) {

        // 获取场景实例信息
        HumanCondition condition = new HumanCondition();
        if (StringUtils.isNotBlank(name)){
            condition.filterStartsWith(SceneModel::getName, name);
        }
        condition.distinct();
        condition.setLimit(limit);
        List<SceneModel> sceneModels = sceneMapper.list(condition);

        // 结果转换
        List<SceneInstanceResponse> responseList = new ArrayList<>();
        for (SceneModel sceneModel : sceneModels){
            SceneInstanceResponse response = SceneInstanceResponse.builder().id(sceneModel.getId()).name(sceneModel.getName()).build();
            responseList.add(response);
        }
        return new ListObjServiceResult<>(responseList, responseList.size()).asResult();
    }

    /**
     * 补充list查询条件
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowActuatorQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getInstanceId())) {
            humanCondition.filterEqual("instanceId", queryParams.getInstanceId());
        }

        if (Objects.nonNull(queryParams.getStatus())) {
            humanCondition.filterEqual("status", queryParams.getStatus());
        }
    }

    private List<String> getTaskIdsByActuatorIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<FlowTaskTaskModel> taskModels = flowTaskTaskService.listByActuatorIds(ids);
        List<String> taskIds = new ArrayList<>();
        for (FlowTaskTaskModel model : taskModels) {
            taskIds.add(model.getId());
        }
        return taskIds;
    }

}
