package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * 路由分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "路由分页请求信息")
public class RouterPageRequest extends BasePageRequest implements Serializable {

    /**
     * 路由名称
     */
    @ApiModelProperty(value = "路由名称")
    private String name;

}
