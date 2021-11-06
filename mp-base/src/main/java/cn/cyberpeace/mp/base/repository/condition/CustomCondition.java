package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 自定义业务条件
 */
@Data
@AllArgsConstructor
public class CustomCondition implements BaseCondition {

    /**
     * 自定义业务条件默认载入方法
     * 默认载入字段名称匹配以及字段值类型匹配的变量
     * @param params 原始业务条件参数
     * @return 自定义条件
     */
    public CustomCondition loadFromParams(Map<String, Object> params) {
        String[] fieldNames = ClassUtils.getBeanClassFields(this.getClass());
        final BeanWrapper objWrapper = new BeanWrapperImpl(this);
        for (String fieldName: fieldNames) {
            Field field = null;
            Object fieldValue = params.getOrDefault(fieldName, null);
            if (fieldValue != null) {
                field = ClassUtils.getField(this.getClass(), fieldName, true, false);
            }
            if (field == null) {
                continue;
            }

            Class<?> fieldType = field.getType();
            Class<?> fieldValueType = fieldValue.getClass();
            if (fieldType == fieldValueType) {
                objWrapper.setPropertyValue(fieldName, fieldValue);
            }
        }

        return this;
    }

    @Override
    public boolean isEmpty() {
        String[] fieldNames = ClassUtils.getBeanClassFields(this.getClass());
        final BeanWrapper objWrapper = new BeanWrapperImpl(this);
        for (String fieldName: fieldNames) {
            Object fieldValue = objWrapper.getPropertyValue(fieldName);
            if (fieldValue == null) {
                continue;
            }
            if ((fieldValue instanceof String)
                    || (fieldValue instanceof List<?> && !((List<?>) fieldValue).isEmpty())
                    || (fieldValue instanceof String[] && ((String[]) fieldValue).length > 0)) {
                return false;
            }
        }

        return true;
    }
}
