 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnakeHttpData.java.
 // File size 915.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
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
