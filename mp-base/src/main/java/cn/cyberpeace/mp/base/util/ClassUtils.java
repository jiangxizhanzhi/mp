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

    public static Field getField(Class<?> clazz, String fieldName, Boolean findSuper, Boolean ignoreNotFound) {
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
        Field field = getField(clazz, fieldName, true, false);
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
        Field field = getField(obj.getClass(), fieldName, true, false);
        if (field == null) {
            return false;
        }

        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = getMethod(obj.getClass(), methodName, field.getType());
        if (method == null) {
            return false;
        }

        return setBeanObjFieldValue(method, obj, value);
    }

    public static String[] getBeanClassFields(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, Boolean[]> fieldFlagMap = new HashMap<>();
        for (Method method: methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                Field field = getField(clazz, fieldName, true, true);
                if (field == null) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 0) {
                    continue;
                }

                Boolean[] fieldFlag = fieldFlagMap.getOrDefault(fieldName, new Boolean[]{ false, false });
                fieldFlag[0] = true;
                fieldFlagMap.put(fieldName, fieldFlag);
            } else if (methodName.startsWith("set")) {
                String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                Field field = getField(clazz, fieldName, true, true);
                if (field == null) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1 || parameterTypes[0] != field.getType()) {
                    continue;
                }

                Boolean[] fieldFlag = fieldFlagMap.getOrDefault(fieldName, new Boolean[]{ false, false });
                fieldFlag[1] = true;
                fieldFlagMap.put(fieldName, fieldFlag);
            }
        }

        List<String> fields = new ArrayList<>();
        fieldFlagMap.forEach((fieldName, fieldFlag) -> {
            if (fieldFlag[0] && fieldFlag[1]) {
                fields.add(fieldName);
            }
        });

        return fields.toArray(new String[0]);
    }
}
