package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualInternet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
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
    private static boolean multiEnable = false;

    public static final String DEFAULT = "default";

    private static final Map<String, CloudConfig> cloudConfigs = new ConcurrentHashMap<>();

    @Resource
    private CloudAppConfig cloudAppConfig;

    public static CloudHttpClient get(String key) {
        if (!multiEnable) {
            // 未开启多云 则无论传什么key都返回默认的云
            return cloudConfigs.get(DEFAULT).getCloudHttpClient();
        } else {
            if (key == null) {
                key = DEFAULT;
            }
            return cloudConfigs.get(key).getCloudHttpClient();
        }
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
            multiEnable = false;
            cloudConfigs.put(DEFAULT, new CloudConfig(cloudAppConfig.getCpcloudServer()));
        } else {
            multiEnable = true;
            String[] cloudInfoList = multiCloudInfo.split(",");
            for (String cloudInfo : cloudInfoList) {
                String[] keyAndIp = cloudInfo.split("-");
                String key = keyAndIp[0];
                String server = keyAndIp[1];
                cloudConfigs.put(key, new CloudConfig(server));
            }
        }
        log.info("multi cloud is {}", multiEnable ? "enable" : "disable");
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

        public CloudConfig(String server) {
            this.serverUrl = server;
            this.cloudHttpClient = new CloudHttpClient(serverUrl);
        }

        public synchronized String getExternalNetId() {
            if (!StringUtils.isEmpty(externalNetId)) {
                return externalNetId;
            }

            try {
                externalNetId = new VirtualInternet(cloudHttpClient).create().getExternalNetId();
            } catch (Exception e) {
                log.error("Try to get external net failed: ", e);
                throw e;
            }

            return externalNetId;
        }

        public synchronized String getExternalNetCidr() {
            if (!StringUtils.isEmpty(externalNetCidr)) {
                return externalNetCidr;
            }

            var netId = getExternalNetId();
            if (StringUtils.isEmpty(netId)) {
                return null;
            }

            try {
                externalNetCidr = new Network(cloudHttpClient).get(netId).getSubnet().getCidr();
            } catch (Exception e) {
                log.error("Try to get external net cidr failed: ", e);
                throw e;
            }

            return externalNetCidr;
        }

        public synchronized String getMgmtNetId() {
            if (!StringUtils.isEmpty(mgmtNetId)) {
                return mgmtNetId;
            }

            String mgmtName = CloudSingleton.cloudAppConfig.getMgmtName();
            NetworkData networkData;
            try {
                networkData = new Network(cloudHttpClient).getByName(mgmtName);
            } catch (Exception e) {
                log.error("Try to get mgmt net failed: ", e);
                throw e;
            }

            if (networkData != null) {
                mgmtNetId = networkData.getId();
            } else {
                var message = String.format("Unable to find mgmt network with name `%s`", mgmtName);
                log.error("Try to get mgmt net failed: {}", message);
                throw new MpRuntimeException(message);
            }

            return mgmtNetId;
        }
    }
}
