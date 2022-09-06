package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.google.common.io.CharStreams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data
public class CollectionCommonConfigService {
    private CollectionCommonConfigService(){

    }

    private static final String CONFIG_PATH_SUFFIX = ".json";

    public static final String DEFAULT_CUSTOM_PROBES = "default_custom_probes";

    public static final String LOG_PROBE_LOCATIONS = "log_probe_locations";

    private static final String[] CONFIGS = new String[]{DEFAULT_CUSTOM_PROBES, LOG_PROBE_LOCATIONS};

    private static volatile boolean LOADED = false;

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    public static ServiceResult<String> getConfigString(String config) {
        init();
        if (CONFIG_MAP.containsKey(config)) {
            return new ServiceResult<>(CONFIG_MAP.get(config));
        }
        return new ServiceResult<>(StringUtils.EMPTY);
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
