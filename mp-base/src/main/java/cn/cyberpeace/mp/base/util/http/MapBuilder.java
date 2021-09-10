package cn.cyberpeace.mp.base.util.http;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
public class MapBuilder extends AbstractBuilder {

    public MapBuilder(ParseMode mode, InputParser inputParser) {
        super(mode, inputParser);
    }

    public MapBuilder addKeyValue(String key, Object value) {
        super.add(key, value);
        return this;
    }

    public MapBuilder addKeyValues(Map<String, Object> input) {
        if (input != null) {
            input.entrySet().forEach(entry -> super.add(entry.getKey(), entry.getValue()));
        }
        return this;
    }
}
