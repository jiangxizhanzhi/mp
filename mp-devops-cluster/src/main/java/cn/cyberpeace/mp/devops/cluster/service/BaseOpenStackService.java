package cn.cyberpeace.mp.devops.cluster.service;

/**
 * 基础对接云平台接口
 * @author wanggang
 */
public interface BaseOpenStackService {

    /**
     * cpcloud平台URL前缀
     */
    String CPCLOUD_URL = "/cpcloud";

    /**
     * cpcloud平台info模块URL前缀
     */
    String CPCLOUD_INFO_URL = CPCLOUD_URL + "/info";

    /**
     * cpcloud平台NPC模块URL前缀
     */
    String CPCLOUD_NPC_URL = CPCLOUD_URL + "/npc_node";

    /**
     * 版本
     */
    String URL_VERSION = "/v1";
}
