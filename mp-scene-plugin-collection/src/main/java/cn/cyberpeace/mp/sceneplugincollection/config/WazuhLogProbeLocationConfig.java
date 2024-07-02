package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 日志采集地址配置类
 * @Author： hewei
 * @Date： 2022.8.3 18:57
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
