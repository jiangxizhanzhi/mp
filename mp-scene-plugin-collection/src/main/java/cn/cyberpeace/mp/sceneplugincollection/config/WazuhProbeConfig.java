package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 默认自定义探针配置类
 * @Author： hewei
 * @Date： 2022.3.23 15:49
 */
@Component
public class WazuhProbeConfig {
    private static DefaultProbe defaultProbe;

    private static volatile boolean isInit = false;

    public static synchronized void init() {
        if (isInit) {
            return;
        }
        defaultProbe = JsonCommon.snakeParse(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.DEFAULT_CUSTOM_PROBES),
                DefaultProbe.class);
        isInit = true;
    }

    public DefaultProbe getDefaultProbe() {
        return defaultProbe;
    }

    @Data
    public static class DefaultProbe {
        private String defaultInterval;
        private List<NewProbe> probes;

        @Data
        public static class NewProbe {
            private String probeName;
            private String id;
            @ApiModelProperty(value = "上报间隔")
            private String interval;
            @ApiModelProperty(value = "输出格式json、文本")
            private String outputFormat;
        }
    }
}
