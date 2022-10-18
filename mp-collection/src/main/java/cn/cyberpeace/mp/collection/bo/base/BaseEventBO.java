package cn.cyberpeace.mp.collection.bo.base;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEventBO {
    /**
     * 代理名称
     */
    private String agentName;

    /**
     * 代理ip
     */
    private String agentIp;

    /**
     * 事件上报时间
     */
    private LocalDateTime time;
}
