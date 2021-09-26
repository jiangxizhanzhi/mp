package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageUtils {

    private static MessageSource messageSource;

    @SuppressWarnings({"java:S1144", "java:S3010"})
    private MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
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
