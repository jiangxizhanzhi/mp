package cn.cyberpeace.mp.scenepluginconfig.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Table("mp_plugin_link_agent_type")
public class PluginLinkAgentType extends BaseModel {

    @ApiModelProperty("插件类型")
    private String pluginType;

    @ApiModelProperty("代理类型")
    private String agentType;

}
