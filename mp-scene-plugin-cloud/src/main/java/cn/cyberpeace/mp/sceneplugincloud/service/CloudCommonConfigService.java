package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data
public class CloudCommonConfigService {

    private static String CONFIG_PATH_SUFFIX = ".json";

    private static String CLOUD_MESSAGE_CONVERT = "cloud_message_convert";

    private static String SYSTEM_TYPE_TO_AGENT_SUPPORT = "system_type_to_agent_support";

    private static String[] CONFIGS = new String[]{ CLOUD_MESSAGE_CONVERT, SYSTEM_TYPE_TO_AGENT_SUPPORT };

    private static volatile boolean LOADED = false;

    private final static Map<String, String> CONFIG_MAP = new HashMap<>();

    public ServiceResult<Map<String, Object>> getSystemTypeToAgentSupport() {
        this.init();
        String result = CONFIG_MAP.get(SYSTEM_TYPE_TO_AGENT_SUPPORT);
        if (result != null) {
            var map = JsonCommon.parseMap(result);
            return new ServiceResult<>(map);
        }
        return new ServiceResult<>(new HashMap<>());
    }

    public ServiceResult<String> getCloudMessageConvert() {
        this.init();
        if (CONFIG_MAP.containsKey(CLOUD_MESSAGE_CONVERT)) {
            return new ServiceResult<>(CONFIG_MAP.get(CLOUD_MESSAGE_CONVERT));
        }
        return new ServiceResult<>(StringUtils.EMPTY);
    }

    private synchronized void init() {
        if (LOADED) {
            return;
        } else {
            for (String config : CONFIGS) {
                try {
                    File file = ResourceUtils.getFile("classpath:config/" + config + CONFIG_PATH_SUFFIX);
                    String content = FileUtil.readString(file, "utf8");
                    CONFIG_MAP.put(config, content);
                } catch (Exception e) {
                    log.error("faild to read config : {}", config);
                }
            }
            LOADED = true;
        }
    }
}
