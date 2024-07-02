package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.flow.service.impl.FlowPcapServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName InitFlowFileInitJob
 * @Description 导入预制流量信息
 * @Author qiaokang
 * @Date 2021/9/1 14:36
 * @Version 1.0.0
 */
@Component
@Slf4j
public class InitFlowFileInitJob {

    @Autowired
    private FlowInfoServiceImpl flowInfoService;

    @Autowired
    private FlowPcapServiceImpl flowPcapService;

    @MpInitiate(description = "初始化pcap包", id = "flow_file_init")
    public void run() {

        try {
            FlowInfoModel flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            if (Objects.nonNull(flowInfoModel)) {
                log.info("=========== no need to import flow file ===========");
                // 即使流量已经存在，也需要判断对应的配置参数信息是否初始化完成
                flowInfoService.checkDefaultFlowScriptParams();
            } else {
                flowInfoService.batchImportFlowInfo();
                flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            }

//            int pcapCount = flowPcapService.countAll();
//            if (pcapCount > 135) {
//                log.info("=========== no need to init pcap analyse ===========");
//            } else {
//                // 初始化pcap包解析
//                TaskCommon.executeTask(() -> flowInfoService.initPcapAnalyse());
//            }

            FlowPcapModel flowPcapModel = flowPcapService.getByFlowId(flowInfoModel.getId());
            if (Objects.nonNull(flowPcapModel) &&
                    !flowInfoModel.getPcapFile().equals(flowPcapModel.getPcapFile())
                    && flowPcapModel.getCreateTime().equals(flowPcapModel.getUpdateTime())) {
                flowInfoService.replacePcapPath();
            } else {
                log.info("=========== no need to init pcap analyse ===========");
            }
        } catch (Exception e) {
            log.error("import flow file error，{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
