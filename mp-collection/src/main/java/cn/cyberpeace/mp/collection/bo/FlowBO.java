package cn.cyberpeace.mp.collection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;

@Data
public class FlowBO {

    @ApiModelProperty(value = "时间")
    @JSONField(name = "@timestamp")
    private LocalDateTime time;

    @ApiModelProperty(value = "源IP")
    @JSONField(name = "source.ip")
    private String sourceIp;

    @ApiModelProperty(value = "目的IP")
    @JSONField(name = "destination.ip")
    private String targetIp;

    @ApiModelProperty(value = "攻击方式")
    @JSONField(name = "suricata.category")
    private String attackType;

    @ApiModelProperty(value = "告警级别")
    @JSONField(name = "suricata.severity")
    private String severity;

    @ApiModelProperty(value = "协议")
    @JSONField(name = "protocol")
    private String protocol;

}
