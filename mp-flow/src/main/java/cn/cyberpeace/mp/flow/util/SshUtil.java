package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.jcraft.jsch.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * @Description: ssh工具类
 * @Author qiaokang
 * @Date：2022/12/3 09:37
 */
@Slf4j
public class SshUtil {

    private static final int TIME_OUT = 30000;

    private static final int MAX_RETRIES = 4;
    private static final long INITIAL_RETRY_DELAY = 5000; // 初始重试延迟1秒

    public static void refreshDir(ServerInfo serverInfo, String commands, String srcFilePath, String destFilePath) {
        for (int i = 0; i <= 50; i++) {
            try {
                doRefreshDir(serverInfo, commands, srcFilePath, destFilePath);
                return;
            } catch (Exception e) {
                log.error("failed to refresh dir at {} times", i);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static void scpFileToRemote(ServerInfo serverInfo,
                                String remoteDir, ClassPathResource sourceFile) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            Session session = null;
            ChannelSftp channel = null;
            try {
                // 1. 创建JSch会话
                JSch jsch = new JSch();
                session = jsch.getSession(serverInfo.getUser(), serverInfo.getHost(), serverInfo.getPort());
                session.setPassword(serverInfo.getPassword());
                session.setConfig("StrictHostKeyChecking", "no"); // 仅为示例，生产环境应使用已知主机
                session.setServerAliveInterval(30000);
                session.setServerAliveCountMax(3);

                // 2. 连接会话
                session.connect(TIME_OUT);

                // 3. 创建SFTP通道
                channel = (ChannelSftp) session.openChannel("sftp");
                channel.connect(TIME_OUT);

                // 4. 确保远程目录存在
                createRemoteDirectory(channel, remoteDir);

                // 5. 从resources读取文件
                if (!sourceFile.exists()) {
                    throw new RuntimeException("Source file not found: " + sourceFile.getFilename());
                }

                // 6. 执行SCP传输
                try (InputStream inputStream = sourceFile.getInputStream()) {
                    String fileName = sourceFile.getFilename();
                    channel.put(inputStream, remoteDir + "/" + fileName);
                }

                System.out.println("File transferred successfully!");
                return; // 传输成功则退出方法

            } catch (JSchException | SftpException | IOException e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    log.error(e.getMessage(), e);
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format(
                            "SCP transfer failed after " + MAX_RETRIES + " attempts"));
                }

                // 计算指数退避延迟
                long delay = INITIAL_RETRY_DELAY * (1L << (attempt - 1));
                log.error("Attempt {}  failed. Retrying in {}ms...", attempt, delay);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    log.error("Thread interrupted: ", interruptedException);
                }

            } finally {
                // 7. 清理资源
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    private static void createRemoteDirectory(ChannelSftp channel, String remoteDir) throws SftpException {
        try {
            channel.cd(remoteDir);
        } catch (SftpException e) {
            // 目录不存在则逐级创建
            String[] folders = remoteDir.split("/");
            StringBuilder pathBuilder = new StringBuilder();
            for (String folder : folders) {
                if (folder.isEmpty()) continue;
                pathBuilder.append("/").append(folder);
                try {
                    channel.cd(pathBuilder.toString());
                } catch (SftpException ex) {
                    channel.mkdir(pathBuilder.toString());
                    channel.cd(pathBuilder.toString());
                }
            }
        }
    }



    /**
     * 刷新目录
     * @param serverInfo
     * @param commands
     * @param srcFilePath
     * @param destFilePath
     */
    public static void doRefreshDir(ServerInfo serverInfo, String commands, String srcFilePath, String destFilePath) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        ByteArrayOutputStream outputStream = null;
        try {
            File file = new File(srcFilePath);
            if (!file.exists()) {
                log.error("[{}] file not exist", srcFilePath);
                return;
            }

            // 1.创建session
            session = createSession(jsch, serverInfo.getHost(), serverInfo.getPort(), serverInfo.getUser());
            // 2.密码登录
            session.setPassword(serverInfo.getPassword());
            // 3.连接
            session.connect(TIME_OUT);

            log.info("open sftp channel");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            // 4.建立sftp通道连接
            sftpChannel.connect(TIME_OUT);

            // 5. 上传代码
            sftpChannel.put(srcFilePath, destFilePath + "/" + file.getName(), ChannelSftp.OVERWRITE);
            log.info("put file [{}] from [{}] to [{}] success", file.getName(), srcFilePath, destFilePath);

            outputStream = new ByteArrayOutputStream();
            log.info("start exec shell script: {}", commands);
            String execResult = null;
            Session finalSession = session;
            ByteArrayOutputStream finalOutputStream = outputStream;
            Callable<String> task = () -> exec(finalSession, commands, finalOutputStream);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                Future<String> future = executorService.submit(task);
                execResult = future.get(1000 * 10, TimeUnit.MILLISECONDS); //任务处理超时时间设为10秒
                log.info("exec shell task result: {}", execResult);
            } catch (TimeoutException ex) {
                log.error("exec shell task timeout", ex);
                throw new MpRuntimeException(BaseErrorCode.ERROR, ex);
            } catch (Exception e) {
                log.error("exec shell task failed", e);
                throw new MpRuntimeException(BaseErrorCode.ERROR, e);
            } finally {
                log.info("exec shell end and close thread pool");
                executorService.shutdown();
                close(finalSession);
                IOUtils.closeQuietly(finalOutputStream);
            }

            // 执行失败时execResult返回null
            if (StringUtils.isEmpty(execResult)) {
                log.info("exec shell script error:{}", outputStream.toString(StandardCharsets.UTF_8));
            } else {
                log.info("exec shell script success");
            }
        } catch (Exception e) {
            //设置执行失败的响应内容
            log.error("refresh dir error , serverInfo is {},exception is {}", JsonCommon.stringify(serverInfo), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        } finally {
            close(sftpChannel);
            close(session);
            IOUtils.closeQuietly(outputStream);
        }
    }

    public static boolean checkPortConnectivity(String ip, int port) {
        Instant startTime = Instant.now();
        final long maxTimeoutMs = 300_000; // 5分钟超时（毫秒）

        while (Duration.between(startTime, Instant.now()).toMillis() < maxTimeoutMs) {
            try (Socket socket = new Socket()) {
                // 设置单次连接超时（每次尝试5秒）
                socket.connect(new InetSocketAddress(ip, port), 5000);
                return true; // 连接成功
            } catch (IOException e) {
                // 失败后等待1秒重试
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false; // 超时未连通
    }

    public static String executeCommandWithReturn(ServerInfo serverInfo,
                                           String command) {
        if (!checkPortConnectivity(serverInfo.getHost(), serverInfo.getPort())) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("目标服务器超时无法连接"));
        }
        log.info("execute command {}", command);
        Session session = null;
        ChannelExec channel = null;
        InputStream stdout = null;
        InputStream stderr = null;
        BufferedReader stdoutReader = null;
        BufferedReader stderrReader = null;

        try {
            // 1. 创建JSch会话
            JSch jsch = new JSch();
            session = jsch.getSession(serverInfo.user, serverInfo.getHost(), serverInfo.port);
            session.setPassword(serverInfo.getPassword());
            session.setServerAliveInterval(30000);
            session.setServerAliveCountMax(3);

            // 安全配置（生产环境应使用已知主机）
            session.setConfig("StrictHostKeyChecking", "no");

            // 2. 连接会话（带超时）
            session.connect(5000);

            // 3. 创建执行通道
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // 4. 设置输出流
            stdout = channel.getInputStream();
            stderr = channel.getErrStream();

            // 5. 连接通道（开始执行命令）
            channel.connect();

            // 6. 准备读取输出
            stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            stderrReader = new BufferedReader(new InputStreamReader(stderr));

            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // 7. 设置超时监控
            long startTime = System.currentTimeMillis();

            // 8. 循环读取输出（直到通道关闭或超时）
            while (!channel.isClosed()) {
                // 检查超时
                if (System.currentTimeMillis() - startTime > TimeUnit.SECONDS.toMillis(300)) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("命令执行超时"));
                }

                // 读取标准输出
                while (stdoutReader.ready()) {
                    output.append(stdoutReader.readLine()).append("\n");
                }

                // 读取错误输出
                while (stderrReader.ready()) {
                    error.append(stderrReader.readLine()).append("\n");
                }

                // 短暂休眠避免CPU过度占用
                Thread.sleep(10000);
            }

            // 9. 读取剩余输出
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = stderrReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            // 10. 获取退出状态码
            int exitStatus = channel.getExitStatus();

            log.info("命令执行完成，退出状态: {}", exitStatus);

            return StringUtils.isEmpty(error.toString().trim()) ? output.toString().trim() : error.toString().trim();

        } catch (JSchException | InterruptedException | IOException e) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("SSH命令执行失败: " + e.getMessage()));
        } finally {
            // 11. 安全关闭资源
            try {
                if (channel != null) channel.disconnect();
                if (session != null) session.disconnect();
                if (stdoutReader != null) stdoutReader.close();
                if (stderrReader != null) stderrReader.close();
                if (stdout != null) stdout.close();
                if (stderr != null) stderr.close();
            } catch (IOException e) {
                log.error("关闭SSH资源时出错", e);
            }
        }
    }

    /**
     * 执行命令
     * @param serverInfo
     * @param commands
     */
    public static void execCommand(ServerInfo serverInfo, String commands) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        ByteArrayOutputStream outputStream = null;
        try {
            // 1.创建session
            session = createSession(jsch, serverInfo.getHost(), serverInfo.getPort(), serverInfo.getUser());
            // 2.密码登录
            session.setPassword(serverInfo.getPassword());
            // 3.连接
            session.connect(TIME_OUT);

            outputStream = new ByteArrayOutputStream();
            log.info("start exec shell script: {}", commands);
            String execResult = null;
            Session finalSession = session;
            ByteArrayOutputStream finalOutputStream = outputStream;
            Callable<String> task = () -> exec(finalSession, commands, finalOutputStream);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                Future<String> future = executorService.submit(task);
                execResult = future.get(1000 * 25, TimeUnit.MILLISECONDS); //任务处理超时时间设为10秒
                log.info("exec shell commands result: {}", execResult);
            } catch (TimeoutException ex) {
                log.error("exec shell commands timeout", ex);
            } catch (Exception e) {
                log.error("exec shell commands failed", e);
            } finally {
                log.info("exec shell commands end and close thread pool");
                executorService.shutdown();
                close(finalSession);
                IOUtils.closeQuietly(finalOutputStream);
            }

            // 执行失败时execResult返回null
            if (StringUtils.isEmpty(execResult)) {
                log.info("exec shell commands error:{}", outputStream.toString(StandardCharsets.UTF_8));
            } else {
                log.info("exec shell commands success");
            }
        } catch (Exception e) {
            //设置执行失败的响应内容
            log.error("exec shell commands error", e);
        } finally {
            close(sftpChannel);
            close(session);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * 创建一个SSH会话，重用已经使用的会话
     *
     * @param jsch    {@link JSch}
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名，如果为null，默认root
     */
    private static Session createSession(JSch jsch, String sshHost, int sshPort, String sshUser) {
        // 默认root用户
        if (StringUtils.isEmpty(sshUser)) {
            sshUser = "root";
        }

        if (null == jsch) {
            jsch = new JSch();
        }

        Session session;
        try {
            session = jsch.getSession(sshUser, sshHost, sshPort);
        } catch (JSchException e) {
            throw new MpRuntimeException("create ssh session error");
        }

        // 设置第一次登录的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");

        return session;
    }

    /**
     * 执行Shell命令（使用EXEC方式）
     * <p>
     * 此方法单次发送一个命令到服务端，不读取环境变量，执行结束后自动关闭channel，不会产生阻塞。
     * </p>
     *
     * @param session   Session会话
     * @param cmd       命令
     * @param errStream 错误信息输出到的位置
     * @return 执行结果内容
     */
    private static String exec(Session session, String cmd, OutputStream errStream) {
        Charset charset = StandardCharsets.UTF_8;
        final ChannelExec channel = (ChannelExec) createChannel(session);
        channel.setCommand(bytes(cmd, charset));
        channel.setInputStream(null);
        channel.setErrStream(errStream);
        InputStream in = null;
        try {
            channel.connect();
            in = channel.getInputStream();
            return read(in, charset);
        } catch (Exception e) {
            log.error("exec remote command error", e);
            throw new MpRuntimeException("exec remote command error");
        } finally {
            IOUtils.closeQuietly(in);
            close(channel);
        }
    }

    /**
     * 读取inputStream
     * @param inputStream
     * @param charset
     * @return
     */
    private static String read(InputStream inputStream, Charset charset) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("read inputStream error:", e);
        }
        return sb.toString();
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    private static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 创建Channel连接
     *
     * @param session     Session会话
     */
    private static Channel createChannel(Session session) {
        Channel channel;
        try {
            if (!session.isConnected()) {
                session.connect();
            }
            channel = session.openChannel("exec");
        } catch (JSchException e) {
            log.error("create channel error:", e);
            throw new MpRuntimeException("create channel error");
        }
        return channel;
    }

    /**
     * 关闭SSH连接会话
     *
     * @param session SSH会话
     */
    private static void close(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 关闭会话通道
     *
     * @param channel 会话通道
     * @since 4.0.3
     */
    private static void close(Channel channel) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
    }

    @Data
    @NoArgsConstructor
    public static final class ServerInfo {

        private String host;

        private String user;

        private String password;

        private int port = 22;

        public ServerInfo(String host, String user, String password) {
            this.host = host;
            this.user = user;
            this.password = password;
        }

        public ServerInfo(String host, String user, String password, int port) {
            this.host = host;
            this.user = user;
            this.password = password;
            this.port = port;
        }
    }

}
