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

/**
 * @Description: ssh工具类
 * @Author qiaokang
 * @Date：2022/12/3 09:37
 */
@Slf4j
public class SshUtil {

    private static final int TIME_OUT = 10000;

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
            String execResult = JschUtil.exec(session, commands, CharsetUtil.CHARSET_UTF_8, outputStream);

            // 执行失败时execResult返回null
            if (StrUtil.isEmpty(execResult)) {
                log.info("exec shell script error:{}", outputStream.toString(CharsetUtil.CHARSET_UTF_8));
            } else {
                log.info("exec shell script success: {}", execResult);
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

    @Data
    public static final class ServerInfo {

        private String host;

        private String user;

        private String password;

        private int port = 22;
    }

}
