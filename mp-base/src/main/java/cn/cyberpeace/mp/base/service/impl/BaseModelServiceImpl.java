package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.CustomCondition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.service.stereotype.CustomConditionClass;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public abstract class BaseModelServiceImpl<M extends BaseModel,
        P extends BaseModelMapper<M>, F extends BaseModelFields> implements BaseModelService<M> {

    public static final Class<? extends CustomCondition> customConditionClass = null;

    private P modelMapper;

    private F modelFields;

    private HumanCondition getHumanCondition(Map<String, Object> params, String search, String[] searchFields,
                                             String[] orders, int page, int pageSize) {
        var humanCondition = new HumanCondition();
        humanCondition.setPage(page);
        humanCondition.setPageSize(pageSize);

        humanCondition.setSearch(search);
        String[] lastSearchFields = null;
        if (modelFields != null) {
            DynamicFields validSearchFields = modelFields.getValidSearchFields(searchFields);
            if (validSearchFields != null && validSearchFields.hasValidFields()) {
                lastSearchFields = validSearchFields.getFields();
            } else {
                DynamicFields availableSearchFields = modelFields.getSearchFields();
                lastSearchFields = availableSearchFields != null ? availableSearchFields.getFields() : null;
            }
        }
        humanCondition.setSearchFields(lastSearchFields);

        String[] lastOrders = null;
        if (modelFields != null) {
            lastOrders = modelFields.getValidOrders(orders);
        }
        humanCondition.setOrders(lastOrders);

        loadFilterCondition(humanCondition, params);
        humanCondition.setCustomCondition(getCustomCondition(params));
        return humanCondition;
    }

    public void loadFilterCondition(HumanCondition humanCondition, Map<String, Object> params) {}

    public CustomCondition getCustomCondition(Map<String, Object> params) {
        Class<?> actualCustomConditionClass;
        var customConditionClassAnnotation = getClass().getAnnotation(CustomConditionClass.class);
        if (customConditionClassAnnotation != null) {
            actualCustomConditionClass = customConditionClassAnnotation.value();
        } else {
            actualCustomConditionClass = ClassUtils.getStaticFieldValue(getClass(),
                    "customConditionClass", Class.class);
        }

        if (actualCustomConditionClass == null) {
            actualCustomConditionClass = CustomCondition.class;
        }

        CustomCondition customCondition;
        try {
            customCondition = (CustomCondition) actualCustomConditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Create custom condition by {} failed: {}", actualCustomConditionClass, e);
            customCondition = new CustomCondition();
        }

        return customCondition.loadFromParams(params);
    }

    @Override
    public ServiceResult list(Map<String, Object> params, String search, String[] searchFields, String[] orders,
                              int page, int pageSize, String[] fields) {
        var humanCondition = getHumanCondition(params, search, searchFields, orders, page, pageSize);
        DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
        String[] selectFields = validModelFields != null ? TextUtils.humpToLine(validModelFields.getFields()) : null;
        List<M> data;
        int total;
        try {
            data = modelMapper.list(humanCondition, selectFields);
            total = modelMapper.total(humanCondition);
        } catch (Exception e) {
            log.error("Service list error: {}", e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        return new ServiceResult(new ListObjResult<>(data, total, validModelFields));
    }

    @Override
    public ServiceResult get(String id, String[] fields) {
        DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
        String[] selectFields = validModelFields != null ? TextUtils.humpToLine(validModelFields.getFields()) : null;
        M obj;
        try {
            obj = modelMapper.get(id, selectFields);
        } catch (Exception e) {
            log.error("Service get {} error: {}", id, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }
        if (obj == null) {
            return new ServiceResult(null, BaseErrorCode.DATA_NOT_EXIST);
        }

        return new ServiceResult(new ObjResult<>(obj, validModelFields));
    }

    @Override
    public ServiceResult create(M obj, String[] fields) {
        if (modelFields != null) {
            modelFields.clearNotWritableFields(obj);
        }

        boolean result;
        try {
            result = modelMapper.create(obj);
        } catch (DuplicateKeyException e) {
            log.error("Service create {} failed: {}", obj, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service create {} error: {}", obj, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service create {} failed, so strange", obj);
            return new ServiceResult(false, BaseErrorCode.ERROR);
        }
    }

    @Override
    public ServiceResult update(M obj, String[] fields) {
        if (modelFields != null) {
            modelFields.clearNotWritableFields(obj, true);
        }

        boolean result;
        try {
            result = modelMapper.update(obj);
        } catch (DuplicateKeyException e) {
            log.error("Service update {} failed: {}", obj, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service update {} error: {}", obj, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service update {} failed, maybe not exist", obj);
            return new ServiceResult(false, BaseErrorCode.DATA_NOT_EXIST);
        }
    }

    @Override
    public ServiceResult delete(String id) {
        boolean result;
        try {
            result = modelMapper.delete(id);
        } catch (Exception e) {
            log.error("Service delete {} error: {}", id, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            return new ServiceResult(true);
        } else {
            log.error("Service delete {} failed, maybe not exist", id);
            return new ServiceResult(false, BaseErrorCode.DATA_NOT_EXIST);
        }
    }

    @Override
    public ServiceResult bulkCreate(List<M> objs) {
        if (modelFields != null) {
            objs.forEach(obj -> modelFields.clearNotWritableFields(obj));
        }

        int result;
        try {
            result = modelMapper.bulkCreate(objs);
        } catch (DuplicateKeyException e) {
            log.error("Service bulk create {} failed: {}", objs, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service bulk create {} error: {}", objs, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < objs.size()) {
            log.error("Service bulk create {} with some failed, maybe some not exist", objs);
            return new ServiceResult(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult(result);
        }
    }

    @Override
    public ServiceResult bulkUpdate(List<M> objs) {
        if (modelFields != null) {
            objs.forEach(obj -> modelFields.clearNotWritableFields(obj, true));
        }

        int result;
        try {
            result = modelMapper.bulkUpdate(objs);
        } catch (DuplicateKeyException e) {
            log.error("Service bulk update {} failed: {}", objs, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service bulk update {} error: {}", objs, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < objs.size()) {
            log.error("Service bulk update {} with some failed, maybe some not exist", objs);
            return new ServiceResult(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult(result);
        }
    }

    @Override
    public ServiceResult bulkDelete(String[] ids) {
        int result;
        try {
            result = modelMapper.bulkDelete(ids);
        } catch (Exception e) {
            log.error("Service bulk delete {} error: {}", ids, e.getMessage());
            return new ServiceResult(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < ids.length) {
            log.error("Service bulk delete {} with some failed", Arrays.toString(ids));
            return new ServiceResult(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult(result);
        }
    }
}
