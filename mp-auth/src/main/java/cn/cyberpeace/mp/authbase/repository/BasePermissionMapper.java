 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePermissionMapper.java.
 // File size 13129.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.authbase.repository;

import cn.cyberpeace.mp.authbase.model.BasePermissionModel;
import cn.cyberpeace.mp.authbase.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.AndCondition;
import cn.cyberpeace.mp.base.repository.condition.ConnectorCond;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName BasePermissionMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
public interface BasePermissionMapper<P extends BasePermissionModel, T extends BaseModel> extends BaseModelMapper {


    /**
     * listTargetsByOwner
     *
     * @param owner
     * @param outerCondition
     * @return
     */
    @Recommendation
    default List<T> listTargetsByOwner(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = processOuterCondition(owner, outerCondition);

        List<Map<String, Object>> mapList = originList(innerCondition);

        List<T> objs = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            T obj = mapToTargetObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        }
        return objs;
    }

    /**
     * totalTargetsByOwner
     *
     * @param owner
     * @param outerCondition
     * @return int
     */
    @Recommendation
    default int totalTargetsByOwner(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = processOuterCondition(owner, outerCondition);

        return total(innerCondition);
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

    /**
     * deleteByOwner
     *
     * @param owner
     * @return int
     */
    @Recommendation
    default int deleteByOwner(String owner) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return filterDelete(humanCondition);
    }


    /**
     * deleteByTarget
     *
     * @param target
     * @return int
     */
    @Recommendation
    default int deleteByTarget(String target) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return filterDelete(humanCondition);
    }

    /**
     * processOuterCondition
     *
     * @param owner
     * @param outerCondition
     * @return 查询条件构造器
     */
    private HumanCondition processOuterCondition(String owner, HumanCondition outerCondition) {
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
        return innerCondition;
    }


    /**
     * getPermissionClass
     *
     * @return
     */
    private Class<P> getPermissionClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        PermissionModelClass modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
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


    /**
     * getTargetClass
     *
     * @return
     */
    private Class<T> getTargetClass() {
        Class<?> actualModelClassSource;
        Class<?> mapperInterface = getMapperInterface();
        PermissionModelClass modelClassAnnotation = mapperInterface.getAnnotation(PermissionModelClass.class);
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


    /**
     * mapToTargetObj
     *
     * @param map
     * @return
     */
    private T mapToTargetObj(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Class<T> actualModelClass = getTargetClass();
        if (actualModelClass == null) {
            return null;
        }

        T obj;
        try {
            obj = actualModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Convert {} to {} instance failed: {}", map, actualModelClass, e);
            return null;
        }

        Map<String, Class<?>> fieldTypes = ClassUtils.getBeanClassFieldTypes(actualModelClass);
        final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
        for (Map.Entry<String, Class<?>> entry : fieldTypes.entrySet()) {
            String fieldName = entry.getKey();
            Class<?> fieldType = entry.getValue();
            String columnName = TextUtils.humpToLine(fieldName);
            if (map.containsKey(columnName)) {
                Object fieldValue = handleRawFieldValue(fieldType, map.get(columnName));
                objWrapper.setPropertyValue(fieldName, fieldValue);
            }
        }

        return obj;
    }


    /**
     * handleRawFieldValue
     *
     * @param fieldType
     * @param fieldValue
     * @return
     */
    private Object handleRawFieldValue(Class<?> fieldType, Object fieldValue) {
        if (fieldType == LocalDateTime.class && fieldValue instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) fieldValue).toLocalDateTime();
        } else if (fieldType == LocalDate.class && fieldValue instanceof java.sql.Date) {
            return ((java.sql.Date) fieldValue).toLocalDate();
        } else if (fieldType == LocalTime.class && fieldValue instanceof java.sql.Time) {
            return ((java.sql.Time) fieldValue).toLocalTime();
        } else {
            return fieldValue;
        }
    }

    /**
     * getMapperInterface
     *
     * @param proxy
     * @return
     */
    private Class<?> getMapperInterface(Proxy proxy) {
        return ClassUtils.getObjFieldValue(Proxy.getInvocationHandler(proxy),
                "mapperInterface", Class.class);
    }

    /**
     * getMapperInterface
     *
     * @return
     */
    private Class<?> getMapperInterface() {
        return getMapperInterface((Proxy) this);
    }


    /**
     * getInnerCondition
     *
     * @param owner
     * @return 查询条件构造器
     */
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


    /**
     * convertFieldName
     *
     * @param currentLinkName
     * @param fieldName
     * @return
     */
    private String convertFieldName(String currentLinkName, String fieldName) {
        String prefix = currentLinkName + ".";
        if(fieldName.contains(prefix)) {
            return fieldName;
        }
        return prefix + fieldName;
    }

    /**
     * convertSelectFields
     *
     * @param currentLinkName
     * @param condition
     * @return
     */
    private Map<String, String> convertSelectFields(String currentLinkName, HumanCondition condition) {
        Map<String, String> selectFields = condition.getSelectFields();
        if (MapUtils.isEmpty(selectFields)) {
            return new HashMap<>();
        }
        Map<String, String> newSelectFields = new HashMap<>();
        for (Map.Entry<String, String> entry : selectFields.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            newSelectFields.put(convertFieldName(currentLinkName, k), v);
        }

        return newSelectFields;
    }

    /**
     * convertGroupFields
     *
     * @param currentLinkName
     * @param condition
     * @return
     */
    private List<String> convertGroupFields(String currentLinkName, HumanCondition condition) {
        List<String> groupFields = condition.getGroupFields();
        if (CollectionUtils.isEmpty(groupFields)) {
            return new ArrayList<>();
        }
        List<String> newGroupFields = new ArrayList<>();
        for (String g : groupFields) {
            String s = convertFieldName(currentLinkName, g);
            newGroupFields.add(s);
        }

        return newGroupFields;
    }

    /**
     * convertOrders
     *
     * @param currentLinkName
     * @param condition
     * @return
     */
    private List<String> convertOrders(String currentLinkName, HumanCondition condition) {
        List<String> orders = condition.getOrders();
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }
        List<String> newOrders = new ArrayList<>();
        for (String g : orders) {
            String s = convertFieldName(currentLinkName, g);
            newOrders.add(s);
        }

        return newOrders;
    }

    /**
     * convertAndCondition
     *
     * @param currentLinkName
     * @param condition
     * @return
     */
    private AndCondition convertAndCondition(String currentLinkName, HumanCondition condition) {
        AndCondition andCondition = condition.getAndCondition();
        if (Objects.isNull(andCondition)) {
            return andCondition;
        }

        List<Object> condList = (List<Object>) ClassUtils.getFieldValue(AndCondition.class, "conds", andCondition);

        assert condList != null;
        for (Object c : condList) {
            if (c instanceof ConnectorCond) {
                Field fNameField = ClassUtils.getField(ConnectorCond.class, "fieldName", false, false);
                if (Objects.nonNull(fNameField)) {
                    String oldFieldName = (String) ClassUtils.getFieldValue(fNameField, c);
                    ClassUtils.setFieldValue(fNameField, c, convertFieldName(currentLinkName, oldFieldName));
                }
            }
        }

        return andCondition;

    }




}
