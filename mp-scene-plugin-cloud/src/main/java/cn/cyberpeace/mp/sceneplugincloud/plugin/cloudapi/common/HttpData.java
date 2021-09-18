package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HttpData {

    Logger log = LoggerFactory.getLogger(HttpData.class);

    default String toContent() {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Stringify obj error: ", e);
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }
    }

}
