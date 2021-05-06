package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.model.stereotype.Table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelUtils {

    private static final Map<Class<?>, Method[]> initDefaultMethodsCache = new HashMap<>();

    private ModelUtils() {}

    public static <M> String getTableName(Class<M> clazz) {
        var table = clazz.getAnnotation(Table.class);
        return table != null ? table.value() : null;
    }

    public static <M> Method[] getInitDefaultMethods(Class<M> clazz) {
        Method[] methods = initDefaultMethodsCache.getOrDefault(clazz, null);
        if (methods != null) {
            return methods;
        }

        String[] fields = ClassUtils.getBeanClassFields(clazz);
        List<Method> methodList = new ArrayList<>();
        for (String fieldName: fields) {
            String methodName = "initDefault" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method;
            try {
                method = clazz.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                continue;
            }

            methodList.add(method);
        }

        methods = methodList.toArray(new Method[0]);
        initDefaultMethodsCache.put(clazz, methods);

        return methods;
    }
}
