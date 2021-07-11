package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class WazuhLogProbeLocationConfig {
    private static List<ProbeLocation> locations;

    private static volatile boolean isInit = false;

    public static synchronized void init() {
        if (isInit) {
            return;
        }
        locations = JSON.parseArray(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.LOG_PROBE_LOCATIONS), ProbeLocation.class);
        isInit = true;
    }

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
