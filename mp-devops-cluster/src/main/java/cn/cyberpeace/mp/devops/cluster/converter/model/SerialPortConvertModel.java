package cn.cyberpeace.mp.devops.cluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPortConvertModel {

    @ApiModelProperty("TCP连接生存时间/分钟")
    private Integer comAliveT;

    @ApiModelProperty("最大连接数")
    private Integer comLocalP;

    @ApiModelProperty("本地端口")
    private Integer tcpLinkMax;

    @ApiModelProperty("串口交换机的IP地址")
    private String host;

    @ApiModelProperty("串口ID")
    private String interfaceId;
}
