package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.util.CastUtils;

import java.util.*;

public class DynamicFields {

    private Map<String, Object> fieldsMap;

    public DynamicFields() {
        this.resetFieldsMap();
    }

    public DynamicFields(String[] fields) {
        List<String> listFields = Arrays.asList(fields);
        setFields(listFields);
    }

    public DynamicFields(List<String> fields) {
        setFields(fields);
    }

    public DynamicFields(Map<String, Object> fieldsMap) {
        setFieldsMap(fieldsMap);
    }

    public DynamicFields(DynamicFields dynamicFields) {
        setFieldsMap(dynamicFields.getFieldsMap());
    }

    public List<String> getFields() {
        List<String> fields = new ArrayList<>();
        fieldsMap.forEach((field, value) -> {
            if ((Boolean) value) {
                fields.add(field);
            }
        });
        return fields;
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

    public DynamicFields merge(List<String> targetFields) {
        targetFields.forEach((field) -> {
            merge(field, true);
        });
        return this;
    }

    public DynamicFields merge(Map<String, Object> targetFieldsMap) {
        targetFieldsMap.forEach(this::merge);
        return this;
    }

    public DynamicFields merge(DynamicFields targetDynamicFields) {
        merge(targetDynamicFields.getFieldsMap());
        return this;
    }

    public DynamicFields intersect(List<String> targetFields) {
        DynamicFields intersectFields = new DynamicFields();
        targetFields.forEach((field) -> {
            Object originValue = this.fieldsMap.getOrDefault(field, false);
            if (originValue instanceof Boolean && (Boolean) originValue) {
                intersectFields.merge(field, true);
            } else if (originValue instanceof DynamicFields) {
                intersectFields.merge(field, (DynamicFields) originValue);
            }
        });

        return intersectFields;
    }

    public DynamicFields intersect(Map<String, Object> targetFieldsMap) {
        DynamicFields intersectFields = new DynamicFields();
        targetFieldsMap.forEach((field, value) -> {
            Object originValue = this.fieldsMap.getOrDefault(field, false);
            if (originValue instanceof Boolean && (Boolean) originValue) {
                if (value instanceof Boolean && (Boolean) value) {
                    intersectFields.merge(field, true);
                } else if (value instanceof List<?>) {
                    List<String> subFields = CastUtils.castList(value, String.class);
                    if (subFields != null) {
                        intersectFields.merge(field, subFields);
                    }
                } else if (value instanceof Map<?, ?>) {
                    Map<String, Object> subFieldsMap = CastUtils.castMap(value, String.class, Object.class);
                    if (subFieldsMap != null) {
                        intersectFields.merge(field, subFieldsMap);
                    }
                } else if (value instanceof DynamicFields) {
                    intersectFields.merge(field, (DynamicFields) value);
                }
            } else if (originValue instanceof DynamicFields) {
                if (value instanceof Boolean && (Boolean) value) {
                    intersectFields.merge(field, (DynamicFields) originValue);
                } else if (value instanceof List<?>) {
                    List<String> subFields = CastUtils.castList(value, String.class);
                    if (subFields != null) {
                        intersectFields.merge(field, ((DynamicFields) originValue).intersect(subFields));
                    }
                } else if (value instanceof Map<?, ?>) {
                    Map<String, Object> subFieldsMap = CastUtils.castMap(value, String.class, Object.class);
                    if (subFieldsMap != null) {
                        intersectFields.merge(field, ((DynamicFields) originValue).intersect(subFieldsMap));
                    }
                } else if (value instanceof DynamicFields) {
                    intersectFields.merge(field, ((DynamicFields) originValue).intersect((DynamicFields) value));
                }
            }
        });

        return intersectFields;
    }

    public DynamicFields intersect(DynamicFields targetDynamicFields) {
        return intersect(targetDynamicFields.getFieldsMap());
    }
}
