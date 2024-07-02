package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName SnakeHttpParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpParam
 */
public interface SnakeHttpParam extends HttpParam {

    /**
     * toPathString
     *
     * @param httpParam
     * @return
     */
    static String toPathString(Object httpParam) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : BeanUtils.getMap(httpParam).entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if (!Objects.isNull(fieldValue)) {
                map.put(TextUtils.humpToLine(fieldName), fieldValue);
            }
        }
        return URLUtil.buildQuery(map, StandardCharsets.UTF_8);
    }

    /**
     * toPathString
     *
     * @return
     */
    @Override
    default String toPathString() {
        return toPathString(this);
    }

}
