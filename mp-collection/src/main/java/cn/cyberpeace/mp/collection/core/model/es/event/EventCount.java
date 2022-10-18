package cn.cyberpeace.mp.collection.core.model.es.event;

import lombok.Data;

@Data
public class EventCount {
    private String agentName;

    private long count;
}
