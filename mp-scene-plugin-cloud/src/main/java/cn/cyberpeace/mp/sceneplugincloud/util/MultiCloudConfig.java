package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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

@Slf4j
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
        String multiCloudInfo = cloudAppConfig.getMultiCloudInfo();
        if (StringUtils.isEmpty(multiCloudInfo)) {
            log.error("failed to read multi cloud info !");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        String[] cloudInfoList = multiCloudInfo.split(",");
        for (String cloudInfo : cloudInfoList) {
            String[] keyAndIp = cloudInfo.split("-");
            String key = keyAndIp[0];
            String ip = keyAndIp[1];
            cloudConfigs.put(key, new CloudConfig(ip, 11111));
        }
        for (String key : cloudConfigs.keySet()) {
            log.info("find cloud info, {} -> {}", key, cloudConfigs.get(key).getServerUrl());
        }
    }

    @Slf4j
    @Getter
    @Setter
    private static class CloudConfig {
        private String serverUrl;

        private CloudHttpClient cloudHttpClient;

        private String externalNetId;

        private String externalNetCidr;

        private String mgmtNetId;

        public CloudConfig(String server, Integer port) {
            this.serverUrl = "http://" + server + ":" + port;
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
