package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

@Slf4j
public class NetworkUtils {

    private static final int DEFAULT_PROBE_TIMEOUT = 300 * 1000;

    private NetworkUtils() {}

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
        try (var socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

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

    public static void probePort(String host, int port, Runnable callback, Runnable timeoutCallback)
            throws Exception {
        probePort(host, port, 0, callback, timeoutCallback);
    }

    public static void probePort(String host, int port, int timeout, Runnable callback, Runnable timeoutCallback)
            throws Exception {
        probePort(host, port, 0, 0, timeout, null, 0, callback, timeoutCallback);
    }

    /**
     * 探测目标端口直到连通
     */
    public static void probePort(String host, int port, int perCheckTimeout, int stepTime, int timeout,
                          Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                          Runnable callback, Runnable timeoutCallback) throws Exception {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkPortConnectable(host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host port[%s:%d]", host, port));
    }

    public static void probeHost(String host, Runnable callback, Runnable timeoutCallback)
            throws Exception {
        probeHost(host, 0, callback, timeoutCallback);
    }

    public static void probeHost(String host, int timeout, Runnable callback, Runnable timeoutCallback)
            throws Exception {
        probeHost(host, 0, 0, timeout, null, 0, callback, timeoutCallback);
    }

    /**
     * 探测目标地址直到连通
     */
    public static void probeHost(String host, int perCheckTimeout, int stepTime, int timeout,
                          Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                          Runnable callback, Runnable timeoutCallback) throws Exception {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkHostReachable(host, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host[%s]", host));
    }

}
