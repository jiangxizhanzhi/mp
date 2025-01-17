 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogEsServiceImpl.java.
 // File size 10041.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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

 /**
  * 操作日志西文服务impl
  *
  * @author wanggang@date 2025/01/17
  */
 @Slf4j
@Component
@ConditionalOnProperty(prefix = "mp.log", name = "log-db", havingValue = LogConstant.LOG_DB_ES)
public class OperateLogEsServiceImpl implements OperateLogDbService {

     /**
      * 默认语言标签
      */
     private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

     /**
      * 操作日志服务地图
      */
     private Map<SiteTypeEnums, MpOperateLogService> operateLogServiceMap = new ConcurrentHashMap();

     /**
      * 国会议员操作日志服务列表
      */
     private List<MpOperateLogService> mpOperateLogServiceList;

     /**
      * 消息来源
      */
     @Autowired
    private MessageSource messageSource;

     /**
      * 初始化
      */
     @PostConstruct
    public void init(){
        operateLogServiceMap = mpOperateLogServiceList.stream().collect(Collectors.toMap(MpOperateLogService::siteType, Function.identity()));
    }

     /**
      * 操作日志西文服务impl
      *
      * @param mpOperateLogServiceList 国会议员操作日志服务列表
      */
     @Autowired
    public OperateLogEsServiceImpl(List<MpOperateLogService> mpOperateLogServiceList) {
        this.mpOperateLogServiceList = mpOperateLogServiceList;
    }

     /**
      * 创建日志
      *
      * @param request
      * @param locale  语言环境
      */
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

     /**
      * 创建日志
      *
      * @param operateLog
      * @param request    请求
      * @param locale     语言环境
      */
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
        //操作内容
        processDefaultContent(model);
        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return;
        }
        this.create(model);
    }

     /**
      * 创建日志
      *
      * @param operationLogModel 操作日志模型
      * @return {@link IndexResponse }
      */
     private IndexResponse create(OperationLogModel operationLogModel){
       return operateLogServiceMap.get(operationLogModel.getSiteType()).create(operationLogModel);
    }

     /**
      * 查询日志
      *
      * @param request
      * @return
      */
     public SearchResponse search(OperationLogPageRequest request){
       return operateLogServiceMap.get(request.getSiteType()).search(request);
    }

     /**
      * 搜索
      *
      * @param request 请求
      * @param locale  语言环境
      * @return {@link ListObjServiceResult }<{@link OperationLogPageResponse }>
      */
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

     /**
      * 处理默认内容
      *
      * @param model 模型
      */
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

     /**
      * @param locale 语言环境
      * @param msgKey
      * @param params
      * @return {@link String }
      */
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
