package cn.cyberpeace.mp.agent.netconf.controller.request;

import cn.cyberpeace.mp.agent.netconf.service.BasicAccessInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NetConfAccessInfo extends BasicAccessInfo {

    //@ApiModelProperty("节点id")
    //private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;


}
