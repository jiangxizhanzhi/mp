package cn.cyberpeace.mp.situationaware.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 国际化转换工具类
 */
@Component
public class MessageCommonUtil {
    private MessageCommonUtil() {

    }

    private static List<String> keys;
    private static Map<String, String> zhMap;
    private static Map<String, String> enMap;

    @PostConstruct
    public void init() {
        List<Message> messages = JSON.parseArray(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.INTERNATIONAL_MESSAGE), Message.class);
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        keys = new ArrayList<>();
        zhMap = new ConcurrentHashMap<>();
        enMap = new ConcurrentHashMap<>();
        for (var message : messages) {
            keys.add(message.getKey());
            zhMap.put(message.getKey(), message.getZhName());
            enMap.put(message.getKey(), message.getEnName());
        }
    }

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

    @Data
    public static class Message {
        private String key;
        private String zhName;
        private String enName;
    }
}
