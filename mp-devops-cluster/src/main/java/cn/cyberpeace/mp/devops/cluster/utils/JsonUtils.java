package cn.cyberpeace.mp.devops.cluster.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtils {

    private JsonUtils(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * 将对象的大写转换为下划线加小写
     * @param object
     * @return
     * @throws JsonProcessingException
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
     * @return
     * @throws IOException
     */
    public static <T> T toSnakeObject(String json, Class<T> clazz, Feature... features){
        ParserConfig pc = new ParserConfig();
        pc.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.parseObject(json, clazz, pc,features);
    }
    
}
