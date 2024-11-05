// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name MessageCommonUtil.java.
// File size 3467.
// File create date Fri Jul 12 12:55:27 CST 2024.
// File update date Fri Jul 12 12:55:27 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.util;

import com.alibaba.fastjson.JSON;
import com.google.common.io.CharStreams;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 国际化转换工具类
 */
@Component
public class MessageCommonUtil {
    /**
     * MessageCommonUtil
     */
    private MessageCommonUtil() {

    }

    @PostConstruct
    public void init() {
        keys = new ArrayList<>();
        zhMap = new ConcurrentHashMap<>();
        enMap = new ConcurrentHashMap<>();

        Resource resource = null;
        try {
            resource = new ClassPathResource(URLDecoder.decode("international_message.json", "UTF-8"));
            String content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
            List<Message> messages = JSON.parseArray(content, Message.class);
            if (CollectionUtils.isNotEmpty(messages)) {
                for (Message message : messages) {
                    keys.add(message.getKey());
                    zhMap.put(message.getKey(), message.getZhName());
                    enMap.put(message.getKey(), message.getEnName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> keys;
    private static Map<String, String> zhMap;
    private static Map<String, String> enMap;

    public static final String INTERNATIONAL_MESSAGE = "international_message";


    /**
     * getProbeIdsByName
     *
     * @param name
     * @param probes
     * @return
     */

    /**
     * convertMessage
     * @param name
     * @return
     */
    public static String convertMessage(String name) {
        if (!keys.contains(name)) {
            return name;
        }
        if (LocaleContextHolder.getLocale().getLanguage().equals("zh")) {
            return zhMap.get(name);
        } else {
            return enMap.get(name);
        }
    }

    /**
     * convertMessageWithNull
     * @param name
     * @return
     */
    public static String convertMessageWithNull(String name) {
        if (!keys.contains(name)) {
            return null;
        }
        if (LocaleContextHolder.getLocale().getLanguage().equals("zh")) {
            return zhMap.get(name);
        } else {
            return enMap.get(name);
        }
    }

    @Data
    public static class Message {
        private String key;
        private String zhName;
        private String enName;
    }
}
