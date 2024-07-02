package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowTaskTcpConf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("tcp配置")
public class FlowTaskTcpConf {

    /**
     * MSS
     */
    @ApiModelProperty("MSS")
    private Integer mss;

    /**
     * UGR
     */
    @ApiModelProperty("UGR")
    private Integer ugr;

    /**
     * ACK
     */
    @ApiModelProperty("ACK")
    private Integer ack;

    /**
     * PSH
     */
    @ApiModelProperty("PSH")
    private Integer psh;

    /**
     * RST
     */
    @ApiModelProperty("RST")
    private Integer rst;

    /**
     * SYN
     */
    @ApiModelProperty("SYN")
    private Integer syn;

    /**
     * FIN
     */
    @ApiModelProperty("FIN")
    private Integer fin;

    /**
     * 源端口
     */
    @ApiModelProperty("源端口")
    private Integer sport;

    /**
     * 目的端口
     */
    @ApiModelProperty("目的端口")
    private Integer dport;
}
