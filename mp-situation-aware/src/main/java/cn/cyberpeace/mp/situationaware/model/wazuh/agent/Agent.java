package cn.cyberpeace.mp.situationaware.model.wazuh.agent;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@ApiModel("wazuh代理")
public class Agent {

    private OS os;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "组")
    private List<String> group;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "最近一次活跃时间")
    private Date lastKeepAlive;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "节点名称")
    @JSONField(name = "node_name")
    private String nodeName;

    @ApiModelProperty(value = "加入时间")
    private Date dateAdd;

    @ApiModelProperty(value = "代理名称")
    private String name;

    @ApiModelProperty(value = "管理者")
    private String manager;

    @ApiModelProperty(value = "注册IP")
    private String registerIP;

}
