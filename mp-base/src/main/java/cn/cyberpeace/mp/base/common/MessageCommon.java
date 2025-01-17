 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MessageCommon.java.
 // File size 1834.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageCommon {

    private static MessageSource messageSource;

    @SuppressWarnings({"java:S1144", "java:S3010"})
    private MessageCommon(MessageSource messageSource) {
        MessageCommon.messageSource = messageSource;
    }

    public static String get(String msgKey) {
        try {
            return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("Get message by key `{}` on locale {} failed: {}", msgKey, LocaleContextHolder.getLocale(),
                    e.getMessage());
            return msgKey;
        }
    }

    public static String get(String msgKey, String... params) {
        try {
            return messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("Get message by key `{}` params `{}` on locale {} failed: {}", msgKey, params,
                    LocaleContextHolder.getLocale(), e.getMessage());
            return msgKey;
        }
    }

    public static String getOrDefault(String msgKey, String defaultMsg, String... params) {
        try {
            return messageSource.getMessage(msgKey, params, defaultMsg, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("Get message by key `{}` params `{}` defaultMsg `{}` on locale {} failed: {}", msgKey, params,
                    defaultMsg, LocaleContextHolder.getLocale(), e.getMessage());
            return msgKey;
        }
    }
}
