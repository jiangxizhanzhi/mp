package cn.cyberpeace.mp.base.util.json;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class JsonHandler {

    @Getter
    private ObjectMapper objectMapper;

    public JsonHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T parse(String content, Class<T> valueType) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    public <T> T parse(String content, TypeReference<T> valueTypeRef) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    public Map<String, Object> parseMap(String content) {
        return parse(content, new TypeReference<>() {});
    }

    public List<Object> parseList(String content) {
        return parse(content, new TypeReference<>() {});
    }

    public JsonNode parseTree(String content) {
        JsonNode obj;
        try {
            obj = objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }
        return obj;
    }

    public String stringify(Object obj) {
        String result;
        try {
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }

        return result;
    }

    public <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        return parse(stringify(map), beanClass);
    }

    public <T> Map<String, Object> beanToMap(T obj) {
        return parse(stringify(obj), new TypeReference<>() {});
    }

    public boolean isJsonString(final String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException jpe) {
            return false;
        }
    }
}
