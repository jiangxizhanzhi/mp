package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:06
 */
@Data
@Builder
@ApiModel("资源统计-靶标类型统计")
public class TargetSummary {

    @ApiModelProperty("容器靶标")
    private Integer containerTarget;

    @ApiModelProperty("实体靶标")
    private Integer physicalTarget;

    @ApiModelProperty("tgm靶标")
    private Integer tgmTarget;

    @ApiModelProperty("靶场测试")
    private Integer cyberTestScene;

    @ApiModelProperty("虚拟靶标")
    private Integer vmTarget;

}
