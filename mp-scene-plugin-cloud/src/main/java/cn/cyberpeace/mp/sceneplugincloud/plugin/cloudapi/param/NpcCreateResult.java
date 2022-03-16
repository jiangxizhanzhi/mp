package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NpcCreateResult {
    private Boolean success;
    private String messages;
    private String requestId;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Map<String, List<Map<String, String>>> npcData;
    }
}
