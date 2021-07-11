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


@Slf4j
@Component
@ConditionalOnProperty(prefix = "mp.log", name = "log-db", havingValue = LogConstant.LOG_DB_SQL)
public class OperateLogSqlServiceImpl extends BaseModelServiceImpl<OperateLogSqlModel, OperateLogSqlMapper, OperateLogSqlFields, QueryParams>
        implements OperateLogDbService {
    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    @Autowired
    public OperateLogSqlServiceImpl(OperateLogSqlMapper operateLogSqlMapper) {
        setModelMapper(operateLogSqlMapper);
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public void create(OperationLogRequest request, Locale locale) {
        OperateLogSqlModel model = opRequest2Model(request, locale);
        this.create(model);
    }

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
        
        model.setSiteType(SiteTypeEnums.MP.toValue());
        if (Objects.nonNull(operateLog.module())) {
            
            model.setOpModule(getlocalizedMessage(locale, operateLog.module().getCode()));
            
            model.setOpModuleId(operateLog.module().ordinal());
        }
        if (Objects.nonNull(operateLog.type())) {
            
            model.setOpType(getlocalizedMessage(locale, operateLog.type().getCode()));
            
            model.setOpTypeId(operateLog.type().ordinal());
        }
        if (Objects.nonNull(operateLog.target())) {
            
            model.setOpTarget(getlocalizedMessage(locale, operateLog.target().getCode()));
            
            model.setOpTargetId(operateLog.target().ordinal());
        }
        
        model.setTitle(operateLog.title());
        
        model.setDescription(operateLog.desc());
        
        model.setOpTime(LocalDateTime.now());
        
        model.setOpIp(SnUtils.getRealRemoteIp(request));
        
        model.setLogType(operateLog.type().ordinal());
        
        model.setFrom(request.getRequestURI());
        
        processDefaultContent(model);
        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return;
        }
        this.create(model);
    }

    @Override
    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale) {
        HumanCondition humanCondition = new HumanCondition();


        if (StringUtils.isNotBlank(request.getUserId())) {
            humanCondition.filterEqual(OperateLogSqlModel::getUserId, request.getUserId());
        }

        if (Objects.nonNull(request.getModule())) {
            humanCondition.filterEqual(OperateLogSqlModel::getOpModuleId, request.getModule());
        } else {
            List<Integer> moduleOrdinals = Arrays.stream(OpModuleEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            moduleOrdinals.add(-1);
            humanCondition.filterIn(OperateLogSqlModel::getOpModuleId, moduleOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getOpType())) {
            humanCondition.filterEqual(OperateLogSqlModel::getOpTypeId, request.getOpType());
        } else {
            List<Integer> opTypeOrdinals = Arrays.stream(OpTypeEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            opTypeOrdinals.add(-1);
            humanCondition.filterIn(OperateLogSqlModel::getOpTypeId, opTypeOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getUserNameOrOpTarget())) {
            humanCondition.filterOrStart()
                    .orContains(OperateLogSqlModel::getUserName, request.getUserNameOrOpTarget())
                    .orContains(OperateLogSqlModel::getOpTarget, request.getUserNameOrOpTarget())
                    .filterOrEnd();
        }
        String createTime = BeanUtils.getBeanGetterFieldName(OperateLogSqlModel::getCreateTime);
        if (Objects.nonNull(request.getStartDate())) {
            humanCondition.filterGreaterThanOrEqual(createTime, request.getStartDate());
        }
        if (Objects.nonNull(request.getEndDate())) {
            humanCondition.filterLessThanOrEqual(createTime, request.getEndDate());
        }
        
        int total = getModelMapper().total(humanCondition);
        humanCondition.orderBy("-" + createTime);

        
        humanCondition.setLimit(request.getPerPage());
        humanCondition.setOffset((request.getPage() - 1) * request.getPerPage());
        List<OperateLogSqlModel> models = getModelMapper().list(humanCondition);

        List<OperationLogPageResponse> list = opModel2Response(models, locale);
        return new ListObjServiceResult(new ListObjResult<>(list, total).asResult()).asResult();
    }

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
