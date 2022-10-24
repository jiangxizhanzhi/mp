package cn.cyberpeace.mp.cscene.flow;

import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.service.FlowGenerateService;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/19
 */
public class TrexFlowTask {

    private FlowGenerateService generateService;

    public void check() {
        // 校验带宽等资源
    }

    public void start() {
        // generateService.startFlowTask();
        FlowTaskRecordBO recordBO = new FlowTaskRecordBO();
        recordBO.setBandwidth(1);
        recordBO.setCategory(1);
        recordBO.setTargetMac("");
        recordBO.setLoadMode(null);
        generateService.startFlowTask(recordBO);
        //
    }
}
