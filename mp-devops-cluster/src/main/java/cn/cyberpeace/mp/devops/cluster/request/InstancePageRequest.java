package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实例分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实例分页请求信息")
public class InstancePageRequest extends BasePageRequest implements Serializable {

    /**
     * 主机名称/节点名称
     */
    @ApiModelProperty(value = "主机名称/节点名称")
    private String host;
}
