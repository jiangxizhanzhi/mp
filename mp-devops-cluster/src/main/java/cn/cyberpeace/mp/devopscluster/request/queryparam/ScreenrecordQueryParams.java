package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ScreenrecordQueryParams
 * @Description 录屏通用查询条件
 * @Author qiaokang
 * @Date 2021/8/31 17:17
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "录屏通用查询条件")
public class ScreenrecordQueryParams extends QueryParams {

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "场景id")
    private String csceneId;

    @ApiModelProperty(value = "节点id")
    private String nodeId;

    @ApiModelProperty(value = "用户id")
    private String userId;

}
