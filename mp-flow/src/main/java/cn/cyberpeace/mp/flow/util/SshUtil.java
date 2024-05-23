package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.jcraft.jsch.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * @Description: ssh工具类
 * @Author qiaokang
 * @Date：2022/12/3 09:37
 */
@Slf4j
public class SshUtil {

    private static final int TIME_OUT = 30000;

    /**
     * 刷新目录
     * @param serverInfo
     * @param commands
     * @param srcFilePath
     * @param destFilePath
     */
    public static void refreshDir(ServerInfo serverInfo, String commands, String srcFilePath, String destFilePath) {
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
            } catch (Exception e) {
                log.error("exec shell task failed", e);
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
        } finally {
            close(sftpChannel);
            close(session);
            IOUtils.closeQuietly(outputStream);
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
                execResult = future.get(1000 * 10, TimeUnit.MILLISECONDS); //任务处理超时时间设为10秒
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
    public static final class ServerInfo {

        private String host;

        private String user;

        private String password;

        private int port = 22;
    }

}
