package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperationLogModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.MpOperateLogService;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
@ConditionalOnProperty(prefix = "mp.log", name = "log-db", havingValue = LogConstant.LOG_DB_ES)
public class OperateLogEsServiceImpl implements OperateLogDbService {

    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    private Map<SiteTypeEnums, MpOperateLogService> operateLogServiceMap = new ConcurrentHashMap();

    private List<MpOperateLogService> mpOperateLogServiceList;

    @Autowired
    private MessageSource messageSource;

    @PostConstruct
    public void init(){
        operateLogServiceMap = mpOperateLogServiceList.stream().collect(Collectors.toMap(MpOperateLogService::siteType, Function.identity()));
    }

    @Autowired
    public OperateLogEsServiceImpl(List<MpOperateLogService> mpOperateLogServiceList) {
        this.mpOperateLogServiceList = mpOperateLogServiceList;
    }

    
    @Async
    public void create(OperationLogRequest request, Locale locale){
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
        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return;
        }
        this.create(model);
    }

    
    @Async
    public void create(OperateLog operateLog, HttpServletRequest request, Locale locale) {
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
        
        model.setSiteType(SiteTypeEnums.MP);
        if (Objects.nonNull(operateLog.module())) {
            
            model.setOpModule(getlocalizedMessage(locale, operateLog.module().getCode()));
            
            model.setOpModuleID(operateLog.module().ordinal());
        }
        if (Objects.nonNull(operateLog.type())) {
            
            model.setOpType(getlocalizedMessage(locale, operateLog.type().getCode()));
            
            model.setOpTypeID(operateLog.type().ordinal());
        }
        if (Objects.nonNull(operateLog.target())) {
            
            model.setOpTarget(getlocalizedMessage(locale, operateLog.target().getCode()));
            
            model.setOpTargetID(operateLog.target().ordinal());
        }
        
        model.setTitle(operateLog.title());
        
        model.setDescription(operateLog.desc());
        
        model.setOpTime(LocalDateTime.now());
        
        model.setOpIp(SnUtils.getRealRemoteIp(request));
        
        processDefaultContent(model);
        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return;
        }
        this.create(model);
    }


    
    private IndexResponse create(OperationLogModel operationLogModel){
       return operateLogServiceMap.get(operationLogModel.getSiteType()).create(operationLogModel);
    }

    
    public SearchResponse search(OperationLogPageRequest request){
       return operateLogServiceMap.get(request.getSiteType()).search(request);
    }

    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale){
        SearchResponse searchResponse = this.search(request);
        if (Objects.isNull(searchResponse)) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }
        SearchHits searchHits = searchResponse.getHits();
        List<OperationLogPageResponse> list = new ArrayList<>(searchHits.getHits().length);
        for (SearchHit hit : searchHits.getHits()) {
            OperationLogPageResponse operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogPageResponse.class);
            if (Objects.nonNull(operationLog) && Objects.nonNull(operationLog.getOpModuleID())) {
                OpModuleEnums opModuleEnums = OpModuleEnums.fromValue(operationLog.getOpModuleID());
                String opModuleCode = this.getlocalizedMessage(locale, opModuleEnums.getCode());
                operationLog.setOpModule(opModuleCode);
            }
            if (Objects.nonNull(operationLog) && Objects.nonNull(operationLog.getOpTypeID())) {
                OpTypeEnums opTypeEnum = OpTypeEnums.fromValue(operationLog.getOpTypeID());
                String opTypeCode = this.getlocalizedMessage(locale, opTypeEnum.getCode());
                operationLog.setOpType(opTypeCode);
            }
            list.add(operationLog);
        }
        return new ListObjServiceResult(new ListObjResult<>(list, Integer.valueOf(String.valueOf(searchHits.getTotalHits().value)))).asResult();
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
                    locale, ExceptionUtils.getStackTrace(e));
            return msgKey;
        }
    }
}
