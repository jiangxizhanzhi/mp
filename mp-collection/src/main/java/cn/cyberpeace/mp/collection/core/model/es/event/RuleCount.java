package cn.cyberpeace.mp.collection.core.model.es.event;

import lombok.Data;

@Data
public class RuleCount {
    private String ruleId;

    private long count;
}
