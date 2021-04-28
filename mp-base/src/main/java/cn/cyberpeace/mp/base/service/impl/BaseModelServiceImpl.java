package cn.cyberpeace.mp.base.service.impl;

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
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public abstract class BaseModelServiceImpl<Model extends BaseModel,
        ModelMapper extends BaseModelMapper<Model>, ModelFields extends BaseModelFields> implements BaseModelService<Model> {

    public static final Class<? extends CustomCondition> customConditionClass = null;

    private ModelMapper modelMapper;

    private ModelFields modelFields;

    private HumanCondition getHumanCondition(Map<String, Object> params, String search, String[] searchFields,
                                             String[] orders, Integer page, Integer pageSize) {
        HumanCondition humanCondition = new HumanCondition();
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

        humanCondition.setCustomCondition(getCustomCondition(params));
        return humanCondition;
    }

    public CustomCondition getCustomCondition(Map<String, Object> params) {
        Class<?> customConditionClass = ClassUtils.getStaticFieldValue(getClass(),
                "customConditionClass", Class.class);
        if (customConditionClass == null) {
            customConditionClass = CustomCondition.class;
        }

        CustomCondition customCondition;
        try {
            customCondition = (CustomCondition) customConditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Create custom condition by {} failed: {}", customConditionClass, e);
            customCondition = new CustomCondition();
        }

        return customCondition.loadFromParams(params);
    }

    @Override
    public ServiceResult list(Map<String, Object> params, String search, String[] searchFields, String[] orders,
                              Integer page, Integer pageSize, String[] fields) {
        HumanCondition humanCondition = getHumanCondition(params, search, searchFields, orders, page, pageSize);
        DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
        String[] selectFields = validModelFields != null ? TextUtils.humpToLine(validModelFields.getFields()) : null;
        List<Model> data = new ArrayList<>(modelMapper.list(humanCondition, selectFields));
        Integer total = modelMapper.total(humanCondition);
        return new ServiceResult(new ListObjResult<>(data, total, validModelFields));
    }

    @Override
    public ServiceResult get(String id, String[] fields) {
        DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
        String[] selectFields = validModelFields != null ? TextUtils.humpToLine(validModelFields.getFields()) : null;
        Model obj = modelMapper.get(id, selectFields);
        return new ServiceResult(new ObjResult<>(obj, validModelFields));
    }

    @Override
    public ServiceResult create(Model obj, String[] fields) {
        if (modelFields != null) {
            modelFields.clearNotWritableFields(obj);
        }

        Boolean result = modelMapper.create(obj);

        if (result) {
            DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service create {} failed", obj);
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult update(Model obj, String[] fields) {
        if (modelFields != null) {
            modelFields.clearNotWritableFields(obj, true);
        }

        Boolean result = modelMapper.update(obj);

        if (result) {
            DynamicFields validModelFields = modelFields != null ? modelFields.getValidFields(fields) : null;
            return new ServiceResult(new ObjResult<>(obj, validModelFields));
        } else {
            log.error("Service update {} failed", obj);
            return new ServiceResult(false);
        }
    }

    @Override
    public ServiceResult delete(String id) {
        Boolean result = modelMapper.delete(id);

        if (!result) {
            log.error("Service delete {} failed", id);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkCreate(List<Model> objs) {
        if (modelFields != null) {
            objs.forEach(obj -> {
                modelFields.clearNotWritableFields(obj);
            });
        }

        Integer result = modelMapper.bulkCreate(objs);

        if (result < objs.size()) {
            log.error("Service bulk create {} with some failed", objs);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkUpdate(List<Model> objs) {
        if (modelFields != null) {
            objs.forEach(obj -> {
                modelFields.clearNotWritableFields(obj, true);
            });
        }

        Integer result = modelMapper.bulkUpdate(objs);

        if (result < objs.size()) {
            log.error("Service bulk update {} with some failed", objs);
        }

        return new ServiceResult(result);
    }

    @Override
    public ServiceResult bulkDelete(String[] ids) {
        Integer result = modelMapper.bulkDelete(ids);

        if (result < ids.length) {
            log.error("Service bulk delete {} with some failed", Arrays.toString(ids));
        }

        return new ServiceResult(result);
    }
}
