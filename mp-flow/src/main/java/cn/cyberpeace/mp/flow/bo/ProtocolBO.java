package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 协议解析结果对象
 * @Author qiaokang
 * @Date：2022/11/15 17:14
 */
@Data
@ApiModel("协议解析结果对象BO")
public class ProtocolBO implements Comparable<ProtocolBO> {

    @ApiModelProperty("协议名称")
    private String name;

    @ApiModelProperty("协议百分比")
    private double rate;

    @ApiModelProperty("协议描述")
    private String desc;

    @Override
    public int compareTo(ProtocolBO bo) {
        return Double.compare(bo.getRate(), this.getRate());
    }
}
