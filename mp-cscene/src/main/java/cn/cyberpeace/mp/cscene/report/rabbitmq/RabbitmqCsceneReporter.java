package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RabbitmqCsceneReporter {

    public static final String MESSAGE_TYPE_DELETE_CSCENE_INSTANCE = "delete_cscene_instance";

    @Autowired
    private CsceneReportRabbitmqSender messageSender;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    public void reportDeleteCsceneInstance(String csceneInstanceId) {
        messageSender.send(csceneInstanceId, MESSAGE_TYPE_DELETE_CSCENE_INSTANCE, null, csceneAppConfig.getSceneMessageTimeout());
    }

}
