package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.TimeCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.request.FlowExecuteRequest;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
public class FlowExecuteScheduleMethodImpl implements ScheduleMethod {

    @Autowired
    private FlowTaskServiceImpl flowTaskService;

    @Override
    public void execute(FlowBaseModel model) {
        FlowExecuteRequest request = (FlowExecuteRequest) model;
        try {
            
            JSONObject object = new JSONObject();
            object.put("uuid", request.getUuid());
            object.put("action", request.getAction());
            object.put("time", TimeCommon.localDateTimeToString(LocalDateTime.now()));

            String actuatorTaskRsp = HttpUtils.sendPost(request.getUrlPath(),
                    JSONObject.toJSONString(object));
            JSONObject actuatorTaskObj = JSONObject.parseObject(actuatorTaskRsp);
            log.info("actuator task response:{}", actuatorTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(actuatorTaskObj)) {
                log.info("actuator task success");

                
                flowTaskService.increaseRunningCount(request.getTaskId());
            } else {
                log.error("actuator task failed:[{}]", request.getTaskId());
            }
        } catch (Exception e) {
            log.error("actuator task error:[{}, {}]", request.getTaskId(), ExceptionUtils.getStackTrace(e));
        }
    }
}
