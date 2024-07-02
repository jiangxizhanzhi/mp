package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @ClassName WazuhModifyEventConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class WazuhModifyEventConfig {
    private static ModifyProbe modifyProbe;

    private static volatile boolean isInit = false;

    /**
     * init
     */
    public static synchronized void init() {
        if (isInit) {
            return;
        }
        modifyProbe = JsonCommon.snakeParse(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.MODIFY_PROBES),
                ModifyProbe.class);
        isInit = true;
    }

    /**
     * getModifyProbe
     *
     * @return
     */
    public ModifyProbe getModifyProbe() {
        return modifyProbe;
    }

    @Data
    public static class ModifyProbe {
        private WazuhProbeConfig.DefaultProbe.NewProbe fileProbe;

        private WazuhProbeConfig.DefaultProbe.NewProbe registryProbe;
    }

    public static class ProbeType {
        /**
         * ProbeType
         */
        private ProbeType() {

        }

        public static final int FILE = 0;
        public static final int REGISTRY = 1;
    }
}
