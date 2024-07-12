 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamicFields.java.
 // File size 23641.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.fields;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.CastUtils;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;

/**
 * @ClassName DynamicFields
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class DynamicFields {

    private Map<String, Object> fieldsMap;

    /**
     * DynamicFields
     */
    public DynamicFields() {
        this.resetFieldsMap();
    }

    /**
     * DynamicFields
     *
     * @param fields
     */
    public DynamicFields(String... fields) {
        setFields(fields);
    }

    /**
     * DynamicFields
     *
     * @param fieldGetters
     */
    @SafeVarargs
    public <T> DynamicFields(SerializableFunction<T>... fieldGetters) {
        this(Arrays.stream(fieldGetters).map(BeanUtils::getBeanGetterFieldName).toArray(String[]::new));
    }

    /**
     * DynamicFields
     *
     * @param fields
     */
    public DynamicFields(List<String> fields) {
        setFields(fields);
    }

    /**
     * DynamicFields
     *
     * @param fieldsMap
     */
    public DynamicFields(Map<String, Object> fieldsMap) {
        setFieldsMap(fieldsMap);
    }

    /**
     * DynamicFields
     *
     * @param dynamicFields
     */
    public DynamicFields(DynamicFields dynamicFields) {
        if (dynamicFields != null) {
            setFieldsMap(dynamicFields.getFieldsMap());
        } else {
            this.resetFieldsMap();
        }
    }

    /**
     * 过滤数据对象字段, 核心数据对象返回过滤后的map
     * @param obj 数据对象
     * @return 过滤后的数据对象
     */
    public Object apply(Object obj) {
        if (obj == null || fieldsMap == null) {
            return obj;
        }

        if (obj instanceof Object[]) {
            List<Object> resultList = new ArrayList<>();
            for (Object objItem: (Object[]) obj) {
                resultList.add(apply(objItem));
            }
            return resultList;
        }

        if (obj instanceof List<?>) {
            List<Object> resultList = new ArrayList<>();
            for (Object objItem : ((List<?>) obj)) {
                resultList.add(apply(objItem));
            }
            return resultList;
        }

        Map<Object, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldFlag = entry.getValue();
            Object fieldValue;
            if (obj instanceof Map) {
                fieldValue = ((Map<?, ?>) obj).getOrDefault(fieldName, null);
            } else {
                // 未注入jackson注解
                final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
                fieldValue = objWrapper.getPropertyValue(fieldName);
            }
            if (fieldFlag instanceof Boolean && (boolean) fieldFlag) {
                resultMap.put(fieldName, fieldValue);
            } else if (fieldFlag instanceof DynamicFields) {
                resultMap.put(fieldName, ((DynamicFields) fieldFlag).apply(fieldValue));
            }
        }

        return resultMap;
    }

    /**
     * 过滤数据对象字段, 只是把不需要的字段设置成null
     * @param obj 数据对象
     * @return 过滤后的数据对象
     */
    @SuppressWarnings("unchecked")
    public <T> T applyWithNull(T obj) {
        if (obj == null || fieldsMap == null) {
            return obj;
        }

        if (obj instanceof Object[]) {
            for (Object objItem: (Object[]) obj) {
                applyWithNull(objItem);
            }
            return obj;
        }

        if (obj instanceof List<?>) {
            for (Object o : ((List<?>) obj)) {
                applyWithNull(o);
            }
            return obj;
        }

        if (obj instanceof Map) {
            Map<String, Object> actualObj = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : actualObj.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                Object fieldFlag = fieldsMap.getOrDefault(fieldName, null);
                if (fieldFlag == null || (fieldFlag instanceof Boolean && !(boolean) fieldFlag)) {
                    actualObj.put(fieldName, null);
                } else if (fieldFlag instanceof DynamicFields) {
                    ((DynamicFields) fieldFlag).applyWithNull(fieldValue);
                }
            }
        } else {
            final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
            for (String fieldName: ClassUtils.getBeanClassFields(obj.getClass())) {
                Object fieldFlag = fieldsMap.getOrDefault(fieldName, null);
                if (fieldFlag == null || (fieldFlag instanceof Boolean && !(boolean) fieldFlag)) {
                    objWrapper.setPropertyValue(fieldName, null);
                } else if (fieldFlag instanceof DynamicFields) {
                    Object fieldValue = objWrapper.getPropertyValue(fieldName);
                    ((DynamicFields) fieldFlag).applyWithNull(fieldValue);
                }
            }
        }

        return obj;
    }

    /**
     * hasField
     *
     * @param fieldName
     * @return boolean
     */
    public boolean hasField(String fieldName) {
        return (boolean) fieldsMap.getOrDefault(fieldName, false);
    }

    /**
     * getFields
     *
     * @return
     */
    public String[] getFields() {
        List<String> fields = new ArrayList<>();
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            if ((boolean) value) {
                fields.add(field);
            }
        }
        return fields.toArray(new String[0]);
    }

    /**
     * setFields
     *
     * @param fields
     */
    public void setFields(String[] fields) {
        this.resetFieldsMap();
        this.merge(fields);
    }

    /**
     * setFields
     *
     * @param fields
     */
    public void setFields(List<String> fields) {
        this.resetFieldsMap();
        this.merge(fields);
    }

    /**
     * getFieldsMap
     *
     * @return
     */
    public Map<String, Object> getFieldsMap() {
        return fieldsMap;
    }

    /**
     * setFieldsMap
     *
     * @param fieldsMap
     */
    public void setFieldsMap(Map<String, Object> fieldsMap) {
        this.resetFieldsMap();
        this.merge(fieldsMap);
    }

    /**
     * resetFieldsMap
     */
    public void resetFieldsMap() {
        this.fieldsMap = new HashMap<>();
    }

    /**
     * hasValidFields
     *
     * @return boolean
     */
    public boolean hasValidFields() {
        for(Object entry : this.fieldsMap.values()) {
            if ((entry instanceof Boolean && (boolean) entry)
                    || (entry instanceof DynamicFields && ((DynamicFields) entry).hasValidFields())) {
                return true;
            }
        }

        return false;
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, boolean value) {
        this.fieldsMap.put(field, value);
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, String[] value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, List<String> value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, Map<String, Object> value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, DynamicFields value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    /**
     * merge
     *
     * @param field
     * @param value
     */
    public void merge(String field, Object value) {
        if (value instanceof Boolean) {
            merge(field, (boolean) value);
        } else if (value instanceof String[]) {
            merge(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (!subFields.isEmpty()) {
                merge(field, subFields);
            }
        } else if (value instanceof Map<?, ?>) {
            Map<String, Object> subFieldsMap = CastUtils.castMap(value, String.class, Object.class);
            if (subFieldsMap != null) {
                merge(field, subFieldsMap);
            }
        } else if (value instanceof DynamicFields) {
            merge(field, (DynamicFields) value);
        }
    }

    /**
     * merge
     *
     * @param targetFields
     * @return
     */
    public DynamicFields merge(String[] targetFields) {
        if (targetFields == null) {
            return this;
        }

        for (String field: targetFields) {
            merge(field, true);
        }
        return this;
    }

    /**
     * merge
     *
     * @param targetFields
     * @return
     */
    public DynamicFields merge(List<String> targetFields) {
        if (targetFields == null) {
            return this;
        }

        for (String field : targetFields) {
            merge(field, true);
        }
        return this;
    }

    /**
     * merge
     *
     * @param targetFieldsMap
     * @return
     */
    public DynamicFields merge(Map<String, Object> targetFieldsMap) {
        if (targetFieldsMap == null) {
            return this;
        }

        for (Map.Entry<String, Object> entry : targetFieldsMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            merge(key, value);
        }
        return this;
    }

    /**
     * merge
     *
     * @param targetDynamicFields
     * @return
     */
    public DynamicFields merge(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return this;
        }

        merge(targetDynamicFields.getFieldsMap());
        return this;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, boolean value) {
        if (!value) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return true;
        } else if (originValue instanceof DynamicFields) {
            return originValue;
        }

        return null;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, String[] value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, List<String> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, Map<String, Object> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, DynamicFields value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    /**
     * intersect
     *
     * @param field
     * @param value
     * @return
     */
    public Object intersect(String field, Object value) {
        if (value instanceof Boolean) {
            return intersect(field, (boolean) value);
        } else if (value instanceof String[]) {
            return intersect(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (!subFields.isEmpty()) {
                return intersect(field, subFields);
            }
        } else if (value instanceof Map<?, ?>) {
            Map<String, Object> subFieldsMap = CastUtils.castMap(value, String.class, Object.class);
            if (subFieldsMap != null) {
                return intersect(field, subFieldsMap);
            }
        } else if (value instanceof DynamicFields) {
            return intersect(field, (DynamicFields) value);
        }

        return null;
    }

    /**
     * intersect
     *
     * @param targetFields
     * @return
     */
    public DynamicFields intersect(String[] targetFields) {
        if (targetFields == null) {
            return null;
        }

        DynamicFields intersectFields = new DynamicFields();
        for (String field: targetFields) {
            Object intersectionValue = intersect(field, true);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        }

        return intersectFields;
    }

    /**
     * intersect
     *
     * @param targetFields
     * @return
     */
    public DynamicFields intersect(List<String> targetFields) {
        if (targetFields == null) {
            return null;
        }

        DynamicFields intersectFields = new DynamicFields();
        for (String field : targetFields) {
            Object intersectionValue = intersect(field, true);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        }

        return intersectFields;
    }

    /**
     * intersect
     *
     * @param targetFieldsMap
     * @return
     */
    public DynamicFields intersect(Map<String, Object> targetFieldsMap) {
        if (targetFieldsMap == null) {
            return null;
        }

        DynamicFields intersectFields = new DynamicFields();
        for (Map.Entry<String, Object> entry : targetFieldsMap.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            Object intersectionValue = intersect(field, value);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        }

        return intersectFields;
    }

    /**
     * intersect
     *
     * @param targetDynamicFields
     * @return
     */
    public DynamicFields intersect(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return null;
        }

        return intersect(targetDynamicFields.getFieldsMap());
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    @SuppressWarnings("java:S1172")
    public Object subtract(String field, boolean value) {
        if (value) {
            return false;
        }

        return null;
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    public Object subtract(String field, String[] value) {
        if (value.length == 0) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    public Object subtract(String field, List<String> value) {
        if (value.isEmpty()) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    public Object subtract(String field, Map<String, Object> value) {
        if (value.size() == 0) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    public Object subtract(String field, DynamicFields value) {
        if (!value.hasValidFields()) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    /**
     * subtract
     *
     * @param field
     * @param value
     * @return
     */
    public Object subtract(String field, Object value) {
        if (value instanceof Boolean) {
            return subtract(field, (boolean) value);
        } else if (value instanceof String[]) {
            return subtract(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (!subFields.isEmpty()) {
                return subtract(field, subFields);
            }
        } else if (value instanceof Map<?, ?>) {
            Map<String, Object> subFieldsMap = CastUtils.castMap(value, String.class, Object.class);
            if (subFieldsMap != null) {
                return subtract(field, subFieldsMap);
            }
        } else if (value instanceof DynamicFields) {
            return subtract(field, (DynamicFields) value);
        }

        return null;
    }

    /**
     * subtract
     *
     * @param targetFields
     * @return
     */
    public DynamicFields subtract(String[] targetFields) {
        DynamicFields subtractionFields = new DynamicFields(this);
        if (targetFields == null) {
            return subtractionFields;
        }

        for (String field: targetFields) {
            Object subtractionValue = subtract(field, true);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        }

        return subtractionFields;
    }

    /**
     * subtract
     *
     * @param targetFields
     * @return
     */
    public DynamicFields subtract(List<String> targetFields) {
        DynamicFields subtractionFields = new DynamicFields(this);
        if (targetFields == null) {
            return subtractionFields;
        }

        for (String field : targetFields) {
            Object subtractionValue = subtract(field, true);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        }

        return subtractionFields;
    }

    /**
     * subtract
     *
     * @param targetFieldsMap
     * @return
     */
    public DynamicFields subtract(Map<String, Object> targetFieldsMap) {
        DynamicFields subtractionFields = new DynamicFields(this);
        if (targetFieldsMap == null) {
            return subtractionFields;
        }

        for (Map.Entry<String, Object> entry : targetFieldsMap.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            Object subtractionValue = subtract(field, value);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        }

        return subtractionFields;
    }

    /**
     * subtract
     *
     * @param targetDynamicFields
     * @return
     */
    public DynamicFields subtract(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return new DynamicFields(this);
        }

        return subtract(targetDynamicFields.getFieldsMap());
    }
}
