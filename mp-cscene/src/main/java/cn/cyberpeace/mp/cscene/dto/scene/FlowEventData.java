 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowEventData.java.
 // File size 721.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
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
