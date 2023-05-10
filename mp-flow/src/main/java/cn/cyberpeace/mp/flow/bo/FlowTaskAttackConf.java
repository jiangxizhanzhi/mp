package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 攻击流量参数配置
 * @Author: guquan
 * @Date：2023/5/9 13:56
 */
@Data
@ApiModel("攻击流量参数配置")
public class FlowTaskAttackConf {

    /**
     * 参数名
     */
    @ApiModelProperty("参数名")
    private String confKey;

    /**
     * 参数值
     */
    @ApiModelProperty("参数值")
    private String confValue;

    /**
     * 参数类型：0 string,1 IP,2 number,3 bool
     */
    @ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
    private Integer confType;

    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    private String confDesc;
}
