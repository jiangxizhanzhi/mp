package cn.cyberpeace.mp.flow.bo;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量协议BO
 * @Author qiaokang
 * @Date：2023/4/27 10:15
 */
@Data
@ApiModel("流量协议BO")
public class FlowProtocolBO {

    /**
     * 协议名称
     */
    @ApiModelProperty(value = "协议名称", required = true)
    @DefaultStr
    private String protocolName;

    /**
     * 比例
     */
    @ApiModelProperty(value = "比例", required = true)
    private Float rate;

    /**
     * 是否勾选：0 否，1 是
     */
    @ApiModelProperty(value = "是否勾选：0 否，1 是", required = true)
    private Integer marked;

}
