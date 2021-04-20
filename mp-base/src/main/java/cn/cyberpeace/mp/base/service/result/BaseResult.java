package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.model.DynamicFields;
import cn.cyberpeace.mp.base.util.ClassUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseResult {
    @JsonIgnore
    private DynamicFields fields = null;

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
            ((List<?>) obj).forEach(objItem -> {
                resultList.add(objToResult(objItem, fields));
            });
            return resultList;
        }

        Map<String, Object> resultMap = new HashMap<>();

        fields.getFieldsMap().forEach((fieldName, fieldFlag) -> {
            Object fieldValue = ClassUtils.getBeanObjFieldValue(obj, fieldName);
            if (fieldFlag instanceof Boolean && (Boolean) fieldFlag) {
                resultMap.put(fieldName, fieldValue);
            } else if (fieldFlag instanceof DynamicFields) {
                resultMap.put(fieldName, objToResult(fieldValue, (DynamicFields) fieldFlag));
            }
        });

        return resultMap;
    }

    public abstract Object asResult();
}
