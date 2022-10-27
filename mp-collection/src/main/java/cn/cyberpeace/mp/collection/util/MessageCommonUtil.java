package cn.cyberpeace.mp.collection.util;

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

    private static List<String> names;
    private static Map<String, String> zhMap;
    private static Map<String, String> enMap;

    @PostConstruct
    public void init() {
        List<Message> messages = JSON.parseArray(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.INTERNATIONAL_MESSAGE), Message.class);
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        names = new ArrayList<>();
        zhMap = new ConcurrentHashMap<>();
        enMap = new ConcurrentHashMap<>();
        for (var message : messages) {
            names.add(message.getName());
            zhMap.put(message.getName(), message.getZhName());
            enMap.put(message.getName(), message.getEnName());
        }
    }

    public static String convertMessage(String name) {
        if (!names.contains(name)) {
            return name;
        }
        // 暂时只返回中文
        return zhMap.get(name);
//        if (LocaleContextHolder.getLocale().getLanguage().equals("zh")) {
//            return zhMap.get(name);
//        } else {
//            return enMap.get(name);
//        }
    }

    @Data
    public static class Message {
        private String name;
        private String zhName;
        private String enName;
    }
}
