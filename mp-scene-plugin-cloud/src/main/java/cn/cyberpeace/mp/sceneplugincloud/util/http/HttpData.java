package cn.cyberpeace.mp.sceneplugincloud.util.http;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashMap;
import java.util.Map;

public interface HttpData {

    Logger log = LoggerFactory.getLogger(HttpData.class);

    default boolean multipart() {
        return false;
    }

    default String toContent() {
        return JsonUtils.stringify(this);
    }

    default Map<String, Object> toMultipart() {
        Map<String, Object> multipart = new HashMap<>();
        final BeanWrapper wrapper = new BeanWrapperImpl(this);
        var propertyDescriptors = wrapper.getPropertyDescriptors();
        for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();
            if (!StringUtils.equals("class", fieldName)) {
                multipart.put(fieldName, wrapper.getPropertyValue(fieldName));
            }
        }
        return multipart;
    }

}
