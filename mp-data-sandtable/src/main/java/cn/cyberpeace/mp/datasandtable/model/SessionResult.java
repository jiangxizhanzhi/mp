package cn.cyberpeace.mp.datasandtable.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SessionResult {

    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("源Mac")
    private String sourceMac;

    @ApiModelProperty("目的Mac")
    private String targetMac;

    @ApiModelProperty("源端口")
    private String srcPort;

    @ApiModelProperty("目的端口")
    private String dstPort;

    /**
     * 协议类型
     */
    @ApiModelProperty("协议类型")
    private String protocolType;

    @ApiModelProperty("时间")
    private String timestamp;

    @ApiModelProperty("流量大小")
    private Integer totalBytes;
}
