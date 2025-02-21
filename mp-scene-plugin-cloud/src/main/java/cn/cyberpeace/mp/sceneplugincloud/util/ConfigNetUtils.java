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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigNetUtils {

    private static String startCidrNet;

    private static String endCidrNet;

    private static String nextCidrNet;

    private static String snRouterId;

    private static String snRouterStartIp;

    private static String snRouterEndIp;

    private static List<String> getAvailableCidrIps(int require) {
        List<String> cidrIps = new ArrayList<>();
        int cycleTimes = 0;
        while (require > 0) {
            // 避免循环太久
            if (cycleTimes > 50) {
                throw new MpRuntimeException("无可用的配采网cidr");
            }

            // 按照顺序 从前往计算cidr ip
            Set<String> tmp = getOrderedCidrIps(require);
            if (tmp.size() == 0) {
                throw new MpRuntimeException("无可用的配采网cidr");
            }

            // 将计算出的cidr ip和数据库中已用的排重
            CloudPluginSceneConfigNetworkMapper networkMapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
            var list = networkMapper.list(new HumanCondition().filterIn(CloudPluginSceneConfigNetworkModel::getCidrIp, tmp.toArray(new String[]{})));
            for (var l : list) {
                tmp.remove(l.getCidrIp());
            }

            cidrIps.addAll(tmp);
            require = require - tmp.size();
            cycleTimes++;
        }
        return cidrIps;
    }

    // 获取可用的配采网信息，包括cidr、sn router上的下一跳ip地址
    public static synchronized List<ConfigNetInfo> getAvailableConfigNetInfo(String sceneId, int totalTerminalNum) {
        initNextCidrNet();
        var configNetGroupNum = BeanCommon.getBean(CloudAppConfig.class).getConfigNetGroupNum();
        int cidrNum = (int) Math.ceil(totalTerminalNum / configNetGroupNum);
        List<String> cidrIps = getAvailableCidrIps(cidrNum);
        List<String> nextHops = getAvailableNextHops(cidrNum);

        List<CloudPluginSceneConfigNetworkModel> models = new ArrayList<>();
        List<ConfigNetInfo> result = new ArrayList<>();
        for (int i = 0; i < cidrNum; i++) {
            String cidrIp = cidrIps.get(i);
            String nextHop = nextHops.get(i);

            ConfigNetInfo c = new ConfigNetInfo(cidrIp, nextHop);
            result.add(c);

            CloudPluginSceneConfigNetworkModel model = new CloudPluginSceneConfigNetworkModel();
            model.setCidrIpLong(c.getCidrIpLong());
            model.setCidrIp(c.getCidrIp());
            model.setSnRouterId(snRouterId);
            model.setNextHopIp(c.getNextHop());
            model.setCidr(c.getCidr());
            model.setGatewayIp(c.getGatewayIp());
            model.setScene(sceneId);
            models.add(model);
        }
        CloudPluginSceneConfigNetworkMapper mapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
        mapper.bulkCreate(models);

        return new ArrayList<>(result);
    }

    public static Set<String> check(Set<String> cidrIps) {
        CloudPluginSceneConfigNetworkMapper networkMapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
        var list = networkMapper.list(new HumanCondition().filterIn(CloudPluginSceneConfigNetworkModel::getCidrIp, cidrIps.toArray(new String[]{})));
        for (var l : list) {
            cidrIps.remove(l.getCidrIp());
        }
        return cidrIps;
    }

    private static void initNextCidrNet() {
        if (nextCidrNet != null) {
            return;
        }

        CloudAppConfig cloudAppConfig = BeanCommon.getBean(CloudAppConfig.class);
        startCidrNet = cloudAppConfig.getConfigNetStartCidr();
        endCidrNet = cloudAppConfig.getConfigNetEndCidr();

        // 从数据库中查询使用中的最大的cidr
        var mapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
        var dbUsedMaxCidr = mapper.first(new HumanCondition().orderBy(CloudPluginSceneConfigNetworkModel::getCidrIpLong, true));
        if (dbUsedMaxCidr == null) {
            nextCidrNet = startCidrNet;
        } else {
            String usedMaxCidr = dbUsedMaxCidr.getCidr();
            String userMaxCidrNet = usedMaxCidr.split("/")[0];
            // 初始化 nextCidrNet
            // 取 startCidr 、 usedMaxCidr 中的最大值
            long startCidrLong = ip2Long(startCidrNet);
            long usedMaxCidrLong = ip2Long(userMaxCidrNet);
            long endCidrLong = ip2Long(endCidrNet);
            if (usedMaxCidrLong >= endCidrLong) {
                nextCidrNet = startCidrNet;
            } else if (usedMaxCidrLong >= startCidrLong) {
                nextCidrNet = longToIp(usedMaxCidrLong + 256);
            } else {
                nextCidrNet = startCidrNet;
            }
        }
    }

    private static List<String> getAvailableNextHops(int require) {
        if (snRouterStartIp == null) {
            CloudAppConfig cloudAppConfig = BeanCommon.getBean(CloudAppConfig.class);
            snRouterStartIp = cloudAppConfig.getSnRouterStartIp();
            snRouterEndIp = cloudAppConfig.getSnRouterEndIp();
            snRouterId = cloudAppConfig.getSnRouterNetId();
        }

        // 从数据库中查询使用中的ip
        var mapper = BeanCommon.getBean(CloudPluginSceneConfigNetworkMapper.class);
        var dbUsedNextHop = mapper.list(new HumanCondition());
        Set<String> usedNextHop = new HashSet<>();
        for (CloudPluginSceneConfigNetworkModel d : dbUsedNextHop) {
            usedNextHop.add(d.getNextHopIp());
        }
        Set<String> result = new HashSet<>();
        long startIp = ip2Long(snRouterStartIp);
        long endIp = ip2Long(snRouterEndIp);
        for (long i = startIp; i <= endIp; i++) {
            result.add(longToIp(i));
        }
        result.removeAll(usedNextHop);
        if (result.size() < require) {
            throw new MpRuntimeException("无可用的配采网 下一跳地址");
        }
        return new ArrayList<>(result).subList(0, require);
    }

    private static Set<String> getOrderedCidrIps(int num) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < num; i++) {
            long oneCidrIpLong = ip2Long(nextCidrNet);
            result.add(longToIp(oneCidrIpLong));
            long nextCidrIpLong = oneCidrIpLong + 256;
            if (nextCidrIpLong > ip2Long(endCidrNet)) {
                nextCidrNet = startCidrNet;
            } else {
                nextCidrNet = longToIp(nextCidrIpLong);
            }
            if (result.size() == num) {
                return result;
            }
        }
        throw new MpRuntimeException("no available cidr");
    }

    public static List<String> getIpPool(String cidrIp, int start, int num) {
        long startIp = ip2Long(cidrIp) + start;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            result.add(longToIp(startIp + i));
        }
        return result;
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

        public ConfigNetInfo(String cidrIp, String nextHop) {
            this.cidrIp = cidrIp;
            this.cidr = cidrIp + "/24";
            this.cidrIpLong = ip2Long(cidrIp);
            this.gatewayIp = longToIp(this.cidrIpLong + 254);
            this.nextHop = nextHop;
        }
    }
}
