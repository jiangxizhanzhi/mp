package cn.cyberpeace.mp.situationaware.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuricataData {

    @ApiModelProperty("匹配规则时进行的操作")
    private String action;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("源ip")
    private String source;

    @ApiModelProperty("源端口")
    private String sourcePorts;

    @ApiModelProperty("方向")
    private String direction;

    @ApiModelProperty("目的ip")
    private String destination;

    @ApiModelProperty("目的端口")
    private String destinationPorts;

    
    @ApiModelProperty("规则体")
    private String ruleMsg;

    

    @ApiModelProperty("规则名称")
    private String ruleName;

    
    @ApiModelProperty("SID")
    private String sid;

    
    @ApiModelProperty("Att&ck")
    private String attck;

}
