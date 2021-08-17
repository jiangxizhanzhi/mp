package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.NpcConverter;
import cn.cyberpeace.mp.devops.cluster.service.NpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NpcConverterImpl implements NpcConverter {

    private NpcService npcService;

    @Autowired
    public NpcConverterImpl(NpcService npcService) {
        this.npcService = npcService;
    }

    @Override
    public Integer getTotalNPCs() {
        return npcService.getTotalNPCs();
    }
}
