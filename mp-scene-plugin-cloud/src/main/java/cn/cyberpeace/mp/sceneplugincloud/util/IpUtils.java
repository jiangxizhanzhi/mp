package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.util.RandomUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.hutool.core.net.Ipv4Util;

import java.util.ArrayList;
import java.util.List;

public class IpUtils {

    private IpUtils() {}

    /**
     * 随机网段 0/24
     * @param allCidrs cidr池
     * @param count 随机数量
     * @param excludeCidrs 排除的网段
     * @return 随机网段列表
     */
    public static List<String> randomCidrs(List<String> allCidrs, int count, List<String> excludeCidrs) {
        List<String> cidrPool;
        if (excludeCidrs == null) {
            cidrPool = allCidrs;
        } else {
            cidrPool = new ArrayList<>(allCidrs);
            cidrPool.removeAll(excludeCidrs);
        }

        return RandomUtils.sample(cidrPool, count);
    }

    /**
     * 随机ip
     * @param cidr 网段
     * @param count 随机数量
     * @param excludeIps 排除的ip
     * @param allocationPools 地址池
     * @return 随机ip列表
     */
    public static List<String> randomIps(String cidr, int count, List<String> excludeIps,
                                         List<AllocationPool> allocationPools) {
        List<String> ips = getCidrIps(cidr);
        if (!ips.isEmpty()) {
            ips.remove(0);
        }
        if (!ips.isEmpty()) {
            ips.remove(ips.size() - 1);
        }

        if (excludeIps != null && !excludeIps.isEmpty()) {
            ips.removeAll(excludeIps);
        }

        if (allocationPools != null && !allocationPools.isEmpty()) {
            List<String> allocationPoolsIps = getAllocationPoolsIps(allocationPools, true);
            ips.retainAll(allocationPoolsIps);
        }

        return RandomUtils.sample(ips, count);
    }

    /**
     * 获取cidr段可用ip列表
     * @param cidr cidr
     * @return ip列表
     */
    public static List<String> getCidrIps(String cidr) {
        return Ipv4Util.list(cidr, true);
    }

    /**
     * 获取范围可用ip列表
     * @param ipFrom 开始ip
     * @param ipTo 结束ip
     * @return ip列表
     */
    public static List<String> getRangeIps(String ipFrom, String ipTo) {
        return Ipv4Util.list(ipFrom, ipTo);
    }

    /**
     * 获取地址池ips列表
     * @param allocationPools 地址池
     * @return ips列表
     */
    public static List<String> getAllocationPoolsIps(List<AllocationPool> allocationPools, boolean abandonDhcp) {
        List<String> ips = new ArrayList<>();
        if (allocationPools == null) {
            return ips;
        }

        for (var allocationPool : allocationPools) {
            List<String> allocationPoolIps = getRangeIps(allocationPool.getStart(), allocationPool.getEnd());
            if (abandonDhcp && !allocationPoolIps.isEmpty()) {
                allocationPoolIps.remove(0);
            }
            ips.addAll(allocationPoolIps);
        }

        return ips;
    }
}
