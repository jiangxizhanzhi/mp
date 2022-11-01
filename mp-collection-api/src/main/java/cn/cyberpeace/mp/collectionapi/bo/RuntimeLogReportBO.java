package cn.cyberpeace.mp.collectionapi.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RuntimeLogReportBO {
    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "设备类型")
    private String type;

    @ApiModelProperty(value = "日志时间")
    private String time;

    @ApiModelProperty(value = "日志类型")
    private String logType;

    @ApiModelProperty(value = "原始日志信息")
    private String message;
}
