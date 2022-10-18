package cn.cyberpeace.mp.collection.core.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WazuhEventRequest {

    /**
     * 查询开始时间
     */
    private LocalDateTime startTime;

    /**
     * 查询结束时间
     */
    private LocalDateTime endTime;

    /**
     * 查询条数默认限制
     */
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;


    /**
     * agent names
     */
    private List<String> agentNames;

    /**
     * 关键字集合
     */
    private List<String> keyword;

    /**
     * 级别
     */
    private List<Integer> levels;

    /**
     * 指定rule.groups
     */
    private List<String> ruleGroups;

    /**
     * 指定rule.id
     */
    private List<String> ruleIds;

    /**
     * 指定path 模糊查询
     */
    private String path;

    /**
     * 是否是滚动分页请求
     */
    private boolean scroll = false;

    private List<String> probeIds;

    private boolean desc = true;

    private boolean trackTotalHits = false;

    private Integer topSize;
}
