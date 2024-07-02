package cn.cyberpeace.mp.base.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @ClassName MessageCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class MessageCommon {

    private static MessageSource messageSource;

    /**
     * MessageCommon
     *
     * @param messageSource
     */
    @SuppressWarnings({"java:S1144", "java:S3010"})
    private MessageCommon(MessageSource messageSource) {
        MessageCommon.messageSource = messageSource;
    }

    /**
     * get
     *
     * @param msgKey
     * @return
     */
    public static String get(String msgKey) {
        try {
            return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("Get message by key `{}` on locale {} failed: {}", msgKey, LocaleContextHolder.getLocale(),
                    e.getMessage());
            return msgKey;
        }
    }

    /**
     * get
     *
     * @param msgKey
     * @param params
     * @return
     */
    public static String get(String msgKey, String... params) {
        try {
            return messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("Get message by key `{}` params `{}` on locale {} failed: {}", msgKey, params,
                    LocaleContextHolder.getLocale(), e.getMessage());
            return msgKey;
        }
    }

    /**
     * getOrDefault
     *
     * @param msgKey
     * @param defaultMsg
     * @param params
     * @return
     */
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
