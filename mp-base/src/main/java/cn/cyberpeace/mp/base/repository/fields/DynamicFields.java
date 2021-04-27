package cn.cyberpeace.mp.base.repository.fields;

import cn.cyberpeace.mp.base.util.CastUtils;

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
        }
    }

    public Boolean hasField(String fieldName) {
        return (Boolean) fieldsMap.getOrDefault(fieldName, false);
    }

    public String[] getFields() {
        List<String> fields = new ArrayList<>();
        fieldsMap.forEach((field, value) -> {
            if ((Boolean) value) {
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

    public Boolean hasValidFields() {
        for(Object entry : this.fieldsMap.values()) {
            if (entry instanceof Boolean) {
                if ((Boolean) entry) {
                    return true;
                }
            } else if (entry instanceof DynamicFields) {
                if (((DynamicFields) entry).hasValidFields()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void merge(String field, Boolean value) {
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
            merge(field, (Boolean) value);
        } else if (value instanceof String[]) {
            merge(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (subFields != null) {
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

        targetFields.forEach((field) -> {
            merge(field, true);
        });
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

    public Object intersect(String field, Boolean value) {
        if (!value) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return true;
        } else if (originValue instanceof DynamicFields) {
            return originValue;
        }

        return null;
    }

    public Object intersect(String field, String[] value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, List<String> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, Map<String, Object> value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, DynamicFields value) {
        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return value;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).intersect(value);
        }

        return null;
    }

    public Object intersect(String field, Object value) {
        if (value instanceof Boolean) {
            return intersect(field, (Boolean) value);
        } else if (value instanceof String[]) {
            return intersect(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (subFields != null) {
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

        DynamicFields intersectFields = new DynamicFields();
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

        DynamicFields intersectFields = new DynamicFields();
        targetFields.forEach((field) -> {
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

        DynamicFields intersectFields = new DynamicFields();
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

    public Object subtract(String field, Boolean value) {
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
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    public Object subtract(String field, List<String> value) {
        if (value.size() == 0) {
            return null;
        }

        Object originValue = this.fieldsMap.getOrDefault(field, false);
        if (originValue instanceof Boolean && (Boolean) originValue) {
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
        if (originValue instanceof Boolean && (Boolean) originValue) {
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
        if (originValue instanceof Boolean && (Boolean) originValue) {
            return false;
        } else if (originValue instanceof DynamicFields) {
            return ((DynamicFields) originValue).subtract(value);
        }

        return null;
    }

    public Object subtract(String field, Object value) {
        if (value instanceof Boolean) {
            return subtract(field, (Boolean) value);
        } else if (value instanceof String[]) {
            return subtract(field, (String[]) value);
        } else if (value instanceof List<?>) {
            List<String> subFields = CastUtils.castList(value, String.class);
            if (subFields != null) {
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

    public DynamicFields subtract(List<String> targetFields) {
        DynamicFields subtractionFields = new DynamicFields(this);
        if (targetFields == null) {
            return subtractionFields;
        }

        targetFields.forEach((field) -> {
            Object subtractionValue = subtract(field, true);
            if (subtractionValue != null) {
                subtractionFields.merge(field, subtractionValue);
            }
        });

        return subtractionFields;
    }

    public DynamicFields subtract(Map<String, Object> targetFieldsMap) {
        DynamicFields subtractionFields = new DynamicFields(this);
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
