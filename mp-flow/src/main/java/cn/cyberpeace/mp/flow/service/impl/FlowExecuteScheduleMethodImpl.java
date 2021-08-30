package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.request.FlowExecuteRequest;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 流量执行实现类
 * @Author qiaokang
 * @Date：2021/6/16 15:51
 */
@Service
@Slf4j
public class FlowExecuteScheduleMethodImpl implements ScheduleMethod {

    @Override
    public void execute(FlowBaseModel model) {
        try {
            FlowExecuteRequest request = (FlowExecuteRequest) model;
            // 执行任务参数
            JSONObject object = new JSONObject();
            object.put("uuid", request.getUuid());
            object.put("action", request.getAction());

            String actuatorTaskRsp = HttpUtils.sendPost(request.getUrlPath(),
                    JSONObject.toJSONString(object));
            JSONObject actuatorTaskObj = JSONObject.parseObject(actuatorTaskRsp);
            log.info("actuator task response:{}", actuatorTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(actuatorTaskObj)) {
                log.info("actuator task success");
            } else {
                log.error("actuator task failed");
            }
        } catch (Exception e) {
            log.error("actuator task error", e);
        }
    }
}
