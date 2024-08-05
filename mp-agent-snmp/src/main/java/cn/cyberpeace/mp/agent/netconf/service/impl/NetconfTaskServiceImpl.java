package cn.cyberpeace.mp.agent.netconf.service.impl;

import cn.cyberpeace.mp.agent.netconf.controller.request.NetConfAccessInfo;
import cn.cyberpeace.mp.agent.netconf.netconf.NetconfOperator;
import cn.cyberpeace.mp.agent.snmp.service.Command;
import cn.cyberpeace.mp.agent.snmp.service.CommandConfig;
import cn.cyberpeace.mp.agent.snmp.service.Param;
import com.jcraft.jsch.ChannelSubsystem;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import net.juniper.netconf.XML;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Slf4j
@Service("netconfTaskService")
public class NetconfTaskServiceImpl {

    public Boolean execTask(NetConfAccessInfo netConfAccessInfo, CommandConfig config) {
        NetconfOperator operator = new NetconfOperator(netConfAccessInfo.getHostIp(), netConfAccessInfo.getUsername(), netConfAccessInfo.getPassword(), Integer.parseInt(netConfAccessInfo.getPort()));
        String message = formatConfig(config.getCommands().get(0));
        try {
            XML send = operator.send(message);
            Document document = DocumentHelper.parseText(send.toString());
            // 获取根节点rpc-reply
            Element rootElement = document.getRootElement();
            // 获取根节点下的某个元素
            Element errorFlag = rootElement.element("rpc-error");
            if (null != errorFlag) {
                // 执行出错，获取错误信息
                Element errorType = errorFlag.element("error-type");
                Element errorTag = errorFlag.element("error-tag");
                String failMessage = errorType.getData() + ":" + errorTag.getData();
                log.error("代理执行失败，错误信息：" + failMessage);
            } else {
                log.info("代理执行成功，请求信息为：" + message);
            }
        } catch (Exception e) {
            log.error("代理执行失败", e);
        } finally {
            operator.close();
        }

        return Boolean.TRUE;
    }

    public static void main(String[] args) {
        String username = "admin";
        String password = "admin";
        String host = "10.100.27.164";
        int port = 12345; // NETCONF over SSH port

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // 设置 SSH 会话属性
            Properties config2 = new Properties();
            config2.put("StrictHostKeyChecking", "no");
            session.setConfig(config2);

            // 连接 SSH 会话
            session.connect();

            // 打开 NETCONF 子系统通道
            ChannelSubsystem channel = (ChannelSubsystem) session.openChannel("subsystem");
            channel.setSubsystem("netconf");

            // 获取输入输出流
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();

            // 发送 NETCONF 请求
            String netconfRequest = "<rpc><get-config><source><running/></source></get-config></rpc>";
            out.write(netconfRequest.getBytes());
            out.flush();

            // 读取 NETCONF 响应
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(new InputSource(in));

            // 解析 NETCONF 响应
            NodeList nodes = doc.getDocumentElement().getElementsByTagName("data");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println(node.getTextContent());
                }
            }

            // 关闭 NETCONF 通道和 SSH 会话
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatConfig(Command command) {
        String finalCommand = command.getCommand();

        for (Param param : command.getParams()) {

            finalCommand = finalCommand.replaceAll("\\$\\{" + param.getCode() + "}", "\"" + param.getValue() + "\"");
        }

        return finalCommand;
    }
}
