package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SnakeJsonMessageConverter implements MessageConverter {

    @Override
    public <T> T fromMessageString(String messageString, Class<T> messageClass) {
        try {
            return JsonUtils.toSnakeObject(messageString, messageClass);
        } catch (IOException e) {
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }
    }

    @Override
    public <T> String toMessageString(T message) {
        try {
            return JsonUtils.toUnderlineJSONString(message);
        } catch (IOException e) {
            throw new MpRuntimeException("Stringify obj error: " + e.getMessage());
        }
    }

}
