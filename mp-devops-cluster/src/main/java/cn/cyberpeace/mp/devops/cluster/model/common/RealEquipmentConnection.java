package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("实装登录信息")
public class RealEquipmentConnection {

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("IP")
    private String hostname;

    @ApiModelProperty("链接ID")
    private Integer connectionId;

    @ApiModelProperty("登录URL")
    private String url;

    @ApiModelProperty("内部IP")
    private String internalIp;
}
