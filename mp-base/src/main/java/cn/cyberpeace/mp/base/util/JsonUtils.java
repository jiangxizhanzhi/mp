package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
