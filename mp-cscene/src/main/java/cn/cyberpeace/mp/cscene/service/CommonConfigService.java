package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.FileConfigConstant;
import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data
public class CommonConfigService {

    private static volatile boolean LOADED = false;

    private final static Map<String, String> CONFIG_MAP = new HashMap<>();

    public ServiceResult<Map<String, Object>> getSystemTypeToAgentSupport() {
        this.init();
        String result = CONFIG_MAP.get(FileConfigConstant.SYSTEM_TYPE_TO_AGENT_SUPPORT);
        if (result != null) {
            var map = JsonCommon.parseMap(result);
            return new ServiceResult<>(map);
        }
        return new ServiceResult<>(new HashMap<>());
    }

    public ServiceResult<String> getCloudMessageConvert() {
        this.init();
        if (CONFIG_MAP.containsKey(FileConfigConstant.CLOUD_MESSAGE_CONVERT)) {
            return new ServiceResult<>(CONFIG_MAP.get(FileConfigConstant.CLOUD_MESSAGE_CONVERT));
        }
        return new ServiceResult<>(StringUtils.EMPTY);
    }

    public synchronized void init() {
        if (LOADED) {
            return;
        } else {
            for (String config : FileConfigConstant.CONFIGS) {
                try {
                    File file = ResourceUtils.getFile("classpath:config/" + config + FileConfigConstant.CONFIG_PATH_SUFFIX);
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
