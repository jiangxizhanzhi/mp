package cn.cyberpeace.mp.agent.ssh;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.mptool.ExceptionUtil;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Slf4j
public class SshUtil {
    public static List<String> execListCmds(String host, String user, String password, int port, List<String> cmds) {
        JSch jsch = new JSch();
        Session session = null;

        List<String> result = new ArrayList<>();
        try {
            // 创建并连接 SSH 会话
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(5000);
            session.connect();

            // 打开一个持久的 SSH 通道
            ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
            InputStream in = channelShell.getInputStream();
            OutputStream out = channelShell.getOutputStream();
            channelShell.connect();

            // 发送命令到远程 shell
            for (String command : cmds) {
                out.write((command + "\n").getBytes());
                out.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Vector<String> lines = new Vector<>();
            Thread readTask = new Thread(() -> {
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        log.info("exec cmd result : [ {} ]", line);
                        lines.add(line);
                        if (line.contains("root") && line.contains(">") && line.contains("exit")) {
                            break;
                        }
                        if (line.contains("Setting timestamp")) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            });
            readTask.start();
            try {
                readTask.join(10000);
                if (readTask.isAlive()) {
                    readTask.interrupt();
                }
            } catch (Exception e) {
                log.error("failed to exec cmds", e);
            }

            // 发送退出命令
            out.write("exit\n".getBytes());
            out.flush();
            result.addAll(lines);
            return result;
        } catch (Exception e) {
            log.error("failed to exec cmds , host:{},port:{},user:{},passwd:{},cmds:{},execption is {}", host, port, user, password, JsonCommon.stringify(cmds), ExceptionUtil.stacktraceToString(e));
            return result;
        } finally {
            // 关闭 SSH 会话
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
