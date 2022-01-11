package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.RandomUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.hutool.core.net.Ipv4Util;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
     * @param abandonDhcp 是否去除dhcp地址
     * @return 随机ip列表
     */
    public static List<String> randomIps(String cidr, int count, List<String> excludeIps,
                                         List<AllocationPool> allocationPools, boolean abandonDhcp) {
        List<String> ips = getAvailableCidrIps(cidr);

        // dhcp情况，默认地址池
        if (abandonDhcp && (allocationPools == null || allocationPools.isEmpty())) {
            allocationPools = List.of(new AllocationPool(ips.get(0), ips.get(ips.size() - 1)));
        }
        // 和地址池求交集
        if (allocationPools != null && !allocationPools.isEmpty()) {
            List<String> allocationPoolsIps = getAllocationPoolsIps(allocationPools, abandonDhcp);
            ips.retainAll(allocationPoolsIps);
        }

        // 移除指定过滤的ip
        if (excludeIps != null && !excludeIps.isEmpty()) {
            ips.removeAll(excludeIps);
        }

        return RandomUtils.sample(ips, count);
    }

    /**
     * 分配默认路由ip，默认路由ip有特殊限制，必须在地址池外
     * @param cidr 网段
     * @param count 随机数量
     * @param excludeIps 排除的ip
     * @param allocationPools 地址池
     * @return 随机ip列表
     */
    public static List<String> allocateDefaultRouterIps(String cidr, int count, List<String> excludeIps,
                                                        List<AllocationPool> allocationPools) {
        List<String> ips = getAvailableCidrIps(cidr);

        if (allocationPools != null && !allocationPools.isEmpty()) {
            // 包含dhcp地址以去除
            List<String> allocationPoolsIps = getAllocationPoolsIps(allocationPools, false);
            // 和地址池求差集
            ips.removeAll(allocationPoolsIps);
        }

        // 移除指定过滤的ip
        if (excludeIps != null && !excludeIps.isEmpty()) {
            ips.removeAll(excludeIps);
        }

        if (ips.size() < count) {
            throw new MpRuntimeException("No enough resource for default router address pick");
        }

        return ips.subList(ips.size() - count, ips.size());
    }

    /**
     * 获取cidr段可用ip列表
     * @param cidr cidr
     * @return ip列表
     */
    public static List<String> getAvailableCidrIps(String cidr) {
        List<String> ips = getCidrIps(cidr);
        // 移除第一个网络自身地址
        if (!ips.isEmpty()) {
            ips.remove(0);
        }
        // 移除最后一个地址
        if (!ips.isEmpty()) {
            ips.remove(ips.size() - 1);
        }
        return ips;
    }

    /**
     * 获取cidr段ip列表
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
                // 移除第一个默认的dhcp地址
                allocationPoolIps.remove(0);
            }
            ips.addAll(allocationPoolIps);
        }

        return ips;
    }

    /**
     * 获取ip地址的下一个地址
     * @param ip ip地址
     * @return 下一个ip地址
     */
    public static String nextIp(String ip) {
        var ipLong = Ipv4Util.ipv4ToLong(ip);
        var nextIpLong = ipLong + 1L;
        String nextIp = null;
        try {
            nextIp = Ipv4Util.longToIpv4(nextIpLong);
        } catch (Exception e) {
            log.warn("Get ip({}) next ip error: ", ip, e);
        }
        return nextIp;
    }

    /**
     * 判断ip是否在cidr内
     * @param ip ip地址
     * @param cidr cidr
     * @return ip是否在cidr内
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        return getCidrIps(cidr).contains(ip);
    }

}
