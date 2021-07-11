package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;


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
                
                flowInfoService.checkDefaultFlowScriptParams();
            } else {
                flowInfoService.batchImportFlowInfo();
                flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            }









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
