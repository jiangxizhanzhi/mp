package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.util.json.JsonHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JsonCommon {

    private static JsonHandler jsonHandler;

    private static JsonHandler snakeJsonHandler;

    private JsonCommon(BaseCommon baseCommon) {
        JsonCommon.jsonHandler = new JsonHandler(BaseCommon.createCommonObjectMapper());
        JsonCommon.snakeJsonHandler = new JsonHandler(BaseCommon.createSnakeObjectMapper());
    }

    public static <T> T parse(String content, Class<T> valueType) {
        return jsonHandler.parse(content, valueType);
    }

    public static <T> T parse(String content, TypeReference<T> valueTypeRef) {
        return jsonHandler.parse(content, valueTypeRef);
    }

    public static Map<String, Object> parseMap(String content) {
        return jsonHandler.parseMap(content);
    }

    public static List<Object> parseList(String content) {
        return jsonHandler.parseList(content);
    }

    public static JsonNode parseTree(String content) {
        return jsonHandler.parseTree(content);
    }

    public static String stringify(Object obj) {
        return jsonHandler.stringify(obj);
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        return jsonHandler.mapToBean(map, beanClass);
    }

    public static <T> Map<String, Object> beanToMap(T obj) {
        return jsonHandler.beanToMap(obj);
    }

    public static boolean isJsonString(String json) {
        return jsonHandler.isJsonString(json);
    }

    public static <T> T snakeParse(String content, Class<T> valueType) {
        return snakeJsonHandler.parse(content, valueType);
    }

    public static <T> T snakeParse(String content, TypeReference<T> valueTypeRef) {
        return snakeJsonHandler.parse(content, valueTypeRef);
    }

    public static <T> T snakeMapToBean(Map<String, Object> map, Class<T> beanClass) {
        return snakeJsonHandler.mapToBean(map, beanClass);
    }

    public static <T> Map<String, Object> beanToSnakeMap(T obj) {
        return snakeJsonHandler.beanToMap(obj);
    }

    public static String snakeStringify(Object obj) {
        return snakeJsonHandler.stringify(obj);
    }

}
