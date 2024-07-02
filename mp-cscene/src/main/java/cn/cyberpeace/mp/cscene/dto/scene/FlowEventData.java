package cn.cyberpeace.mp.cscene.dto.scene;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName FlowEventData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class FlowEventData {

    private Port source;

    private Port destination;

    private Suricata suricata;

    private String timestamp;

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
