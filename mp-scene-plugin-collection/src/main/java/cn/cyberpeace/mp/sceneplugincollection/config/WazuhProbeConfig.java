 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhProbeConfig.java.
 // File size 1407.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName WazuhProbeConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class WazuhProbeConfig {
    private static DefaultProbe defaultProbe;

    private static volatile boolean isInit = false;

    /**
     * init
     */
    public static synchronized void init() {
        if (isInit) {
            return;
        }
        defaultProbe = JsonCommon.snakeParse(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.DEFAULT_CUSTOM_PROBES),
                DefaultProbe.class);
        isInit = true;
    }

    /**
     * getDefaultProbe
     *
     * @return
     */
    public DefaultProbe getDefaultProbe() {
        var list = LocalFileMappingCommon.getMappings("wazuh_agent_installer");
        for (var l : list) {
            if (StringUtils.equals(l.getName(), "default_custom_probes.json")) {
                String content = FsCommon.downloadContent(l.getRemoteFile());
                return JsonCommon.snakeParse(content, DefaultProbe.class);
            }
        }
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
            private String interval;
            private String outputFormat;
        }
    }
}
