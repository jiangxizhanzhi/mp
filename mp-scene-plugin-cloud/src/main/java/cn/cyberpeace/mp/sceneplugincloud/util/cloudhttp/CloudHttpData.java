package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashMap;
import java.util.Map;

public interface CloudHttpData extends HttpData {

    @Override
    default String toContent() {
        return JsonUtils.snakeStringify(this);
    }

    @Override
    default Map<String, Object> toMultipart() {
        Map<String, Object> multipart = new HashMap<>();
        final BeanWrapper wrapper = new BeanWrapperImpl(this);
        var propertyDescriptors = wrapper.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                multipart.put(TextUtils.humpToLine(fieldName), wrapper.getPropertyValue(fieldName));
            }
        }
        return multipart;
    }

}
