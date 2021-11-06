package cn.cyberpeace.mp.base.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    private static final JsonHandler jsonHandler = new JsonHandler(createDefaultObjectMapper());

    private static final JsonHandler snakeJsonHandler = new JsonHandler(createDefaultSnakeObjectMapper());

    private JsonUtils() {}

    private static ObjectMapper createDefaultObjectMapper() {
        var objectMapper = new ObjectMapper();
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private static ObjectMapper createDefaultSnakeObjectMapper() {
        var objectMapper = createDefaultObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
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
