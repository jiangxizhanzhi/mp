package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("系统监控事件变更查询条件")
public class WazuhEventRequest {

    
    @ApiModelProperty(value = "开始时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime startTime;

    
    @ApiModelProperty(value = "结束时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime endTime;

    
    @ApiModelProperty("预警消息默认展示100条")
    private Integer pageSize = 100;

    
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    
    @ApiModelProperty(value = "macId集合", required = false)
    private List<String> macIds;

    
    @ApiModelProperty(value = "agentName列表", required = false)
    private List<String> agentNames;

    
    @ApiModelProperty(value = "分类集合 分类 windows; linux; cisco; hp; freebsd; junos; mail_server 邮件服务器; web_server web服务器; database 数据库; net_server 网络服务器; app 应用软件; cloud 云; load_balancer 负载均衡; app_framework 应用框架; av; fw; ids/ips; vpn; other 其他; ")
    private List<String> catagories;

    
    @ApiModelProperty(value = "关键字集合")
    private List<String> keyword;

    
    @ApiModelProperty(value = "级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    
    @ApiModelProperty(value = "wazuh events规则组集合")
    private List<String> ruleGroups;

    
    @ApiModelProperty(value = "wazuh events规则id集合")
    private List<String> ruleIds;

    
    @ApiModelProperty(value = "文件或注册表路径")
    private String path;

    
    @ApiModelProperty(value = "是否是滚动分页请求")
    private boolean scroll = false;

    
    @ApiModelProperty(value = "事件触发标识id")
    private String sicLaunchId;

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

}
