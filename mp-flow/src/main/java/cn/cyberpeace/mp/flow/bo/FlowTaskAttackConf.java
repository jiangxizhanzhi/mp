package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("攻击流量参数配置")
public class FlowTaskAttackConf {

    
    @ApiModelProperty("参数名")
    private String confKey;

    
    @ApiModelProperty("参数值")
    private String confValue;

    
    @ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
    private Integer confType;

    
    @ApiModelProperty(value = "参数说明")
    private String confDesc;
}
