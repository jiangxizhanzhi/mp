package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogVO {
    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "原始日志信息")
    private String content;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "攻击源ip(战队ip)")
    private String attackIp;

    @ApiModelProperty(value = "日志安全等级")
    private Integer severity;
}
