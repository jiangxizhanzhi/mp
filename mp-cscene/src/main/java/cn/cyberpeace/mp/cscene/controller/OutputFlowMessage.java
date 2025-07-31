package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.out.FlowInfo;
import cn.cyberpeace.mp.base.out.IOutputFlowMessage;
import cn.cyberpeace.mp.cscene.ws.WebSocketClientService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
public class OutputFlowMessage implements IOutputFlowMessage {

    public static Map<String, CsceneOutController.PintTest> pingTestMap = new ConcurrentHashMap<>();

    @Override
    public void sendFlow(FlowInfo flowInfo) {
        CsceneOutController.PintTest pintTest = pingTestMap.get(flowInfo.getTaskId());
        if(pintTest != null) {
            WebSocketClientService webSocketClientService = BeanCommon.getBean(WebSocketClientService.class);
            flowInfo.setDelay(pintTest.getDelay());
            flowInfo.setPacketLoss(pintTest.getPacketLoss());
            flowInfo.setConnected(pintTest.getConnected());
            // 在这里可以执行对外部系统发送数据的逻辑
            JSONObject jsonObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            // 将flowInfo对象转换为JSON字符串
            JSONObject flowInfoJson = JSONObject.parseObject(JSONObject.toJSONString(flowInfo));
            flowInfoJson.put("taskId", pintTest.getTaskId());
            flowInfoJson.put("attackerId", pintTest.getAttackerId());
            flowInfoJson.put("targetId", pintTest.getTargetId());
            dataObject.put("data", flowInfoJson);
            jsonObject.put("Data", dataObject.toJSONString());
            //        发送构建数据
            String jsonString = jsonObject.toJSONString();
            log.info("发送数据:{}",jsonString);
            webSocketClientService.sendMessage(jsonString);
            log.info("发送数据结束");
        }
    }

}
