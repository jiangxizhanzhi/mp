package cn.cyberpeace.mp.collectionapi.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogReportBO {
    @ApiModelProperty(value = "设备id")
    private String id;

    @ApiModelProperty(value = "时间")
    private String time;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "攻击源ip")
    private String attackIp;

    @ApiModelProperty(value = "日志安全等级 0:正常 1:异常")
    private Integer severity;

    @ApiModelProperty(value = "日志具体内容")
    private String message;
}
