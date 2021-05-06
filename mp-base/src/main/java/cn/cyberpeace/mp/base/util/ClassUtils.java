package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ClassUtils {

    private static final Map<Class<?>, String[]> beanClassFieldsCache = new HashMap<>();

    private ClassUtils() {}

    public static Field getField(Class<?> clazz, String fieldName, boolean findSuper, boolean ignoreNotFound) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (findSuper && superClass != null) {
                return getField(superClass, fieldName, true, ignoreNotFound);
            } else {
                if (!ignoreNotFound) {
                    log.error("Get field `{}` on {} failed: {}", fieldName, clazz, e.getMessage());
                }
            }
            return null;
        }
        return field;
    }

    @SuppressWarnings("java:S3011")
    public static Object getFieldValue(Field field, Object obj) {
        boolean canAccess = field.canAccess(obj);
        if (!canAccess) {
            field.setAccessible(true);
        }

        Object value;
        try {
            value = field.get(obj);
        } catch (IllegalAccessException e) {
            log.error("Get field `{}` value on instance {} failed: {}", field, obj, e.getMessage());
            return null;
        } finally {
            if (!canAccess) {
                field.setAccessible(false);
            }
        }

        return value;
    }

    @SuppressWarnings("java:S3011")
    public static boolean setFieldValue(Field field, Object obj, Object value) {
        boolean canAccess = field.canAccess(obj);
        if (!canAccess) {
            field.setAccessible(true);
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("Set field `{}` value {} on instance {} failed: {}", field, value, obj, e.getMessage());
            return false;
        } finally {
            if (!canAccess) {
                field.setAccessible(false);
            }
        }

        return true;
    }

    public static <T> T castFieldType(Object value, Class<T> expectedFieldType) {
        T fieldValue = null;
        if (value != null) {
            try {
                fieldValue = expectedFieldType.cast(value);
            } catch (ClassCastException e) {
                log.error("Cast value `{}` to {} failed: {}", value, expectedFieldType, e.getMessage());
            }
        }

        return fieldValue;
    }

    public static Object getFieldValue(Class<?> clazz, String fieldName, Object obj) {
        var field = getField(clazz, fieldName, true, false);
        if (field == null) {
            return null;
        }

        return getFieldValue(field, obj);
    }

    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj, Class<T> expectedFieldType) {
        Object value = getFieldValue(clazz, fieldName, obj);
        return castFieldType(value, expectedFieldType);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return getFieldValue(clazz, fieldName, null);
    }

    public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(clazz, fieldName, null, expectedFieldType);
    }

    public static Object getObjFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj.getClass(), fieldName, obj);
    }

    public static <T> T getObjFieldValue(Object obj, String fieldName, Class<T> expectedFieldType) {
        return getFieldValue(obj.getClass(), fieldName, obj, expectedFieldType);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            log.error("Get method `{}` on {} failed: {}", methodName, clazz, e.getMessage());
            return null;
        }
        return method;
    }

    @SuppressWarnings("java:S3011")
    public static Object getBeanObjFieldValue(Method method, Object obj) {
        boolean canAccess = method.canAccess(obj);
        if (!canAccess) {
            method.setAccessible(true);
        }

        Object value;
        try {
            value = method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Invoke method `{}` on instance {} failed: {}", method, obj, e.getMessage());
            return null;
        } finally {
            if (!canAccess) {
                method.setAccessible(false);
            }
        }

        return value;
    }

    @SuppressWarnings("java:S3011")
    public static boolean setBeanObjFieldValue(Method method, Object obj, Object value) {
        boolean canAccess = method.canAccess(obj);
        if (!canAccess) {
            method.setAccessible(true);
        }

        try {
            method.invoke(obj, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Invoke method `{}` on instance {} failed: {}", method, obj, e.getMessage());
            return false;
        } finally {
            if (!canAccess) {
                method.setAccessible(false);
            }
        }

        return true;
    }

    public static Object getBeanObjFieldValue(Object obj, String fieldName) {
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        var method = getMethod(obj.getClass(), methodName);
        if (method == null) {
            return null;
        }

        return getBeanObjFieldValue(method, obj);
    }

    public static <T> T getBeanObjFieldValue(Object obj, String fieldName, Class<T> expectedFieldType) {
        Object value = getBeanObjFieldValue(obj, fieldName);
        return castFieldType(value, expectedFieldType);
    }

    public static boolean setBeanObjFieldValue(Object obj, String fieldName, Object value) {
        var field = getField(obj.getClass(), fieldName, true, false);
        if (field == null) {
            return false;
        }

        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        var method = getMethod(obj.getClass(), methodName, field.getType());
        if (method == null) {
            return false;
        }

        return setBeanObjFieldValue(method, obj, value);
    }

    public static String getBeanGetterRefFieldName(Class<?> clazz, Method getter) {
        String methodName = getter.getName();
        if (!methodName.startsWith("get")) {
            return null;
        }

        String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        var field = getField(clazz, fieldName, true, true);
        if (field == null) {
            return null;
        }

        Class<?>[] parameterTypes = getter.getParameterTypes();
        if (parameterTypes.length != 0) {
            return null;
        }

        return fieldName;
    }

    public static String getBeanSetterRefFieldName(Class<?> clazz, Method setter) {
        String methodName = setter.getName();
        if (!methodName.startsWith("set")) {
            return null;
        }

        String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        var field = getField(clazz, fieldName, true, true);
        if (field == null) {
            return null;
        }

        Class<?>[] parameterTypes = setter.getParameterTypes();
        if (parameterTypes.length != 1 || parameterTypes[0] != field.getType()) {
            return null;
        }

        return fieldName;
    }

    public static String[] getBeanClassFields(Class<?> clazz) {
        String[] fields = beanClassFieldsCache.getOrDefault(clazz, null);
        if (fields != null) {
            return fields;
        }

        Method[] methods = clazz.getMethods();
        Map<String, boolean[]> fieldFlagMap = new HashMap<>();
        for (Method method: methods) {
            String getterFieldName = getBeanGetterRefFieldName(clazz, method);
            if (getterFieldName != null) {
                boolean[] fieldFlag = fieldFlagMap.getOrDefault(getterFieldName, new boolean[]{ false, false });
                fieldFlag[0] = true;
                fieldFlagMap.put(getterFieldName, fieldFlag);
                continue;
            }

            String setterFieldName = getBeanSetterRefFieldName(clazz, method);
            if (setterFieldName != null) {
                boolean[] fieldFlag = fieldFlagMap.getOrDefault(setterFieldName, new boolean[]{ false, false });
                fieldFlag[1] = true;
                fieldFlagMap.put(setterFieldName, fieldFlag);
            }
        }

        List<String> fieldList = new ArrayList<>();
        fieldFlagMap.forEach((fieldName, fieldFlag) -> {
            if (fieldFlag[0] && fieldFlag[1]) {
                fieldList.add(fieldName);
            }
        });

        fields = fieldList.toArray(new String[0]);
        beanClassFieldsCache.put(clazz, fields);

        return fields;
    }
}
