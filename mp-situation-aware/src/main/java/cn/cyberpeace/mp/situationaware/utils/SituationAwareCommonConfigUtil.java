package cn.cyberpeace.mp.situationaware.utils;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SituationAwareCommonConfigUtil {
    private SituationAwareCommonConfigUtil() {

    }

    private static final String CONFIG_PATH_SUFFIX = ".json";

    public static final String BIZ_SRV_MONITORS = "biz_srv_monitors";

    public static final String CP_SRV_MONITORS = "cp_srv_monitors";

    private static final String[] CONFIGS = new String[]{BIZ_SRV_MONITORS, CP_SRV_MONITORS};

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
                log.error("failed to read config : {}", config, e);
            }
        }
        LOADED = true;
    }
}
