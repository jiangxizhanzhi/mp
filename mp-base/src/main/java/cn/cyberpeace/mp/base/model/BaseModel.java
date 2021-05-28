package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.generator.Generator;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 基础model类
 * 约定：所有数据表都有pk(数据库主键，对model隐藏)，id(业务id)，创建时间，更新时间
 */
@Slf4j
@Data
public class BaseModel {
    @DefaultGen(UuidGenerator.class)
    private String id;

    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime createTime;

    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime updateTime;

    /**
     * 初始化字段默认值
     * @param fieldName 字段
     * @param initDefaultFields 具有默认值定义的字段信息
     */
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

    /**
     * 初始化所有默认值
     */
    public void initDefault() {
        Map<String, Object[]> initDefaultFields = ModelUtils.getInitDefaultFields(getClass());
        initDefaultFields.forEach((fieldName, value) -> initDefault(fieldName));
    }

    /**
     * 初始化指定字段默认值
     * @param fieldName 字段名称
     */
    public void initDefault(String fieldName) {
        Map<String, Object[]> initDefaultFields = ModelUtils.getInitDefaultFields(getClass());
        initDefault(fieldName, initDefaultFields);
    }

    /**
     * 初始化指定字段默认值
     * @param fieldGetter 字段的bean get方法引用
     * @param <T> T
     */
    public <T> void initDefault(SerializableFunction<T> fieldGetter) {
        initDefault(BeanUtils.getBeanGetterFieldName(fieldGetter));
    }

    /**
     * 数据是否被逻辑删除，用于扩展定义逻辑删除条件
     * @return boolean
     */
    public boolean hasDeleted() {
        return false;
    }

    /**
     * 设置逻辑删除条件
     * @return true-逻辑删除  false-物理删除
     */
    public boolean prepareDeleteFlag() {
        return false;
    }
}
