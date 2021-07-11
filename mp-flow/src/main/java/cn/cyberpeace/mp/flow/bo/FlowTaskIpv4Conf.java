package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("ipv4配置")
public class FlowTaskIpv4Conf {

    
    @ApiModelProperty("TTL")
    private Integer ttl;

    
    @ApiModelProperty("DSCP")
    private String dscp;

    
    @ApiModelProperty("源IP")
    private String sip;

    
    @ApiModelProperty("目的IP")
    private String dip;

}
