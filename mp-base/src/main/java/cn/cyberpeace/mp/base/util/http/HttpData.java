 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpData.java.
 // File size 1299.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;

import java.util.Map;

/**
 * @ClassName HttpData
 * @Description http请求体数据，实现该接口定义对应bean
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface HttpData {

    /**
     * toContent
     *
     * @param httpData
     * @return
     */
    static String toContent(Object httpData) {
        return JsonUtils.stringify(httpData);
    }

    /**
     * toMultipart
     *
     * @param httpData
     * @return
     */
    static Map<String, Object> toMultipart(Object httpData) {
        return BeanUtils.getMap(httpData);
    }

    /**
     * 重写本方法返回true来表示表单数据
     */
    default boolean multipart() {
        return false;
    }

    /**
     * 序列化请求参数
     */
    default String toContent() {
        return toContent(this);
    }

    /**
     * 转化为表单参数map
     */
    default Map<String, Object> toMultipart() {
        return toMultipart(this);
    }

    /**
     * contentType
     *
     * @return
     */
    default String contentType() {
        return null;
    }

}
