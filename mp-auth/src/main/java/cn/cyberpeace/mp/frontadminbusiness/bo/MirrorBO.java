/**
 * 镜像业务对象类，用于封装镜像相关的业务信息。
 */
package cn.cyberpeace.mp.frontadminbusiness.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MirrorBO {
    /**
     * 镜像的IP地址，用于标识镜像所在的网络地址。
     */
    @ApiModelProperty(value = "镜像的ip")
    private String ip;

    /**
     * 全局流量镜像状态，用于控制是否对流量进行镜像。
     * 状态为"on"表示开启镜像，"off"表示关闭镜像。
     */
    @ApiModelProperty(value = "全局流量镜像状态，on为开，off为关")
    private String mirrorStatus;
}
