package cn.cyberpeace.mp.cscene.dto.scene;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FlowEventData {

    private Port source;

    private Port destination;

    private Suricata suricata;

    @Data
    @NoArgsConstructor
    public static class Port {
        private String ip;
        private String port;
        private List<String> mac;
    }

    @Data
    @NoArgsConstructor
    public static class Suricata {
        private List<String> category;
    }
}
