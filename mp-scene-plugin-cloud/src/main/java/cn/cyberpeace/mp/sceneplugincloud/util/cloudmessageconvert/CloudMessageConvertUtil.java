package cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Getter
@Setter
@Slf4j
public class CloudMessageConvertUtil {
    private static List<Convert> converts = new ArrayList<>();

    public static void init(String content) {
        try {
            converts.clear();
            List<Convert> result = JSON.parseArray(content, Convert.class);
            converts.addAll(result);
        } catch (Exception e) {
            log.error("failed to init cloud message convert,content is {}", content);
        }
    }

    public static String getMessage(String target, String... params) {
        String formatMessage = target.toLowerCase();
        for (var convert : converts) {
            if (convert.hit(formatMessage)) {
                return convert.getMessage(params);
            }
        }
        return null;
    }

    @Setter
    @Getter
    private static class Convert {
        private List<ConvertRule> rules;
        private List<Message> messages;


        public boolean hit(String target) {
            for (ConvertRule rule : rules) {
                if (!rule.hit(target)) {
                    return false;
                }
            }
            return true;
        }

        public String getMessage(String... params) {
            Locale locale = LocaleContextHolder.getLocale();
            for (Message message : messages) {
                if (locale.getLanguage().equals(message.locale)) {
                    return this.formatMessage(message.getMessage(), params);
                }
            }
            return null;
        }

        private String formatMessage(String message, String... params) {
            String formatMessage = message;
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    formatMessage = StringUtils.replaceOnce(formatMessage, "{" + i + "}", params[i]);
                }
            }
            return formatMessage;
        }
    }

    @Getter
    @Setter
    private static class Message {
        private String locale;
        private String message;
    }
}
