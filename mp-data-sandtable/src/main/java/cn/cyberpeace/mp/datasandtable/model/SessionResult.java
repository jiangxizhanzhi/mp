package cn.cyberpeace.mp.datasandtable.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class SessionResult {

    /**
     * 分组id
     */
    private String groupId;


    private String sourceIp;

    private String targetIp;

    private String sourceMac;

    private String targetMac;

    private String srcPort;

    private String dstPort;

    /**
     * 协议类型
     */
    private String protocalType;
}
