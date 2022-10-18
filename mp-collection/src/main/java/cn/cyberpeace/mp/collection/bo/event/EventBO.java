package cn.cyberpeace.mp.collection.bo.event;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class EventBO extends BaseEventBO {
    /**
     * 规则id
     */
    private String ruleId;

    /**
     * 规则等级
     */
    private Integer level;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 事件描述
     */
    private String message;
}
