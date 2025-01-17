 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JsonMessageConverter.java.
 // File size 518.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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
