package cn.cyberpeace.mp.auth.base.repository;

import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.AndCondition;
import cn.cyberpeace.mp.base.repository.condition.ConnectorCond;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.setting.Setting;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface BasePermissionMapper<P extends BasePermissionModel, T extends BaseModel> extends BaseModelMapper {



    @Recommendation
    default List<T> listTargetsByOwner(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = getInnerCondition(owner);
        String currentLinkName = innerCondition.getCurrentLinkName();

        // distinct
        innerCondition.setDistinct(outerCondition.getDistinct());
        // select fields
        innerCondition.getSelectFields().putAll(convertSelectFields(currentLinkName, outerCondition));
        // paging
        innerCondition.setLimit(outerCondition.getLimit());
        innerCondition.setOffset(outerCondition.getOffset());
        // group
        innerCondition.getGroupFields().addAll(convertGroupFields(currentLinkName, outerCondition));
        // orders
        innerCondition.getOrders().addAll(convertOrders(currentLinkName, outerCondition));
        // andi
        innerCondition.getAndCondition().addCondition(convertAndCondition(currentLinkName, outerCondition));

        return list(innerCondition);
    }

    /*
    @Recommendation
    default List<P> listPermissionsByOwner(String owner) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return list(humanCondition);
    }


    @Recommendation
    default List<P> listPermissionsByTarget(String target) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return list(humanCondition);
    }
    */

    @Recommendation
    default int deleteByOwner(String owner) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return filterDelete(humanCondition);
    }


    @Recommendation
    default int deleteByTarget(String target) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return filterDelete(humanCondition);
    }



    private Class<P> getPermissionClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        var modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
        if (modelClassAnnotation != null) {
            actualModelClassSource = modelClassAnnotation.permissionClass();
        } else {
            throw new MpRuntimeException("PermissionClass not found in annotation!");
        }

        Class<P> actualModelClass;
        try {
            actualModelClass = (Class<P>) actualModelClassSource;
        } catch (Exception e) {
            log.error("Invalid model class[{}] for {}", actualModelClassSource, getClass());
            return null;
        }

        return actualModelClass;
    }


    private Class<T> getTargetClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        var modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
        if (modelClassAnnotation != null) {
            actualModelClassSource = modelClassAnnotation.targetClass();
        } else {
            throw new MpRuntimeException("TargetClass not found in annotation!");
        }

        Class<T> actualModelClass;
        try {
            actualModelClass = (Class<T>) actualModelClassSource;
        } catch (Exception e) {
            log.error("Invalid model class[{}] for {}", actualModelClassSource, getClass());
            return null;
        }

        return actualModelClass;
    }


    private Class<?> getMapperInterface(Proxy proxy) {
        return ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(proxy),
                "mapperInterface", Class.class);
    }

    private Class<?> getMapperInterface() {
        return getMapperInterface((Proxy) this);
    }



    private HumanCondition getInnerCondition(String owner) {
        HumanCondition condition = new HumanCondition();

        Class<P> permClz = getPermissionClass();
        Class<T> targetClz = getTargetClass();
        condition.link(targetClz);
        condition.link(permClz);
        condition.filterLink(permClz, "target", targetClz, "id");
        condition.filterEqual("owner", owner);
        condition.currentLink(targetClz);
        return condition;
    }


    private String convertFieldName(String currentLinkName, String fieldName) {
        return currentLinkName + "." + fieldName;
    }

    private Map<String, String> convertSelectFields(String currentLinkName, HumanCondition condition) {
        Map<String, String> selectFields = condition.getSelectFields();
        if (CollectionUtil.isEmpty(selectFields)) {
            return new HashMap<>();
        }
        Map<String, String> newSelectFields = new HashMap<>();
        selectFields.forEach((k, v) -> {
            newSelectFields.put(convertFieldName(currentLinkName, k), v);
        });

        return newSelectFields;
    }

    private List<String> convertGroupFields(String currentLinkName, HumanCondition condition) {
        List<String> groupFields = condition.getGroupFields();
        if (CollectionUtil.isEmpty(groupFields)) {
            return new ArrayList<>();
        }
        List<String> newGroupFields = groupFields.stream().map(g -> convertFieldName(currentLinkName, g)).collect(Collectors.toList());

        return newGroupFields;
    }

    private List<String> convertOrders(String currentLinkName, HumanCondition condition) {
        List<String> orders = condition.getOrders();
        if (CollectionUtil.isEmpty(orders)) {
            return new ArrayList<>();
        }
        List<String> newOrders = orders.stream().map(g -> convertFieldName(currentLinkName, g)).collect(Collectors.toList());

        return newOrders;
    }

    private AndCondition convertAndCondition(String currentLinkName, HumanCondition condition) {
        AndCondition andCondition = condition.getAndCondition();
        if (Objects.isNull(andCondition)) {
            return andCondition;
        }

        List<Object> condList = (List<Object>) ClassUtils.getFieldValue(AndCondition.class, "conds", andCondition);
        log.info("before convert conds:{}", condList);

        assert condList != null;
        condList.forEach(c -> {
            if (c instanceof ConnectorCond) {
                Field fNameField = ClassUtils.getField(ConnectorCond.class, "fieldName", false, false);
                if (Objects.nonNull(fNameField)) {
                    String oldFieldName =  (String) ClassUtils.getFieldValue(fNameField, c);
                    ClassUtils.setFieldValue(fNameField, c, convertFieldName(currentLinkName, oldFieldName));
                }
            }
        });

        log.info("after convert conds:{}", condList);
        return andCondition;

    }



}
