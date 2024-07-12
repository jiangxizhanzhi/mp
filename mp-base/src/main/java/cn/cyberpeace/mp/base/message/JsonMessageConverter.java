 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name JsonMessageConverter.java.
 // File size 880.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.common.JsonCommon;
import org.springframework.stereotype.Component;

/**
 * @ClassName JsonMessageConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see MessageConverter
 */
@Component
public class JsonMessageConverter implements MessageConverter {

    /**
     * fromMessageString
     *
     * @param messageString
     * @param messageClass
     * @return
     */
    @Override
    public <T> T fromMessageString(String messageString, Class<T> messageClass) {
        return JsonCommon.parse(messageString, messageClass);
    }

    /**
     * toMessageString
     *
     * @param message
     * @return
     */
    @Override
    public <T> String toMessageString(T message) {
        return JsonCommon.stringify(message);
    }

}
