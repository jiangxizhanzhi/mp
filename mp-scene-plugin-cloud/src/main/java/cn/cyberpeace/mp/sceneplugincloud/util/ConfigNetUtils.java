package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigNetworkMapper;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigNetUtils {

    private static String startCidrNet;

    private static String endCidrNet;

    // 获取可用的配采网信息，包括cidr、sn router上的下一跳ip地址
    public static synchronized List<ConfigNetInfo> getAvailableConfigNetInfo(String sceneId, int totalTerminalNum) {
        // 从配置中查询多少台终端共用一个配采网
        var configNetGroupNum = BeanCommon.getBean(CloudAppConfig.class).getConfigNetGroupNum();
        // 计算 totalTerminalNum 个终端需要多少个配采网
        int cidrNum = (int) Math.ceil(totalTerminalNum / configNetGroupNum);

        // 从数据库中查询已用的配采网cidr
        CloudPluginSceneConfigNetworkMapper mapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
        var list = mapper.list(new HumanCondition());
        List<String> usedCidrIps = new LinkedList<>();
        for (var m : list) {
            usedCidrIps.add(m.getCidrIp());
        }

        // 查询可用的配采网cidr
        List<String> cidrIps = getAvailableCidrIps(cidrNum, usedCidrIps);

        // 拼接结果
        List<ConfigNetInfo> result = new ArrayList<>();
        List<CloudPluginSceneConfigNetworkModel> models = new ArrayList<>();
        for (int i = 0; i < cidrNum; i++) {
            String cidrIp = cidrIps.get(i);

            ConfigNetInfo c = new ConfigNetInfo(cidrIp, configNetGroupNum);
            result.add(c);

            var model = createCloudPluginSceneConfigNetworkModel(sceneId, c);
            models.add(model);
        }
        // 把用掉的配采网和下一跳ip写入到数据库做占用记录
        mapper.bulkCreate(models);

        return new ArrayList<>(result);
    }

    private static CloudPluginSceneConfigNetworkModel createCloudPluginSceneConfigNetworkModel(String sceneId, ConfigNetInfo c) {
        CloudPluginSceneConfigNetworkModel model = new CloudPluginSceneConfigNetworkModel();
        model.setCidrIpLong(c.getCidrIpLong());
        model.setCidrIp(c.getCidrIp());
        model.setSnRouterId(BeanCommon.getBean(CloudAppConfig.class).getSnRouterId());
        model.setNextHopIp(c.getNextHop());
        model.setCidr(c.getCidr());
        model.setGatewayIp(c.getGatewayIp());
        model.setScene(sceneId);
        return model;
    }

    private static List<String> getAvailableCidrIps(int require, List<String> usedCidrIps) {
        if (startCidrNet == null) {
            CloudAppConfig cloudAppConfig = BeanCommon.getBean(CloudAppConfig.class);
            startCidrNet = cloudAppConfig.getConfigNetStartCidr();
            endCidrNet = cloudAppConfig.getConfigNetEndCidr();
        }
        List<String> result = new LinkedList<>();
        long startIp = ip2Long(startCidrNet);
        long endIp = ip2Long(endCidrNet);
        for (long i = startIp; i <= endIp; i += 256) {
            result.add(longToIp(i));
        }
        result.removeAll(usedCidrIps);
        if (result.size() < require) {
            throw new MpRuntimeException("无可用的配采网 网段");
        }
        return result.subList(0, require);
    }

    private static Long ip2Long(String baseIp) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(baseIp);
            byte[] ip = address.getAddress();
            long baseIpLong = ((ip[0] & 0xFFL) << 24) | ((ip[1] & 0xFFL) << 16) | ((ip[2] & 0xFFL) << 8) | (ip[3] & 0xFFL);
            return baseIpLong;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String longToIp(long ipLong) {
        return ((ipLong >> 24) & 0xFF) + "." + ((ipLong >> 16) & 0xFF) + "." + ((ipLong >> 8) & 0xFF) + "." + (ipLong & 0xFF);
    }

    @Getter
    @Setter
    public static class ConfigNetInfo {
        private String cidr;
        private String cidrIp;
        private Long cidrIpLong;
        private String gatewayIp;
        private String nextHop;
        private List<String> ipPool;

        public ConfigNetInfo(String cidrIp, int configNetGroupNum) {
            this.cidrIp = cidrIp;
            this.cidr = cidrIp + "/24";
            this.cidrIpLong = ip2Long(cidrIp);
            this.gatewayIp = longToIp(this.cidrIpLong + 254);
            this.nextHop = gatewayIp;

            long startIp = ip2Long(cidrIp) + 2;
            this.ipPool = new ArrayList<>();
            for (int i = 0; i < configNetGroupNum; i++) {
                this.ipPool.add(longToIp(startIp + i));
            }
        }
    }
}
