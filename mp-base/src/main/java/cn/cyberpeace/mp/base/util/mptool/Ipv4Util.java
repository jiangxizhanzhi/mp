package cn.cyberpeace.mp.base.util.mptool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ipv4Util {

    public static final String IP_MASK_SPLIT_MARK = "/";
    public static final String IP_SPLIT_MARK = "-";

    public static final int IP_MASK_MAX = 32;



    /**
     * 用掩码位获取子网掩码
     *
     * @param maskBit
     * @return
     */
    public static String getMaskByMaskBit(int maskBit) {
        // 根据位数计算掩码的十进制值
        int maskValue = 0xffffffff << (32 - maskBit);

        // 将十进制值转换为IP地址格式的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int octet = (maskValue >> (8 * (3 - i))) & 0xff;
            sb.append(octet);
            if (i < 3) {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    /**
     * IPv4转换为长整型
     *
     * @param strIP
     * @return
     */
    public static long ipv4ToLong(String strIP) {
        final long[] ip = convertToLongArray(strIP.split(StrUtil.IP_DOT));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static long[] convertToLongArray(String[] stringArray) {
        long[] longArray = new long[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            longArray[i] = Long.parseLong(stringArray[i]);
        }

        return longArray;
    }

    public static int[] convertToIntArray(String[] stringArray) {
        int[] longArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            longArray[i] = Integer.parseInt(stringArray[i]);
        }

        return longArray;
    }

    /**
     * 长整型转换为IPv4
     *
     * @param longIP
     * @return
     */
    public static String longToIpv4(long longIP) {
        final StringBuilder sb = new StringBuilder();
        // 直接右移24位
        sb.append(longIP >> 24 & 0xFF);
        sb.append(StrUtil.DOT);
        // 将高8位置0，然后右移16位
        sb.append(longIP >> 16 & 0xFF);
        sb.append(StrUtil.DOT);
        sb.append(longIP >> 8 & 0xFF);
        sb.append(StrUtil.DOT);
        sb.append(longIP & 0xFF);
        return sb.toString();
    }

    /**
     * 获取cidr段ip列表
     *
     * @param ipRange
     * @param isAll   true:全量地址，false:可用地址；仅在ipRange为X.X.X.X/X时才生效
     */
    public static List<String> list(String ipRange, boolean isAll) {
        if (ipRange.contains(IP_SPLIT_MARK)) {
            // X.X.X.X-X.X.X.X
            final String[] range = StrUtil.split(ipRange, IP_SPLIT_MARK);
            return list(range[0], range[1]);
        } else if (ipRange.contains(IP_MASK_SPLIT_MARK)) {
            // X.X.X.X/X
            final String[] param = StrUtil.split(ipRange, IP_MASK_SPLIT_MARK);
            return list(param[0], Integer.parseInt(param[1]), isAll);
        } else {
            return CollectionUtil.toList(ipRange);
        }
    }

    public static List<String> list(String ip, int maskBit, boolean isAll) {
        if (maskBit == IP_MASK_MAX) {
            final List<String> list = new ArrayList<>();
            if (isAll) {
                list.add(ip);
            }
            return list;
        }

        String startIp = getBeginIpStr(ip, maskBit);
        String endIp = getEndIpStr(ip, maskBit);
        if (isAll) {
            return list(startIp, endIp);
        }

        int lastDotIndex = startIp.lastIndexOf(StrUtil.DOT) + 1;
        startIp = StrUtil.subPre(startIp, lastDotIndex) +
                (Integer.parseInt(Objects.requireNonNull(StrUtil.subSuf(startIp, lastDotIndex))) + 1);
        lastDotIndex = endIp.lastIndexOf(StrUtil.DOT) + 1;
        endIp = StrUtil.subPre(endIp, lastDotIndex) +
                (Integer.parseInt(Objects.requireNonNull(StrUtil.subSuf(endIp, lastDotIndex))) - 1);
        return list(startIp, endIp);
    }


    /**
     * 获取IP地址区间内ip
     *
     * @param ipFrom
     * @param ipTo
     * @return
     */
    public static List<String> list(String ipFrom, String ipTo) {
        final int[] ipf = convertToIntArray(ipFrom.split(StrUtil.IP_DOT));
        final int[] ipt = convertToIntArray(ipTo.split(StrUtil.IP_DOT));
        final List<String> ips = new ArrayList<>();
        for (int a = ipf[0]; a <= ipt[0]; a++) {
            for (int b = (a == ipf[0] ? ipf[1] : 0); b <= (a == ipt[0] ? ipt[1]
                    : 255); b++) {
                for (int c = (b == ipf[1] ? ipf[2] : 0); c <= (b == ipt[1] ? ipt[2]
                        : 255); c++) {
                    for (int d = (c == ipf[2] ? ipf[3] : 0); d <= (c == ipt[2] ? ipt[3]
                            : 255); d++) {
                        ips.add(a + "." + b + "." + c + "." + d);
                    }
                }
            }
        }
        return ips;
    }


    /**
     * 根据 ip/掩码位 计算IP段的终止IP
     *
     * @param ip
     * @param maskBit
     * @return
     */
    public static String getEndIpStr(String ip, int maskBit) {
        return longToIpv4(getEndIpLong(ip, maskBit));
    }

    private static Long getEndIpLong(String ip, int maskBit) {
        return getBeginIpLong(ip, maskBit)
                + ~ipv4ToLong(getMaskByMaskBit(maskBit));
    }


    /**
     * 根据 ip/掩码位 计算IP段的起始IP
     *
     * @param ip
     * @param maskBit
     * @return
     */
    public static String getBeginIpStr(String ip, int maskBit) {
        return longToIpv4(getBeginIpLong(ip, maskBit));
    }

    private static Long getBeginIpLong(String ip, int maskBit) {
        return ipv4ToLong(ip) & ipv4ToLong(getMaskByMaskBit(maskBit));
    }

}
