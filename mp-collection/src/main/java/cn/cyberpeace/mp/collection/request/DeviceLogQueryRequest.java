package cn.cyberpeace.mp.collection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class DeviceLogQueryRequest {
    @ApiModelProperty(value = "开始时间")
    private String timeStart;

    @ApiModelProperty(value = "结束时间")
    private String timeEnd;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "设备类型")
    @NotBlank
    private String type;

    @ApiModelProperty(value = "日志类型")
    @NotBlank
    private String logType;

    @ApiModelProperty(value = "每页数量 默认100")
    private Integer pageSize = 100;

    @ApiModelProperty(value = "当前页 默认1")
    private Integer pageIndex = 1;
}
