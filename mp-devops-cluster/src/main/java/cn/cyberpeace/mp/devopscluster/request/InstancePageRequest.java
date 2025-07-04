package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实例分页请求信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstancePageRequest extends BasePageRequest implements Serializable {

    /**
     * 主机名称/节点名称
     */
    @ApiModelProperty(value = "所属物理机")
    private String host;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "虚拟机名称")
    private String deviceName;

    @ApiModelProperty(value = "状态,  已启动 ; 2 未启动 ;")
    private String status;

    @ApiModelProperty(value = "IP地址")
    private String addr;
}
