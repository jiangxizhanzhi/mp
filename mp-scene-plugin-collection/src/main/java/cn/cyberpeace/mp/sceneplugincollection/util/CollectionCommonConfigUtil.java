 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionCommonConfigUtil.java.
 // File size 2226.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.util;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CollectionCommonConfigUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class CollectionCommonConfigUtil {
    /**
     * CollectionCommonConfigUtil
     */
    private CollectionCommonConfigUtil() {

    }

    private static final String CONFIG_PATH_SUFFIX = ".json";

    public static final String DEFAULT_CUSTOM_PROBES = "default_custom_probes";

    public static final String DEFAULT_BIZ_PROBES = "default_biz_probes";

    public static final String LOG_PROBE_LOCATIONS = "log_probe_locations";

    public static final String MODIFY_PROBES = "modify_probes";

    private static final String[] CONFIGS = new String[]{DEFAULT_CUSTOM_PROBES, LOG_PROBE_LOCATIONS, MODIFY_PROBES, DEFAULT_BIZ_PROBES};

    private static volatile boolean LOADED = false;

    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    /**
     * getConfigString
     *
     * @param config
     * @return
     */
    public static String getConfigString(String config) {
        init();
        if (CONFIG_MAP.containsKey(config)) {
            return CONFIG_MAP.get(config);
        }
        return StringUtils.EMPTY;
    }

    /**
     * init
     */
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
