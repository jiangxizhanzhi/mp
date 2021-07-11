package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class HostEventQueryParams extends QueryParams {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;

    @ApiModelProperty(value = "分类 windows; linux; cisco; hp; freebsd; junos; mail_server 邮件服务器; web_server web服务器; database 数据库; net_server 网络服务器; app 应用软件; cloud 云; load_balancer 负载均衡; app_framework 应用框架; av; fw; ids/ips; vpn; other 其他; ")
    private List<String> catagories;

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty(value = "keywords集合")
    private List<String> keyword;

    @ApiModelProperty(value = "应用名称")
    private List<String> appNames;

    @ApiModelProperty(value = "rule group集合")
    private List<String> ruleGroups;

    @ApiModelProperty(value = "文件或注册表路径 模糊查询")
    private String path;

    
    @ApiModelProperty(value = "是否是滚动分页请求")
    private Boolean scroll;
}
