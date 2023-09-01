package cn.cyberpeace.mp.base.util.mptool;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AnnotationUtil {
    /**
     * 设置自定义注解的属性值
     *
     * @param annotation     自定义注解实例
     * @param annotationField 注解的属性名
     * @param value          属性值
     */
    public static void setValue(Annotation annotation, String annotationField, Object value) {
        try {
            // 获取注解的代理实例
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
            // 设置字段可访问
            memberValuesField.setAccessible(true);
            // 获取 memberValues 的值
            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
            // 设置属性的新值
            memberValues.put(annotationField, value);
        } catch (Exception e) {
        }
    }


}

