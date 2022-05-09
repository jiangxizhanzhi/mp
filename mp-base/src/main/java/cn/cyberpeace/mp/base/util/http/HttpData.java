package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;

import java.util.Map;

/**
 * http请求体数据，实现该接口定义对应bean
 */
public interface HttpData {

    static String toContent(Object httpData) {
        return JsonUtils.stringify(httpData);
    }

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

}
