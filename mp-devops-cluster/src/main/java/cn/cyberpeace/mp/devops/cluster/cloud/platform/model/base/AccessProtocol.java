package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 访问协议
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocol implements Serializable {

    /**
     * 协议
     */
    @ApiModelProperty("协议")
    private String protocol;

    /**
     * 认证协议
     */
    @ApiModelProperty("认证协议")
    private String authProtocol;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String hostname;

    /**
     * 端口
     */
    @ApiModelProperty("端口")
    private Integer port;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 是否密码
     */
    @ApiModelProperty("是否密码访问")
    private Boolean privateKeyEnable;

    @ApiModelProperty("秘钥文件")
    private String privateKeyPath;

    /**d
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

}
