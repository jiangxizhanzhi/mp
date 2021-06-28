package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
import cn.cyberpeace.mp.devops.cluster.model.NpcOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.service.NpcService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class NpcServiceImpl extends BaseOpenStackServiceImpl implements NpcService {

    public NpcServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig,objectMapper);
    }

    @Override
    public Integer getTotalNPCs() {
        NpcOpenStackModel npcOpenStackModel =  super.sendGet(CPCLOUD_NPC_URL + URL_VERSION + "/bulk_create" , NpcOpenStackModel.class);
        return Objects.isNull(npcOpenStackModel) || Objects.isNull(npcOpenStackModel.getNpc_nums()) ? 0 : npcOpenStackModel.getNpc_nums();
    }
}
