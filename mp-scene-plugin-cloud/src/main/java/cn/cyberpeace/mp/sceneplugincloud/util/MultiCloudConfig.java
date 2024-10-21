package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualInternet;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    public static String getExternalNetId(String key) {
        return cloudConfigs.get(key).getExternalNetId();
    }

    public static String getExternalNetCidr(String key) {
        return cloudConfigs.get(key).getExternalNetCidr();
    }

    @PostConstruct
    public void init() {
        cloudConfigs.put("default", new CloudConfig("10.100.27.1", 11111));
        cloudConfigs.put("a1", new CloudConfig("10.4.1.36", 11111));
        cloudConfigs.put("a2", new CloudConfig("10.4.1.37", 11111));
    }

    @Getter
    @Setter
    private static class CloudConfig {
        private CloudHttpClient cloudHttpClient;

        private String externalNetId;

        private String externalNetCidr;

        public CloudConfig(String server, Integer port) {
            String serverUrl = "http://" + server + ":" + port;
            this.cloudHttpClient = new CloudHttpClient(serverUrl);
            this.externalNetId = new VirtualInternet(cloudHttpClient).create().getExternalNetId();
            this.externalNetCidr = new Network(cloudHttpClient).get(this.externalNetId).getSubnet().getCidr();
        }
    }
}
