 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelServiceImpl.java.
 // File size 15526.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.CustomCondition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.*;
import cn.cyberpeace.mp.base.service.stereotype.CustomConditionClass;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseModelServiceImpl
 * @Description
 * 单表通用服务实现类，包含通用的增删改查能力
 *
 * @param <M> 数据model
 * @param <P> 数据mapper接口
 * @param <F> 业务处理字段
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
@Slf4j
@Data
public abstract class BaseModelServiceImpl<M extends BaseModel, P extends BaseModelMapper<M>, F extends BaseModelFields,
        Q extends QueryParams> implements BaseModelService<M, Q> {

    private P modelMapper;

    private F modelFields;

    /**
     * handleSearchCondition
     *
     * @param humanCondition
     * @param search
     * @param searchFields
     */
    private void handleSearchCondition(HumanCondition humanCondition, String search, String[] searchFields) {
        F actualModelFields = getModelFields();
        if (StringUtils.isEmpty(search) || actualModelFields == null) {
            return;
        }

        String[] lastSearchFields;
        DynamicFields validSearchFields = actualModelFields.getValidSearchFields(searchFields);
        if (validSearchFields != null && validSearchFields.hasValidFields()) {
            lastSearchFields = validSearchFields.getFields();
        } else {
            DynamicFields availableSearchFields = actualModelFields.getSearchFields();
            lastSearchFields = availableSearchFields != null ? availableSearchFields.getFields() : null;
        }

        if (lastSearchFields != null) {
            HumanCondition.OrConditionProxy orCondition = humanCondition.filterOrStart();
            for (String searchField : lastSearchFields) {
                orCondition.orIContains(searchField, search);
            }
            orCondition.filterOrEnd();
        }
    }

    /**
     * getHumanCondition
     *
     * @param selectFields
     * @return 查询条件构造器
     */
    public HumanCondition getHumanCondition(String[] selectFields) {
        HumanCondition humanCondition = new HumanCondition();
        if (selectFields != null) {
            humanCondition.select(selectFields);
        }
        return humanCondition;
    }


    /**
     * getHumanCondition
     *
     * @param selectFields
     * @param ids
     * @param exclude
     * @param search
     * @param searchFields
     * @param orders
     * @param page
     * @param pageSize
     * @return 查询条件构造器
     */
    public HumanCondition getHumanCondition(String[] selectFields, String[] ids, String[] exclude,
                                            String search, String[] searchFields,
                                            String[] orders, Integer page, Integer pageSize) {
        HumanCondition humanCondition = getHumanCondition(selectFields);
        if (ids != null && ids.length > 0) {
            humanCondition.filterIn(BaseModel::getId, ids);
        }
        if (exclude != null && exclude.length > 0) {
            humanCondition.filterNotIn(BaseModel::getId, exclude);
        }
        humanCondition.pagination(page, pageSize);
        handleSearchCondition(humanCondition, search, searchFields);
        F actualModelFields = getModelFields();
        if (actualModelFields != null) {
            String[] lastOrders = actualModelFields.getValidOrders(orders);
            if (lastOrders != null) {
                humanCondition.orderBy(lastOrders);
            }
        } else if (orders != null && orders.length > 0) {
            humanCondition.orderBy(orders);
        }
        return humanCondition;
    }

    /**
     * getCustomCondition
     *
     * @param params
     * @param customConditionClass
     * @return 自定义业务条件
     */
    public CustomCondition getCustomCondition(Map<String, Object> params,
                                              Class<? extends CustomConditionClass> customConditionClass) {
        Class<?> actualCustomConditionClass;
        CustomConditionClass customConditionClassAnnotation = getClass().getAnnotation(customConditionClass);
        if (customConditionClassAnnotation != null) {
            actualCustomConditionClass = customConditionClassAnnotation.value();
        } else {
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

    /**
     * loadListFilterCondition
     *
     * @param humanCondition
     * @param queryParams
     */
    public void loadListFilterCondition(HumanCondition humanCondition, Q queryParams) {
    }

    /**
     * list
     *
     * @param queryParams
     * @param withTotal
     * @return
     */
    @Override
    public ListObjServiceResult<M> list(Q queryParams, boolean withTotal) {
        F actualModelFields = getModelFields();
        DynamicFields validModelFields = actualModelFields != null ?
                actualModelFields.getValidFields(queryParams.getFields()) : null;
        String[] selectFields = validModelFields != null ? validModelFields.getFields() : null;
        HumanCondition humanCondition = getHumanCondition(selectFields, queryParams.getIds(), queryParams.getExclude(),
                queryParams.getSearch(), queryParams.getSearchFields(), queryParams.getOrders(), queryParams.getPage(),
                queryParams.getPageSize());
        loadListFilterCondition(humanCondition, queryParams);
        humanCondition.setCustomCondition(getCustomCondition(JsonCommon.beanToMap(queryParams),
                CustomConditionClass.class));

        List<M> data;
        int total = 0;
        try {
            data = getModelMapper().list(humanCondition);
            if (withTotal) {
                total = getModelMapper().total(humanCondition);
            }
        } catch (Exception e) {
            log.error("Service list error: {}", e.getMessage());
            return new ListObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }

        DynamicFields resultFields = (validModelFields == null && actualModelFields != null) ?
                actualModelFields.getAvailableFields() : validModelFields;
        return new ListObjServiceResult<>(new ListObjResult<>(data, total, resultFields));
    }

    /**
     * get
     *
     * @param id
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<M> get(String id, String[] fields) {
        F actualModelFields = getModelFields();
        DynamicFields validModelFields = actualModelFields != null ? actualModelFields.getValidFields(fields) : null;
        String[] selectFields = validModelFields != null ? validModelFields.getFields() : null;
        HumanCondition humanCondition = getHumanCondition(selectFields);
        M obj;
        try {
            obj = getModelMapper().get(id, humanCondition);
        } catch (Exception e) {
            log.error("Service get {} error: {}", id, e.getMessage());
            return new ObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }
        if (obj == null) {
            return new ObjServiceResult<>(null, BaseErrorCode.DATA_NOT_EXIST);
        }

        DynamicFields resultFields = (validModelFields == null && actualModelFields != null) ?
                actualModelFields.getAvailableFields() : validModelFields;
        return new ObjServiceResult<>(new ObjResult<>(obj, resultFields));
    }

    /**
     * create
     *
     * @param obj
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<M> create(M obj, String[] fields) {
        F actualModelFields = getModelFields();
        if (actualModelFields != null) {
            actualModelFields.clearNotWritableFields(obj);
        }

        boolean result;
        try {
            result = getModelMapper().create(obj);
        } catch (DuplicateKeyException e) {
            log.error("Service create {} failed: {}", obj, e.getMessage());
            return new ObjServiceResult<>(null, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service create {} error: {}", obj, e.getMessage());
            return new ObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            DynamicFields validModelFields = actualModelFields != null ? actualModelFields.getValidFields(fields) : null;
            DynamicFields resultFields = (validModelFields == null && actualModelFields != null) ?
                    actualModelFields.getAvailableFields() : validModelFields;
            return new ObjServiceResult<>(new ObjResult<>(obj, resultFields));
        } else {
            log.error("Service create {} failed", obj);
            return new ObjServiceResult<>(null, BaseErrorCode.ERROR);
        }
    }

    /**
     * update
     *
     * @param obj
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<M> update(M obj, String[] fields) {
        F actualModelFields = getModelFields();
        if (actualModelFields != null) {
            actualModelFields.clearNotWritableFields(obj, true);
        }

        boolean result;
        try {
            result = getModelMapper().update(obj);
        } catch (DuplicateKeyException e) {
            log.error("Service update {} failed: {}", obj, e.getMessage());
            return new ObjServiceResult<>(null, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service update {} error: {}", obj, e.getMessage());
            return new ObjServiceResult<>(null, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            DynamicFields validModelFields = actualModelFields != null ? actualModelFields.getValidFields(fields) : null;
            DynamicFields resultFields = (validModelFields == null && actualModelFields != null) ?
                    actualModelFields.getAvailableFields() : validModelFields;
            return new ObjServiceResult<>(new ObjResult<>(obj, resultFields));
        } else {
            log.error("Service update {} failed, maybe not exist", obj);
            return new ObjServiceResult<>(null, BaseErrorCode.DATA_NOT_EXIST);
        }
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @Override
    public ServiceResult<Boolean> delete(String id) {
        boolean result;
        try {
            result = getModelMapper().logicDelete(id);
        } catch (Exception e) {
            log.error("Service delete {} error: {}", id, e.getMessage());
            return new ServiceResult<>(false, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result) {
            return new ServiceResult<>(true);
        } else {
            log.error("Service delete {} failed, maybe not exist", id);
            return new ServiceResult<>(false, BaseErrorCode.DATA_NOT_EXIST);
        }
    }

    /**
     * bulkCreate
     *
     * @param objs
     * @return
     */
    @Override
    public ServiceResult<Integer> bulkCreate(List<M> objs) {
        F actualModelFields = getModelFields();
        if (actualModelFields != null) {
            for (M obj : objs) {
                actualModelFields.clearNotWritableFields(obj);
            }
        }

        int result;
        try {
            result = getModelMapper().bulkCreate(objs).getSuccessCount();
        } catch (DuplicateKeyException e) {
            log.error("Service bulk create {} failed: {}", objs, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service bulk create {} error: {}", objs, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < objs.size()) {
            log.error("Service bulk create {} with some failed, maybe some not exist", objs);
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    /**
     * bulkUpdate
     *
     * @param objs
     * @return
     */
    @Override
    public ServiceResult<Integer> bulkUpdate(List<M> objs) {
        F actualModelFields = getModelFields();
        if (actualModelFields != null) {
            for (M obj : objs) {
                actualModelFields.clearNotWritableFields(obj, true);
            }
        }

        int result;
        try {
            result = getModelMapper().bulkUpdate(objs).getSuccessCount();
        } catch (DuplicateKeyException e) {
            log.error("Service bulk update {} failed: {}", objs, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.DATA_EXISTS);
        } catch (Exception e) {
            log.error("Service bulk update {} error: {}", objs, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < objs.size()) {
            log.error("Service bulk update {} with some failed, maybe some not exist", objs);
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @return
     */
    @Override
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        int result;
        try {
            result = getModelMapper().bulkLogicDelete(ids).getSuccessCount();
        } catch (Exception e) {
            log.error("Service bulk delete {} error: {}", ids, e.getMessage());
            return new ServiceResult<>(0, BaseErrorCode.ERROR, e.getMessage());
        }

        if (result < ids.length) {
            log.error("Service bulk delete {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

}
