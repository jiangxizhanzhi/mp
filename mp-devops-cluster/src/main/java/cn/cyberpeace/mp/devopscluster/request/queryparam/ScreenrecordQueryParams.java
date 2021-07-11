package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "录屏通用查询条件")
public class ScreenrecordQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "场景id")
    private String csceneId;

    @ApiModelProperty(value = "节点id")
    private String nodeId;

    @ApiModelProperty(value = "用户id")
    private String userId;

}
