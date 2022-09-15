package cn.cyberpeace.mp.devopslog.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devopslog.annotation.OperateLog;
import cn.cyberpeace.mp.devopslog.constant.LogConstant;
import cn.cyberpeace.mp.devopslog.enums.OpModuleEnums;
import cn.cyberpeace.mp.devopslog.enums.OpTypeEnums;
import cn.cyberpeace.mp.devopslog.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devopslog.model.OperationLogDbModel;
import cn.cyberpeace.mp.devopslog.repository.OperationLogDbMapper;
import cn.cyberpeace.mp.devopslog.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devopslog.request.OperationLogRequest;
import cn.cyberpeace.mp.devopslog.response.OperationLogPageResponse;
import cn.cyberpeace.mp.devopslog.service.OperationLogDbService;
import cn.cyberpeace.mp.devopslog.service.field.OperationLogDbFields;
import cn.cyberpeace.mp.devopslog.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/13
 */
@Slf4j
@Component
public class OperationLogDbServiceImpl extends BaseModelServiceImpl<OperationLogDbModel, OperationLogDbMapper, OperationLogDbFields, QueryParams>
        implements OperationLogDbService {
    private static final String DEFAULT_LANGUAGE_TAG = "zh-CN";

    @Autowired
    public OperationLogDbServiceImpl(OperationLogDbMapper operationLogDbMapper) {
        setModelMapper(operationLogDbMapper);
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public ObjServiceResult<OperationLogDbModel> create(OperationLogRequest request, Locale locale) {
        OperationLogDbModel model = opRequest2Model(request, locale);
        return this.create(model);
    }

    @Override
    public ObjServiceResult<OperationLogDbModel> create(OperateLog operateLog, HttpServletRequest request, Locale locale) {
        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        OperationLogDbModel model = new OperationLogDbModel();
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

        processDefaultContent(model);

        if (StringUtils.isBlank(model.getUserName())) {
            log.error("operationlog, username cannot be null. {}", model);
            return null;
        }
        return this.create(model);
    }

    @Override
    public ListObjServiceResult<OperationLogPageResponse> search(OperationLogPageRequest request, Locale locale) {
        HumanCondition humanCondition = new HumanCondition();
        if (Objects.nonNull(request.getModule())) {
            humanCondition.filterEqual(OperationLogDbModel::getOpModuleId, request.getModule());
        } else {
            List<Integer> moduleOrdinals = Arrays.stream(OpModuleEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            humanCondition.filterIn(OperationLogDbModel::getOpModuleId, moduleOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getOpType())) {
            humanCondition.filterEqual(OperationLogDbModel::getOpTypeId, request.getOpType());
        } else {
            List<Integer> opTypeOrdinals = Arrays.stream(OpTypeEnums.values()).map(m -> m.ordinal()).collect(Collectors.toList());
            humanCondition.filterIn(OperationLogDbModel::getOpTypeId, opTypeOrdinals.toArray(new Integer[0]));
        }

        if (Objects.nonNull(request.getUserNameOrOpTarget())) {
            humanCondition.filterOrStart()
                    .orEqual(OperationLogDbModel::getUserName, request.getUserNameOrOpTarget())
                    .orEqual(OperationLogDbModel::getOpTarget, request.getUserNameOrOpTarget())
                    .filterOrEnd();
        }
        String createTime = BeanUtils.getBeanGetterFieldName(OperationLogDbModel::getCreateTime);
        humanCondition.orderBy("-" + createTime);
        List<OperationLogDbModel> models = getModelMapper().list(humanCondition);
        List<OperationLogPageResponse> list = opModel2Response(models, locale);
        return new ListObjServiceResult(new ListObjResult<>(list, list.size()).asResult()).asResult();
    }

    private List<OperationLogPageResponse> opModel2Response(List<OperationLogDbModel> models, Locale locale) {
        List<OperationLogPageResponse> list = new ArrayList<>(models.size());
        for (OperationLogDbModel model : models) {
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

    private OperationLogDbModel opRequest2Model(OperationLogRequest request, Locale locale) {
        OperationLogDbModel model = new OperationLogDbModel();
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

    private void processDefaultContent(OperationLogDbModel model) {
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
                    locale, e.getMessage());
            return msgKey;
        }
    }

    /*public Integer getTidFromTarget(String target, Locale locale) {
        OpTargetEnums[] opTargetEnums = OpTargetEnums.values();
        for (OpTargetEnums opTarget : opTargetEnums) {
            if (Objects.equals(target, getlocalizedMessage(locale, opTarget.getCode()))) {
                return opTarget.ordinal();
            }
        }
        return OpTargetEnums.DEFAULT.ordinal();
    }*/
}
