package cn.cyberpeace.mp.agent.netconf.model;

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
