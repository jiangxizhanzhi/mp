package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.sceneplugincollection.service.CollectionCommonConfigService;
import com.alibaba.fastjson.JSON;
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

    public static synchronized void init() {
        if (isInit) {
            return;
        }
        locations = JSON.parseArray(CollectionCommonConfigService.getConfigString(CollectionCommonConfigService.LOG_PROBE_LOCATIONS).getSource(), ProbeLocation.class);
        isInit = true;
    }

    public List<ProbeLocation> getLocations() {
        if (!isInit) {
            init();
        }
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
