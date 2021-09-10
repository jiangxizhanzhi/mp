package cn.cyberpeace.mp.base.util.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
abstract class AbstractBuilder {
    private Map<String, Object> mapValues;

    private ParseMode mode;

    private InputParser parser;

    public AbstractBuilder(ParseMode mode, InputParser inputParser) {
        this.mapValues = new HashMap<>();
        this.mode = mode;
        this.parser = inputParser;
    }

    protected void add(String key, Object value) {
        this.mapValues.put(key, value);
    }

    public ParamsContainer build() {
        return new ParamsContainer(this.parser.parse(this.mapValues, mode));
    }
}
