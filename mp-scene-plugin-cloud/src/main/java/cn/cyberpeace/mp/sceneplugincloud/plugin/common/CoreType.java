package cn.cyberpeace.mp.sceneplugincloud.plugin.common;


public enum CoreType {
    NETWORK_0, NETWORK, NETWORK_L2, ROUTER, ROUTER_L2, FIREWALL, TERMINAL;

    public static boolean isNetwork(CoreType coreType) {
        if (coreType == null) {
            return false;
        } else {
            return coreType == NETWORK || coreType == NETWORK_L2;
        }
    }

    public static boolean isRouter(CoreType coreType) {
        if (coreType == null) {
            return false;
        } else {
            return coreType == ROUTER || coreType == ROUTER_L2;
        }
    }
}