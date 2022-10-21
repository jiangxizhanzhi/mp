package cn.cyberpeace.mp.collection.util;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CollectionCommonConfigUtil {
    private CollectionCommonConfigUtil() {

    }

    private static final String CONFIG_PATH_SUFFIX = ".json";

    public static final String INTERNATIONAL_MESSAGE = "international_message";

    private static final String[] CONFIGS = new String[]{INTERNATIONAL_MESSAGE};

    private static volatile boolean LOADED = false;

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    public static String getConfigString(String config) {
        init();
        if (CONFIG_MAP.containsKey(config)) {
            return CONFIG_MAP.get(config);
        }
        return StringUtils.EMPTY;
    }

    private static synchronized void init() {
        if (LOADED) {
            return;
        }
        for (String config : CONFIGS) {
            try {
                Resource resource = new ClassPathResource("config/" + config + CONFIG_PATH_SUFFIX);
                String content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
                CONFIG_MAP.put(config, content);
            } catch (Exception e) {
                log.error("failed to read config : {}, {}", config, ExceptionUtils.getStackTrace(e));
            }
        }
        LOADED = true;
    }
}
