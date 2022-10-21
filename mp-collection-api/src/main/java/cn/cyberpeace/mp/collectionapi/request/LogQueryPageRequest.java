package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogQueryPageRequest {
    @ApiModelProperty(value = "设备名称")
    private String equipment;

    @ApiModelProperty(value = "设备类型")
    private String equipmentType;

    @ApiModelProperty(value = "战队名称")
    private String team;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "查询类型 0:白盒所有 1:白盒异常 2:黑盒异常")
    private Integer type;

    @ApiModelProperty(value = "当前页 默认1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量 默认10")
    private Integer pageSize = 10;
}
