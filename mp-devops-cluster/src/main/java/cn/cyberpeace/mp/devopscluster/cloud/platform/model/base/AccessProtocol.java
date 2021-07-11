package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocol implements Serializable {

    
    @ApiModelProperty("协议")
    private String protocol;

    
    @ApiModelProperty("认证协议")
    private String authProtocol;

    
    @ApiModelProperty("IP地址")
    private String hostname;

    
    @ApiModelProperty("端口")
    private Integer port;

    
    @ApiModelProperty("用户名")
    private String username;

    
    @ApiModelProperty("密码")
    private String password;

    
    @ApiModelProperty("是否秘钥访问")
    private Boolean privateKeyEnable;

    @ApiModelProperty("秘钥文件")
    private String privateKeyPath;

    @ApiModelProperty("秘钥文件名称")
    private String privateKeyFileName;

    
    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("是否管理接入")
    private Boolean manageMode;

}
