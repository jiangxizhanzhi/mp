package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.util.protocolprobe.ProtocolProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.RDPProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.SSHProbe;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * @ClassName NetworkUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class NetworkUtils {

    private static final int DEFAULT_PROBE_TIMEOUT = 300 * 1000;

    /**
     * NetworkUtils
     */
    private NetworkUtils() {
    }

    /**
     * checkPortConnectable
     *
     * @param host
     * @param port
     * @return boolean
     */
    public static boolean checkPortConnectable(String host, int port) {
        return checkPortConnectable(host, port, 0);
    }

    /**
     * 检查端口是否可连接
     * @param host 主机地址
     * @param port 主机端口
     * @param timeout 超时时间
     * @return 是否可连接
     */
    public static boolean checkPortConnectable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * checkPortConnectable
     *
     * @param protocol
     * @param host
     * @param port
     * @param timeout
     * @return boolean
     */
    public static boolean checkPortConnectable(String protocol, String host, int port, int timeout) {
        if ("ssh".equalsIgnoreCase(protocol)) {
            ProtocolProbe protocolProbe = new SSHProbe(host, port);
            return protocolProbe.probe();
        } else if ("rdp".equalsIgnoreCase(protocol)) {
            ProtocolProbe protocolProbe = new RDPProbe(host, port);
            return protocolProbe.probe();
        } else {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeout);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    }

    /**
     * checkHostReachable
     *
     * @param host
     * @return boolean
     */
    public static boolean checkHostReachable(String host) {
        return checkHostReachable(host, 0);
    }

    /**
     * 检查主机是否可达
     * @param host 主机地址
     * @param timeout 超时时间
     * @return 是否可达
     */
    public static boolean checkHostReachable(String host, int timeout) {
        try {
            return InetAddress.getByName(host).isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * probePort
     *
     * @param protocol
     * @param host
     * @param port
     * @return boolean
     */
    public static boolean probePort(String protocol, String host, int port) {
        return probePort(protocol, host, port, 0, 0, 0, null, 0);
    }

    /**
     * 探测目标端口直到连通,可区分协议(目前支持ssh)
     */
    public static boolean probePort(String protocol, String host, int port, int perCheckTimeout, int stepTime, int timeout,
                                    Callable<Boolean> stopCheck, int stopCheckIntervalCount) {
        final int probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final int everyCheckTimeout = Math.max(perCheckTimeout, 0);
        return CheckUtils.loopCheck(() -> checkPortConnectable(protocol, host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, String.format("host port[%s:%d]", host, port));
    }

    /**
     * 探测目标端口直到连通
     */
    public static boolean probePort(String host, int port, int perCheckTimeout, int stepTime, int timeout,
                                    Callable<Boolean> stopCheck, int stopCheckIntervalCount) {
        final int probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final int everyCheckTimeout = Math.max(perCheckTimeout, 0);
        return CheckUtils.loopCheck(() -> checkPortConnectable(host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, String.format("host port[%s:%d]", host, port));
    }

    /**
     * probeHost
     *
     * @param host
     * @return boolean
     */
    public static boolean probeHost(String host) {
        return probeHost(host, 0);
    }

    /**
     * probeHost
     *
     * @param host
     * @param timeout
     * @return boolean
     */
    public static boolean probeHost(String host, int timeout) {
        return probeHost(host, 0, 0, timeout, null, 0);
    }

    /**
     * 探测目标地址直到连通
     */
    public static boolean probeHost(String host, int perCheckTimeout, int stepTime, int timeout,
                                    Callable<Boolean> stopCheck, int stopCheckIntervalCount) {
        final int probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final int everyCheckTimeout = Math.max(perCheckTimeout, 0);
        return CheckUtils.loopCheck(() -> checkHostReachable(host, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount,
                String.format("host[%s]", host));
    }

}
