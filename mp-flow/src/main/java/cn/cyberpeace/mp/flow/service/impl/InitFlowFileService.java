package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description:  导入预制流量信息
 * @Author qiaokang
 * @Date：2021/9/1 14:36
 */
@Component
@Slf4j
public class InitFlowFileService implements CommandLineRunner {

    @Autowired
    private FlowInfoServiceImpl flowInfoService;

    @Autowired
    private FlowPcapServiceImpl flowPcapService;

    @Override
    public void run(String... args) {

        try {
            FlowInfoModel flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            if (Objects.nonNull(flowInfoModel)) {
                log.info("=========== no need to import flow file ===========");
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
            if (Objects.isNull(flowPcapModel) || (!flowInfoModel.getPcapFile().equals(flowPcapModel.getPcapFile())
                    && flowPcapModel.getCreateTime().equals(flowPcapModel.getUpdateTime()))) {
                flowInfoService.replacePcapPath();
            } else {
                log.info("=========== no need to init pcap analyse ===========");
            }
        } catch (Exception e) {
            log.error("import flow file error，{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
