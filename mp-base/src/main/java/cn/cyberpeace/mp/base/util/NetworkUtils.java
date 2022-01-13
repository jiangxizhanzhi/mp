package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static boolean checkPortConnectable(String protocol, String host, int port, int timeout) {
        if ("ssh".equals(protocol)) {
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                socket = SocketFactory.getDefault().createSocket();
                socket.setSoTimeout(2000);
                socket.connect(new InetSocketAddress(host, port));
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                // 建立完tcp连接,尝试和远端交换ssh的版本号,交换成功才算可连接
                outputStream.write("SSH-2.0-mp".getBytes());
                outputStream.flush();
                byte[] remoteVersion = new byte[20];
                inputStream.read(remoteVersion);
                if (new String(remoteVersion).startsWith("SSH")) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                log.error("failed to connect {}//{}:{}", protocol, host, port, e);
                return false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("failed to close inputStream", e);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        log.error("failed to close outputStream", e);
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        log.error("failed to close socket", e);
                    }
                }
            }
        } else {
            try (var socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeout);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
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

    public static void probePort(String host, int port, Runnable callback, Runnable timeoutCallback) {
        probePort(host, port, 0, callback, timeoutCallback);
    }

    public static void probePort(String host, int port, int timeout, Runnable callback, Runnable timeoutCallback) {
        probePort(host, port, 0, 0, timeout, null, 0, callback, timeoutCallback);
    }

    public static void probePort(String protocol, String host, int port, Runnable callback, Runnable timeoutCallback) {
        probePort(protocol, host, port, 0, 0, 0, null, 0, callback, timeoutCallback);
    }

    /**
     * 探测目标端口直到连通,可区分协议(目前支持ssh)
     */
    public static void probePort(String protocol, String host, int port, int perCheckTimeout, int stepTime, int timeout,
                                 Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                                 Runnable callback, Runnable timeoutCallback) {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkPortConnectable(protocol, host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host port[%s:%d]", host, port));
    }

    /**
     * 探测目标端口直到连通
     */
    public static void probePort(String host, int port, int perCheckTimeout, int stepTime, int timeout,
                          Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                          Runnable callback, Runnable timeoutCallback) {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkPortConnectable(host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host port[%s:%d]", host, port));
    }

    public static void probeHost(String host, Runnable callback, Runnable timeoutCallback) {
        probeHost(host, 0, callback, timeoutCallback);
    }

    public static void probeHost(String host, int timeout, Runnable callback, Runnable timeoutCallback) {
        probeHost(host, 0, 0, timeout, null, 0, callback, timeoutCallback);
    }

    /**
     * 探测目标地址直到连通
     */
    public static void probeHost(String host, int perCheckTimeout, int stepTime, int timeout,
                          Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                          Runnable callback, Runnable timeoutCallback) {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkHostReachable(host, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host[%s]", host));
    }

}
