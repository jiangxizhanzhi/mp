package cn.cyberpeace.mp.devops.cluster.request;

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
    @ApiModelProperty(value = "主机名称/节点名称")
    private String host;

    @ApiModelProperty(value = "IP地址")
    private String addr;

    public InstancePageRequest(String host) {
        this.host = host;
    }

    public InstancePageRequest(String host, String addr) {
        this.host = host;
        this.addr = addr;
    }
}
