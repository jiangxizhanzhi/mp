 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JsonHandler.java.
 // File size 4380.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.json;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @ClassName JsonHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class JsonHandler {

    @Getter
    private final ObjectMapper objectMapper;

    /**
     * JsonHandler
     *
     * @param objectMapper
     */
    public JsonHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * parse
     *
     * @param content
     * @param valueType
     * @return
     */
    public <T> T parse(String content, Class<T> valueType) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    /**
     * parse
     *
     * @param content
     * @param valueTypeRef
     * @return
     */
    public <T> T parse(String content, TypeReference<T> valueTypeRef) {
        T obj;
        try {
            obj = objectMapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }

        return obj;
    }

    /**
     * parseMap
     *
     * @param content
     * @return
     */
    public Map<String, Object> parseMap(String content) {
        return parse(content, new TypeReference<>() {
        });
    }

    /**
     * parseList
     *
     * @param content
     * @return
     */
    public List<Object> parseList(String content) {
        return parse(content, new TypeReference<>() {
        });
    }

    /**
     * parseList
     *
     * @param content
     * @param valueType
     * @return
     */
    public <T> List<T> parseList(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }
    }

    /**
     * parseTree
     *
     * @param content
     * @return
     */
    public JsonNode parseTree(String content) {
        JsonNode obj;
        try {
            obj = objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Parse json text error: " + e.getMessage());
        }
        return obj;
    }


    /**
     * stringify
     *
     * @param obj
     * @return
     */
    public String stringify(Object obj) {
        String result;
        try {
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }

        return result;
    }

    /**
     * mapToBean
     *
     * @param map
     * @param beanClass
     * @return
     */
    public <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        return parse(stringify(map), beanClass);
    }

    /**
     * beanToMap
     *
     * @param obj
     * @return
     */
    public <T> Map<String, Object> beanToMap(T obj) {
        return parse(stringify(obj), new TypeReference<>() {
        });
    }

    /**
     * isJsonString
     *
     * @param json
     * @return boolean
     */
    public boolean isJsonString(final String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException jpe) {
            return false;
        }
    }

    /**
     * createArrayNode
     *
     * @return
     */
    public ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

}
