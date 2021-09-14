package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class InstanceQueryParams extends QueryParams {
    @ApiModelProperty(value = "引用类型")
    private String referenceType;
    @ApiModelProperty(value = "场景实例状态")
    private String sceneStatus;
    @ApiModelProperty(value = "是否只显示活动状态")
    private String active;
    @ApiModelProperty(value = "是否只显示已结束")
    private String over;
    @ApiModelProperty(value = "是否只显示异常")
    private String error;
}
