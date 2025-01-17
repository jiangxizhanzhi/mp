 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnakeHttpParam.java.
 // File size 861.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.hutool.core.util.URLUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface SnakeHttpParam extends HttpParam {

    static String toPathString(Object httpParam) {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.getMap(httpParam).forEach((fieldName, fieldValue) -> {
            if(!Objects.isNull(fieldValue)){
                map.put(TextUtils.humpToLine(fieldName), fieldValue);
            }
        });
        return URLUtil.buildQuery(map, StandardCharsets.UTF_8);
    }

    @Override
    default String toPathString() {
        return toPathString(this);
    }

}
