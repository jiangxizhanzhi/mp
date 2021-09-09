package cn.cyberpeace.mp.devops.cluster.service;

/**
 * 基础对接云平台接口
 * @author wanggang
 */
public interface BaseCloudPlatformService {

    /**
     * cpcloud平台URL前缀
     */
    String CPCLOUD_URL = "/cpcloud";

    /**
     * cpcloud平台info模块URL前缀
     */
    String CPCLOUD_INFO_URL = CPCLOUD_URL + "/info";

    /**
     * cpcloud平台device
     */
    String CPCLOUD_DEVICE = CPCLOUD_URL + "/device";

    /**
     * cpcloud平台ccm
     */
    String CPCLOUD_CCM = CPCLOUD_URL + "/ccm";

    /**
     * cpcloud平台NPC模块URL前缀
     */
    String CPCLOUD_NPC_URL = CPCLOUD_URL + "/npc_node";

    /**
     * cpcloud平台Switch模块URL前缀
     */
    String CPCLOUD_SWITCH_URL = CPCLOUD_URL + "/switch";

    /**
     * cpcloud平台流控模块URL前缀
     */
    String CPCLOUD_OPENFLOW = CPCLOUD_URL + "/sdn_app";

    /**
     * 版本
     */
    String URL_VERSION = "/v1";

    /**
     * 流表监控操作
     */
    String OPENFLOW_ACTION_METHOD = "list";

}
