package cn.cyberpeace.mp.aggregation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:09
 */
@Data
@Builder
@ApiModel("用户资源")
public class UserResourceResponse {

    @ApiModelProperty("容器数量")
    private Integer containerCount;

    @ApiModelProperty("实装设备数量")
    private Integer entityCount;

    @ApiModelProperty("场景数量")
    private Integer sceneCount;

    @ApiModelProperty("虚拟机数量")
    private Integer vmCount;

}
