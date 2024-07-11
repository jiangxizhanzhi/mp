package cn.cyberpeace.mp.common.util;

import cn.cyberpeace.mp.common.model.AddressSpace;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 校验工具
 **/
public class CheckUtil {

    // 校验字符串是否是IP地址、IP地址加端口号、域名或域名加端口号
    public static boolean checkPrivateCloudAddress(String address) {
        String ipPattern = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
        String ipWithPortPattern = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}:\\d{1,5}\\b";
        String domainPattern = "\\b[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+\\b";
        String domainWithPortPattern = "\\b[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+:\\d{1,5}\\b";

        String combinedPattern = String.format("^(%s)$|^(%s)$|^(%s)$|^(%s)$",
                ipPattern, ipWithPortPattern, domainPattern, domainWithPortPattern);

        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    // 校验字符串是否是IP地址
    public static boolean checkStringIsIpAddress(String address) {
        String ipPattern = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
        String combinedPattern = String.format("^(%s)$|^(%s)$|^(%s)$|^(%s)$",ipPattern);
        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

//    //比较两个地址空间案例
//    public static void main(String[] args) {
//        String largeAddress = "10.0.0.0";
//        Integer largeAddressSize = 16;
//        String smallAddress = "10.0.1.0";
//        Integer smallAddressSize = 24;
//        boolean result = checkIpAddressSpace(largeAddress, largeAddressSize, smallAddress, smallAddressSize);
//    }

    //比较两个地址空间, 判断小地址空间是否在大地址空间内
    public static boolean checkIpAddressSpace(String largeAddress, Integer largeAddressSize, String smallAddress, Integer smallAddressSize) {
        try {
            BigInteger network1 = getNetworkAddress(smallAddress, smallAddressSize);
            BigInteger network2 = getNetworkAddress(largeAddress, largeAddressSize);
            BigInteger mask = BigInteger.valueOf(-1).shiftLeft(32 - largeAddressSize);
            return network1.and(mask).equals(network2);
        } catch (UnknownHostException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static BigInteger getNetworkAddress(String ip, int prefixLength) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] addressBytes = inetAddress.getAddress();
        BigInteger ipAsBigInt = new BigInteger(1, addressBytes);
        BigInteger mask = BigInteger.valueOf(-1).shiftLeft(32 - prefixLength);
        return ipAsBigInt.and(mask);
    }

//    //判断ip是否在地址空间内案例
//    public static void main(String[] args) {
//        String address = "10.0.0.0";
//        Integer addressSize = 16;
//        String ip = "10.0.1.0";
//        boolean result = checkIpInAddressSpace(ip, address, addressSize);
//        System.out.println("IP is in address space: " + result);
//    }

    //判断ip是否在地址空间内
    public static boolean checkIpInAddressSpace(String ip, String address, Integer addressSize) {
        try {
            InetAddress ipAddr = InetAddress.getByName(ip);
            InetAddress networkAddr = InetAddress.getByName(address);
            ByteBuffer ipBuffer = ByteBuffer.wrap(ipAddr.getAddress());
            ByteBuffer networkBuffer = ByteBuffer.wrap(networkAddr.getAddress());

            int shift = 32 - addressSize;
            int ipValue = ipBuffer.getInt() & (~0 << shift);
            int networkValue = networkBuffer.getInt() & (~0 << shift);

            return ipValue == networkValue;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

//    //判断ips是否在该地址空间集合内案例
//    public static void main(String[] args) {
//        List<AddressSpace> addressSpaces = new ArrayList<>();
//        addressSpaces.add(new AddressSpace("10.0.0.0", 16));
//        addressSpaces.add(new AddressSpace("10.1.0.0", 16));
//        List<String> ips = new ArrayList<>();
//        ips.add("10.0.1.0");
//        ips.add("10.1.1.1");
//        boolean result = checkIpsInAddressSpaces(ips, addressSpaces);
//        System.out.println("IPs is in address spaces: " + result);
//    }

    //判断ips是否在该地址空间集合内
    public static boolean checkIpsInAddressSpaces(List<String> ips, List<AddressSpace> addressSpaces) {
        try {
            for (String ip : ips) {
                boolean inAnyAddressSpace = false;
                for (AddressSpace addressSpace : addressSpaces) {
                    if (checkIpInAddressSpace(ip, addressSpace.getAddress(), addressSpace.getAddressSize())) {
                        inAnyAddressSpace = true;
                        break;
                    }
                }
                if (!inAnyAddressSpace) {
                    // 如果某个IP不在任何地址空间内，返回false
                    return false;
                }
            }
            // 所有IP都在至少一个地址空间内，返回true
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
