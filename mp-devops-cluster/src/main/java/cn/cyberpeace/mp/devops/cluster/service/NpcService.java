package cn.cyberpeace.mp.devops.cluster.service;

/**
 * 运维管控-系统运维-计算实例管理-npc
 * @author wanggang
 */
public interface NpcService extends BaseCloudPlatformService {

    /**
     * 获取NPC总数
     * @return
     */
    Integer getTotalNPCs();
}
