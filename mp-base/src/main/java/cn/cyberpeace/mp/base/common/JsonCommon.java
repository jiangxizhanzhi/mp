package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.util.json.JsonHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JsonCommon {

    private static JsonHandler jsonHandler;

    private static JsonHandler snakeJsonHandler;

    /**
     * JsonCommon
     *
     * @param baseCommon
     */
    private JsonCommon(BaseCommon baseCommon) {
        JsonCommon.jsonHandler = new JsonHandler(BaseCommon.createCommonObjectMapper());
        JsonCommon.snakeJsonHandler = new JsonHandler(BaseCommon.createSnakeObjectMapper());
    }

    /**
     * parse
     *
     * @param content
     * @param valueType
     * @return
     */
    public static <T> T parse(String content, Class<T> valueType) {
        return jsonHandler.parse(content, valueType);
    }

    /**
     * parse
     *
     * @param content
     * @param valueTypeRef
     * @return
     */
    public static <T> T parse(String content, TypeReference<T> valueTypeRef) {
        return jsonHandler.parse(content, valueTypeRef);
    }

    /**
     * parseMap
     *
     * @param content
     * @return
     */
    public static Map<String, Object> parseMap(String content) {
        return jsonHandler.parseMap(content);
    }

    /**
     * parseList
     *
     * @param content
     * @return
     */
    public static List<Object> parseList(String content) {
        return jsonHandler.parseList(content);
    }

    /**
     * parseList
     *
     * @param content
     * @param valueType
     * @return
     */
    public static <T> List<T> parseList(String content, Class<T> valueType) {
        return jsonHandler.parseList(content, valueType);
    }


    /**
     * parseTree
     *
     * @param content
     * @return
     */
    public static JsonNode parseTree(String content) {
        return jsonHandler.parseTree(content);
    }

    /**
     * stringify
     *
     * @param obj
     * @return
     */
    public static String stringify(Object obj) {
        return jsonHandler.stringify(obj);
    }

    /**
     * mapToBean
     *
     * @param map
     * @param beanClass
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        return jsonHandler.mapToBean(map, beanClass);
    }

    /**
     * beanToMap
     *
     * @param obj
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T obj) {
        return jsonHandler.beanToMap(obj);
    }

    /**
     * isJsonString
     *
     * @param json
     * @return boolean
     */
    public static boolean isJsonString(String json) {
        return jsonHandler.isJsonString(json);
    }

    /**
     * snakeParse
     *
     * @param content
     * @param valueType
     * @return
     */
    public static <T> T snakeParse(String content, Class<T> valueType) {
        return snakeJsonHandler.parse(content, valueType);
    }

    /**
     * snakeParse
     *
     * @param content
     * @param valueTypeRef
     * @return
     */
    public static <T> T snakeParse(String content, TypeReference<T> valueTypeRef) {
        return snakeJsonHandler.parse(content, valueTypeRef);
    }

    /**
     * snakeMapToBean
     *
     * @param map
     * @param beanClass
     * @return
     */
    public static <T> T snakeMapToBean(Map<String, Object> map, Class<T> beanClass) {
        return snakeJsonHandler.mapToBean(map, beanClass);
    }

    /**
     * beanToSnakeMap
     *
     * @param obj
     * @return
     */
    public static <T> Map<String, Object> beanToSnakeMap(T obj) {
        return snakeJsonHandler.beanToMap(obj);
    }

    /**
     * snakeStringify
     *
     * @param obj
     * @return
     */
    public static String snakeStringify(Object obj) {
        return snakeJsonHandler.stringify(obj);
    }

    /**
     * createArrayNode
     *
     * @return
     */
    public static ArrayNode createArrayNode() {
        return jsonHandler.createArrayNode();
    }

}
