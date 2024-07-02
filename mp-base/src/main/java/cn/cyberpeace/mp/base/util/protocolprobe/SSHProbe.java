package cn.cyberpeace.mp.base.util.protocolprobe;

/**
 * @ClassName SSHProbe
 * @Description
 *         探测ssh服务是否准备好
 *         主要原理: 建立TCP连接之后,给服务端发送客户端的协议版本号,服务端要能返回一个SSH协议版本号
 * @Author maojun
 * @Date 2022/1/14
 * @Version 1.0.0
 * @see ProtocolProbe
 */
public class SSHProbe extends ProtocolProbe {
    private final static String PROTOCOL = "ssh";

    private final static String CLIENT_SSH_VERSION = "SSH-2.0-mp";

    /**
     * SSHProbe
     *
     * @param host
     * @param port
     */
    public SSHProbe(String host, int port) {
        super(PROTOCOL, host, port);
    }

    /**
     * getClientInfo
     *
     * @return
     */
    @Override
    protected byte[] getClientInfo() {
        return CLIENT_SSH_VERSION.getBytes();
    }

    /**
     * checkServerInfo
     *
     * @param serverInfo
     * @return boolean
     */
    @Override
    protected boolean checkServerInfo(byte[] serverInfo) {
        if (serverInfo == null || serverInfo.length == 0) {
            return false;
        }
        return new String(serverInfo).startsWith("SSH");
    }
}
