 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RDPProbe.java.
 // File size 1908.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.protocolprobe;

/**
 * @ClassName RDPProbe
 * @Description
 *         探测rdp服务是否准备好
 *         主要原理: 建立TCP连接之后,给服务端发送一个TPKT数据包,服务端要能返回一个TPKT数据包
 * @Author maojun
 * @Date 2022/1/14
 * @Version 1.0.0
 * @see ProtocolProbe
 */
public class RDPProbe extends ProtocolProbe {
    private final static String PROTOCOL = "rdp";

    // rdp的探测可以共用这一种 协议内容
    private final static byte[] COMMON_CLIENT_INFO;

    static {
        byte[] header = new byte[]{
                // 协议版本号
                0x03, 0x00,
                // 描述长度
                0x00, 0x2f, 0x2a,
                (byte) 0xe0, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        byte[] cookie = "Cookie: mstshash=Administr".getBytes();
        byte[] footer = new byte[]{
                0x0d, 0x0a, 0x01, 0x00, 0x08, 0x00, 0x03, 0x00, 0x00, 0x00
        };
        COMMON_CLIENT_INFO = new byte[header.length + cookie.length + footer.length];
        System.arraycopy(header, 0, COMMON_CLIENT_INFO, 0, header.length);
        System.arraycopy(cookie, 0, COMMON_CLIENT_INFO, header.length, cookie.length);
        System.arraycopy(footer, 0, COMMON_CLIENT_INFO, header.length + cookie.length, footer.length);
    }


    public RDPProbe(String host, int port) {
        super(PROTOCOL, host, port);
    }

    @Override
    protected byte[] getClientInfo() {
        return COMMON_CLIENT_INFO;
    }

    @Override
    protected boolean checkServerInfo(byte[] serverInfo) {
        if (serverInfo == null || serverInfo.length <= 3) {
            return false;
        }
        return (serverInfo[0] & 0x03) == 0x03 &&
                (serverInfo[1] & 0x00) == 0x00 &&
                (serverInfo[2] & 0x00) == 0x00;
    }


}
