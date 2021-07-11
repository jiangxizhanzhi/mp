package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel
public class CollectionAppRequest extends BaseTimeRangeRequest {

    @ApiModelProperty(value = "分类 windows; linux; cisco; hp; freebsd; junos; mail_server 邮件服务器; web_server web服务器; database 数据库; net_server 网络服务器; app 应用软件; cloud 云; load_balancer 负载均衡; app_framework 应用框架; av; fw; ids/ips; vpn; other 其他; ")
    private List<String> catagories;

}
