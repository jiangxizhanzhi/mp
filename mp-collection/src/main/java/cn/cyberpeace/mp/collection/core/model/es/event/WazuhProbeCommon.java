package cn.cyberpeace.mp.collection.core.model.es.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WazuhProbeCommon {

    private String agentId;

    private String agentName;

    private String agentIp;

    private LocalDateTime time;

    private String rawOutPut;

    private String probeId;
}
