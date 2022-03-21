package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.RandomUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class IpUtils {

    private IpUtils() {
    }

    private static final Random Random = new Random();

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

    public static List<String> newRandomIps(String cidr, int count, List<String> excludeIps,
                                            List<AllocationPool> allocationPools, boolean abandonDhcp) {
        List<String> ips = new ArrayList<>();
        String beginIp = null;
        String lastIp = null;
        if (cidr.contains(Ipv4Util.IP_MASK_SPLIT_MARK)) {
            lastIp = IpUtils.lastIp(cidr);
            beginIp = IpUtils.beginIp(cidr);
        } else if (cidr.contains(Ipv4Util.IP_SPLIT_MARK)) {
            String[] newIps = StrUtil.split(cidr, Ipv4Util.IP_SPLIT_MARK);
            beginIp = newIps[0];
            lastIp = newIps[1];
        }
        long beginIpLong = Ipv4Util.ipv4ToLong(nextIp(beginIp));
        long size = Ipv4Util.ipv4ToLong(previousIp(lastIp)) - beginIpLong;

        // dhcp情况，默认地址池
        if (abandonDhcp && (allocationPools == null || allocationPools.isEmpty())) {
            allocationPools = List.of(new AllocationPool(nextIp(beginIp), previousIp(lastIp)));
            for (int i = 0; i < count; i++) {
                String randomIp = randomIp(beginIpLong, size);
                if (excludeIps.contains(randomIp)) {
                    i--;
                    continue;
                }
                if (ips.contains(randomIp)) {
                    i--;
                    continue;
                }
                ips.add(randomIp);
            }
        } else if (allocationPools != null && !allocationPools.isEmpty()) {
            List<String> allocationPoolsIps = getAllocationPoolsIps(allocationPools, abandonDhcp);
            if (allocationPoolsIps.size() < size) {
                long begin = Ipv4Util.ipv4ToLong(allocationPoolsIps.get(0));
                long newSize = Ipv4Util.ipv4ToLong(allocationPoolsIps.get(allocationPoolsIps.size() - 1)) - begin;
                for (int i = 0; i < count; i++) {
                    String randomIp = randomIp(begin, newSize);
                    if (excludeIps.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    if (ips.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    ips.add(randomIp);
                }
            } else {
                for (int i = 0; i < count; i++) {
                    String randomIp = randomIp(beginIpLong, size);
                    if (excludeIps.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    if (ips.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    ips.add(randomIp);
                }
            }
        }
        return ips;
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

    public static List<String> newAllocateDefaultRouterIps(String cidr, int count, List<String> excludeIps,
                                                           List<AllocationPool> allocationPools) {
        List<String> ips = new ArrayList<>();
        List<String> allocationPoolsIps = new ArrayList<>();
        if (allocationPools != null && !allocationPools.isEmpty()) {
            // 包含dhcp地址以去除
            allocationPoolsIps = getAllocationPoolsIps(allocationPools, false);
        }
        String previousIp = null;
        String beginIp = null;
        if (cidr.contains(Ipv4Util.IP_MASK_SPLIT_MARK)) {
            previousIp = IpUtils.lastIp(cidr);
            beginIp = IpUtils.beginIp(cidr);
        } else if (cidr.contains(Ipv4Util.IP_SPLIT_MARK)) {
            String[] newIps = StrUtil.split(cidr, Ipv4Util.IP_SPLIT_MARK);
            beginIp = newIps[0];
            previousIp = newIps[1];
        }
        int ipSize = (int) (Ipv4Util.ipv4ToLong(previousIp) - Ipv4Util.ipv4ToLong(beginIp));
        if (ipSize < count) {
            throw new MpRuntimeException("No enough resource for default router address pick");
        }
        for (int i = 0; i < count; i++) {
            previousIp = IpUtils.previousIp(previousIp);
            if (allocationPoolsIps.contains(previousIp)) {
                i--;
                continue;
            }
            if (excludeIps != null && !excludeIps.isEmpty() && excludeIps.contains(previousIp)) {
                i--;
                continue;
            }
            ips.add(previousIp);
        }
        return ips;
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

    public static boolean newIsInCidr(String ip, String cidr) {
        String beginIp = null;
        String lastIp = null;
        if (cidr.contains(Ipv4Util.IP_MASK_SPLIT_MARK)) {
            lastIp = IpUtils.lastIp(cidr);
            beginIp = IpUtils.beginIp(cidr);
        } else if (cidr.contains(Ipv4Util.IP_SPLIT_MARK)) {
            String[] newIps = StrUtil.split(cidr, Ipv4Util.IP_SPLIT_MARK);
            beginIp = newIps[0];
            lastIp = newIps[1];
        }
        long size = Ipv4Util.ipv4ToLong(lastIp) - Ipv4Util.ipv4ToLong(beginIp);
        long ipLong = Ipv4Util.ipv4ToLong(ip);
        return (ipLong - Ipv4Util.ipv4ToLong(beginIp)) <= size;
    }

    /**
     * 获取cidr段最后一个ip地址
     * @param cidr cidr
     * @return 最后一个ip地址
     */
    public static String lastIp(String cidr) {
        String[] param = StrUtil.split(cidr, Ipv4Util.IP_MASK_SPLIT_MARK);
        return Ipv4Util.getEndIpStr(param[0], Integer.parseInt(param[1]));
    }

    /**
     * 获取ip地址的上一个地址
     * @param ip ip地址
     * @return 上一个ip地址
     */
    public static String previousIp(String ip) {
        var ipLong = Ipv4Util.ipv4ToLong(ip);
        var previousIpLong = ipLong - 1L;
        String previousIp = null;
        try {
            previousIp = Ipv4Util.longToIpv4(previousIpLong);
        } catch (Exception e) {
            log.warn("Get ip({}} previous ip error: ", ip, e);
        }
        return previousIp;
    }

    /**
     * 获取cidr段第一个ip地址
     * @param cidr cidr
     * @return 第一个ip地址
     */
    public static String beginIp(String cidr) {
        String[] param = StrUtil.split(cidr, Ipv4Util.IP_MASK_SPLIT_MARK);
        return Ipv4Util.getBeginIpStr(param[0], Integer.parseInt(param[1]));
    }

    /**
     * 获取地址段内随机ip
     * @param begin 第一个ip地址long
     * @param size 地址段大小
     * @return 随机ip地址
     */
    public static String randomIp(long begin, long size) {
        long randomData = Random.nextInt((int) size);
        return Ipv4Util.longToIpv4(randomData + begin);
    }
}
