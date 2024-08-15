package cn.cyberpeace.mp.sceneplugincollection.util;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    // 默认自定义探针
    public static final String DEFAULT_CUSTOM_PROBES = "default_custom_probes";

    // 默认通用探针
    public static final String DEFAULT_BIZ_PROBES = "default_biz_probes";

    // 日志文件采集探针
    public static final String LOG_PROBE_LOCATIONS = "log_probe_locations";

    // 文件修改探针
    public static final String MODIFY_PROBES = "modify_probes";

    private static final String[] CONFIGS = new String[]{DEFAULT_CUSTOM_PROBES, LOG_PROBE_LOCATIONS, MODIFY_PROBES, DEFAULT_BIZ_PROBES};

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
