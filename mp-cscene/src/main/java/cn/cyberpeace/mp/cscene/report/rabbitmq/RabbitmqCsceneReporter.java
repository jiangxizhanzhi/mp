package cn.cyberpeace.mp.cscene.report.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqCsceneReporter {

    public static final String MESSAGE_TYPE_DELETE_CSCENE_INSTANCE = "delete_cscene_instance";

    @Autowired
    private CsceneReportRabbitmqSender messageSender;

    public void reportDeleteCsceneInstance(String csceneInstanceId) {
        messageSender.send(csceneInstanceId, MESSAGE_TYPE_DELETE_CSCENE_INSTANCE);
    }

}
