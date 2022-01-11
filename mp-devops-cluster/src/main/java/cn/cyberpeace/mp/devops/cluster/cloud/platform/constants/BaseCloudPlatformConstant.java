package cn.cyberpeace.mp.devops.cluster.cloud.platform.constants;

/**
 * 基础对接云平台接口
 * @author wanggang
 */
public final class BaseCloudPlatformConstant {

    private BaseCloudPlatformConstant(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * cpcloud平台URL前缀
     */
    public static final String CPCLOUD_URL = "/cpcloud";

    /**
     * cpcloud平台info模块URL前缀
     */
    public static final String CPCLOUD_INFO_URL = CPCLOUD_URL + "/info";

    /**
     * cpcloud平台device
     */
    public static final String CPCLOUD_DEVICE = CPCLOUD_URL + "/device";

    /**
     * cpcloud平台ccm
     */
    public static final String CPCLOUD_CCM = CPCLOUD_URL + "/ccm";

    /**
     * cpcloud平台NPC模块URL前缀
     */
    public static final String CPCLOUD_NPC_URL = CPCLOUD_URL + "/npc_node";

    /**
     * cpcloud平台Switch模块URL前缀
     */
    public static final String CPCLOUD_SWITCH_URL = CPCLOUD_URL + "/switch";

    /**
     * cpcloud平台流控模块URL前缀
     */
    public static final String CPCLOUD_OPENFLOW = CPCLOUD_URL + "/sdn_app";

    /**
     * 版本
     */
    public static final String URL_VERSION = "/v1";

    /**
     * 流表监控操作
     */
    public static final String OPENFLOW_ACTION_METHOD = "list";

    /**
     * 截屏模块URL前缀
     */
    public static final String SCREENRECORD_METHOD = CPCLOUD_URL + "/guacamole" + URL_VERSION;

}
