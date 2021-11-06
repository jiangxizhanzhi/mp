package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.common.JsonCommon;
import org.springframework.stereotype.Component;

@Component
public class JsonMessageConverter implements MessageConverter {

    @Override
    public <T> T fromMessageString(String messageString, Class<T> messageClass) {
        return JsonCommon.parse(messageString, messageClass);
    }

    @Override
    public <T> String toMessageString(T message) {
        return JsonCommon.stringify(message);
    }

}
