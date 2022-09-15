package cn.cyberpeace.mp.base.util.ssh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SshClient implements Closeable {

    public static final int DEFAULT_PORT = 22;

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final String privateKey;
    private final Integer keepAlive;

    private final SSHClient innerClient;
    private final List<ServerSocket> localPortForwardingSockets = new ArrayList<>();

    public static class SshClientBuilder {
        private String host;
        private Integer port = DEFAULT_PORT;
        private String username;
        private String password;
        private String privateKey;
        private Integer keepAlive;

        public SshClientBuilder configHost(String host) {
            this.host = host;
            return this;
        }

        public SshClientBuilder configPort(int port) {
            this.port = port;
            return this;
        }

        public SshClientBuilder configUsername(String username) {
            this.username = username;
            return this;
        }

        public SshClientBuilder configPassword(String password) {
            this.password = password;
            return this;
        }

        public SshClientBuilder configPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public SshClientBuilder configKeepAlive(Integer keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public SshClient build() {
            return new SshClient(host, port, username, password, privateKey, keepAlive);
        }
    }

    private void initConnByPassword() {
        log.info("Ssh client connect to {}@{}:{} by password", username, host, port);
        try {
            innerClient.addHostKeyVerifier(new PromiscuousVerifier());
            innerClient.connect(host, port);
            innerClient.authPassword(username, password);
        } catch (IOException e) {
            var msg = String.format("Ssh client connect to %s@%s:%s by password error: %s",
                    username, host, port, e.getMessage());
            log.error(msg);
            close(false);
            throw new MpRuntimeException(msg);
        }
    }

    private void initConnByPrivateKey() {
        log.info("Ssh client connect to {}@{}:{} by privateKey", username, host, port);
        try {
            var keyProvider = innerClient.loadKeys(privateKey, null, null);
            innerClient.addHostKeyVerifier(new PromiscuousVerifier());
            innerClient.connect(host, port);
            innerClient.authPublickey(username, keyProvider);
        } catch (IOException e) {
            var msg = String.format("Ssh client connect to %s@%s:%s by privateKey error: %s",
                    username, host, port, e.getMessage());
            log.error(msg);
            close(false);
            throw new MpRuntimeException(msg);
        }
    }

    private void initConnByKnownHosts() {
        log.info("Ssh client connect to {}@{}:{} by known hosts", username, host, port);
        try {
            innerClient.loadKnownHosts();
            innerClient.connect(host, port);
            innerClient.authPublickey(username);
        } catch (IOException e) {
            var msg = String.format("Ssh client connect to %s@%s:%s by known hosts error: %s",
                    username, host, port, e.getMessage());
            log.error(msg);
            close(false);
            throw new MpRuntimeException(msg);
        }
    }

    private SshClient(String host, int port, String username, String password, String privateKey, Integer keepAlive) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.keepAlive = keepAlive;

        var defaultConfig = new DefaultConfig();
        if (this.keepAlive != null) {
            defaultConfig.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        }
        this.innerClient = new SSHClient(defaultConfig);
        if (this.keepAlive != null) {
            this.innerClient.getConnection().getKeepAlive().setKeepAliveInterval(this.keepAlive);
        }

        if (!StringUtils.isEmpty(this.password)) {
            initConnByPassword();
        } else if (!StringUtils.isEmpty(this.privateKey)) {
            initConnByPrivateKey();
        } else {
            initConnByKnownHosts();
        }
    }

    public static SshClientBuilder builder() {
        return new SshClientBuilder();
    }

    public String execReturn(String command) {
        return execReturn(command, null);
    }

    public String execReturn(String command, Long timeout) {
        var os = new ByteArrayOutputStream();
        exec(command, os, timeout);
        return os.toString(StandardCharsets.UTF_8);
    }

    public void exec(String command) {
        exec(command, null, null);
    }

    public void exec(String command, Long timeout) {
        exec(command, null, timeout);
    }

    public void exec(String command, OutputStream os) {
        exec(command, os, null);
    }

    public void exec(String command, OutputStream os, Long timeout) {
        var timeoutFlag = timeout != null && timeout.compareTo(0L) > 0;
        try (var session = innerClient.startSession()) {
            session.allocateDefaultPTY();
            var cmd = session.exec(command);
            if (timeoutFlag) {
                if (os != null) {
                    new Thread(() -> {
                        try {
                            new StreamCopier(cmd.getInputStream(), os, LoggerFactory.DEFAULT).copy();
                        } catch (IOException e) {
                            var msg = String.format("Ssh client run command on %s@%s:%s error: %s",
                                    username, host, port, e.getMessage());
                            log.warn(msg);
                        }
                    }).start();
                }
                session.join(timeout, TimeUnit.MILLISECONDS);
            } else {
                if (os != null) {
                    new StreamCopier(cmd.getInputStream(), os, LoggerFactory.DEFAULT).copy();
                }
            }
        } catch (IOException e) {
            if (timeoutFlag && e.getCause().getClass().equals(TimeoutException.class)) {
                var msg = String.format("Ssh client run command on %s@%s:%s timeout: %s",
                        username, host, port, timeout);
                log.warn(msg);
            } else {
                var msg = String.format("Ssh client run command on %s@%s:%s error: %s",
                        username, host, port, e.getMessage());
                log.error(msg);
                throw new MpRuntimeException(msg);
            }
        }
    }

    public void scpDownload(String remotePath, String localPath) {
        try {
            innerClient.newSCPFileTransfer().download(remotePath, localPath);
        } catch (IOException e) {
            var msg = String.format("Ssh client download %s on %s@%s:%s to %s by scp error: %s",
                    remotePath, username, host, port, localPath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public void scpUpload(String localPath, String remotePath) {
        try {
            innerClient.newSCPFileTransfer().upload(localPath, remotePath);
        } catch (IOException e) {
            var msg = String.format("Ssh client upload %s to %s on %s@%s:%s by scp error: %s",
                    localPath, remotePath, username, host, port, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public void sftpDownload(String remotePath, String localPath) {
        try (var sftp = innerClient.newSFTPClient()) {
            sftp.get(remotePath, localPath);
        } catch (IOException e) {
            var msg = String.format("Ssh client download %s on %s@%s:%s to %s by sftp error: %s",
                    remotePath, username, host, port, localPath, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public void sftpUpload(String localPath, String remotePath) {
        try (var sftp = innerClient.newSFTPClient()) {
            sftp.put(localPath, remotePath);
        } catch (IOException e) {
            var msg = String.format("Ssh client upload %s to %s on %s@%s:%s by sftp error: %s",
                    localPath, remotePath, username, host, port, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    public void localPortForwarding(String localHost, int localPort, String remoteHost, int remotePort) {
        log.info("Ssh client local port forwarding {}:{} to {}:{} on {}@{}:{}",
                localHost, localPort, remoteHost, remotePort, username, host, port);
        var params = new Parameters(localHost, localPort, remoteHost, remotePort);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(params.getLocalHost(), params.getLocalPort()));
            localPortForwardingSockets.add(serverSocket);
            innerClient.newLocalPortForwarder(params, serverSocket).listen();
        } catch (IOException e) {
            var msg = String.format(
                    "Ssh client local port forwarding %s:%s to %s:%s on %s@%s:%s error: %s",
                    localHost, localPort, remoteHost, remotePort, username, host, port, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        } finally {
            if (serverSocket != null) {
                closeServerSocket(serverSocket);
            }
        }
    }

    public void remotePortForwarding(int remotePort, String localHost, int localPort) {
        log.info("Ssh client remote port forwarding {} on {}@{}:{} to {}:{}",
                remotePort, username, host, port, localHost, localPort);
        try {
            innerClient.getRemotePortForwarder().bind(
                    new RemotePortForwarder.Forward(remotePort),
                    new SocketForwardingConnectListener(new InetSocketAddress(localHost, localPort)));
            innerClient.getTransport().join();
        } catch (IOException e) {
            var msg = String.format(
                    "Ssh client remote port forwarding %s on %s@%s:%s to %s:%s error: %s",
                    remotePort, username, host, port, localHost, localPort, e.getMessage());
            log.error(msg);
            throw new MpRuntimeException(msg);
        }
    }

    @Override
    public void close() {
        close(true);
    }

    public void close(boolean raiseException) {
        for (var serverSocket : localPortForwardingSockets.toArray(new ServerSocket[0])) {
            localPortForwardingSockets.remove(serverSocket);
        }

        if (this.innerClient != null) {
            try {
                this.innerClient.close();
            } catch (IOException e) {
                var msg = String.format("Ssh client close to %s@%s:%s error: %s",
                        username, host, port, e.getMessage());
                log.error(msg);
                if (raiseException) {
                    throw new MpRuntimeException(msg);
                }
            }
        }
    }

    private void closeServerSocket(ServerSocket serverSocket) {
        localPortForwardingSockets.remove(serverSocket);
        try {
            serverSocket.close();
        } catch (IOException e) {
            var msgI = String.format("Ssh client local port forwarding %s:%s to %s@%s:%s close socket error: %s",
                    serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort(), username, host, port,
                    e.getMessage());
            log.error(msgI);
        }
    }

}
