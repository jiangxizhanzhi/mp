package cn.cyberpeace.mp.devopscluster.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingTest1 {

    public static boolean ping(String ipAddress) {
        return ping(ipAddress, 3000); // 默认3秒超时
    }

    public static boolean ping(String ipAddress, int timeout) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(timeout);
        } catch (UnknownHostException e) {
            System.err.println("未知主机: " + ipAddress);
            return false;
        } catch (Exception e) {
            System.err.println("Ping失败: " + e.getMessage());
            return false;
        }
    }

//    public static void main(String[] args) {
//        String[] testIPs = {"8.159.138.178", "127.0.0.1", "192.168.1.33"};
//
//        for (String ip : testIPs) {
//            boolean reachable = ping(ip, 3000);
//            System.out.println(ip + " is reachable: " + reachable);
//        }
//    }
}