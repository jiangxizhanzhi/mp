package cn.cyberpeace.mp.scenepluginconfig.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_agent_registry")
public class AgentRegistry extends BaseModel {

    @ApiModelProperty("代理名称")
    private String name;

    @ApiModelProperty("代理描述")
    private String description;

    @ApiModelProperty("接收配置接口地址")
    private String url;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("自身代理类型")
    private String type;

    @ApiModelProperty("插件类型")
    @DefaultStr
    private String pluginType;

    @ApiModelProperty("是否为总代理")
    private Integer parent;

    @ApiModelProperty("是否在线")
    private Integer online;

    @ApiModelProperty("代理节点id")
    private String nodeId;

    @ApiModelProperty("最后活跃时间")
    private LocalDateTime lastTime;

}
