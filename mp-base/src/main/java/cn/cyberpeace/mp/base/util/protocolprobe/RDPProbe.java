package cn.cyberpeace.mp.base.util.protocolprobe;


public class RDPProbe extends ProtocolProbe {
    private final static String PROTOCOL = "rdp";

    
    private final static byte[] COMMON_CLIENT_INFO;

    static {
        byte[] header = new byte[]{
                
                0x03, 0x00,
                
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
