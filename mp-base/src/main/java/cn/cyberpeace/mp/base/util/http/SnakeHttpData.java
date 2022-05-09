package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public interface SnakeHttpData extends HttpData {

    static String toContent(Object httpData) {
        return JsonUtils.snakeStringify(httpData);
    }

    static Map<String, Object> toMultipart(Object httpData) {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.getMap(httpData).forEach((fieldName, fieldValue) -> {
            map.put(TextUtils.humpToLine(fieldName), fieldValue);
        });
        return map;
    }

    @Override
    default String toContent() {
        return toContent(this);
    }

    @Override
    default Map<String, Object> toMultipart() {
        return toMultipart(this);
    }

}
