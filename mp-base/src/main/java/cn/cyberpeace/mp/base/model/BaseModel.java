package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.generator.DateGenerator;
import cn.cyberpeace.mp.base.model.generator.Generator;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

@Slf4j
@Data
public class BaseModel {
    @DefaultGen(UuidGenerator.class)
    private String id;

    @DefaultGen(DateGenerator.class)
    private Date createTime;

    @DefaultGen(DateGenerator.class)
    private Date updateTime;

    private void initDefault(String fieldName, Map<String, Object[]> initDefaultFields) {
        Object[] value = initDefaultFields.getOrDefault(fieldName, null);
        if (value == null) {
            return;
        }

        if (!(value[0] instanceof Field)) {
            return;
        }

        var field = (Field) value[0];
        if (ClassUtils.getFieldValue(field, this) != null) {
            return;
        }

        Object defaultValue = value[1];
        if (defaultValue instanceof Method) {
            try {
                ((Method) defaultValue).invoke(this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Invoke initDefaultMethod on instance {} for {} failed: {}", this, fieldName, e.getMessage());
            }
        } else if (defaultValue instanceof Generator) {
            ClassUtils.setFieldValue(field, this, ((Generator) defaultValue).generate());
        } else {
            ClassUtils.setFieldValue(field, this, defaultValue);
        }
    }

    public void initDefault() {
        Map<String, Object[]> initDefaultFields = ModelUtils.getInitDefaultFields(getClass());
        initDefaultFields.forEach((fieldName, value) -> initDefault(fieldName));
    }

    public void initDefault(String fieldName) {
        Map<String, Object[]> initDefaultFields = ModelUtils.getInitDefaultFields(getClass());
        initDefault(fieldName, initDefaultFields);
    }

    public boolean hasDeleted() {
        return false;
    }

    public boolean prepareDeleteFlag() {
        return false;
    }
}
