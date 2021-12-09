package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void run(String... args) {

        try {
            FlowInfoModel flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            if (Objects.nonNull(flowInfoModel)) {
                log.info("=========== no need to import flow file ===========");
                return;
            }

            flowInfoService.batchImportFlowInfo();
        } catch (Exception e) {
            log.error("import flow file error", e);
        }
    }
}
