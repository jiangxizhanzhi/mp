 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProtocolBO.java.
 // File size 1001.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName ProtocolBO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Comparable
 */
@Data
@ApiModel("协议解析结果对象BO")
public class ProtocolBO implements Comparable<ProtocolBO> {

    @ApiModelProperty("协议名称")
    private String protocolName;

    @ApiModelProperty("协议百分比")
    private BigDecimal rate;

    @ApiModelProperty("协议描述")
    private String desc;

    /**
     * 是否勾选：0 否，1 是
     */
    @ApiModelProperty(value = "是否勾选：0 否，1 是", required = true)
    private Integer marked = 1;

    @Override
    public int compareTo(ProtocolBO bo) {
        return bo.getRate().compareTo(this.getRate());
//        return Double.compare(bo.getRate(), this.getRate());
    }
}
