 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomServletRequestDataBinder.java.
 // File size 2069.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName CustomServletRequestDataBinder
 * @Description 自定义请求数据绑定处理类，主要解决将前端传递的下划线格式的字段自动转为驼峰格式
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServletRequestDataBinder
 */
public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    /**
     * CustomServletRequestDataBinder
     *
     * @param target
     */
    public CustomServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    /**
     * CustomServletRequestDataBinder
     *
     * @param target
     * @param objectName
     */
    public CustomServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    /** 遍历请求参数对象 把请求参数的名转换成驼峰体
     * 重写addBindValues绑定数值的方法
     * @param mutablePropertyValues 请求参数列表
     * @param request 请求
     */
    @Override
    protected void addBindValues(MutablePropertyValues mutablePropertyValues, ServletRequest request) {
        List<PropertyValue> propertyValueList = mutablePropertyValues.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue propertyValue : propertyValueList) {
            String name = propertyValue.getName();
            String camel = TextUtils.lineToHump(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, propertyValue.getValue()));
            }
        }
        propertyValueList.addAll(adds);
    }
}
