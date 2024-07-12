 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AnnotationUtil.java.
 // File size 1672.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @ClassName AnnotationUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class AnnotationUtil {
    /**
     * 设置新的注解的属性（字段）值
     * Params:
     * annotation – 注解对象
     * annotationField – 注解属性（字段）名称
     * value – 要更新的属性值
     */
    public static void setValue(Annotation annotation, String annotationField, Object value) {

        try {
            // 获取注解的代理实例
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            if (null == invocationHandler || StringUtils.isBlank(annotationField)) {
                return;
            }
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
            // 设置字段可访问
            memberValuesField.setAccessible(true);
            // 获取 memberValues 的值
            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
            // 设置属性的新值
            memberValues.put(annotationField, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}

