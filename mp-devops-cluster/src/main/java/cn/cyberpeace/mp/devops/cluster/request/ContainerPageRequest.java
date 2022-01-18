package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 容器分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "容器分页请求信息")
public class ContainerPageRequest extends BasePageRequest implements Serializable {

    /**
     * 主机名/节点名称
     */
    @ApiModelProperty(value = "主机名/节点名称")
    private String host;

    /**
     * 容器地址
     */
    @ApiModelProperty(value = "容器地址")
    private String ipaddress;

    public ContainerPageRequest(String host) {
        this.host = host;
    }
}
