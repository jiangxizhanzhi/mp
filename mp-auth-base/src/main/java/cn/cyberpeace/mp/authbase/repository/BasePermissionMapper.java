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
import cn.hutool.core.collection.CollectionUtil;
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
import java.util.stream.Collectors;

public interface BasePermissionMapper<P extends BasePermissionModel, T extends BaseModel> extends BaseModelMapper {



    @Recommendation
    default List<T> listTargetsByOwner(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = processOuterCondition(owner, outerCondition);

        List<Map<String, Object>> mapList = originList(innerCondition);

        List<T> objs = new ArrayList<>();
        mapList.forEach(map -> {
            var obj = mapToTargetObj(map);
            if (obj != null) {
                objs.add(obj);
            }
        });
        return objs;
    }

    @Recommendation
    default int totalTargetsByOwner(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = processOuterCondition(owner, outerCondition);

        return total(innerCondition);
    }

    

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

    private HumanCondition processOuterCondition(String owner, HumanCondition outerCondition) {
        HumanCondition innerCondition = getInnerCondition(owner);
        String currentLinkName = innerCondition.getCurrentLinkName();

        
        innerCondition.setDistinct(outerCondition.getDistinct());
        
        innerCondition.getSelectFields().putAll(convertSelectFields(currentLinkName, outerCondition));
        
        innerCondition.setLimit(outerCondition.getLimit());
        innerCondition.setOffset(outerCondition.getOffset());
        
        innerCondition.getGroupFields().addAll(convertGroupFields(currentLinkName, outerCondition));
        
        innerCondition.getOrders().addAll(convertOrders(currentLinkName, outerCondition));
        
        innerCondition.getAndCondition().addCondition(convertAndCondition(currentLinkName, outerCondition));
        return innerCondition;
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
        fieldTypes.forEach((fieldName, fieldType) -> {
            String columnName = TextUtils.humpToLine(fieldName);
            if (map.containsKey(columnName)) {
                Object fieldValue = handleRawFieldValue(fieldType, map.get(columnName));
                objWrapper.setPropertyValue(fieldName, fieldValue);
            }
        });

        return obj;
    }


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
        String prefix = currentLinkName + ".";
        if(fieldName.contains(prefix)) {
            return fieldName;
        }
        return prefix + fieldName;
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

        return andCondition;

    }




}
