package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

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
public class NpcCreateData implements CloudHttpData {
    public List<NetWorkNpcInfo> netWorkNpcInfos;

    @Getter
    public static class NetWorkNpcInfo {
        private String networkId;
        private List<NpcInfo> npcInfos;

        public NetWorkNpcInfo(String networkId) {
            this.networkId = networkId;
            this.npcInfos = new ArrayList<>();
        }

        public void addNpcInfo(String npcName, String npcIp) {
            this.npcInfos.add(new NpcInfo(npcName, npcIp));
        }
    }

    @Getter
    public static class NpcInfo {
        private String npcName;
        private String npcIp;

        public NpcInfo(String npcName, String npcIp) {
            this.npcName = npcName;
            this.npcIp = npcIp;
        }


    }

    @Override
    public String toContent() {
        Map<String, List<Map<String, String>>> npcs = new HashMap<>();
        netWorkNpcInfos.forEach(netWorkNpcInfo -> {
            String networkId = netWorkNpcInfo.getNetworkId();
            if (!npcs.containsKey(networkId)) {
                npcs.put(networkId, new ArrayList<>());
            }
            netWorkNpcInfo.npcInfos.forEach(n -> {
                npcs.get(networkId).add(Collections.singletonMap(n.getNpcName(), n.getNpcIp()));
            });
        });
        return JsonCommon.stringify(Collections.singletonMap("npc_data", npcs));
    }
}
