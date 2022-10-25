package cn.cyberpeace.mp.collectionapi.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogReportBO {
    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "攻击源ip")
    private String attackIp;

    @ApiModelProperty(value = "日志安全等级 0:正常 1:扰动 2:瞬时逃逸 3:完全逃逸")
    private Integer severity;

    @ApiModelProperty(value = "原始日志信息")
    private String message;
}
