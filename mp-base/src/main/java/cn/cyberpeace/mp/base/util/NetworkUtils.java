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

@Slf4j
public class NetworkUtils {

    private static final int DEFAULT_PROBE_TIMEOUT = 300 * 1000;

    private NetworkUtils() {
    }

    public static boolean checkPortConnectable(String host, int port) {
        return checkPortConnectable(host, port, 0);
    }

    
    public static boolean checkPortConnectable(String host, int port, int timeout) {
        try (var socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean checkPortConnectable(String protocol, String host, int port, int timeout) {
        if ("ssh".equalsIgnoreCase(protocol)) {
            ProtocolProbe protocolProbe = new SSHProbe(host, port);
            return protocolProbe.probe();
        } else if ("rdp".equalsIgnoreCase(protocol)) {
            ProtocolProbe protocolProbe = new RDPProbe(host, port);
            return protocolProbe.probe();
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

    
    public static void probePort(String protocol, String host, int port, int perCheckTimeout, int stepTime, int timeout,
                                 Callable<Boolean> stopCheck, int stopCheckIntervalCount,
                                 Runnable callback, Runnable timeoutCallback) {
        final var probeTimeout = timeout <= 0 ? DEFAULT_PROBE_TIMEOUT : timeout;
        final var everyCheckTimeout = Math.max(perCheckTimeout, 0);
        CheckUtils.loopCheck(() -> checkPortConnectable(protocol, host, port, everyCheckTimeout),
                stepTime, probeTimeout, stopCheck, stopCheckIntervalCount, callback, timeoutCallback,
                String.format("host port[%s:%d]", host, port));
    }

    
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
