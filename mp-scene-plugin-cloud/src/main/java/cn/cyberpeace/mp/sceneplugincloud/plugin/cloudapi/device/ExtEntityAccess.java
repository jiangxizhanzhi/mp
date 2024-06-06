package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntityAccess implements Serializable {

    /**
     * 接入类型
     */
    @ApiModelProperty("接入类型")
    private String accessType;

    /**
     * 交换机端口
     */
    @ApiModelProperty("交换机端口")
    private String switchPort;

    /**
     * 交换机名称
     */
    @ApiModelProperty("交换机名称")
    private String switchName;

    /**
     * 实体端口名
     */
    @ApiModelProperty("实体端口名")
    private String portName;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String ip;

    /**
     * IMAC地址
     */
    @ApiModelProperty("IMAC地址")
    private String mac;

    /**
     * 管理协议列表
     */
    @ApiModelProperty("管理协议列表")
    private List<AccessProtocol> accessProtocols;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AccessProtocol implements Serializable {

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
     * 是否秘钥访问
     */
    @ApiModelProperty("是否秘钥访问")
    private Boolean privateKeyEnable;

    @ApiModelProperty("秘钥文件")
    private String privateKeyPath;

    @ApiModelProperty("秘钥文件名称")
    private String privateKeyFileName;

    /**d
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("是否管理接入")
    private Boolean manageMode;

}
