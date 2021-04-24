package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.util.ClassUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CustomCondition extends BaseCondition {

    public CustomCondition loadFromParams(Map<String, Object> params) {
        String[] fieldNames = ClassUtils.getBeanClassFields(this.getClass());
        for (String fieldName: fieldNames) {
            Object fieldValue = params.getOrDefault(fieldName, null);
            if (fieldValue == null) {
                continue;
            }
            Field field = ClassUtils.getField(this.getClass(), fieldName, true, false);
            if (field == null) {
                continue;
            }
            Class<?> fieldType = field.getType();
            Class<?> fieldValueType = fieldValue.getClass();
            if (fieldType == fieldValueType) {
                ClassUtils.setBeanObjFieldValue(this, fieldName, fieldValue);
            }
        }

        return this;
    }

    @Override
    public Boolean isEmpty() {
        String[] fieldNames = ClassUtils.getBeanClassFields(this.getClass());
        for (String fieldName: fieldNames) {
            Object fieldValue = ClassUtils.getBeanObjFieldValue(this, fieldName);
            if (fieldValue == null) {
                continue;
            }
            if (fieldValue instanceof String) {
                return false;
            } else if (fieldValue instanceof List<?> && !((List<?>) fieldValue).isEmpty()) {
                return false;
            } else if (fieldValue instanceof String[] && ((String[]) fieldValue).length > 0) {
                return false;
            }
        }

        return true;
    }
}
