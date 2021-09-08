package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocol{

    /**
     * 协议
     */
    @ApiModelProperty("协议")
    private String protocol;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String ip;

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

}
