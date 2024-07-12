 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhLogProbeLocationConfig.java.
 // File size 1365.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName WazuhLogProbeLocationConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class WazuhLogProbeLocationConfig {
    private static List<ProbeLocation> locations;

    private static volatile boolean isInit = false;

    /**
     * init
     */
    public static synchronized void init() {
        if (isInit) {
            return;
        }
        locations = JsonCommon.snakeParse(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.LOG_PROBE_LOCATIONS),
                new TypeReference<List<ProbeLocation>>() {
                });
        isInit = true;
    }

    /**
     * getLocations
     *
     * @return
     */
    public List<ProbeLocation> getLocations() {
        return locations;
    }

    @Data
    public static class ProbeLocation {
        private String probeId;
        private String logFormat;
        private List<String> location;
        private String osType;
    }
}
