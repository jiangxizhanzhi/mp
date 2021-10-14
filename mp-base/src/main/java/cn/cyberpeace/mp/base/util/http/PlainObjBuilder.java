package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description 将对象转成请求参数,支持指定部分参数或者移除部分参数
 * @Author maojun
 * @Date 2021/9/9
 */
public class PlainObjBuilder<T> extends AbstractBuilder {
    private T data;

    private Set<String> excludeFields;

    private Set<String> includeFields;

    public PlainObjBuilder(T data, ParseMode mode, InputParser inputParser) {
        super(mode, inputParser);
        this.data = data;
        this.excludeFields = new HashSet<>();
        this.includeFields = new HashSet<>();
    }

    public <T> PlainObjBuilder<T> addExcludeFields(SerializableFunction<T>... fieldGetters) {
        Arrays.stream(fieldGetters).forEach(fieldGetter -> this.excludeFields.add(BeanUtils.getBeanGetterFieldName(fieldGetter)));
        return (PlainObjBuilder<T>) this;
    }

    public <T> PlainObjBuilder<T> addIncludeFields(SerializableFunction<T>... fieldGetters) {
        Arrays.stream(fieldGetters).forEach(fieldGetter -> this.includeFields.add(BeanUtils.getBeanGetterFieldName(fieldGetter)));
        return (PlainObjBuilder<T>) this;
    }

    @Override
    public ParamsContainer build() {
        // 默认全部
        List<Field> finalList = BeanUtils.getFieldsByObject(this.data);
        if (!this.includeFields.isEmpty()) {
            // 只取包含的
            finalList = finalList.stream().filter(field -> this.includeFields.contains(field.getName())).collect(Collectors.toList());
        } else if (!this.excludeFields.isEmpty()) {
            // 去掉排除的
            finalList = finalList.stream().filter(field -> !this.excludeFields.contains(field.getName())).collect(Collectors.toList());
        }
        BeanWrapper objWrapper = new BeanWrapperImpl(this.data);
        finalList.stream().forEach(field -> {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldVal = objWrapper.getPropertyValue(fieldName);
            if (fieldVal != null) {
                super.add(fieldName, fieldVal);
            }
        });
        return super.build();
    }
}
