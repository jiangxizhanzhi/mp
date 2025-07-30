package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.out.FlowInfo;
import cn.cyberpeace.mp.base.out.IOutputFlowMessage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OutputFlowMessage implements IOutputFlowMessage {

    public static Map<String, CsceneOutController.PintTest> pingTestMap = new ConcurrentHashMap<>();

    @Override
    public void sendFlow(FlowInfo flowInfo) {
        CsceneOutController.PintTest pintTest = pingTestMap.get(flowInfo.getTaskId());
        if(pintTest != null) {
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
            System.out.println(JsonCommon.stringify(pintTest));
        }
    }

}
