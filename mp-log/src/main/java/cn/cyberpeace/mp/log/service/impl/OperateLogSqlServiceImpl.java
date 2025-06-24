 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogSqlServiceImpl.java.
 // File size 11618.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import cn.cyberpeace.mp.log.repository.OperateLogSqlMapper;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.log.service.OperateLogDbService;
import cn.cyberpeace.mp.log.service.field.OperateLogSqlFields;
import cn.cyberpeace.mp.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

 /**
  * 操作日志sql服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Component
@ConditionalOnProperty(prefix = "mp.log", name = "log-db", havingValue = LogConstant.LOG_DB_SQL)
public class OperateLogSqlServiceImpl extends BaseModelServiceImpl<OperateLogSqlModel, OperateLogSqlMapper, OperateLogSqlFields, QueryParams>
        implements OperateLogDbService {
     /**
      * 默认语言标签
      */
     private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

     /**
      * 操作日志sql服务impl
      *
      * @param operateLogSqlMapper 操作日志SQL映射器
      */
     @Autowired
    public OperateLogSqlServiceImpl(OperateLogSqlMapper operateLogSqlMapper) {
        setModelMapper(operateLogSqlMapper);
    }

     /**
      * 消息来源
      */
     @Autowired
    private MessageSource messageSource;

     /**
      * 创建
      *
      * @param request 请求
      * @param locale  语言环境
      */
     @Override
    public void create(OperationLogRequest request, Locale locale) {
        OperateLogSqlModel model = opRequest2Model(request, locale);
        this.create(model);
    }

     /**
      * 创建
      *
      * @param operateLog 操作日志
      * @param request    请求
      * @param locale     语言环境
      */
     @Override
    public void create(OperateLog operateLog, HttpServletRequest request, Locale locale) {
        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        OperateLogSqlModel model = new OperateLogSqlModel();
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
        model.setSiteType(SiteTypeEnums.MP.toValue());
        if (Objects.nonNull(operateLog.module())) {
            //操作模块
            model.setOpModule(getlocalizedMessage(locale, operateLog.module().getCode()));
            //操作模块ID
            model.setOpModuleId(operateLog.module().ordinal());
        }
        if (Objects.nonNull(operateLog.type())) {
            //操作类型
            model.setOpType(getlocalizedMessage(locale, operateLog.type().getCode()));
            //操作类型ID
            model.setOpTypeId(operateLog.type().ordinal());
        }
        if (Objects.nonNull(operateLog.target())) {
            //操作对象
            model.setOpTarget(getlocalizedMessage(locale, operateLog.target().getCode()));
            //操作ID
            model.setOpTargetId(operateLog.target().ordinal());
        }
        //操作主题
        model.setTitle(operateLog.title());
        //操作描述
        model.setDescription(operateLog.desc());
        //记录操作时间
        model.setOpTime(LocalDateTime.now());
        //请求IP
        model.setOpIp(SnUtils.getRealRemoteIp(request));
        // 操作类型
        model.setLogType(operateLog.type().ordinal());
        // 操作来源
        model.setFrom(request.getRequestURI());
        // 操作内容
        processDefaultContent(model);
        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return;
        }
        this.create(model);
    }

     /**
      * 搜索
      *
      * @param request 请求
      * @param locale  语言环境
      * @return {@link ListObjServiceResult }<{@link OperationLogPageResponse }>
      */
     @Override
    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale) {
        HumanCondition humanCondition = new HumanCondition();


        if (StringUtils.isNotBlank(request.getUserId())) {
            humanCondition = humanCondition.filterEqual(OperateLogSqlModel::getUserId, request.getUserId());
        }

        if (Objects.nonNull(request.getModule())) {
            humanCondition = humanCondition.filterEqual(OperateLogSqlModel::getOpModuleId, request.getModule());
        } else {
            List<Integer> moduleOrdinals = Arrays.stream(OpModuleEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            moduleOrdinals.add(-1);
            humanCondition = humanCondition.filterIn(OperateLogSqlModel::getOpModuleId, moduleOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getOpType())) {
            humanCondition = humanCondition.filterEqual(OperateLogSqlModel::getOpTypeId, request.getOpType());
        } else {
            List<Integer> opTypeOrdinals = Arrays.stream(OpTypeEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            opTypeOrdinals.add(-1);
            humanCondition = humanCondition.filterIn(OperateLogSqlModel::getOpTypeId, opTypeOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getUserNameOrOpTarget())) {
            humanCondition = humanCondition.filterOrStart()
                    .orContains(OperateLogSqlModel::getUserName, request.getUserNameOrOpTarget())
                    .orContains(OperateLogSqlModel::getOpTarget, request.getUserNameOrOpTarget())
                    .filterOrEnd();
        }
        String createTime = BeanUtils.getBeanGetterFieldName(OperateLogSqlModel::getCreateTime);
        if (Objects.nonNull(request.getStartDate())) {
            humanCondition = humanCondition.filterGreaterThanOrEqual(createTime, request.getStartDate());
        }
        if (Objects.nonNull(request.getEndDate())) {
            humanCondition = humanCondition.filterLessThanOrEqual(createTime, request.getEndDate());
        }
        // 查询符合条件的条数
        int total = getModelMapper().total(humanCondition);
        humanCondition = humanCondition.orderBy("-" + createTime);

        // 设置分页条件、查询
        humanCondition.setLimit(request.getPerPage());
        humanCondition.setOffset((request.getPage() - 1) * request.getPerPage());
        List<OperateLogSqlModel> models = getModelMapper().list(humanCondition);

        List<OperationLogPageResponse> list = opModel2Response(models, locale);
        return new ListObjServiceResult(new ListObjResult<>(list, total).asResult()).asResult();
    }

     /**
      * Op模型2响应
      *
      * @param models 模型
      * @param locale 语言环境
      * @return {@link List }<{@link OperationLogPageResponse }>
      */
     private List<OperationLogPageResponse> opModel2Response(List<OperateLogSqlModel> models, Locale locale) {
        List<OperationLogPageResponse> list = new ArrayList<>(models.size());
        for (OperateLogSqlModel model : models) {
            OperationLogPageResponse response = JSON.parseObject(JSONObject.toJSON(model).toString(), OperationLogPageResponse.class);
            if (Objects.nonNull(response) && Objects.nonNull(response.getOpModuleID())) {
                OpModuleEnums opModuleEnums = OpModuleEnums.fromValue(response.getOpModuleID());
                String opModuleCode = this.getlocalizedMessage(locale, opModuleEnums.getCode());
                response.setOpModule(opModuleCode);
            }
            if (Objects.nonNull(response) && Objects.nonNull(response.getOpTypeID())) {
                OpTypeEnums opTypeEnum = OpTypeEnums.fromValue(response.getOpTypeID());
                String opTypeCode = this.getlocalizedMessage(locale, opTypeEnum.getCode());
                response.setOpType(opTypeCode);
            }
            list.add(response);
        }
        return list;
    }

     /**
      * Op请求2模型
      *
      * @param request 请求
      * @param locale  语言环境
      * @return {@link OperateLogSqlModel }
      */
     private OperateLogSqlModel opRequest2Model(OperationLogRequest request, Locale locale) {
        OperateLogSqlModel model = new OperateLogSqlModel();
        model.setFrom(request.getFrom());
        model.setLogType(request.getLogType());

        if (Objects.nonNull(request.getOpModule())) {
            OpModuleEnums opModuleEnum = OpModuleEnums.fromValue(request.getOpModule());
            if (Objects.nonNull(opModuleEnum)) {
                String localizedMsg = getlocalizedMessage(locale, opModuleEnum.getCode());
                model.setOpModule(localizedMsg);
                model.setOpModuleId(opModuleEnum.ordinal());
            }
        }
        if (Objects.nonNull(request.getOpType())) {
            OpTypeEnums opTypeEnum = OpTypeEnums.fromValue(request.getOpType());
            if (Objects.nonNull(opTypeEnum)) {
                String localizedMsg = getlocalizedMessage(locale, opTypeEnum.getCode());
                model.setOpType(localizedMsg);
                model.setOpTypeId(opTypeEnum.ordinal());
            }
        }
        model.setOpTarget(request.getOpTarget());
        //model.setOpTargetId(getTidFromTarget(model.getOpTarget(), locale));
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
            return null;
        }
        return model;
    }

     /**
      * 处理默认内容
      *
      * @param model 模型
      */
     private void processDefaultContent(OperateLogSqlModel model) {
        if (StringUtils.isNotBlank(model.getContent())) {
            return;
        }
        String content = "";
        if (Objects.equals(model.getOpModuleId(), OpModuleEnums.SYSTEM.ordinal())) {
            content = model.getOpType() + ":" + model.getOpTarget() + ", IP: " + model.getOpIp();
        } else {
            content = model.getOpType() + ":" + model.getOpModule() + ", IP: " + model.getOpIp();
        }
        model.setContent(content);
    }

     /**
      * getlocalized消息
      *
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
