package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseObjResult {
    @JsonIgnore
    private DynamicFields fields;

    public Object objToResult(Object obj, DynamicFields fields) {
        if (fields == null) {
            return obj;
        }

        if (obj instanceof Object[]) {
            List<Object> resultList = new ArrayList<>();
            for (Object objItem: (Object[]) obj) {
                resultList.add(objToResult(objItem, fields));
            }
            return resultList;
        }

        if (obj instanceof List<?>) {
            List<Object> resultList = new ArrayList<>();
            ((List<?>) obj).forEach(objItem -> resultList.add(objToResult(objItem, fields)));
            return resultList;
        }

        Map<Object, Object> resultMap = new HashMap<>();
        fields.getFieldsMap().forEach((fieldName, fieldFlag) -> {
            Object fieldValue = getObjFieldValue(obj, fieldName);
            if (fieldFlag instanceof Boolean && (boolean) fieldFlag) {
                resultMap.put(fieldName, fieldValue);
            } else if (fieldFlag instanceof DynamicFields) {
                resultMap.put(fieldName, objToResult(fieldValue, (DynamicFields) fieldFlag));
            }
        });

        return resultMap;
    }

    public Object getObjField(Object obj, String fieldName) {
        if (obj instanceof Map) {
            return fieldName;
        }

        var field = ClassUtils.getField(obj.getClass(), fieldName, true, true);
        if (field == null) {
            return fieldName;
        }
        return field;
    }

    public Object getObjFieldValue(Object obj, String fieldName) {
        Object fieldValue;
        if (obj instanceof Map) {
            fieldValue = ((Map<?, ?>) obj).getOrDefault(fieldName, null);
        } else {
            // 未注入jackson注解
            final BeanWrapper objWrapper = new BeanWrapperImpl(obj);
            fieldValue = objWrapper.getPropertyValue(fieldName);
        }
        return fieldValue;
    }

    public abstract Object asResult();
}
