package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.NpcCloudPlatformModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_NPC_URL;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 运维管控-系统运维-计算实例管理-npc
 * @author wanggang
 */
@Slf4j
@Service
public class NpcService extends BaseOpenStackServiceImpl {

    public NpcService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 获取NPC总数
     * @return
     */
    public Integer getTotalNPCs() {
        NpcCloudPlatformModel npcOpenStackModel =  super.sendGet(CPCLOUD_NPC_URL + URL_VERSION + "/bulk_create" , NpcCloudPlatformModel.class);
        return Objects.isNull(npcOpenStackModel) || Objects.isNull(npcOpenStackModel.getTotal()) ? NumberUtils.INTEGER_ZERO : npcOpenStackModel.getTotal();
    }
}
