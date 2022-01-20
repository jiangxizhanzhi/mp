package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Slf4j
@Component
public class OperateLogServiceFactory {

    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    private Map<SiteTypeEnums,OperateLogService> operateLogServiceMap = new ConcurrentHashMap();

    private List<OperateLogService> operateLogServiceList;

    @Autowired
    private MessageSource messageSource;

    @PostConstruct
    public void init(){
        operateLogServiceMap = operateLogServiceList.stream().collect(Collectors.toMap(OperateLogService::siteType, Function.identity()));
    }

    @Autowired
    public OperateLogServiceFactory(List<OperateLogService> operateLogServiceList) {
        this.operateLogServiceList = operateLogServiceList;
    }

    /**
     * 创建日志
     * @param request
     * @return
     */
    @Async
    public IndexResponse create(OperationLogRequest request, Locale locale){
        OperationLogModel model = new OperationLogModel();
        model.setFrom(request.getFrom());
        model.setLogType(request.getLogType());

        if (Objects.nonNull(request.getOpModule())) {
            OpModuleEnums opModuleEnum = OpModuleEnums.fromValue(request.getOpModule());
            if (Objects.nonNull(opModuleEnum)) {
                String localizedMsg = getlocalizedMessage(locale, opModuleEnum.getCode());
                model.setOpModule(localizedMsg);
                model.setOpModuleID(opModuleEnum.ordinal());
            }
        }


        if (Objects.nonNull(request.getOpType())) {
            OpTypeEnums opTypeEnum = OpTypeEnums.fromValue(request.getOpType());
            if (Objects.nonNull(opTypeEnum)) {
                String localizedMsg = getlocalizedMessage(locale, opTypeEnum.getCode());
                model.setOpType(localizedMsg);
                model.setOpTypeID(opTypeEnum.ordinal());
            }
        }

        model.setOpTarget(request.getOpTarget());

        model.setTitle(request.getTitle());
        model.setUserId(request.getUserId());
        model.setUserName(request.getUserName());
        model.setUserType(request.getUserType());
        model.setDescription(request.getDescription());
        model.setOpIp(request.getOpIp());
        model.setContent(request.getContent());

        processDefaultContent(model);
        return this.create(model);
    }

    /**
     * 创建日志
     * @param operateLog
     * @return
     */
    @Async
    public IndexResponse create(OperateLog operateLog, HttpServletRequest request, Locale locale) {
        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        OperationLogModel model = new OperationLogModel();
        if (Objects.nonNull(userId)) {
            model.setUserId(userId.toString());
        }

        if (Objects.nonNull(userName)) {
            model.setUserName(userName.toString());
        }

        if (Objects.nonNull(userType)) {
            model.setUserType(userType.toString());
        }

        //站点类型
        model.setSiteType(SiteTypeEnums.MP);

        if (Objects.nonNull(operateLog.module())) {
            //操作模块
            model.setOpModule(getlocalizedMessage(locale, operateLog.module().getCode()));
            //操作模块ID
            model.setOpModuleID(operateLog.module().ordinal());
        }

        if (Objects.nonNull(operateLog.type())) {
            //操作类型
            model.setOpType(getlocalizedMessage(locale, operateLog.type().getCode()));
            //操作类型ID
            model.setOpTypeID(operateLog.type().ordinal());
        }

        if (Objects.nonNull(operateLog.target())) {
            //操作对象
            model.setOpTarget(getlocalizedMessage(locale, operateLog.target().getCode()));
            //操作ID
            model.setOpTargetID(operateLog.target().ordinal());
        }

        //操作主题
        model.setTitle(operateLog.title());
        //操作描述
        model.setDescription(operateLog.desc());
        //记录操作时间
        model.setOpTime(LocalDateTime.now());
        //请求IP
        model.setOpIp(SnUtils.getRealRemoteIp(request));

        processDefaultContent(model);
        return this.create(model);
    }


    /**
     * 创建日志
     * @param operationLogModel
     */
    private IndexResponse create(OperationLogModel operationLogModel){
       return operateLogServiceMap.get(operationLogModel.getSiteType()).create(operationLogModel);
    }

    /**
     * 查询日志
     * @param request
     * @return
     */
    public SearchResponse search(OperationLogPageRequest request){
       return operateLogServiceMap.get(request.getSiteType()).search(request);
    }

    private void processDefaultContent(OperationLogModel model) {
        if (StringUtils.isNotBlank(model.getContent())) {
            return;
        }

        String content = model.getOpType() + ":" + model.getOpModule() + ", IP: " + model.getOpIp();
        model.setContent(content);
    }


    private String getlocalizedMessage(Locale locale, String msgKey, String... params) {
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
