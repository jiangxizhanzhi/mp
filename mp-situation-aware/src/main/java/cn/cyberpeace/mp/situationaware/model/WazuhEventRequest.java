package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("系统监控事件变更查询条件")
public class WazuhEventRequest {

    /**
     * 查询开始时间
     */
    @ApiModelProperty(value = "开始时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime startTime;

    /**
     * 查询
     */
    @ApiModelProperty(value = "结束时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime endTime;

    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("预警消息默认展示100条")
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    /**
     * wazuh macIds
     */
    @ApiModelProperty(value = "macId集合", required = false)
    private List<String> macIds;

    /**
     * agent names
     */
    @ApiModelProperty(value = "agentName列表", required = false)
    private List<String> agentNames;

    /**
     * 分类 Windows，Linux，邮件服务器，Web服务器，网络服务等
     */
    @ApiModelProperty(value = "分类集合 分类 windows; linux; cisco; hp; freebsd; junos; mail_server 邮件服务器; web_server web服务器; database 数据库; net_server 网络服务器; app 应用软件; cloud 云; load_balancer 负载均衡; app_framework 应用框架; av; fw; ids/ips; vpn; other 其他; ")
    private List<String> catagories;

    /**
     * 关键字集合
     */
    @ApiModelProperty(value = "关键字集合")
    private List<String> keyword;

    /**
     * 级别
     */
    @ApiModelProperty(value = "级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    /**
     * 指定rule.groups
     */
    @ApiModelProperty(value = "wazuh events规则组集合")
    private List<String> ruleGroups;
}
