package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementCommonBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public void reportElementCommon(CollectedCsceneElementCommonBO<?> csceneElement) {
        if (StringUtils.isEmpty(csceneElement.getCsceneId())) {
            csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        }
        if (StringUtils.isEmpty(csceneElement.getCallAction())) {
            csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        }
        log.debug("scene message, type :{}, content :{}, context:{}", RabbitmqCsceneSceneReporter.MESSAGE_TYPE_SCENE_ELEMENT, JsonCommon.stringify(csceneElement), JsonCommon.stringify(TransferContext.getContextMap()));
        messageSender.send(csceneElement, RabbitmqCsceneSceneReporter.MESSAGE_TYPE_SCENE_ELEMENT, null, csceneAppConfig.getSceneMessageTimeout());
    }
}
