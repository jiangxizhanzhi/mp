package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * jackson utils
 */
@Slf4j
@Component
public class JsonUtils {

    private static ObjectMapper objectMapper;

    @SuppressWarnings({"java:S1144", "java:S3010"})
    private JsonUtils(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    public static <T> T parse(String content, Class<T> valueType) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    public static <T> T parse(String content, TypeReference<T> valueTypeRef) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    public static JsonNode parseTree(String content) {
        JsonNode obj;
        try {
            obj = objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }
        return obj;
    }

    public static String toJsonString(Object obj) {
        String result;
        try {
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }

        return result;
    }

    /**
     * @param json json text
     * @return judge valid json text
     */
    public static boolean isValidJSON(final String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException jpe) {
            return false;
        }
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        return parse(toJsonString(map), beanClass);
    }

    public static <T> Map<String, Object> beanToMap(T obj) {
        return parse(toJsonString(obj), new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 将对象的大写转换为下划线加小写，例如：userName-->user_name
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnderlineJSONString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String reqJson = mapper.writeValueAsString(object);
        return reqJson;
    }

    /**
     * 将下划线转换为驼峰的形式，例如：user_name-->userName
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T toSnakeObject(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // mapper的configure方法可以设置多种配置（例如：多字段 少字段的处理）　　　　　　
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper.readValue(json, clazz);
    }

}
