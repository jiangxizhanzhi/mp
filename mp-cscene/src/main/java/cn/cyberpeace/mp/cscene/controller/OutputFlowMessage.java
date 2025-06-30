package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.out.FlowInfo;
import cn.cyberpeace.mp.base.out.IOutputFlowMessage;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OutputFlowMessage implements IOutputFlowMessage {

    public static Map<String, CsceneOutController.PintTest> pingTestMap = new ConcurrentHashMap<>();

    @Override
    public void sendFlow(FlowInfo flowInfo) {
        CsceneOutController.PintTest pintTest = pingTestMap.get(flowInfo.getTaskId());
        flowInfo.setDelay(pintTest.getDelay());
        flowInfo.setPacketLoss(pintTest.getPacketLoss());
        flowInfo.setConnected(pintTest.getConnected());
        // 在这里可以执行对外部系统发送数据的逻辑
        System.out.println(JsonCommon.stringify(pintTest));
    }
}
