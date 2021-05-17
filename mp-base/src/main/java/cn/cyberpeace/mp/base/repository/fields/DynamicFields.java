package cn.cyberpeace.mp.base.repository.fields;

import cn.cyberpeace.mp.base.util.CastUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicFields {

    private Map<String, Object> fieldsMap;

    public DynamicFields() {
        this.resetFieldsMap();
    }

    public DynamicFields(String[] fields) {
        setFields(fields);
    }

    public DynamicFields(List<String> fields) {
        setFields(fields);
    }

    public DynamicFields(Map<String, Object> fieldsMap) {
        setFieldsMap(fieldsMap);
    }

    public DynamicFields(DynamicFields dynamicFields) {
        if (dynamicFields != null) {
            setFieldsMap(dynamicFields.getFieldsMap());
        } else {
            this.resetFieldsMap();
        }
    }

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
            ((List<?>) obj).forEach(objItem -> resultList.add(apply(objItem)));
            return resultList;
        }

        Map<Object, Object> resultMap = new HashMap<>();
        fieldsMap.forEach((fieldName, fieldFlag) -> {
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
        });

        return resultMap;
    }

    public boolean hasField(String fieldName) {
        return (boolean) fieldsMap.getOrDefault(fieldName, false);
    }

    public String[] getFields() {
        List<String> fields = new ArrayList<>();
        fieldsMap.forEach((field, value) -> {
            if ((boolean) value) {
                fields.add(field);
            }
        });
        return fields.toArray(new String[0]);
    }

    public void setFields(String[] fields) {
        this.resetFieldsMap();
        this.merge(fields);
    }

    public void setFields(List<String> fields) {
        this.resetFieldsMap();
        this.merge(fields);
    }

    public Map<String, Object> getFieldsMap() {
        return fieldsMap;
    }

    public void setFieldsMap(Map<String, Object> fieldsMap) {
        this.resetFieldsMap();
        this.merge(fieldsMap);
    }

    public void resetFieldsMap() {
        this.fieldsMap = new HashMap<>();
    }

    public boolean hasValidFields() {
        for(Object entry : this.fieldsMap.values()) {
            if ((entry instanceof Boolean && (boolean) entry)
                    || (entry instanceof DynamicFields && ((DynamicFields) entry).hasValidFields())) {
                return true;
            }
        }

        return false;
    }

    public void merge(String field, boolean value) {
        this.fieldsMap.put(field, value);
    }

    public void merge(String field, String[] value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    public void merge(String field, List<String> value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    public void merge(String field, Map<String, Object> value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

    public void merge(String field, DynamicFields value) {
        Object originSubFields = this.fieldsMap.getOrDefault(field, null);
        if (originSubFields instanceof DynamicFields) {
            ((DynamicFields) originSubFields).merge(value);
        } else {
            this.fieldsMap.put(field, new DynamicFields(value));
        }
    }

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

    public DynamicFields merge(String[] targetFields) {
        if (targetFields == null) {
            return this;
        }

        for (String field: targetFields) {
            merge(field, true);
        }
        return this;
    }

    public DynamicFields merge(List<String> targetFields) {
        if (targetFields == null) {
            return this;
        }

        targetFields.forEach(field -> merge(field, true));
        return this;
    }

    public DynamicFields merge(Map<String, Object> targetFieldsMap) {
        if (targetFieldsMap == null) {
            return this;
        }

        targetFieldsMap.forEach(this::merge);
        return this;
    }

    public DynamicFields merge(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return this;
        }

        merge(targetDynamicFields.getFieldsMap());
        return this;
    }

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

    public Object intersect(String field, String[] value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, List<String> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, Map<String, Object> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, DynamicFields value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

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

    public DynamicFields intersect(String[] targetFields) {
        if (targetFields == null) {
            return null;
        }

        var intersectFields = new DynamicFields();
        for (String field: targetFields) {
            Object intersectionValue = intersect(field, true);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        }

        return intersectFields;
    }

    public DynamicFields intersect(List<String> targetFields) {
        if (targetFields == null) {
            return null;
        }

        var intersectFields = new DynamicFields();
        targetFields.forEach(field -> {
            Object intersectionValue = intersect(field, true);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        });

        return intersectFields;
    }

    public DynamicFields intersect(Map<String, Object> targetFieldsMap) {
        if (targetFieldsMap == null) {
            return null;
        }

        var intersectFields = new DynamicFields();
        targetFieldsMap.forEach((field, value) -> {
            Object intersectionValue = intersect(field, value);
            if (intersectionValue != null) {
                intersectFields.merge(field, intersectionValue);
            }
        });

        return intersectFields;
    }

    public DynamicFields intersect(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return null;
        }

        return intersect(targetDynamicFields.getFieldsMap());
    }

    @SuppressWarnings("java:S1172")
    public Object subtract(String field, boolean value) {
        if (value) {
            return false;
        }

        return null;
    }

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

    public DynamicFields subtract(String[] targetFields) {
        var subtractionFields = new DynamicFields(this);
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

    public DynamicFields subtract(List<String> targetFields) {
        var subtractionFields = new DynamicFields(this);
        if (targetFields == null) {
            return subtractionFields;
        }

        targetFields.forEach(field -> {
            Object subtractionValue = subtract(field, true);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        });

        return subtractionFields;
    }

    public DynamicFields subtract(Map<String, Object> targetFieldsMap) {
        var subtractionFields = new DynamicFields(this);
        if (targetFieldsMap == null) {
            return subtractionFields;
        }

        targetFieldsMap.forEach((field, value) -> {
            Object subtractionValue = subtract(field, value);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        });

        return subtractionFields;
    }

    public DynamicFields subtract(DynamicFields targetDynamicFields) {
        if (targetDynamicFields == null) {
            return new DynamicFields(this);
        }

        return subtract(targetDynamicFields.getFieldsMap());
    }
}
