package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.NpcCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.NpcService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class NpcServiceImpl extends BaseOpenStackServiceImpl implements NpcService {

    public NpcServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    @Override
    public Integer getTotalNPCs() {
        NpcCloudPlatformModel npcOpenStackModel =  super.sendGet(CPCLOUD_NPC_URL + URL_VERSION + "/bulk_create" , NpcCloudPlatformModel.class);
        return Objects.isNull(npcOpenStackModel) || Objects.isNull(npcOpenStackModel.getTotal()) ? NumberUtils.INTEGER_ZERO : npcOpenStackModel.getTotal();
    }

}
