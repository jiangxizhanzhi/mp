package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

/**
 * @ClassName CoreType
 * @Description
 * @Author maojun
 * @Date 2022/3/10
 * @Version 1.0.0
 * @see Enum
 */
public enum CoreType {
    NETWORK_0, NETWORK, NETWORK_L2, ROUTER, ROUTER_L2, FIREWALL, TERMINAL;

    /**
     * isNetwork
     *
     * @param coreType
     * @return boolean
     */
    public static boolean isNetwork(CoreType coreType) {
        if (coreType == null) {
            return false;
        } else {
            return coreType == NETWORK || coreType == NETWORK_L2;
        }
    }

    /**
     * isRouter
     *
     * @param coreType
     * @return boolean
     */
    public static boolean isRouter(CoreType coreType) {
        if (coreType == null) {
            return false;
        } else {
            return coreType == ROUTER || coreType == ROUTER_L2;
        }
    }
}