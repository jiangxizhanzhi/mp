package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 将业务参数转成url请求参数的格式, a=1&b=2
 * @Author maojun
 * @Date 2021/9/9
 */
class LineStrParser implements InputParser {
    @Override
    public String parse(Map<String, Object> values, ParseMode mode) {
        if (mode == ParseMode.SNAKE) {
            Map<String, Object> snakeKeyMap = Maps.newHashMapWithExpectedSize(values.size());
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                snakeKeyMap.put(TextUtils.humpToLine(entry.getKey()), entry.getValue());
            }
            return doParse(snakeKeyMap);
        } else {
            return doParse(values);
        }
    }

    private String doParse(Map<String, Object> values) {
        // todo
        List<String> strList = values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
                .collect(Collectors.toList());
        return StringUtils.join(strList, "&");
    }
}
