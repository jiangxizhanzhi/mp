package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("tcp配置")
public class FlowTaskTcpConf {

    
    @ApiModelProperty("MSS")
    private Integer mss;

    
    @ApiModelProperty("UGR")
    private Integer ugr;

    
    @ApiModelProperty("ACK")
    private Integer ack;

    
    @ApiModelProperty("PSH")
    private Integer psh;

    
    @ApiModelProperty("RST")
    private Integer rst;

    
    @ApiModelProperty("SYN")
    private Integer syn;

    
    @ApiModelProperty("FIN")
    private Integer fin;

    
    @ApiModelProperty("源端口")
    private Integer sport;

    
    @ApiModelProperty("目的端口")
    private Integer dport;
}
