package cn.cyberpeace.mp.situationaware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModel {

    
    private String index;

    private String srcIp;

    private List<String> srcMac;

    private String dstIp;

    private List<String> dstMac;

    private List<String> protocol;

    private Integer srcPort;

    private Integer dstPort;

    private Long totBytes;

    private Long timestamp;
}
