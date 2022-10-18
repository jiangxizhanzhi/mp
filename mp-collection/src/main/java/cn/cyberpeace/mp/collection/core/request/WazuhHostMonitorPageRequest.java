package cn.cyberpeace.mp.collection.core.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WazuhHostMonitorPageRequest {
    /**
     * 查询条数默认限制
     */
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;

    private List<String> agentNames;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
