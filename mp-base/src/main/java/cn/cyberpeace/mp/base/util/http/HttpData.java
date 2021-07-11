package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;

import java.util.Map;


public interface HttpData {

    static String toContent(Object httpData) {
        return JsonUtils.stringify(httpData);
    }

    static Map<String, Object> toMultipart(Object httpData) {
        return BeanUtils.getMap(httpData);
    }

    
    default boolean multipart() {
        return false;
    }

    
    default String toContent() {
        return toContent(this);
    }

    
    default Map<String, Object> toMultipart() {
        return toMultipart(this);
    }

    default String contentType() {
        return null;
    }

}
