package cn.cyberpeace.mp.base.util.hutool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ipv4Util {

    public static final String IP_MASK_SPLIT_MARK = "-";
    public static final String IP_SPLIT_MARK = ".";

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
     * @param ip
     * @return
     */
    public static long ipv4ToLong(String ip) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
        }
        byte[] bytes = inetAddress.getAddress();
        long result = 0;
        for (byte b : bytes) {
            result <<= 8;
            result |= b & 0xFF;
        }
        return result;
    }

    /**
     * 长整型转换为IPv4
     *
     * @param ip
     * @return
     */
    public static String longToIpv4(long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((ip >> 24) & 0xFF).append(".")
                .append((ip >> 16) & 0xFF).append(".")
                .append((ip >> 8) & 0xFF).append(".")
                .append(ip & 0xFF);
        return sb.toString();
    }

    /**
     * 获取cidr段ip列表
     *
     * @param cidr
     * @param ordered true:全量地址，false:可用地址；仅在ipRange为X.X.X.X/X时才生效
     * @return
     */
    public static List<String> list(String cidr, boolean ordered) {
        List<String> ipAddressList = new ArrayList<>();

        // 使用正则表达式提取CIDR中的IP地址范围
        String[] cidrParts = cidr.split("/");
        String ipAddress = cidrParts[0];
        int cidrPrefix = Integer.parseInt(cidrParts[1]);

        // 将IP地址转换为字节数组
        String[] ipAddressParts = ipAddress.split("\\.");
        byte[] ipAddressBytes = new byte[]{
                (byte) Integer.parseInt(ipAddressParts[0]),
                (byte) Integer.parseInt(ipAddressParts[1]),
                (byte) Integer.parseInt(ipAddressParts[2]),
                (byte) Integer.parseInt(ipAddressParts[3])
        };

        // 计算CIDR中的IP地址数量
        int totalIpAddressCount = (int) Math.pow(2, 32 - cidrPrefix);

        // 将每个IP地址添加到列表中
        for (int i = 0; i < totalIpAddressCount; i++) {
            ipAddressList.add(String.format("%d.%d.%d.%d",
                    ipAddressBytes[0] & 0xFF,
                    ipAddressBytes[1] & 0xFF,
                    ipAddressBytes[2] & 0xFF,
                    ipAddressBytes[3] & 0xFF));

            // 逐个递增IP地址
            for (int j = ipAddressBytes.length - 1; j >= 0; j--) {
                if ((ipAddressBytes[j] & 0xFF) == 255) {
                    ipAddressBytes[j] = 0;
                } else {
                    ipAddressBytes[j]++;
                    break;
                }
            }
        }

        // 如果ordered为true，则按顺序排列IP地址列表
        if (ordered) {
            ipAddressList.sort(String::compareTo);
        }

        return ipAddressList;
    }


    /**
     * 获取IP地址区间内ip
     *
     * @param ipFrom
     * @param ipTo
     * @return
     */
    public static List<String> list(String ipFrom, String ipTo) {
        List<String> ipList = new ArrayList<>();
        try {
            InetAddress start = InetAddress.getByName(ipFrom);
            InetAddress end = InetAddress.getByName(ipTo);
            long startIp = ipToLong(start.getHostAddress());
            long endIp = ipToLong(end.getHostAddress());
            for (long ip = startIp; ip <= endIp; ip++) {
                ipList.add(longToIp(ip));
            }
        } catch (UnknownHostException e) {
        }
        return ipList;
    }

    private static long ipToLong(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16)
                + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    private static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((ip >> 24) & 0xFF).append(".");
        sb.append((ip >> 16) & 0xFF).append(".");
        sb.append((ip >> 8) & 0xFF).append(".");
        sb.append(ip & 0xFF);
        return sb.toString();
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP
     *
     * @param ip
     * @param maskBit
     * @return
     */
    public static String getEndIpStr(String ip, int maskBit) {
        // 获取IP地址的二进制表示
        String binaryIp = ipToBinary(ip);

        // 创建掩码位的二进制字符串
        StringBuilder maskBinary = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i < maskBit) {
                maskBinary.append("1");
            } else {
                maskBinary.append("0");
            }
        }

        // 计算终止IP的二进制表示
        StringBuilder endIpBinary = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i < maskBit) {
                endIpBinary.append(binaryIp.charAt(i));
            } else {
                endIpBinary.append("1");
            }
        }

        // 将二进制IP转换为字符串表示
        String endIp = binaryToIp(endIpBinary.toString());
        return endIp;
    }

    private static String ipToBinary(String ip) {
        StringBuilder binaryIp = new StringBuilder();
        String regex = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()) {
            for (int i = 1; i <= 4; i++) {
                String segment = matcher.group(i);
                String binarySegment = Integer.toBinaryString(Integer.parseInt(segment));
                binaryIp.append(addZeroPadding(binarySegment));
            }
        }
        return binaryIp.toString();
    }

    private static String addZeroPadding(String binarySegment) {
        StringBuilder paddedSegment = new StringBuilder();
        int paddingSize = 8 - binarySegment.length();
        for (int i = 0; i < paddingSize; i++) {
            paddedSegment.append("0");
        }
        paddedSegment.append(binarySegment);
        return paddedSegment.toString();
    }

    private static String binaryToIp(String binaryIp) {
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String binarySegment = binaryIp.substring(i * 8, (i + 1) * 8);
            int decimalSegment = Integer.parseInt(binarySegment, 2);
            ip.append(decimalSegment);
            if (i < 3) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP
     * @param ip
     * @param maskBit
     * @return
     */
    public static String getBeginIpStr(String ip, int maskBit) {
        String[] ipArray = ip.split("\\.");
        int[] ipSections = new int[ipArray.length];
        for (int i = 0; i < ipArray.length; i++) {
            ipSections[i] = Integer.parseInt(ipArray[i]);
        }
        int ipInt = ipSections[0] << 24 | ipSections[1] << 16 | ipSections[2] << 8 | ipSections[3];
        int mask = ~((1 << (32 - maskBit)) - 1);
        int beginIpInt = ipInt & mask;

        StringJoiner sj = new StringJoiner(".");
        sj.add(String.valueOf((beginIpInt >> 24) & 0xFF))
                .add(String.valueOf((beginIpInt >> 16) & 0xFF))
                .add(String.valueOf((beginIpInt >> 8) & 0xFF))
                .add(String.valueOf(beginIpInt & 0xFF));

        return sj.toString();
    }

}
