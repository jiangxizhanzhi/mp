package cn.cyberpeace.mp.aggregation.request.sceneimport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/21
 */
@Data
@ApiModel
public class MoundRequest {
    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "路径")
    private String path;
}
