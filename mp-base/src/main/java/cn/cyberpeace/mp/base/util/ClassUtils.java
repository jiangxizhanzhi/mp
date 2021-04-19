package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ClassUtils {
    public static Field getField(Class<?> clazz, String fieldName) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.error("Get field `{}` on {} failed: {}", fieldName, clazz, e.getMessage());
            return null;
        }
        return field;
    }

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

    public static Boolean setFieldValue(Field field, Object obj, Object value) {
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

    public static <FieldType> FieldType castFieldType(Object value, Class<FieldType> expectedFieldType) {
        FieldType fieldValue = null;
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
        Field field = getField(clazz, fieldName);
        if (field == null) {
            return null;
        }

        return getFieldValue(field, obj);
    }

    public static <FieldType> FieldType getFieldValue(Class<?> clazz, String fieldName, Object obj,
                                                      Class<FieldType> expectedFieldType) {
        Object value = getFieldValue(clazz, fieldName, obj);
        return castFieldType(value, expectedFieldType);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return getFieldValue(clazz, fieldName, null);
    }

    public static <FieldType> FieldType getStaticFieldValue(Class<?> clazz, String fieldName,
                                                            Class<FieldType> expectedFieldType) {
        return getFieldValue(clazz, fieldName, null, expectedFieldType);
    }

    public static Object getObjFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj.getClass(), fieldName, obj);
    }

    public static <FieldType> FieldType getObjFieldValue(Object obj, String fieldName,
                                                         Class<FieldType> expectedFieldType) {
        return getFieldValue(obj.getClass(), fieldName, obj, expectedFieldType);
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        Method method;
        try {
            method = clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.error("Get method `{}` on {} failed: {}", methodName, clazz, e.getMessage());
            return null;
        }
        return method;
    }

    public static Object getBeanObjFieldValue(Method method, Object obj) {
        boolean canAccess = method.canAccess(obj);
        if (!canAccess) {
            method.setAccessible(true);
        }

        Object value;
        try {
            value = method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Invoke method `{}` value on instance {} failed: {}", method, obj, e.getMessage());
            return null;
        } finally {
            if (!canAccess) {
                method.setAccessible(false);
            }
        }

        return value;
    }

    public static Boolean setBeanObjFieldValue(Method method, Object obj, Object value) {
        boolean canAccess = method.canAccess(obj);
        if (!canAccess) {
            method.setAccessible(true);
        }

        try {
            method.invoke(obj, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Invoke method `{}` value on instance {} failed: {}", method, obj, e.getMessage());
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
        Method method = getMethod(obj.getClass(), methodName);
        if (method == null) {
            return null;
        }

        return getBeanObjFieldValue(method, obj);
    }

    public static <FieldType> FieldType getBeanObjFieldValue(Object obj, String fieldName,
                                                             Class<FieldType> expectedFieldType) {
        Object value = getBeanObjFieldValue(obj, fieldName);
        return castFieldType(value, expectedFieldType);
    }

    public static Boolean setBeanObjFieldValue(Object obj, String fieldName, Object value) {
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = getMethod(obj.getClass(), methodName);
        if (method == null) {
            return false;
        }

        return setBeanObjFieldValue(method, obj, value);
    }
}
