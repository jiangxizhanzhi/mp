package cn.cyberpeace.mp.agent.netconf.model;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class NetconfRegisterBody implements Serializable, SnakeHttpData {

    @ApiModelProperty("代理名称")
    private String agentName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("代理服务ip地址")
    private String ipAddress;

    @ApiModelProperty("代理服务端口")
    private Integer port;

    @ApiModelProperty("代理接收配置接口地址")
    private String url;

    @ApiModelProperty("代理类型")
    private String agentType;

    @ApiModelProperty("是否为总代理")
    private Integer parent;

    @ApiModelProperty("代理节点编号")
    private String nodeId;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("所属系统id")
    private String systemId;

    @ApiModelProperty("是否为总代理")
    private Boolean fatherAgent;

}
