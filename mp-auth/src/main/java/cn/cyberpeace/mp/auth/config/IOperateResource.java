package cn.cyberpeace.mp.auth.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface IOperateResource {
    int resourceType(Object[] args, OperateAuth operateAuth);

    default Map<String, Field> getAllFields(Class clazz) {
        Map<String, Field> result = new HashMap<>();
        while (clazz != null) {
            var fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                result.put(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    default Field getField(Class clazz, String name) {
        return getAllFields(clazz).get(name);
    }
}
