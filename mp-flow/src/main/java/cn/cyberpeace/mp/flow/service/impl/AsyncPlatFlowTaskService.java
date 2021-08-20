package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskMapper;
import cn.cyberpeace.mp.flow.request.AddAppTaskRequest;
import cn.cyberpeace.mp.flow.request.AddStandardsTaskRequest;
import cn.cyberpeace.mp.flow.util.HttpDeleteUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 流量任务异步执行服务类
 * @Autho qiaokang
 * @Date：2021/8/20 09:56
 */
@Service
@Slf4j
public class AsyncPlatFlowTaskService {

    /**
     * 流量发生平台任务地址
     */
    @Value("${mp.flow.actuator-task-url}")
    private String actuatorTaskUrl;

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    /**
     * 异步保存流量发生平台任务
     *
     * @param flowTaskModel
     * @param isUpdate
     */
    @Async
    public void savePlatFlowTask(FlowTaskModel flowTaskModel, boolean isUpdate) {
        log.info("start to " + (isUpdate ? "update" : "add") + "platform flow task");

        // 流量任务存在校验
        if (Objects.isNull(flowTaskModel)) {
            log.error("flow task not exist");
            return;
        }

        // 流量平台任务是否已创建校验
        if (isUpdate) {
            if (StringUtils.isBlank(flowTaskModel.getThirdTaskId())) {
                log.error("third flow task is not exist");
                return;
            }
        } else {
            if (StringUtils.isNotBlank(flowTaskModel.getThirdTaskId())) {
                log.error("third flow task is already exist");
                return;
            }
        }

        // 流量信息存在校验
        FlowInfoModel flowInfoModel = flowInfoMapper.get(flowTaskModel.getFlowId());
        if (Objects.isNull(flowInfoModel)) {
            log.error("flow info in task not exist:{}", flowTaskModel.getFlowId());
            return;
        }

        // 流量执行器信息查询
        String sceneId = flowTaskModel.getSceneId();
        if (StringUtils.isBlank(sceneId)) {
            log.error("sceneId in flow task is empty");
            return;
        }
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.selectBySceneId(sceneId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flow actuator is empty for sceneId:{}", sceneId);
            return;
        }

        String thirdTaskId = "";

        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            // 构建保存app任务请求
            AddAppTaskRequest appTaskRequest = new AddAppTaskRequest(flowTaskModel, flowActuatorModel);

            try {
                String saveAppTaskRsp = null;
                if (isUpdate) {
                    // 更新
                    saveAppTaskRsp = HttpUtils.sendPatch(actuatorTaskUrl + "/tasks/app/" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(appTaskRequest));
                } else {
                    // 新增
                    saveAppTaskRsp = HttpUtils.sendPost(actuatorTaskUrl + "/tasks/app",
                            JsonUtils.toUnderlineJSONString(appTaskRequest));
                }

                JSONObject saveAppTaskObj = JSONObject.parseObject(saveAppTaskRsp);
                log.info("save app task response:{}", saveAppTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveAppTaskObj)) {
                    // 第三方任务id
                    thirdTaskId = saveAppTaskObj.getJSONObject(BaseConstants.STR_DATA).getString("uuid");
                } else {
                    log.error("save app task failed");
                }
            } catch (Exception e) {
                log.error("save app task error", e);
            }
        } else {
            try {
                // 封装请求参数
                AddStandardsTaskRequest standTaskRequest = new AddStandardsTaskRequest(flowTaskModel);
                standTaskRequest.setPort(flowActuatorModel.getClientMac());
                String saveStandTaskRsp = null;
                if (isUpdate) {
                    saveStandTaskRsp = HttpUtils.sendPatch(actuatorTaskUrl + "/tasks/standards" + flowTaskModel.getThirdTaskId(),
                            JsonUtils.toUnderlineJSONString(standTaskRequest));
                } else {
                    saveStandTaskRsp = HttpUtils.sendPost(actuatorTaskUrl + "/tasks/standards",
                            JsonUtils.toUnderlineJSONString(standTaskRequest));
                }

                JSONObject saveStandTaskObj = JSONObject.parseObject(saveStandTaskRsp);
                log.info("save standard task response:{}", saveStandTaskObj.toJSONString());
                if (ResponseUtils.checkResponse(saveStandTaskObj)) {
                    // 第三方任务id
                    thirdTaskId = saveStandTaskObj.getString(BaseConstants.STR_DATA);
                } else {
                    log.error("save standard task failed");
                }
            } catch (Exception e) {
                log.error("save standard task error", e);
            }
        }

        // 更新第三方任务id
        if (StringUtils.isNotBlank(thirdTaskId)) {
            flowTaskModel.setThirdTaskId(thirdTaskId);
            flowTaskMapper.update(flowTaskModel);
        }

    }

    /**
     * 异步删除流量发生平台任务
     * @param resourceType
     * @param thirdTaskIds
     */
    @Async
    public void deletePlatFlowTask(int resourceType, List<String> thirdTaskIds) {
        String delUrl = "";
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            log.info("start to delete app task");
            // 删除pcap包地址
            delUrl = "/tasks/app";
        } else {
            log.info("start to delete standard task");
            // 删除脚本文件地址
            delUrl = "/tasks/standards";
        }

        if (CollectionUtils.isEmpty(thirdTaskIds)) {
            log.info("no thirdTaskIds to delete");
            return;
        }

        JSONObject object = new JSONObject();
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(thirdTaskIds));
        object.put("uuids", array);

        try {
            String rsp = HttpDeleteUtil.sendDelete(actuatorTaskUrl + delUrl, object.toJSONString());
            JSONObject rspObject = JSONObject.parseObject(rsp);
            log.info("delete flow task response: {}", rspObject.toJSONString());

            if (ResponseUtils.checkResponse(rspObject)) {
                log.info("delete flow task success");
            } else {
                log.error("delete flow task response error");
            }
        } catch (Exception e) {
            log.error("delete task from flow actuator platform error");
        }
    }

}
