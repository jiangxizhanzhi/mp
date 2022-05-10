package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.RandomUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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
        beginIp = nextIp(beginIp);
        long beginIpLong = Ipv4Util.ipv4ToLong(beginIp);
        long size = Ipv4Util.ipv4ToLong(previousIp(lastIp)) - beginIpLong;

        // dhcp情况，默认地址池
        if (allocationPools == null || allocationPools.isEmpty()) {
            if (abandonDhcp) {
                beginIp = nextIp(beginIp);
                beginIpLong = Ipv4Util.ipv4ToLong(beginIp);
                size = size - 1L;
            }
            allocationPools = List.of(new AllocationPool(nextIp(beginIp), previousIp(lastIp)));
            int ipSize = (int) (Ipv4Util.ipv4ToLong(lastIp) - beginIpLong) + 1;
            if (excludeIps != null && !excludeIps.isEmpty()) {
                ipSize = ipSize - excludeIps.size();
            }
            if (ipSize < count) {
                throw new MpRuntimeException("No enough resource for random ip address pick");
            }
            for (int i = 0; i < count; i++) {
                String randomIp = randomIp(beginIpLong, size);
                if (excludeIps != null && excludeIps.contains(randomIp)) {
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
            long last = Ipv4Util.ipv4ToLong(previousIp(lastIp));
            int allocationPoolsIpSize = getAvailableAllocationPoolIpSize(allocationPools, beginIpLong, last);
            if (allocationPoolsIpSize < size) {
                if (abandonDhcp) {
                    allocationPoolsIpSize = allocationPoolsIpSize - allocationPools.size();
                }
                List<AllocationPool> newAllocationPools = getAvailableAllocationPool(allocationPools, beginIpLong, last);
                if (excludeIps != null && !excludeIps.isEmpty()) {
                    for (String excludeIp : excludeIps) {
                        boolean isInPools = isInPools(newAllocationPools, excludeIp);
                        if (isInPools) {
                            allocationPoolsIpSize--;
                        }
                    }
                }
                if (allocationPoolsIpSize < count) {
                    throw new MpRuntimeException("No enough resource for random ip address pick");
                }
                List<String> startIps = new ArrayList<>();
                for (AllocationPool allocationPool : newAllocationPools) {
                    startIps.add(allocationPool.getStart());
                }
                for (int i = 0; i < count; i++) {
                    int randomPool = randomPool(newAllocationPools.size());
                    beginIpLong = Ipv4Util.ipv4ToLong(newAllocationPools.get(randomPool).getStart());
                    size = Ipv4Util.ipv4ToLong(newAllocationPools.get(randomPool).getEnd()) - beginIpLong;
                    if (abandonDhcp) {
                        beginIpLong++;
                        size--;
                    }
                    String randomIp = randomIp(beginIpLong, size);
                    if (excludeIps != null && !excludeIps.isEmpty() && excludeIps.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    if (ips.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    if (abandonDhcp && startIps.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    ips.add(randomIp);
                }
            } else {
                List<String> startIps = new ArrayList<>();
                for (AllocationPool allocationPool : allocationPools) {
                    startIps.add(allocationPool.getStart());
                }
                if (abandonDhcp) {
                    beginIp = nextIp(beginIp);
                    beginIpLong = Ipv4Util.ipv4ToLong(beginIp);
                    size = size - 1;
                }
                int ipSize = (int) (Ipv4Util.ipv4ToLong(lastIp) - beginIpLong) + 1;
                if (excludeIps != null && !excludeIps.isEmpty()) {
                    ipSize = ipSize - excludeIps.size();
                }
                if (ipSize < count) {
                    throw new MpRuntimeException("No enough resource for random ip address pick");
                }
                for (int i = 0; i < count; i++) {
                    String randomIp = randomIp(beginIpLong, size);
                    if (excludeIps != null && excludeIps.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    if (ips.contains(randomIp)) {
                        i--;
                        continue;
                    }
                    boolean isInPool = isInPools(allocationPools, randomIp);
                    if (!isInPool) {
                        i--;
                        continue;
                    }
                    if (abandonDhcp && startIps.contains(randomIp)) {
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
    public static List<String> newAllocateDefaultRouterIps(String cidr, int count, List<String> excludeIps,
                                                           List<AllocationPool> allocationPools) {
        if (allocationPools == null) {
            allocationPools = new ArrayList<>();
        }
        if (excludeIps == null) {
            excludeIps = new ArrayList<>();
        }
        List<String> ips = new ArrayList<>();
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
        long begin = Ipv4Util.ipv4ToLong(nextIp(beginIp));
        long last = Ipv4Util.ipv4ToLong(previousIp(previousIp));
        int ipSize = getAvailableDefaultRouterIpSize(begin, last, excludeIps, allocationPools);
        if (ipSize < count) {
            throw new MpRuntimeException("No enough resource for default router address pick");
        }
        for (int i = 0; i < count; i++) {
            previousIp = previousIp(previousIp);
            boolean isInPool = isInPools(allocationPools, previousIp);
            if (isInPool) {
                i--;
                continue;
            }
            if (!excludeIps.isEmpty() && excludeIps.contains(previousIp)) {
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
    private static String lastIp(String cidr) {
        String[] param = StrUtil.split(cidr, Ipv4Util.IP_MASK_SPLIT_MARK);
        return Ipv4Util.getEndIpStr(param[0], Integer.parseInt(param[1]));
    }

    /**
     * 获取ip地址的上一个地址
     * @param ip ip地址
     * @return 上一个ip地址
     */
    private static String previousIp(String ip) {
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
    private static String beginIp(String cidr) {
        String[] param = StrUtil.split(cidr, Ipv4Util.IP_MASK_SPLIT_MARK);
        return Ipv4Util.getBeginIpStr(param[0], Integer.parseInt(param[1]));
    }

    /**
     * 获取地址段内随机ip
     * @param begin 第一个ip地址long
     * @param size 地址段大小
     * @return 随机ip地址
     */
    private static String randomIp(long begin, long size) {
        long randomData = Random.nextInt((int) size + 1);
        return Ipv4Util.longToIpv4(randomData + begin);
    }

    /**
     *
     * @param size 地址池大小
     * @return 随机地址池序号
     */
    private static int randomPool(int size) {
        return Random.nextInt(size);
    }

    /**
     * 获取地址池Ip总数
     * @param allocationPools 地址池集合
     * @return 地址池Ip总数
     */
    private static int getAllocationPoolIpSize(List<AllocationPool> allocationPools) {
        int size;
        allocationPools.sort(Comparator.comparing(allocationPool -> Ipv4Util.ipv4ToLong(allocationPool.getEnd())));
        long[] starts = new long[allocationPools.size()];
        long[] ends = new long[allocationPools.size()];
        int i = 0;
        for (AllocationPool allocationPool : allocationPools) {
            starts[i] = Ipv4Util.ipv4ToLong(allocationPool.getStart());
            ends[i] = Ipv4Util.ipv4ToLong(allocationPool.getEnd());
            i++;
        }
        size = (int) (ends[0] - starts[0]) + 1;
        for (i = 1; i < allocationPools.size(); i++) {
            if (starts[i] > ends[i - 1]) {
                size = size + (int) (ends[i] - starts[i]) + 1;
            } else {
                size = size + (int) (ends[i] - ends[i - 1]);
            }
        }
        return size;
    }

    /**
     *
     * @param begin 开始ip long
     * @param last 结束ip long
     * @param excludeIps 排除的ip集合
     * @param allocationPools ip地址池
     * @return 默认路由可分配ip大小
     */
    private static int getAvailableDefaultRouterIpSize(long begin, long last, List<String> excludeIps, List<AllocationPool> allocationPools) {
        int size = (int) (last - begin) + 1;
        int excludeSize = 0;
        int allocationPoolIpSize;
        if (!excludeIps.isEmpty()) {
            if (!allocationPools.isEmpty()) {
                for (String excludeIp : excludeIps) {
                    boolean isInPool = isInPools(allocationPools, excludeIp);
                    if (!isInPool) {
                        excludeSize++;
                    }
                }
                allocationPoolIpSize = getAvailableAllocationPoolIpSize(allocationPools, begin, last);
                size = size - allocationPoolIpSize - excludeSize;
            } else {
                size = size - excludeIps.size();
            }
        } else if (!allocationPools.isEmpty()) {
            allocationPoolIpSize = getAvailableAllocationPoolIpSize(allocationPools, begin, last);
            size = size - allocationPoolIpSize;
        }
        return size;
    }

    /**
     *
     * @param allocationPools 地址池集合
     * @param begin cidr开始ip long
     * @param last cidr结束ip long
     * @return 地址池与cidr交集大小
     */
    private static int getAvailableAllocationPoolIpSize(List<AllocationPool> allocationPools, long begin, long last) {
        int allocationPoolIpSize = getAllocationPoolIpSize(allocationPools);
        List<AllocationPool> newAllocationPools = getAvailableAllocationPool(allocationPools, begin, last);
        newAllocationPools.sort(Comparator.comparing(allocationPool -> Ipv4Util.ipv4ToLong(allocationPool.getEnd())));
        if (Ipv4Util.ipv4ToLong(newAllocationPools.get(newAllocationPools.size() - 1).getEnd()) > last) {
            int endSize = (int) (Ipv4Util.ipv4ToLong(newAllocationPools.get(newAllocationPools.size() - 1).getEnd()) - last);
            allocationPoolIpSize = allocationPoolIpSize - endSize;
        }
        newAllocationPools.sort(Comparator.comparing(allocationPool -> Ipv4Util.ipv4ToLong(allocationPool.getStart())));
        if (Ipv4Util.ipv4ToLong(newAllocationPools.get(0).getStart()) < begin) {
            int startSize = (int) (begin - Ipv4Util.ipv4ToLong(newAllocationPools.get(0).getStart()));
            allocationPoolIpSize = allocationPoolIpSize - startSize;
        }
        return allocationPoolIpSize;
    }

    /**
     *
     * @param allocationPools 地址池集合
     * @param begin cidr开始ip long
     * @param last cidr结束ip long
     * @return 与cidr有交集的地址池集合
     */
    private static List<AllocationPool> getAvailableAllocationPool(List<AllocationPool> allocationPools, long begin, long last) {
        List<AllocationPool> newAllocationPools = new ArrayList<>();
        for (AllocationPool allocationPool : allocationPools) {
            long start = Ipv4Util.ipv4ToLong(allocationPool.getStart());
            long end = Ipv4Util.ipv4ToLong(allocationPool.getEnd());
            boolean startIsInRange = isInRange(start, begin, last);
            boolean endIsInRange = isInRange(end, begin, last);
            if (startIsInRange || endIsInRange) {
                newAllocationPools.add(allocationPool);
            }
        }
        return newAllocationPools;
    }

    /**
     *
     * @param allocationPools 地址池
     * @param ip ip地址
     * @return ip是否在地址池内
     */
    private static boolean isInPools(List<AllocationPool> allocationPools, String ip) {
        long ipLong = Ipv4Util.ipv4ToLong(ip);
        for (AllocationPool allocationPool : allocationPools) {
            long start = Ipv4Util.ipv4ToLong(allocationPool.getStart());
            long end = Ipv4Util.ipv4ToLong(allocationPool.getEnd());
            boolean isInPools = isInRange(ipLong, start, end);
            if (isInPools) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param ip 需要判断的地址 long
     * @param start 地址段起始ip long
     * @param end 地址段结束ip long
     * @return ip是否在地址段内
     */
    private static boolean isInRange(long ip, long start, long end) {
        boolean isOverStart = (ip >= start);
        boolean isBeyondEnd = (ip <= end);
        return (isOverStart && isBeyondEnd);
    }

    /**
     * 通过掩码位数格式化ip地址
     * 192.168.10.254/24 -> 192.168.10.0/24
     * @param cidrIp
     * @return
     */
    public static String getFormatCidrIp(String cidrIp) {
        String[] info = cidrIp.split("/");
        String ip = info[0];
        String maskBit = info[1];
        String mask = Ipv4Util.getMaskByMaskBit(Integer.parseInt(maskBit));
        long maskIpLong = Ipv4Util.ipv4ToLong(mask);
        long ipLong = Ipv4Util.ipv4ToLong(ip);
        String formatIp = Ipv4Util.longToIpv4(ipLong & maskIpLong);
        return formatIp + "/" + maskBit;
    }
}
