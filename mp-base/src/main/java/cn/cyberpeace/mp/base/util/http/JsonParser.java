package cn.cyberpeace.mp.base.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
class JsonParser implements InputParser {
    private static SerializeConfig SNAKE_PARSE_CONFIG = new SerializeConfig() {{
        this.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
    }};

    @Override
    public String parse(Map<String, Object> values, ParseMode mode) {
        if (mode == ParseMode.SNAKE_JSON) {
            JSONObject jsonObject = new JSONObject();
            values.entrySet().forEach(entry -> jsonObject.put(entry.getKey(), entry.getValue()));
            return JSON.toJSONString(jsonObject, SNAKE_PARSE_CONFIG);
        } else {
            return JSON.toJSONString(values);
        }
    }
}