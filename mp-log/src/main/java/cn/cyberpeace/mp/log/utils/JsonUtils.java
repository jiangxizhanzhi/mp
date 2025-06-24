 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JsonUtils.java.
 // File size 1499.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

 /**
  * json跑龙套
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class JsonUtils {

     /**
      * json跑龙套
      */
     private JsonUtils(){

    }

     /**
      * 将对象的大写转换为下划线加小写
      *
      * @param object
      * @param serializerFeatures 序列化器特性
      * @return
      */
     public static String toUnderlineJSONString(Object object,SerializerFeature... serializerFeatures) {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(object, config, serializerFeatures);
    }

     /**
      * 将下划线转换为驼峰的形式
      *
      * @param json
      * @param clazz
      * @param features 特性
      * @return
      */
     public static <T> T toSnakeObject(String json, Class<T> clazz, Feature... features){
        ParserConfig pc = new ParserConfig();
        pc.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.parseObject(json, clazz, pc,features);
    }

}
