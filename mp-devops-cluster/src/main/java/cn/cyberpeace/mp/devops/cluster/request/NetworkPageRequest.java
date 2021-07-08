package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 网络分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "网络分页请求信息")
public class NetworkPageRequest extends BasePageRequest implements Serializable {

    /**
     * 网络名称
     */
    @ApiModelProperty(value = "网络名称")
    private String name;

}
