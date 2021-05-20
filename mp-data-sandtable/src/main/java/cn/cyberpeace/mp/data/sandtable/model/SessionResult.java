package cn.cyberpeace.mp.data.sandtable.model;

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

    /**
     * 协议类型
     */
    private String protocalType;
}
