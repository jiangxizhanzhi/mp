package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
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

    private String timestamp;
}
