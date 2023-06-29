package cn.cyberpeace.mp.flow.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
            session = JschUtil.createSession(jsch, serverInfo.getHost(), serverInfo.getPort(), serverInfo.getUser());
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
            Callable<String> task = () -> JschUtil.exec(finalSession, commands, CharsetUtil.CHARSET_UTF_8, finalOutputStream);
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
                JschUtil.close(finalSession);
                IOUtils.closeQuietly(finalOutputStream);
            }

            // 执行失败时execResult返回null
            if (StrUtil.isEmpty(execResult)) {
                log.info("exec shell script error:{}", outputStream.toString(CharsetUtil.CHARSET_UTF_8));
            } else {
                log.info("exec shell script success");
            }
        } catch (Exception e) {
            //设置执行失败的响应内容
            log.error("refresh dir error", e);
        } finally {
            JschUtil.close(sftpChannel);
            JschUtil.close(session);
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
            session = JschUtil.createSession(jsch, serverInfo.getHost(), serverInfo.getPort(), serverInfo.getUser());
            // 2.密码登录
            session.setPassword(serverInfo.getPassword());
            // 3.连接
            session.connect(TIME_OUT);

            outputStream = new ByteArrayOutputStream();
            log.info("start exec shell script: {}", commands);
            String execResult = null;
            Session finalSession = session;
            ByteArrayOutputStream finalOutputStream = outputStream;
            Callable<String> task = () -> JschUtil.exec(finalSession, commands, CharsetUtil.CHARSET_UTF_8, finalOutputStream);
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
                JschUtil.close(finalSession);
                IOUtils.closeQuietly(finalOutputStream);
            }

            // 执行失败时execResult返回null
            if (StrUtil.isEmpty(execResult)) {
                log.info("exec shell commands error:{}", outputStream.toString(CharsetUtil.CHARSET_UTF_8));
            } else {
                log.info("exec shell commands success");
            }
        } catch (Exception e) {
            //设置执行失败的响应内容
            log.error("exec shell commands error", e);
        } finally {
            JschUtil.close(sftpChannel);
            JschUtil.close(session);
            IOUtils.closeQuietly(outputStream);
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
