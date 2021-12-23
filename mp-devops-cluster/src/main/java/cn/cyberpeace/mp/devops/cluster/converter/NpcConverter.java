package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NpcConverter {

    private NpcService npcService;

    @Autowired
    public NpcConverter(NpcService npcService) {
        this.npcService = npcService;
    }

    /**
     * 获取NPC总数
     * @return
     */
    public Integer getTotalNPCs() {
        return npcService.getTotalNPCs();
    }
}
