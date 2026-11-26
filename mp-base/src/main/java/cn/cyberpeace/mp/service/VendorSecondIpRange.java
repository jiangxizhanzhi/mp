package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Validated, numeric IPv4 range; no hostname resolution. */
final class VendorSecondIpRange {
    private final long first;
    private final long last;

    VendorSecondIpRange(XdAppConfig config) {
        first = number(config.getXdTwoIpPoolStart());
        last = number(config.getXdTwoIpPoolEnd());
        long mask = number(config.getXdTwoIpNetmask());
        long gateway = number(config.getXdTwoIpGateway());
        long hostMask = (~mask) & 0xffffffffL;
        long network = first & mask;
        if ((hostMask & (hostMask + 1)) != 0 || first > last || first == network
                || (last & mask) != network || last == (network | hostMask)
                || (gateway & mask) != network || gateway == network || gateway == (network | hostMask)
                || (gateway >= first && gateway <= last)) {
            throw new IllegalArgumentException("厂商二地址池、掩码或网关配置不合法");
        }
    }

    static long number(String ip) {
        if (ip == null || !ip.matches("(?:0|[1-9][0-9]{0,2})(?:\\.(?:0|[1-9][0-9]{0,2})){3}")) {
            throw new IllegalArgumentException("无效的IPv4地址");
        }
        long result = 0;
        for (String part : ip.split("\\.")) {
            int value = Integer.parseInt(part);
            if (value > 255) throw new IllegalArgumentException("无效的IPv4地址");
            result = (result << 8) | value;
        }
        return result;
    }

    private static String address(long value) {
        return (value >>> 24) + "." + ((value >>> 16) & 255) + "." + ((value >>> 8) & 255) + "." + (value & 255);
    }

    boolean contains(String ip) {
        try {
            long value = number(ip);
            return value >= first && value <= last;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    List<String> addresses() {
        List<String> result = new ArrayList<>();
        for (long n = first; n <= last; n++) result.add(address(n));
        return result;
    }

    Set<String> reviewedFree(String configured) {
        Set<String> result = new LinkedHashSet<>();
        if (configured == null || configured.trim().isEmpty()) return result;
        for (String entry : configured.split(",")) {
            String[] ends = entry.trim().split("-", -1);
            if (ends.length > 2 || !contains(ends[0].trim()) || !contains(ends[ends.length - 1].trim())) {
                throw new IllegalArgumentException("厂商二已核对空闲地址超出授权地址池");
            }
            long start = number(ends[0].trim());
            long end = number(ends[ends.length - 1].trim());
            if (start > end) throw new IllegalArgumentException("厂商二已核对空闲地址范围无效");
            for (long n = start; n <= end; n++) result.add(address(n));
        }
        return result;
    }
}
