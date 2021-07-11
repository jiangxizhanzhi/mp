package cn.cyberpeace.mp.base.util.protocolprobe;


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
