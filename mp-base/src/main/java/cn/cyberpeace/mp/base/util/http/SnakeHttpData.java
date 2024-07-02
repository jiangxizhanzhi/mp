package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public interface SnakeHttpData extends HttpData {

    /**
     * toContent
     *
     * @param httpData
     * @return
     */
    static String toContent(Object httpData) {
        return JsonUtils.snakeStringify(httpData);
    }

    /**
     * toMultipart
     *
     * @param httpData
     * @return
     */
    static Map<String, Object> toMultipart(Object httpData) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : BeanUtils.getMap(httpData).entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            map.put(TextUtils.humpToLine(fieldName), fieldValue);
        }
        return map;
    }

    /**
     * toContent
     *
     * @return
     */
    @Override
    default String toContent() {
        return toContent(this);
    }

    /**
     * toMultipart
     *
     * @return
     */
    @Override
    default Map<String, Object> toMultipart() {
        return toMultipart(this);
    }

}
