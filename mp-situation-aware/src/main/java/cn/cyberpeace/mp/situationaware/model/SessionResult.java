package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SessionResult {

    
    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("源Mac")
    private List<String> sourceMac;

    @ApiModelProperty("目的Mac")
    private List<String> targetMac;

    @ApiModelProperty("源端口")
    private String srcPort;

    @ApiModelProperty("目的端口")
    private String dstPort;

    
    @ApiModelProperty("协议类型")
    private List<String> protocolType;

    @ApiModelProperty("时间")
    private Long timestamp;

    @ApiModelProperty("流量大小")
    private Integer totalBytes;
}
