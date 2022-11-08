package cn.cyberpeace.mp.devopslog.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopslog.enums.OpModuleEnums;
import cn.cyberpeace.mp.devopslog.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devopslog.model.OperationLogModel;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanggang
 */
@Slf4j
@Component
public class OperateLogServiceFactory {

    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    private Map<SiteTypeEnums, OperateLogService> operateLogServiceMap = new ConcurrentHashMap();

    private List<OperateLogService> operateLogServiceList;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public OperateLogServiceFactory(List<OperateLogService> operateLogServiceList) {
        this.operateLogServiceList = operateLogServiceList;
    }

    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale) {
        return null;
    }

    private void processDefaultContent(OperationLogModel model) {
        if (StringUtils.isNotBlank(model.getContent())) {
            return;
        }

        String content = "";

        if (Objects.equals(model.getOpModuleID(), OpModuleEnums.SYSTEM.ordinal())) {
            content = model.getOpType() + ":" + model.getOpTarget() + ", IP: " + model.getOpIp();
        } else {
            content = model.getOpType() + ":" + model.getOpModule() + ", IP: " + model.getOpIp();
        }

        model.setContent(content);
    }


    public String getlocalizedMessage(Locale locale, String msgKey, String... params) {
        if (Objects.isNull(locale) || locale.toLanguageTag().equals("und")) {
            locale = Locale.forLanguageTag(DEFAULT_LANGUAGE_TAG);
        }
        try {
            return messageSource.getMessage(msgKey, params, locale);
        } catch (Exception e) {
            log.error("Get message by key `{}` params `{}` on locale {} failed: {}", msgKey, params,
                    locale, e.getMessage());
            return msgKey;
        }
    }
}
