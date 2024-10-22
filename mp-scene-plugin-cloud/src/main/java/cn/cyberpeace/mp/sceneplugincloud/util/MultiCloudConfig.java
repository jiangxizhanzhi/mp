package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualInternet;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MultiCloudConfig {
    public static final String DEFAULT = "default";

    private static final Map<String, CloudConfig> cloudConfigs = new ConcurrentHashMap<>();

    @Resource
    private CloudAppConfig cloudAppConfig;

    public static CloudHttpClient get(String key) {
        return cloudConfigs.get(key).getCloudHttpClient();
    }

    public static String getMgmtNetId(String key) {
        return cloudConfigs.get(key).getMgmtNetId();
    }

    public static String getExternalNetId(String key) {
        return cloudConfigs.get(key).getExternalNetId();
    }

    public static String getExternalNetCidr(String key) {
        return cloudConfigs.get(key).getExternalNetCidr();
    }

    public static List<CloudHttpClient> getAll() {
        List<CloudHttpClient> cloudHttpClients = new ArrayList<>();
        for (CloudConfig c : cloudConfigs.values()) {
            cloudHttpClients.add(c.getCloudHttpClient());
        }
        return cloudHttpClients;
    }

    @PostConstruct
    public void init() {
        cloudConfigs.put("default", new CloudConfig("10.100.27.1", 11111));
        cloudConfigs.put("a1", new CloudConfig("10.4.1.36", 11111));
        cloudConfigs.put("a2", new CloudConfig("10.4.1.37", 11111));
    }

    @Slf4j
    @Getter
    @Setter
    private static class CloudConfig {
        private CloudHttpClient cloudHttpClient;

        private String externalNetId;

        private String externalNetCidr;

        private String mgmtNetId;

        public CloudConfig(String server, Integer port) {
            String serverUrl = "http://" + server + ":" + port;
            this.cloudHttpClient = new CloudHttpClient(serverUrl);
            this.externalNetId = new VirtualInternet(cloudHttpClient).create().getExternalNetId();
            this.externalNetCidr = new Network(cloudHttpClient).get(this.externalNetId).getSubnet().getCidr();
            String mgmtName = CloudSingleton.cloudAppConfig.getMgmtName();
            if (StringUtils.isNotEmpty(mgmtName)) {
                try {
                    this.mgmtNetId = new Network(cloudHttpClient).getByName(mgmtName).getId();
                } catch (Exception e) {
                    log.error("Try to get mgmt net failed: ", e);
                }
            }
        }
    }
}
