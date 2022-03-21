package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NpcDeleteData implements CloudHttpData {
    public List<NpcInfo> npcInfos;

    @Getter
    public static class NpcInfo {
        private String networkId;
        private String npcName;
        private String npcIp;

        public NpcInfo(String networkId, String npcName, String npcIp) {
            this.networkId = networkId;
            this.npcName = npcName;
            this.npcIp = npcIp;
        }
    }

    @Override
    public String toContent() {
        Map<String, List<Map<String, String>>> npcs = new HashMap<>();
        npcInfos.forEach(npcInfo -> {
            String networkId = npcInfo.getNetworkId();
            if (!npcs.containsKey(networkId)) {
                npcs.put(networkId, new ArrayList<>());
            }
            npcs.get(networkId).add(Collections.singletonMap(npcInfo.getNpcName(), npcInfo.getNpcIp()));
        });
        return JsonCommon.stringify(Collections.singletonMap("npc_data", npcs));
    }
}
