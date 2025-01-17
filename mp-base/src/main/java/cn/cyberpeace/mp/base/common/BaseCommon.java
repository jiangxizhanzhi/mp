 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCommon.java.
 // File size 3488.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class BaseCommon {

    public static DateTimeFormatter dateTimeFormatter;

    public static DateTimeFormatter dateFormatter;

    public static DateTimeFormatter timeFormatter;

    public static BaseAppConfig baseAppConfig;

    private BaseCommon(BaseAppConfig baseAppConfig) {
        BaseCommon.baseAppConfig = baseAppConfig;
        BaseCommon.dateTimeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDateTimePattern());
        BaseCommon.dateFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDatePattern());
        BaseCommon.timeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getTimePattern());
    }

    /**
     * 创建通用的object mapper对象 （设定了项目默认值）
     * @return 通用的object mapper对象
     */
    public static ObjectMapper createCommonObjectMapper() {
        var objectMapper = new ObjectMapper();
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        if (Boolean.FALSE.equals(baseAppConfig.getJsonStringifyIncludeNull())) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return objectMapper;
    }

    /**
     * 创建驼峰-蛇形的object mapper对象 （设定了项目默认值）
     * @return 通用的object mapper对象
     */
    public static ObjectMapper createSnakeObjectMapper() {
        var objectMapper = createCommonObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return objectMapper;
    }

}
