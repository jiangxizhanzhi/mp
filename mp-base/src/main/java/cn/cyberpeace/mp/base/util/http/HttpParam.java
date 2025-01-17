 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpParam.java.
 // File size 557.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.util.URLUtil;

import java.nio.charset.StandardCharsets;

/**
 * http查询参数，实现该接口定义对应bean
 */
public interface HttpParam {

    static String toPathString(Object httpParam) {
        return URLUtil.buildQuery(BeanUtils.getMap(httpParam), StandardCharsets.UTF_8);
    }

    /**
     * 转换为Url参数
     */
    default String toPathString() {
        return toPathString(this);
    }

}
