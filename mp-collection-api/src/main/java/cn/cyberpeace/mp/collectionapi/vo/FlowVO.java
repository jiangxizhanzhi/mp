package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowVO {
    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "源IP")
    private String sourceIp;

    @ApiModelProperty(value = "目的IP")
    private String targetIp;

    @ApiModelProperty(value = "攻击方式")
    private String attackType;

    @ApiModelProperty(value = "告警级别")
    private String severity;

    @ApiModelProperty(value = "协议")
    private String protocol;
}
