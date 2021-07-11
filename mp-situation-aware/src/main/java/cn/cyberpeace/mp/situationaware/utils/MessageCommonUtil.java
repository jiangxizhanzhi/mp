package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
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

    public static List<String> getProbeIdsByNameBaseLanguage(String name) {
        if (LocaleContextHolder.getLocale().getLanguage().equals("zh")) {
            return getProbeIdsByName(name, zhMap);
        }
        return getProbeIdsByName(name, enMap);
    }

    private static List<String> getProbeIdsByName(String name, Map<String, String> probes) {
        List<String> probeIds = new ArrayList<>();
        probes.forEach((key, probeName) -> {
            if (key.startsWith(SituationBase18nCode.SITUATION_AWARE_I18N_PROBE_ABILITY_NAME_PREFIX) && probeName.contains(name)) {
                probeIds.add(key.substring(SituationBase18nCode.SITUATION_AWARE_I18N_PROBE_ABILITY_NAME_PREFIX.length()));
            }
        });
        return probeIds;
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
