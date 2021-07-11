package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
@ApiModel("协议解析结果对象BO")
public class ProtocolBO implements Comparable<ProtocolBO> {

    @ApiModelProperty("协议名称")
    private String protocolName;

    @ApiModelProperty("协议百分比")
    private BigDecimal rate;

    @ApiModelProperty("协议描述")
    private String desc;

    
    @ApiModelProperty(value = "是否勾选：0 否，1 是", required = true)
    private Integer marked = 1;

    @Override
    public int compareTo(ProtocolBO bo) {
        return bo.getRate().compareTo(this.getRate());

    }
}
