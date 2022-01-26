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

    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty(value = "keywords集合")
    private List<String> keyword;

}
