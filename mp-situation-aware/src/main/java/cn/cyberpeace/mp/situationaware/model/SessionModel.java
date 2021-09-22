package cn.cyberpeace.mp.situationaware.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * es真是的bean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModel {

    /**
     * 物理索引
     */
    private String index;

    private String srcIp;

    private String srcMac;

    private String dstIp;

    private String dstMac;

    private String protocol;

    private String srcPort;

    private String dstPort;

    private Integer totBytes;

    private LocalDateTime timestamp;
}
