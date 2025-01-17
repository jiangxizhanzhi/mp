 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SSHProbe.java.
 // File size 918.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.protocolprobe;

/**
 * @Description
 * @Author maojun
 * @Date 2022/1/14
 *         探测ssh服务是否准备好
 *         主要原理: 建立TCP连接之后,给服务端发送客户端的协议版本号,服务端要能返回一个SSH协议版本号
 */
public class SSHProbe extends ProtocolProbe {
    private final static String PROTOCOL = "ssh";

    private final static String CLIENT_SSH_VERSION = "SSH-2.0-mp";

    public SSHProbe(String host, int port) {
        super(PROTOCOL, host, port);
    }

    @Override
    protected byte[] getClientInfo() {
        return CLIENT_SSH_VERSION.getBytes();
    }

    @Override
    protected boolean checkServerInfo(byte[] serverInfo) {
        if (serverInfo == null || serverInfo.length == 0) {
            return false;
        }
        return new String(serverInfo).startsWith("SSH");
    }
}
