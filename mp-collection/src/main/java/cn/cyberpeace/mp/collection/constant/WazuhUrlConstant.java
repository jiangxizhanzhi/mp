package cn.cyberpeace.mp.collection.constant;

public class WazuhUrlConstant {
    public static final String AUTHENTICATE_URL = "/security/user/authenticate?raw=true";

    public static final String ALL_AGENT_URL = "/agents";

    public static final String PORT_INFO_URL = "/syscollector/%s/ports";

    public static final String NETIFACE_INFO_URL = "/syscollector/%s/netiface";

    public static final String NETADDR_INFO_URL = "/syscollector/%s/netaddr";

    public static final String OS_INFO_URL = "/syscollector/%s/os";
}
