package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.google.common.io.CharStreams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Data
public class CloudCommonConfigService {

    private static String CLOUD_MESSAGE_CONVERT = "cloud_message_convert.json";

    private static String SYSTEM_TYPE_TO_AGENT_SUPPORT = "system_type_to_agent_support.json";

    private static String FRAME_INSTALL_CMDS = "install_frame.sh";

    private static String[] CONFIGS = new String[]{ CLOUD_MESSAGE_CONVERT, SYSTEM_TYPE_TO_AGENT_SUPPORT, FRAME_INSTALL_CMDS};

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
                    Resource resource = new ClassPathResource("config/" + config);
                    String content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
                    CONFIG_MAP.put(config, content);
                } catch (Exception e) {
                    log.error("failed to read config : {}", config, e);
                }
            }
            LOADED = true;
        }
    }

    public List<String> getInstallFrameCmds(String frameInstallResourceUrl) {
        this.init();
        String content = CONFIG_MAP.get(FRAME_INSTALL_CMDS);
        content = content.replace("${frame_install_resource_url}", frameInstallResourceUrl);
        String[] cmds = content.split("\n");
        List<String> list = new ArrayList<>();
        for (var cmd : cmds) {
            list.add(cmd);
        }
        return list;
    }
}
