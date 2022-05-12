package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.hutool.core.util.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public interface SnakeHttpParam extends HttpParam {

    static String toPathString(Object httpParam) {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.getMap(httpParam).forEach((fieldName, fieldValue) -> {
            map.put(TextUtils.humpToLine(fieldName), fieldValue);
        });
        return URLUtil.buildQuery(map, StandardCharsets.UTF_8);
    }

    @Override
    default String toPathString() {
        return toPathString(this);
    }

}
